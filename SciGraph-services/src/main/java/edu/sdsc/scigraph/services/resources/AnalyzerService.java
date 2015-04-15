/**
 * Copyright (C) 2014 The SciGraph authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.sdsc.scigraph.services.resources;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.neo4j.graphdb.GraphDatabaseService;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.sdsc.scigraph.analyzer.AnalyzeRequest;
import edu.sdsc.scigraph.analyzer.AnalyzerResult;
import edu.sdsc.scigraph.analyzer.HyperGeometricAnalyzer;
import edu.sdsc.scigraph.internal.GraphApi;
import edu.sdsc.scigraph.owlapi.curies.CurieUtil;
import edu.sdsc.scigraph.services.jersey.BaseResource;

@Path("/analyzer")
@Produces(MediaType.APPLICATION_JSON)
public class AnalyzerService extends BaseResource {

  private final GraphDatabaseService graphDb;
  private final GraphApi api;
  private final CurieUtil curieUtil;

  @Inject
  AnalyzerService(GraphDatabaseService graphDb, GraphApi api, CurieUtil curieUtil) {
    this.graphDb = graphDb;
    this.api = api;
    this.curieUtil = curieUtil;
  }

  @GET
  @Timed
  public String analyze(@QueryParam("samples") List<String> samples,
      @QueryParam("ontologyClass") String ontologyClass, @QueryParam("path") String path) {
    return runAnalysis(samples, ontologyClass, path);
  }

  @POST
  @Timed
  public String analyzePost(String body) throws JsonParseException, JsonMappingException,
      IOException {
    ObjectMapper mapper = new ObjectMapper();
    AnalyzeRequest analyzeRequest = mapper.readValue(body, AnalyzeRequest.class);
    // TODO enforce not null
    return runAnalysis(analyzeRequest.getSamples(), analyzeRequest.getOntologyClass(),
        analyzeRequest.getPath());
  }

  private String runAnalysis(List<String> sampleSet, String ontologyClass, String path) {
    HyperGeometricAnalyzer hyperGeometricAnalyzer = new HyperGeometricAnalyzer(graphDb);
    List<AnalyzerResult> pValues = hyperGeometricAnalyzer.analyze(sampleSet, ontologyClass, path);
    String response = "";
    for (AnalyzerResult p : pValues) {
      response += p.getNodeId() + " " + p.getCount() + "\n";
    }
    return response;
  }

}
