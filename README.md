# neo4j-procedure-example

This is a simple example of a Neo4j procedure demonstrating how the `DijkstraBidirectional` algo behaves unexpectedly.

# Build, install and run
1. `./gradlew build`
2. `cp ./build/libs/mt-dijkstra-issue-1.0.0.jar path/to/your/neo4j/plugins/`
3. Restart Neo4j
4. Create the demo graph (see below).
5. Run the demo query (see below).

# Evaluate the result
- The demo query returns only one path while I expect more paths to be found with an increasing cost.
- Check the log output (neo4j.log)
  - The cost evaluator is run twice for every relationship while I expect it to be run only once per relationship.
  - The direction passed into the cost evaluator is always OUTGOING. I expect it to be INCOMING when the path expander is in reverse mode.

# Demo graph
![Demo Graph](/graph.png)
```
CREATE
  (a:Point {id: "A"}),
  (b:Point {id: "B"}),
  (c:Point {id: "C"}),
  (d:Point {id: "D"}),
  (e:Point {id: "E"}),
  (f:Point {id: "F"}),
  (g:Point {id: "G"}),
  (h:Point {id: "H"})
CREATE
  (a) -  [:LINE] -> (c),
  (a) <- [:LINE] - (c),
  (a) -  [:LINE] -> (d),
  (a) <- [:LINE] - (d),
  (a) -  [:LINE] -> (g),
  (a) <- [:LINE] - (g),
  (c) -  [:LINE] -> (f),
  (c) <- [:LINE] - (f),
  (c) -  [:LINE] -> (b),
  (c) <- [:LINE] - (b),
  (d) -  [:LINE] -> (f),
  (d) <- [:LINE] - (f),
  (d) -  [:LINE] -> (e),
  (d) <- [:LINE] - (e),
  (g) -  [:LINE] -> (h),
  (g) <- [:LINE] - (h),
  (f) -  [:LINE] -> (e),
  (f) <- [:LINE] - (e),
  (h) -  [:LINE] -> (b),
  (h) <- [:LINE] - (b),
  (e) -  [:LINE] -> (b),
  (e) <- [:LINE] - (b)
RETURN "Graph created";

MATCH (start:Point) - [l:LINE] -> (end:Point)
SET l.id = start.id + " => " + end.id
RETURN count(*) + " relationship IDs set";
```

# Demo query
```
MATCH (start:Point {id: "A"}), (end:Point {id: "B"})
CALL mt.findPaths(start, end) YIELD path
RETURN count(*) AS number_of_paths_found;
```

