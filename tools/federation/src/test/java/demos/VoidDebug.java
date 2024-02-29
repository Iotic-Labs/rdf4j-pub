/*******************************************************************************
 * Copyright (c) 2019 Eclipse RDF4J contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *******************************************************************************/
package demos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.rdf4j.federated.FedXConfig;
import org.eclipse.rdf4j.federated.FedXFactory;
import org.eclipse.rdf4j.federated.endpoint.Endpoint;
import org.eclipse.rdf4j.federated.endpoint.EndpointFactory;
import org.eclipse.rdf4j.federated.repository.FedXRepository;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;

public class VoidDebug {

	private static final List<Endpoint> ENDPOINTS = Collections.unmodifiableList(Arrays.asList(
		EndpointFactory.loadSPARQLEndpoint("fuseki1", "http://localhost:3030/text/query"),
		EndpointFactory.loadSPARQLEndpoint("fuseki2", "http://localhost:3031/text/query")
	));

	private static FedXRepository makeRepo() {
		return FedXFactory.newFederation()
				.withConfig(new FedXConfig()
				// disable cache for testing purposes
				.withSourceSelectionCacheSpec("maximumSize=0")
				// disable for debugging to not time out
				.withEnforceMaxQueryTime(0)
				.withLogQueries(true)
				.withLogQueryPlan(true)
				.withDebugQueryPlan(true)
				)
				.withMembers(ENDPOINTS)
				.create();
	}

	public static void main(String[] args) throws Exception {
		FedXRepository repo = makeRepo();

		String q =
			"PREFIX iotics: <http://data.iotics.com/iotics#>\n" +
			"SELECT ?twin ?pointName WHERE {\n" +
			"  ?twin a iotics:DigitalTwin . " +
			"  ?twin iotics:advertises ?point ." +
			"  ?point iotics:pointName ?pointName ." +
			// "  ?point iotics:presents ?value ." +
			// "  ?value iotics:valueKey ?valueKey ." +
			"}" +
			"LIMIT 10"
			;

		try {
			repo.init();
			Thread.sleep(2000);
			TupleQuery query = repo.getQueryManager().prepareTupleQuery(q);
			try (TupleQueryResult res = query.evaluate()) {

				while (res.hasNext()) {
					System.out.println(res.next());
				}
			}
		} finally {
			repo.shutDown();
		}
		System.out.println("Done.");
		System.exit(0);
	}
}
