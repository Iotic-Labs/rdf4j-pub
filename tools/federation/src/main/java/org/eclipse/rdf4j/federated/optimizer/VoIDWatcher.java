package org.eclipse.rdf4j.federated.optimizer;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.rdf4j.federated.EndpointManager;
import org.eclipse.rdf4j.federated.endpoint.Endpoint;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* PoC info

 * Ontology spec: https://www.w3.org/TR/void/
 * VoID generator: https://github.com/jervenBolleman/void-generator
 */
public class VoIDWatcher {

    private static final Logger log = LoggerFactory.getLogger(VoIDWatcher.class);

    // key: Endpoint.getId()
    private ConcurrentHashMap<String,DsVoidStats> endpointStats = new ConcurrentHashMap<>();

    private Thread watcher;

    public VoIDWatcher(EndpointManager endpointManager, long pollIntervalMs) {
        watcher = new Thread(new WatcherThread(endpointStats, endpointManager, pollIntervalMs));
    }

    public DsVoidStats getEndpointStats(String endpointId) {
        return endpointStats.get(endpointId);
    }

    public void start() {
        if (!watcher.isAlive()) {
            watcher.start();
        }
    }

    public void stop(boolean wait) {
        watcher.interrupt();
        if (wait) {
            try {
                watcher.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class WatcherThread implements Runnable {

        EndpointManager endpointManager;
        ConcurrentHashMap<String,DsVoidStats> endpointStats;
        long pollInterval;

        private WatcherThread(ConcurrentHashMap<String,DsVoidStats> endpointStats, EndpointManager endpointManager, long pollInterval) {
            this.endpointManager = endpointManager;
            this.endpointStats = endpointStats;
            this.pollInterval = pollInterval;
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                long startTime = System.currentTimeMillis();
                try {
                    for (Endpoint e : endpointManager.getAvailableEndpoints()) {
                        updateVoidStats(e);
                        log.warn("Stats update complete");
                    }
                    // (TODO - discard stats for endpoints which no longer exist)
                    // TODO - instead of a fixed interval, rely on VoID data expiry, per endpoint
                    // Q: dcterms:modified is expected to be included - but applies to the DS itself, not the VoID data.
                    // What expiry to use?
                    Thread.sleep(Math.max(0, pollInterval - (System.currentTimeMillis() - startTime)));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // TODO - limited to default graph
        private static final String FETCH_VOID_DS_DEFAULT_STATS =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX sd: <http://www.w3.org/ns/sparql-service-description#>\n" +
            "PREFIX void: <http://rdfs.org/ns/void#>\n\n" +
            "SELECT ?triples ?subjects ?objects {\n" +
            "  [] rdf:Property sd:Service ;\n" +
            "    sd:defaultDataset / sd:defaultGraph [\n" +
            "      void:triples ?triples ;\n" +
            "      void:distinctSubjects ?subjects ;\n" +
            "      void:distinctObjects ?objects \n" +
            "    ] .\n" +
            "}";

        // TODO - this doesn't differentiate graphs into account. For now combine information from all graphs (i.e. for
        // simplicity assume union default graph mode)
        private static final String FETCH_VOID_PROPERTY_STATS =
            "PREFIX void: <http://rdfs.org/ns/void#>\n\n" +
            "SELECT ?p (SUM(?triplesG) AS ?triples) (SUM(?subjectsG) AS ?subjects) (SUM(?objectsG) AS ?objects) {\n" +
            "  [] void:property ?p ;\n" +
            "    void:triples ?triplesG ;\n" +
            "    void:distinctSubjects ?subjectsG ;\n" +
            "    void:distinctObjects ?objectsG .\n" +
            "}" +
            "GROUP BY ?p";

            private void updateGraphStats(DsVoidStats stats, Endpoint e, RepositoryConnection conn) {
                TupleQuery query = conn.prepareTupleQuery(FETCH_VOID_DS_DEFAULT_STATS);
                try (TupleQueryResult res = query.evaluate()) {
                    if (!res.hasNext()) {
                        log.warn("[{}] Default graph stats not found", e.getName());
                        return;
                    }
                    BindingSet bindings = res.next();
                    stats.setDefaultGraphStats(
                        stats.new VoidStat(
                            ((Literal) bindings.getBinding("triples").getValue()).longValue(),
                            ((Literal) bindings.getBinding("subjects").getValue()).longValue(),
                            ((Literal) bindings.getBinding("objects").getValue()).longValue()
                        )
                    );
                    if (res.hasNext()) {
                        log.warn("[{}] Default graph query produced multiple rows, ignoring", e.getName());
                    }
                }
            }

        private void updatePropStats(DsVoidStats stats, Endpoint e, RepositoryConnection conn) {
            TupleQuery query = conn.prepareTupleQuery(FETCH_VOID_PROPERTY_STATS);
            try (TupleQueryResult res = query.evaluate()) {
                while (res.hasNext()) {
                    BindingSet bindings = res.next();
                    Value prop = bindings.getBinding("p").getValue();
                    if (!prop.isIRI()) {
                        log.warn("[{}] Ignoring non-IRI void:property: {}", e.getName(), prop);
                        continue;
                    }
                    stats.setPropStats(
                        (IRI) prop,
                        stats.new VoidStat(
                            ((Literal) bindings.getBinding("triples").getValue()).longValue(),
                            ((Literal) bindings.getBinding("subjects").getValue()).longValue(),
                            ((Literal) bindings.getBinding("objects").getValue()).longValue()
                        )
                    );
                }
            }
        }

        private void updateVoidStats(Endpoint e) {
            log.debug("[{}] Updating stats", e.getName());
            DsVoidStats stats = new DsVoidStats();

            try (RepositoryConnection conn = e.getConnection()) {
                updateGraphStats(stats, e, conn);
                updatePropStats(stats, e, conn);
                endpointStats.put(e.getId(), stats);
            } catch (Exception ex) {
                log.error("[{}] Update failed: {}", e.getName(), ex);
            }
        }
    }

    public static class DsVoidStats {

        // TODO - per-graph stats ()
        private VoidStat defaultGraph;
        private HashMap<IRI,VoidStat> byProp = new HashMap<>();

        private void setDefaultGraphStats(VoidStat stats) {
            defaultGraph = stats;
        }

        public VoidStat getDefaultGraphStats() {
            return defaultGraph;
        }

        private void setPropStats(IRI p, VoidStat stats) {
            this.byProp.put(p, stats);
        }

        public VoidStat getPropStatsFor(IRI p) {
            return this.byProp.get(p);
        }

        // TODO - generate classes based on ontology & auto-parse fetched VoID RDF data? (Or store as in-memory model?)
        public class VoidStat {
            public final long triples; // void:triples
            public final long subjects; // void:distinctSubjects
            public final long objects; // void:distinctObjects

            public VoidStat(long triples, long subjects, long objects) {
                this.triples = triples;
                this.subjects = subjects;
                this.objects = objects;
            }
        }
    }
}
