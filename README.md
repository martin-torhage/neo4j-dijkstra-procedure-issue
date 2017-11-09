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

# Create demo graph
```
CREATE
  (a:Point {id: "a"}),
  (b:Point {id: "b"}),
  (c:Point {id: "c"}),
  (d:Point {id: "d"}),
  (e:Point {id: "e"}),
  (f:Point {id: "f"}),
  (g:Point {id: "g"}),
  (h:Point {id: "h"})
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
RETURN "Demo graph created";
```

# Demo query
```
MATCH (start:Point {id: "a"}), (end:Point {id: "b"})
CALL mt.findPaths(start, end) YIELD path
RETURN count(*) AS number_of_paths_found;
```

# Sample log output (from neo4j.log)
```
2017-11-09 14:11:38.701+0000 ERROR MtPathExpander: init. OUTGOING
2017-11-09 14:11:38.720+0000 ERROR MtPathExpander: init. INCOMING
2017-11-09 14:11:38.740+0000 ERROR MtPathExpander: expand. INCOMING. Node ID: 1
2017-11-09 14:11:38.747+0000 ERROR MtPathExpander: expand. OUTGOING. Node ID: 0
2017-11-09 14:11:38.748+0000 ERROR MtCostEvaluator: Relationship ID: 20. OUTGOING
2017-11-09 14:11:38.748+0000 ERROR MtCostEvaluator: Relationship ID: 20. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 18. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 18. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 4. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 4. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 2. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 2. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtPathExpander: expand. INCOMING. Node ID: 4
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 16. OUTGOING
2017-11-09 14:11:38.749+0000 ERROR MtCostEvaluator: Relationship ID: 16. OUTGOING
2017-11-09 14:11:38.750+0000 ERROR MtCostEvaluator: Relationship ID: 12. OUTGOING
2017-11-09 14:11:38.750+0000 ERROR MtCostEvaluator: Relationship ID: 12. OUTGOING
2017-11-09 14:11:38.750+0000 ERROR MtPathExpander: expand. OUTGOING. Node ID: 6
2017-11-09 14:11:38.750+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING
2017-11-09 14:11:38.750+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtPathExpander: expand. INCOMING. Node ID: 2
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 7. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 7. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING
2017-11-09 14:11:38.752+0000 ERROR MtPathExpander: expand. OUTGOING. Node ID: 2
2017-11-09 14:11:38.753+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING
2017-11-09 14:11:38.753+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING
2017-11-09 14:11:38.753+0000 ERROR MtCostEvaluator: Relationship ID: 6. OUTGOING
2017-11-09 14:11:38.753+0000 ERROR MtCostEvaluator: Relationship ID: 6. OUTGOING
2017-11-09 14:11:38.753+0000 ERROR MtPathExpander: expand. INCOMING. Node ID: 7
2017-11-09 14:11:38.753+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING
2017-11-09 14:11:38.753+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING
2017-11-09 14:11:38.753+0000 ERROR MtPathExpander: expand. OUTGOING. Node ID: 3
[snip]
```
