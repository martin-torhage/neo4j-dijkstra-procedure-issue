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
2017-11-09 15:52:31.566+0000 ERROR MtPathExpander: init. OUTGOING
2017-11-09 15:52:31.586+0000 ERROR MtPathExpander: init. INCOMING
2017-11-09 15:52:31.609+0000 ERROR MtPathExpander: expand. INCOMING. Node: b
2017-11-09 15:52:31.618+0000 ERROR MtPathExpander: expand. OUTGOING. Node: a
2017-11-09 15:52:31.623+0000 ERROR MtCostEvaluator: Relationship ID: 20. OUTGOING. From: e To: b
2017-11-09 15:52:31.624+0000 ERROR MtCostEvaluator: Relationship ID: 20. OUTGOING. From: e To: b
2017-11-09 15:52:31.624+0000 ERROR MtCostEvaluator: Relationship ID: 18. OUTGOING. From: h To: b
2017-11-09 15:52:31.624+0000 ERROR MtCostEvaluator: Relationship ID: 18. OUTGOING. From: h To: b
2017-11-09 15:52:31.625+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING. From: c To: b
2017-11-09 15:52:31.625+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING. From: c To: b
2017-11-09 15:52:31.625+0000 ERROR MtCostEvaluator: Relationship ID: 4. OUTGOING. From: a To: g
2017-11-09 15:52:31.625+0000 ERROR MtCostEvaluator: Relationship ID: 4. OUTGOING. From: a To: g
2017-11-09 15:52:31.626+0000 ERROR MtCostEvaluator: Relationship ID: 2. OUTGOING. From: a To: d
2017-11-09 15:52:31.626+0000 ERROR MtCostEvaluator: Relationship ID: 2. OUTGOING. From: a To: d
2017-11-09 15:52:31.626+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING. From: a To: c
2017-11-09 15:52:31.626+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING. From: a To: c
2017-11-09 15:52:31.626+0000 ERROR MtPathExpander: expand. INCOMING. Node: e
2017-11-09 15:52:31.627+0000 ERROR MtCostEvaluator: Relationship ID: 16. OUTGOING. From: f To: e
2017-11-09 15:52:31.627+0000 ERROR MtCostEvaluator: Relationship ID: 16. OUTGOING. From: f To: e
2017-11-09 15:52:31.627+0000 ERROR MtCostEvaluator: Relationship ID: 12. OUTGOING. From: d To: e
2017-11-09 15:52:31.628+0000 ERROR MtCostEvaluator: Relationship ID: 12. OUTGOING. From: d To: e
2017-11-09 15:52:31.628+0000 ERROR MtPathExpander: expand. OUTGOING. Node: g
2017-11-09 15:52:31.628+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING. From: g To: h
2017-11-09 15:52:31.628+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING. From: g To: h
2017-11-09 15:52:31.631+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING. From: a To: c
2017-11-09 15:52:31.631+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING. From: c To: b
2017-11-09 15:52:31.631+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING. From: a To: c
2017-11-09 15:52:31.631+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING. From: c To: b
2017-11-09 15:52:31.631+0000 ERROR MtPathExpander: expand. INCOMING. Node: c
2017-11-09 15:52:31.631+0000 ERROR MtCostEvaluator: Relationship ID: 7. OUTGOING. From: f To: c
2017-11-09 15:52:31.632+0000 ERROR MtCostEvaluator: Relationship ID: 7. OUTGOING. From: f To: c
2017-11-09 15:52:31.632+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING. From: a To: c
2017-11-09 15:52:31.632+0000 ERROR MtCostEvaluator: Relationship ID: 0. OUTGOING. From: a To: c
2017-11-09 15:52:31.632+0000 ERROR MtPathExpander: expand. OUTGOING. Node: c
2017-11-09 15:52:31.632+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING. From: c To: b
2017-11-09 15:52:31.633+0000 ERROR MtCostEvaluator: Relationship ID: 8. OUTGOING. From: c To: b
2017-11-09 15:52:31.633+0000 ERROR MtCostEvaluator: Relationship ID: 6. OUTGOING. From: c To: f
2017-11-09 15:52:31.633+0000 ERROR MtCostEvaluator: Relationship ID: 6. OUTGOING. From: c To: f
2017-11-09 15:52:31.633+0000 ERROR MtPathExpander: expand. INCOMING. Node: h
2017-11-09 15:52:31.634+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING. From: g To: h
2017-11-09 15:52:31.634+0000 ERROR MtCostEvaluator: Relationship ID: 14. OUTGOING. From: g To: h
2017-11-09 15:52:31.634+0000 ERROR MtPathExpander: expand. OUTGOING. Node: d
[snip]
```
