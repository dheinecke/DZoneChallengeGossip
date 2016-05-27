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

import java.io.Console;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GossipRunner {

    enum CannedRoute {

        ROUTE0(
            "3 drivers, route lengths between 4 and 5 stops each",
            new int[][] {
                {3, 1, 2, 3},
                {3, 2, 3, 1},
                {4, 2, 3, 4, 5}
            }),
        ROUTE1(
            "2 drivers, route length of 3 stops, no overlaps",
            new int[][] {
                {2, 1, 2},
                {5, 2, 8}
            }),
        ROUTE2(
            "7 drivers, route lengths between 4 and 8 stops ",
            new int[][] {
                {7, 11, 2, 2, 4, 8, 2, 2},
                {3, 0, 11, 8},
                {5, 11, 8, 10, 3, 11},
                {5, 9, 2, 5, 0, 3},
                {7, 4, 8, 2, 8, 1, 0, 5},
                {3, 6, 8, 9},
                {4, 2, 11, 3, 3}
            }),
        ROUTE3(
            "14 drivers, route lengths between 7 and 27 stops",
            new int[][]{
                {12, 23, 15, 2, 8, 20, 21, 3, 23, 3, 27, 20, 0},
                {21, 14, 8, 20, 10, 0, 23, 3, 24, 23, 0, 19, 14, 12, 10, 9, 12, 12, 11, 6, 27, 5},
                {8, 18, 27, 10, 11, 22, 29, 23, 14},
                {13, 7, 14, 1, 9, 14, 16, 12, 0, 10, 13, 19, 16, 17},
                {24, 25, 21, 4, 6, 19, 1, 3, 26, 11, 22, 28, 14, 14, 27, 7, 20, 8, 7, 4, 1, 8, 10, 18, 21},
                {13, 20, 26, 22, 6, 5, 6, 23, 26, 2, 21, 16, 26, 24},
                {6, 7, 17, 2, 22, 23, 21},
                {23, 14, 22, 28, 10, 23, 7, 21, 3, 20, 24, 23, 8, 8, 21, 13, 15, 6, 9, 17, 27, 17, 13, 14},
                {23, 13, 1, 15, 5, 16, 7, 26, 22, 29, 17, 3, 14, 16, 16, 18, 6, 10, 3, 14, 10, 17, 27, 25},
                {25, 28, 5, 21, 8, 10, 27, 21, 23, 28, 7, 20, 6, 6, 9, 29, 27, 26, 24, 3, 12, 10, 21, 10, 12, 17},
                {26, 22, 26, 13, 10, 19, 3, 15, 2, 3, 25, 29, 25, 19, 19, 24, 1, 26, 22, 10, 17, 19, 28, 11, 22, 2, 13},
                {8, 4, 25, 15, 20, 9, 11, 3, 19},
                {24, 29, 4, 17, 2, 0, 8, 19, 11, 28, 13, 4, 16, 5, 15, 25, 16, 5, 6, 1, 0, 19, 7, 4, 6},
                {16, 25, 15, 17, 20, 27, 1, 11, 1, 18, 14, 23, 27, 25, 26, 17, 1}
            }),
        ROUTE4(
            "3 drivers, route length between 3 and 4",
            new int[][] {
                {1, 2, 3, 3},
                {3, 1, 2},
                {2, 3, 1}
            }),
        ROOUTE5(
            "2 drivers, routes identical with 2 stops",
            new int[][] {
                {1, 2},
                {1, 2}
            });

        CannedRoute(String desc, int[][] route) {
            this.desc = desc;
            this.route = route;
        }

        String desc;
        int[][] route;
    }

    public static void main(String[] args) {
        boolean debug = false;
        String file = null;
        int routeIndex = -1;
        try {
            int i = -1;
            while(++i < args.length) {
                if(args[i].startsWith("-f"))
                    file = args[++i];
                else if(args[i].startsWith("-d"))
                    debug = true;
                else if(args[i].startsWith("-c"))
                    routeIndex = Integer.parseInt(args[++i].trim());
                else if(args[i].startsWith("-?") || args[i].startsWith("-h")) {
                    showHelp();
                    System.exit(0);
                }
                else throw new IllegalArgumentException("unknown argument: "+args[i]);
            }
            // validate arguments
            if(file != null && routeIndex != -1)
                throw new IllegalStateException("cannot select both -f and -c options");

            int[][] routes;
            if(0 <= routeIndex) {
                // user selected to use a canned test route
                if(routeIndex >= CannedRoute.values().length)
                    throw new IllegalArgumentException("bad test index: must be digit [0-"+(CannedRoute.values().length-1)+"]");
                routes = CannedRoute.values()[routeIndex].route;
            } else if(file != null) {
                // user selected to use a file to load route
                try(Stream<String> stream = Files.lines(Paths.get(file))) {
                    routes = stream
                            .filter(s -> !s.startsWith("#")) // filter comment lines
                            .map(s -> Arrays.stream(s.split(","))
                                    .map(String::trim)
                                    .mapToInt(Integer::parseInt)
                                    .toArray())
                            .toArray(int[][]::new);
                }
            } else {
                // user is going to enter routes on console
                Console cons = System.console();
                if(cons == null)
                    throw new IllegalStateException("Console input not supported in this environment");
                cons.writer().println("Enter routes as integers separated by commas.");
                cons.writer().println("Enter an empty line to end manual entry and execute test");
                int line_num = 0;
                List<int[]> manualRoutes = new ArrayList<>();
                for(;;) {
                    String line = cons.readLine("Enter route #: "+line_num+" => ");
                    try {
                        if(line == null || "".equals(line.trim()))
                            break;
                        manualRoutes.add(Arrays.stream(line.split(","))
                                .map(String::trim)
                                .mapToInt(Integer::parseInt)
                                .toArray());
                        ++line_num;
                    } catch(Exception ex) {
                        cons.writer().println("Error: " + ex.getMessage());
                    }
                }
                routes = manualRoutes.toArray(new int [manualRoutes.size()][]);
            }
            // execute the Gossip and return the result
            System.out.println(new Gossip(routes).setDebug(debug).getResult());
        } catch(Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            showHelp();
        }
    }

    public static void showHelp() {
        System.out.println("usage: GossipRunner [-d] -f <filename>");
        System.out.println("       GossipRunner [-d] -c <test_index>");
        System.out.println("       GossipRunner [-d]");
        System.out.println("   filename: name of filename to load route information from");
        System.out.println("       each line that doesn't begin with a # is a route");
        System.out.println("       each line should contain a comma-separated list of stops");
        System.out.println("           i.e. 4,6,7,8");
        System.out.println("   test_index: index of the canned route to use [0-"+(CannedRoute.values().length-1)+"]");
        for(int i=0; i < CannedRoute.values().length; ++i) {
            System.out.println("        "+i+": "+CannedRoute.values()[i].desc);
        }
        System.out.println("   without specifying -f or -c, user must manually enter each route on the console");
        System.out.println("   -d: prints debug information for each stop");
        System.out.println("   -h, -?: shows this help");
    }
}
