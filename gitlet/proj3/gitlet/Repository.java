package gitlet;

import java.io.IOException;

/**
 * The repository class.
 * @author Joshua Park
 */
public class Repository {
    /**
     * Repository constructor.
     * Exception handled by caller.
     */
    Repository() throws IOException {
        Paths.GITLET.mkdir();
        Paths.HEAD.createNewFile();
        Paths.COMMITS.mkdir();
        Paths.BLOBS.mkdir();

        Paths.STAGEDFORADD.createNewFile();
        Paths.STAGEDFORREMOVE.createNewFile();

        Paths.BRANCHES.mkdir();

        Stages stages = new Stages();
        Commit initialCommit = new Commit();
        Head headPtr = new Head("master", initialCommit.getSha1());
        Branch master = new Branch("master", initialCommit.getSha1());
    }
}
