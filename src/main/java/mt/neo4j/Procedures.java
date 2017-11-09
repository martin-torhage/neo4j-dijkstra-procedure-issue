package mt.pathfinder;

import org.neo4j.graphdb.Node;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.impl.path.DijkstraBidirectional;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;
import java.util.stream.Stream;

public class Procedures {

    @Procedure(value = "mt.findPaths")
    public Stream<WeightedPathResult> findPaths(
            @Name("startNode") Node startNode,
            @Name("endNode") Node endNode)
    {

        PathFinder<WeightedPath> algo = new DijkstraBidirectional(
                new MtPathExpander(),
                new MtCostEvaluator()
        );
        return WeightedPathResult.streamWeightedPathResult(startNode, endNode, algo);
    }
}