# Sample log output (from neo4j.log)
```
2017-11-09 16:37:20.468+0000 ERROR MtPathExpander: init. OUTGOING
2017-11-09 16:37:20.491+0000 ERROR MtPathExpander: init. INCOMING
2017-11-09 16:37:20.512+0000 ERROR MtPathExpander: expand. INCOMING. Node: B
2017-11-09 16:37:20.522+0000 ERROR MtPathExpander: expand. OUTGOING. Node: A
2017-11-09 16:37:20.525+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.526+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.526+0000 ERROR MtCostEvaluator: OUTGOING H => B
2017-11-09 16:37:20.526+0000 ERROR MtCostEvaluator: OUTGOING H => B
2017-11-09 16:37:20.526+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.526+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.527+0000 ERROR MtCostEvaluator: OUTGOING A => G
2017-11-09 16:37:20.527+0000 ERROR MtCostEvaluator: OUTGOING A => G
2017-11-09 16:37:20.527+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.527+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.527+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.528+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.528+0000 ERROR MtPathExpander: expand. INCOMING. Node: E
2017-11-09 16:37:20.528+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.528+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.529+0000 ERROR MtCostEvaluator: OUTGOING D => E
2017-11-09 16:37:20.529+0000 ERROR MtCostEvaluator: OUTGOING D => E
2017-11-09 16:37:20.529+0000 ERROR MtPathExpander: expand. OUTGOING. Node: G
2017-11-09 16:37:20.529+0000 ERROR MtCostEvaluator: OUTGOING G => H
2017-11-09 16:37:20.529+0000 ERROR MtCostEvaluator: OUTGOING G => H
2017-11-09 16:37:20.533+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.533+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.534+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.534+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.534+0000 ERROR MtPathExpander: expand. INCOMING. Node: C
2017-11-09 16:37:20.534+0000 ERROR MtCostEvaluator: OUTGOING F => C
2017-11-09 16:37:20.535+0000 ERROR MtCostEvaluator: OUTGOING F => C
2017-11-09 16:37:20.535+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.535+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.535+0000 ERROR MtPathExpander: expand. OUTGOING. Node: C
2017-11-09 16:37:20.535+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.535+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.536+0000 ERROR MtCostEvaluator: OUTGOING C => F
2017-11-09 16:37:20.536+0000 ERROR MtCostEvaluator: OUTGOING C => F
2017-11-09 16:37:20.536+0000 ERROR MtPathExpander: expand. INCOMING. Node: H
2017-11-09 16:37:20.536+0000 ERROR MtCostEvaluator: OUTGOING G => H
2017-11-09 16:37:20.537+0000 ERROR MtCostEvaluator: OUTGOING G => H
2017-11-09 16:37:20.537+0000 ERROR MtPathExpander: expand. OUTGOING. Node: D
2017-11-09 16:37:20.537+0000 ERROR MtCostEvaluator: OUTGOING D => E
2017-11-09 16:37:20.537+0000 ERROR MtCostEvaluator: OUTGOING D => E
2017-11-09 16:37:20.538+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.538+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.538+0000 ERROR MtPathExpander: expand. INCOMING. Node: A
2017-11-09 16:37:20.538+0000 ERROR MtCostEvaluator: OUTGOING G => A
2017-11-09 16:37:20.538+0000 ERROR MtCostEvaluator: OUTGOING G => A
2017-11-09 16:37:20.539+0000 ERROR MtCostEvaluator: OUTGOING D => A
2017-11-09 16:37:20.539+0000 ERROR MtCostEvaluator: OUTGOING D => A
2017-11-09 16:37:20.539+0000 ERROR MtCostEvaluator: OUTGOING A => G
2017-11-09 16:37:20.539+0000 ERROR MtCostEvaluator: OUTGOING G => H
2017-11-09 16:37:20.540+0000 ERROR MtCostEvaluator: OUTGOING H => B
2017-11-09 16:37:20.540+0000 ERROR MtPathExpander: expand. OUTGOING. Node: F
2017-11-09 16:37:20.540+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.540+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.541+0000 ERROR MtCostEvaluator: OUTGOING F => C
2017-11-09 16:37:20.541+0000 ERROR MtCostEvaluator: OUTGOING F => C
2017-11-09 16:37:20.541+0000 ERROR MtPathExpander: expand. INCOMING. Node: G
2017-11-09 16:37:20.541+0000 ERROR MtCostEvaluator: OUTGOING A => G
2017-11-09 16:37:20.542+0000 ERROR MtCostEvaluator: OUTGOING A => G
2017-11-09 16:37:20.542+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.542+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.542+0000 ERROR MtCostEvaluator: OUTGOING F => C
2017-11-09 16:37:20.542+0000 ERROR MtCostEvaluator: OUTGOING C => B
2017-11-09 16:37:20.543+0000 ERROR MtPathExpander: expand. OUTGOING. Node: F
2017-11-09 16:37:20.543+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.543+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.543+0000 ERROR MtCostEvaluator: OUTGOING F => D
2017-11-09 16:37:20.543+0000 ERROR MtCostEvaluator: OUTGOING F => D
2017-11-09 16:37:20.544+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.544+0000 ERROR MtCostEvaluator: OUTGOING D => E
2017-11-09 16:37:20.544+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.544+0000 ERROR MtPathExpander: expand. INCOMING. Node: F
2017-11-09 16:37:20.544+0000 ERROR MtCostEvaluator: OUTGOING E => F
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING E => F
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING A => C
2017-11-09 16:37:20.545+0000 ERROR MtCostEvaluator: OUTGOING C => F
2017-11-09 16:37:20.546+0000 ERROR MtCostEvaluator: OUTGOING F => E
2017-11-09 16:37:20.546+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.546+0000 ERROR MtPathExpander: expand. OUTGOING. Node: E
2017-11-09 16:37:20.546+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.546+0000 ERROR MtCostEvaluator: OUTGOING E => B
2017-11-09 16:37:20.546+0000 ERROR MtCostEvaluator: OUTGOING E => F
2017-11-09 16:37:20.546+0000 ERROR MtCostEvaluator: OUTGOING E => F
2017-11-09 16:37:20.546+0000 ERROR MtPathExpander: expand. INCOMING. Node: F
2017-11-09 16:37:20.547+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.547+0000 ERROR MtCostEvaluator: OUTGOING D => F
2017-11-09 16:37:20.547+0000 ERROR MtCostEvaluator: OUTGOING C => F
2017-11-09 16:37:20.547+0000 ERROR MtCostEvaluator: OUTGOING C => F
2017-11-09 16:37:20.547+0000 ERROR MtPathExpander: expand. OUTGOING. Node: B
2017-11-09 16:37:20.548+0000 ERROR MtCostEvaluator: OUTGOING B => E
2017-11-09 16:37:20.548+0000 ERROR MtCostEvaluator: OUTGOING B => E
2017-11-09 16:37:20.548+0000 ERROR MtCostEvaluator: OUTGOING B => H
2017-11-09 16:37:20.548+0000 ERROR MtCostEvaluator: OUTGOING B => H
2017-11-09 16:37:20.548+0000 ERROR MtPathExpander: expand. INCOMING. Node: D
2017-11-09 16:37:20.548+0000 ERROR MtCostEvaluator: OUTGOING F => D
2017-11-09 16:37:20.549+0000 ERROR MtCostEvaluator: OUTGOING F => D
2017-11-09 16:37:20.549+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.549+0000 ERROR MtCostEvaluator: OUTGOING A => D
2017-11-09 16:37:20.549+0000 ERROR MtPathExpander: expand. OUTGOING. Node: H
2017-11-09 16:37:20.549+0000 ERROR MtCostEvaluator: OUTGOING H => B
2017-11-09 16:37:20.550+0000 ERROR MtCostEvaluator: OUTGOING H => B
```
