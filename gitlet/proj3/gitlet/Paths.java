package gitlet;

import java.io.File;

/**
 * Paths class.
 * @author joshua park
 */
public class Paths {
    /**
     * Gitlet directory.
     */
    public static final File GITLET = new File(".gitlet");

    /**
     * Commit directory.
     */
    public static final File COMMITS = Utils.join(GITLET, "commits");

    /**
     * Head file.
     */
    public static final File HEAD = Utils.join(GITLET, "head");

    /**
     * Head file.
     */
    public static final File BLOBS = Utils.join(GITLET, "blobs");

    /**
     * Branch directory.
     */
    public static final File BRANCHES = Utils.join(GITLET, "branches");

    /**
     * File to keep track of staged for additions.
     */
    public static final File STAGEDFORADD = Utils.join(GITLET,
            "stage_addition");

    /**
     * File to keep track of staged for removals.
     */
    public static final File STAGEDFORREMOVE = Utils.join(GITLET,
            "stage_removal");

}
