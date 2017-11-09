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
RETURN "Demo graph created";
```

# Demo query
```
MATCH (start:Point {id: "A"}), (end:Point {id: "B"})
CALL mt.findPaths(start, end) YIELD path
RETURN count(*) AS number_of_paths_found;
```

# Sample log output (from neo4j.log)
```
2017-11-09 16:17:23.321+0000 ERROR MtPathExpander: init. OUTGOING
2017-11-09 16:17:23.321+0000 ERROR MtPathExpander: init. INCOMING
2017-11-09 16:17:23.322+0000 ERROR MtPathExpander: expand. INCOMING. Node: B
2017-11-09 16:17:23.322+0000 ERROR MtPathExpander: expand. OUTGOING. Node: A
2017-11-09 16:17:23.322+0000 ERROR MtCostEvaluator: Relationship ID: 30. OUTGOING. From: C To: B
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 30. OUTGOING. From: C To: B
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 40. OUTGOING. From: H To: B
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 40. OUTGOING. From: H To: B
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 42. OUTGOING. From: E To: B
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 42. OUTGOING. From: E To: B
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 26. OUTGOING. From: A To: G
2017-11-09 16:17:23.323+0000 ERROR MtCostEvaluator: Relationship ID: 26. OUTGOING. From: A To: G
2017-11-09 16:17:23.324+0000 ERROR MtCostEvaluator: Relationship ID: 24. OUTGOING. From: A To: D
2017-11-09 16:17:23.324+0000 ERROR MtCostEvaluator: Relationship ID: 24. OUTGOING. From: A To: D
2017-11-09 16:17:23.324+0000 ERROR MtCostEvaluator: Relationship ID: 22. OUTGOING. From: A To: C
2017-11-09 16:17:23.324+0000 ERROR MtCostEvaluator: Relationship ID: 22. OUTGOING. From: A To: C
2017-11-09 16:17:23.324+0000 ERROR MtPathExpander: expand. INCOMING. Node: C
2017-11-09 16:17:23.324+0000 ERROR MtCostEvaluator: Relationship ID: 29. OUTGOING. From: F To: C
2017-11-09 16:17:23.325+0000 ERROR MtCostEvaluator: Relationship ID: 29. OUTGOING. From: F To: C
2017-11-09 16:17:23.325+0000 ERROR MtCostEvaluator: Relationship ID: 22. OUTGOING. From: A To: C
2017-11-09 16:17:23.325+0000 ERROR MtCostEvaluator: Relationship ID: 22. OUTGOING. From: A To: C
2017-11-09 16:17:23.325+0000 ERROR MtPathExpander: expand. OUTGOING. Node: G
2017-11-09 16:17:23.325+0000 ERROR MtCostEvaluator: Relationship ID: 36. OUTGOING. From: G To: H
2017-11-09 16:17:23.325+0000 ERROR MtCostEvaluator: Relationship ID: 36. OUTGOING. From: G To: H
2017-11-09 16:17:23.326+0000 ERROR MtCostEvaluator: Relationship ID: 22. OUTGOING. From: A To: C
2017-11-09 16:17:23.326+0000 ERROR MtCostEvaluator: Relationship ID: 30. OUTGOING. From: C To: B
2017-11-09 16:17:23.326+0000 ERROR MtCostEvaluator: Relationship ID: 22. OUTGOING. From: A To: C
2017-11-09 16:17:23.326+0000 ERROR MtCostEvaluator: Relationship ID: 30. OUTGOING. From: C To: B
2017-11-09 16:17:23.326+0000 ERROR MtPathExpander: expand. INCOMING. Node: E
2017-11-09 16:17:23.326+0000 ERROR MtCostEvaluator: Relationship ID: 38. OUTGOING. From: F To: E
2017-11-09 16:17:23.326+0000 ERROR MtCostEvaluator: Relationship ID: 38. OUTGOING. From: F To: E
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 34. OUTGOING. From: D To: E
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 34. OUTGOING. From: D To: E
2017-11-09 16:17:23.327+0000 ERROR MtPathExpander: expand. OUTGOING. Node: C
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 30. OUTGOING. From: C To: B
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 30. OUTGOING. From: C To: B
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 28. OUTGOING. From: C To: F
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 28. OUTGOING. From: C To: F
2017-11-09 16:17:23.327+0000 ERROR MtPathExpander: expand. INCOMING. Node: H
2017-11-09 16:17:23.327+0000 ERROR MtCostEvaluator: Relationship ID: 36. OUTGOING. From: G To: H
2017-11-09 16:17:23.328+0000 ERROR MtCostEvaluator: Relationship ID: 36. OUTGOING. From: G To: H
2017-11-09 16:17:23.328+0000 ERROR MtCostEvaluator: Relationship ID: 24. OUTGOING. From: A To: D
2017-11-09 16:17:23.328+0000 ERROR MtCostEvaluator: Relationship ID: 34. OUTGOING. From: D To: E
2017-11-09 16:17:23.328+0000 ERROR MtCostEvaluator: Relationship ID: 42. OUTGOING. From: E To: B
2017-11-09 16:17:23.328+0000 ERROR MtPathExpander: expand. OUTGOING. Node: D
2017-11-09 16:17:23.328+0000 ERROR MtCostEvaluator: Relationship ID: 32. OUTGOING. From: D To: F
2017-11-09 16:17:23.328+0000 ERROR MtCostEvaluator: Relationship ID: 32. OUTGOING. From: D To: F
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 34. OUTGOING. From: D To: E
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 34. OUTGOING. From: D To: E
2017-11-09 16:17:23.329+0000 ERROR MtPathExpander: expand. INCOMING. Node: D
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 24. OUTGOING. From: A To: D
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 24. OUTGOING. From: A To: D
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 33. OUTGOING. From: F To: D
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 33. OUTGOING. From: F To: D
2017-11-09 16:17:23.329+0000 ERROR MtCostEvaluator: Relationship ID: 26. OUTGOING. From: A To: G
2017-11-09 16:17:23.330+0000 ERROR MtCostEvaluator: Relationship ID: 36. OUTGOING. From: G To: H
2017-11-09 16:17:23.330+0000 ERROR MtCostEvaluator: Relationship ID: 40. OUTGOING. From: H To: B
2017-11-09 16:17:23.330+0000 ERROR MtPathExpander: expand. OUTGOING. Node: F
2017-11-09 16:17:23.330+0000 ERROR MtCostEvaluator: Relationship ID: 29. OUTGOING. From: F To: C
2017-11-09 16:17:23.330+0000 ERROR MtCostEvaluator: Relationship ID: 29. OUTGOING. From: F To: C
2017-11-09 16:17:23.330+0000 ERROR MtCostEvaluator: Relationship ID: 38. OUTGOING. From: F To: E
2017-11-09 16:17:23.330+0000 ERROR MtCostEvaluator: Relationship ID: 38. OUTGOING. From: F To: E
2017-11-09 16:17:23.330+0000 ERROR MtPathExpander: expand. INCOMING. Node: G
[snip]
```
