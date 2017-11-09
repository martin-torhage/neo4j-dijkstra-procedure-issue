package mt.pathfinder;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;

import java.util.logging.Level;
import java.util.logging.Logger;

class MtPathExpander implements PathExpander {
    private final Direction direction;
    private static final Logger LOGGER = Logger.getLogger("mt.MtPathExpander");

    MtPathExpander() {
        this(Direction.OUTGOING);
    }

    MtPathExpander(Direction direction) {
        this.direction = direction;
        LOGGER.log(Level.SEVERE, "MtPathExpander: init. " + direction.toString());
    }

    @Override
    public Iterable expand(Path path, BranchState state) {
        Node current = path.endNode();
        LOGGER.log(Level.SEVERE, "MtPathExpander: expand. " + this.direction.toString() + ". Node: " + current.getProperty("id"));
        return current.getRelationships(this.direction);
    }

    @Override
    public MtPathExpander reverse() {
        return new MtPathExpander(Direction.INCOMING);
    }
}
