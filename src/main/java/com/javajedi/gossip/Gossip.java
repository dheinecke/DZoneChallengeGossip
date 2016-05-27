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

import java.util.Objects;
import java.util.stream.IntStream;

/*
 * My solution for the DZone Java Code Challenge: Bus Gossip
 * https://dzone.com/articles/java-code-challenge-bus-gossip
 *
 * Notes:
 *   Not threadsafe
 *   Maximum number of drivers is 32 (uses int to encode  gossipset)
 */
public class Gossip {

    private int mins = 0;         // elapsed minutes
    private int[][] routes;        // the bus routes
    private int allGossip;           // all gossips (2^n-1)
    private int[] currentLocation; // current location of each driver
    private int[] currentGossip;   // current gossip set
    private String result = null; // result of evaluation;
    private boolean isDebug = false;

    public Gossip(final int[][] routes) {
        Objects.requireNonNull(routes, "Illegal number of routes");
        if(routes.length ==0 || routes.length > 32) {
            throw new IllegalArgumentException("Invalid number of routes");
        }
        this.routes = routes;
        this.allGossip = (int) Math.pow(2, routes.length)-1;
        this.currentLocation = new int[routes.length];
        this.currentGossip = new int[routes.length];
        for(int i=0; i<routes.length; ++i) {
            currentGossip[i] = 1 << i;
        }
    }

    public String getResult() {
        // only eval once
        if(result == null) {
            // loop until all gossip is disseminated, or until we've tried for 8 hours
            for(mins = 0; mins <= 60 * 8; ++mins) {
                // exchange gossip
                for(int i = 0; i < routes.length; ++i) {
                    for(int j = 0; j < routes.length; ++j) {
                        if(i == j) {
                            continue;
                        }
                        if(routes[i][currentLocation[i]] == routes[j][currentLocation[j]]) {
                            currentGossip[i] |= currentGossip[j];
                        }
                    }
                }
                if(isDebug) {
                    logState();
                }
                // evaluate completion criteria
                if(IntStream.of(currentGossip).allMatch(g -> g == allGossip)) {
                    result = (mins + 1) + "";
                    break;
                }
                // move all drivers
                for(int i = 0; i < routes.length; ++i) {
                    currentLocation[i] = ++currentLocation[i] % routes[i].length;
                }
            }
            if(result == null) {
                result = "never";
            }
        }
        return result;
    }

    // logs the time, gossip-set, route, and current location for each driver
    void logState() {
        System.out.println("t = "+ mins);
        for(int i=0; i<routes.length; ++i) {
            System.out.print(i);
            System.out.print("\t[");
            for(int j=0; j<currentGossip.length; ++j) {
                System.out.print((currentGossip[i] & 1<<j)>>j);
            }
            System.out.print("]");
            for(int j=0; j<routes[i].length; ++j) {
                System.out.print("\t");
                System.out.print(routes[i][j]);
                if(j == currentLocation[i]) {
                    System.out.print("*"); // current_loc stop
                }
            }
            System.out.println();
        }
    }

    public Gossip setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }
}
