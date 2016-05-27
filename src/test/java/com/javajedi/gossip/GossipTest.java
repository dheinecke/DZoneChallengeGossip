/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.javajedi.gossip;

import org.junit.Assert;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.fail;

public class GossipTest {

    @Test
    public void nullRoutes() {
        try {
            new Gossip(null);
            fail("should have thrown");
        } catch( NullPointerException ex) {}
    }

    @Test
    public void emptyRoutes() {
        try {
            new Gossip(new int[][] {} );
            fail("should have thrown");
        } catch( IllegalArgumentException ex) {}
    }

    @Test
    public void tooManyRoutes() {
        try {
            new Gossip(new int[33][2]);
            fail("should have thrown");
        } catch( IllegalArgumentException ex) {}
    }

    @Test
    public void singleRoute() {
        Gossip gossip = new Gossip(new int[][] {{1,2,3}});
        Assert.assertNull("not eval'ed yet, so should be null", gossip.getResult());
        StringWriter w = setWriter(gossip);
        gossip.eval();
        Assert.assertEquals("Degenerate case, driver had all gossip at first stop", "1", w.toString().trim());
        Assert.assertEquals("getResult should agree with outputWriter", "1", gossip.getResult().trim());
    }

    @Test
    public void publishedTestNumberOne() {
        Gossip gossip = new Gossip(
            new int[][] {
                {3,1,2,3},
                {3,2,3,1},
                {4,2,3,4,5}}).eval();
        Assert.assertEquals("5", gossip.getResult());
    }

    @Test
    public void publishedTestNumberTwo() {
        Gossip gossip = new Gossip(
            new int[][] {
                {2, 1, 2},
                {5, 2, 8}}).eval();
        Assert.assertEquals("never", gossip.getResult());
    }

    @Test
    public void publishedTestNumberThree() {
        Gossip gossip = new Gossip(
                new int[][] {
                    {7, 11, 2, 2, 4, 8, 2, 2},
                    {3, 0, 11, 8},
                    {5, 11, 8, 10, 3, 11},
                    {5, 9, 2, 5, 0, 3},
                    {7, 4, 8, 2, 8, 1, 0, 5},
                    {3, 6, 8, 9},
                    {4, 2, 11, 3, 3}}).eval();
        Assert.assertEquals("9",gossip.getResult());
    }

    @Test
    public void publishedTestNumberFour() {
        Gossip gossip = new Gossip(
                new int[][]{
                {12,23,15,2,8,20,21,3,23,3,27,20,0},
                {21,14,8,20,10,0,23,3,24,23,0,19,14,12,10,9,12,12,11,6,27,5},
                {8,18,27,10,11,22,29,23,14},
                {13,7,14,1,9,14,16,12,0,10,13,19,16,17},
                {24,25,21,4,6,19,1,3,26,11,22,28,14,14,27,7,20,8,7,4,1,8,10,18,21},
                {13,20,26,22,6,5,6,23,26,2,21,16,26,24},
                {6,7,17,2,22,23,21},
                {23,14,22,28,10,23,7,21,3,20,24,23,8,8,21,13,15,6,9,17,27,17,13,14},
                {23,13,1,15,5,16,7,26,22,29,17,3,14,16,16,18,6,10,3,14,10,17,27,25},
                {25,28,5,21,8,10,27,21,23,28,7,20,6,6,9,29,27,26,24,3,12,10,21,10,12,17},
                {26,22,26,13,10,19,3,15,2,3,25,29,25,19,19,24,1,26,22,10,17,19,28,11,22,2,13},
                {8,4,25,15,20,9,11,3,19},
                {24,29,4,17,2,0,8,19,11,28,13,4,16,5,15,25,16,5,6,1,0,19,7,4,6},
                {16,25,15,17,20,27,1,11,1,18,14,23,27,25,26,17,1}}).eval();
        Assert.assertEquals("16", gossip.getResult());
    }

    private StringWriter setWriter(Gossip gossip) {
        StringWriter writer = new StringWriter();
        gossip.setOutputWriter(new PrintWriter(writer, true));
        return writer;
    }
}
