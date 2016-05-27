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

import java.util.stream.IntStream;

/**
 * My solution for the DZone Java Code Challenge: Bus GossipBonus
 *
 * https://dzone.com/articles/java-code-challenge-bus-gossip
 */

public class Gossip {
    public static void main(String[] args) {

//        int[][] routes = {
//            {3,1,2,3},
//            {3,2,3,1},
//            {4,2,3,4,5}
//        };

//        int[][] routes = {
//            {2, 1, 2},
//            {5, 2, 8}
//        };

        int[][] routes = {
            {7, 11, 2, 2, 4, 8, 2, 2},
            {3, 0, 11, 8},
            {5, 11, 8, 10, 3, 11},
            {5, 9, 2, 5, 0, 3},
            {7, 4, 8, 2, 8, 1, 0, 5},
            {3, 6, 8, 9},
            {4, 2, 11, 3, 3}
        };

//        int[][] routes = {
//                {12,23,15,2,8,20,21,3,23,3,27,20,0},
//                {21,14,8,20,10,0,23,3,24,23,0,19,14,12,10,9,12,12,11,6,27,5},
//                {8,18,27,10,11,22,29,23,14},
//                {13,7,14,1,9,14,16,12,0,10,13,19,16,17},
//                {24,25,21,4,6,19,1,3,26,11,22,28,14,14,27,7,20,8,7,4,1,8,10,18,21},
//                {13,20,26,22,6,5,6,23,26,2,21,16,26,24},
//                {6,7,17,2,22,23,21},
//                {23,14,22,28,10,23,7,21,3,20,24,23,8,8,21,13,15,6,9,17,27,17,13,14},
//                {23,13,1,15,5,16,7,26,22,29,17,3,14,16,16,18,6,10,3,14,10,17,27,25},
//                {25,28,5,21,8,10,27,21,23,28,7,20,6,6,9,29,27,26,24,3,12,10,21,10,12,17},
//                {26,22,26,13,10,19,3,15,2,3,25,29,25,19,19,24,1,26,22,10,17,19,28,11,22,2,13},
//                {8,4,25,15,20,9,11,3,19},
//                {24,29,4,17,2,0,8,19,11,28,13,4,16,5,15,25,16,5,6,1,0,19,7,4,6},
//                {16,25,15,17,20,27,1,11,1,18,14,23,27,25,26,17,1},
//        };

//        int[][] routes = {
//            {1,2,3,3},
//            {3,1,2},
//            {2,3,1}
//        };

        // corner-case: drivers start at same stop
//        int[][] routes = {
//                {1,2},
//                {1,2}
//        };

        // current location of each driver
        int[] current_loc = new int[routes.length];
        // gossip is complete when driver has complete set
        final int complete = (int) Math.pow(2, routes.length)-1;
        // give each driver 1 exclusive gossip (bitmap)
        int[] current_gossip = new int[routes.length];
        for(int i=0; i<routes.length; ++i) {
            current_gossip[i] = 1<<i;
        }

        // main loop
        for(int tick=0; tick<=60*8; ++tick) {
            // exchange gossip
            for(int i=0; i<routes.length; ++i) {
                for(int j=0; j<routes.length; ++j) {
                    if(i == j) continue;
                    if (routes[i][current_loc[i]] == routes[j][current_loc[j]]) {
                        current_gossip[i] |= current_gossip[j];
                    }
                }
            }
            // evaluate completion criteria
            if(IntStream.of(current_gossip).allMatch(g -> g == complete)) {
                printState(tick, routes, current_gossip, current_loc);
                System.out.println(tick+1); // # of stops = ticks + 1 (first stop at time 0).
                System.exit(0);
            }
            printState(tick, routes, current_gossip, current_loc);

            // move all drivers
            for(int i=0; i<routes.length; ++i) {
                current_loc[i] = ++current_loc[i] % routes[i].length;
            }
        }
        System.out.println("never");
    }

    // prints each drivers gossip set, route, and current location.
    private static void printState(int tick, int[][] routes, int[] current_gossip, int[] current_loc) {
        System.err.println("t = "+tick);
        for(int i=0; i<routes.length; ++i) {
            final StringBuilder s = new StringBuilder(i+"");
            s.append("\t[");
            for(int j=0; j<current_gossip.length; ++j) {
                s.append((current_gossip[i] & 1<<j)>>j);
            }
            s.append("]");
            for(int j=0; j<routes[i].length; ++j) {
                s.append("\t");
                s.append(routes[i][j]);
                if(j == current_loc[i]) {
                    s.append("*"); // current_loc stop
                }
            }
            System.err.println(s.toString());
        }
    }
}
