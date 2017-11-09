package mt.pathfinder;

import org.neo4j.graphdb.*;
import org.neo4j.graphalgo.CostEvaluator;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MtCostEvaluator implements CostEvaluator<Double>
{
    private static Logger LOGGER = Logger.getLogger("mt.MtCostEvaluator");

    public MtCostEvaluator () {
        super();
    }

    public Double getCost(Relationship rel, Direction direction) {
        String nodeMsg = "From: " + customId(rel.getStartNode()) + " To: " + customId(rel.getEndNode());
        LOGGER.log(Level.SEVERE, "MtCostEvaluator: Relationship ID: " + rel.getId() + ". " + direction.toString() + ". " + nodeMsg);
        return 1.0;
    }

    private String customId(Node node) {
        return node.getProperty("id").toString();
    }
}
