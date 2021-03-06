/*
 *  Copyright 2016 SteelBridge Laboratories, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  For more information: http://steelbridgelabs.com
 */

package com.steelbridgelabs.oss.neo4j.structure;

import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.Relationship;

import java.util.Collections;

/**
 * @author Rogelio J. Baucells
 */
@RunWith(MockitoJUnitRunner.class)
public class Neo4JEdgeWhileCreatingUpdateCommandTest {

    @Mock
    private Neo4JGraph graph;

    @Mock
    private Neo4JSession session;

    @Mock
    private Neo4JVertex outVertex;

    @Mock
    private Neo4JVertex inVertex;

    @Mock
    private Relationship relationship;

    @Mock
    private Neo4JElementIdProvider edgeIdProvider;

    @Mock
    private Transaction transaction;

    @Mock
    private Result statementResult;

    @Mock
    private Record record;

    @Mock
    private Entity entity;

    @Mock
    private Value value;

    @Mock
    private ResultSummary resultSummary;

    @Test
    public void givenDeletedEdgeNodeShouldCreateDeleteCommand() {
        // arrange
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(outVertex.matchPattern(Mockito.any())).thenAnswer(invocation -> "(o)");
        Mockito.when(outVertex.matchPredicate(Mockito.any(), Mockito.any())).thenAnswer(invocation -> "ID(o) = $oid");
        Mockito.when(outVertex.id()).thenAnswer(invocation -> 1L);
        Mockito.when(outVertex.matchStatement(Mockito.anyString(), Mockito.anyString())).thenAnswer(invocation -> "MATCH (o) WHERE ID(o) = $oid");
        Mockito.when(inVertex.matchPattern(Mockito.any())).thenAnswer(invocation -> "(i)");
        Mockito.when(inVertex.matchPredicate(Mockito.any(), Mockito.any())).thenAnswer(invocation -> "ID(i) = $iid");
        Mockito.when(inVertex.id()).thenAnswer(invocation -> 2L);
        Mockito.when(inVertex.matchStatement(Mockito.anyString(), Mockito.anyString())).thenAnswer(invocation -> "MATCH (i) WHERE ID(i) = $iid");
        Mockito.when(relationship.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(relationship.type()).thenAnswer(invocation -> "label");
        Mockito.when(relationship.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(relationship.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 3L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.anyString())).thenAnswer(invocation -> "ID(r)");
        Mockito.when(statementResult.hasNext()).thenAnswer(invocation -> true);
        Mockito.when(statementResult.next()).thenAnswer(invocation -> record);
        Mockito.when(record.get(Mockito.eq(0))).thenAnswer(invocation -> value);
        Mockito.when(value.asEntity()).thenAnswer(invocation -> entity);
        Neo4JEdge edge = new Neo4JEdge(graph, session, edgeIdProvider, outVertex, relationship, inVertex);
        edge.remove();
        // act
        Neo4JDatabaseCommand command = edge.deleteCommand();
        // assert
        Assert.assertNotNull("Failed to create insert command", command);
        Assert.assertNotNull("Failed to create insert command statement", command.getStatement());
        Assert.assertEquals("Invalid insert command statement", command.getStatement(), "MATCH (o) WHERE ID(o) = $oid MATCH (i) WHERE ID(i) = $iid MATCH (o)-[r:`label`]->(i) WHERE ID(r) = $id DELETE r");
        Assert.assertEquals("Invalid insert command statement parameters", command.getParameters().size(), 3);
        Assert.assertNotNull("Failed to create insert command callback", command.getCallback());
        // invoke callback
        command.getCallback().accept(statementResult);
        // assert
        Assert.assertNotNull("Failed get node identifier", edge.id());
    }
}
