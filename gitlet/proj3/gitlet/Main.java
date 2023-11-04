package gitlet;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;
import java.util.Map;
/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Joshua Park
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        runCommand(args);
    }

    /**
     * run commands.
     * @param args string
     */
    private static void runCommand(String... args) {
        if (args[0].equals("init")) {
            if (Objects.nonNull(Utils.plainFilenamesIn(".gitlet"))) {
                System.out.println("A Gitlet version-control system "
                        + "already exists in the current directory.");
                System.exit(0);
            }
        } else {
            if (Objects.isNull(Utils.plainFilenamesIn(".gitlet"))) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
        }
        switch (args[0]) {
        case "init":
            doInit();
            break;
        case "add":
            doAdd(args);
            break;
        case "commit":
            doCommit(args);
            break;
        case "rm":
            doRm(args);
            break;
        case "log":
            doLog();
            break;
        case "global-log":
            doGlobalLog();
            break;
        case "find":
            doFind(args);
            break;
        case "status":
            doStatus();
            break;
        case "checkout":
            switchHelper(args);
            break;
        case "branch":
            doBranch(args);
            break;
        case "rm-branch":
            doRmBranch(args);
            break;
        case "reset":
            doReset(args);
            break;
        case "merge":
            doMerge(args);
            break;
        default:
            System.out.println("No command with that name exists");
            System.exit(0);
        }
    }

    /**
     * switch helper.
     * @param args string
     */
    private static void switchHelper(String... args) {
        switch (args.length) {
        case 3:
            doCheckoutHead(args);
            break;
        case 4:
            doCheckoutCommit(args);
            break;
        case 2:
            doCheckoutBranch(args);
            break;
        default:
            System.out.println("Incorrect operands.");
            System.exit(0);
            break;
        }
    }

    /**
     * initializer.
     */
    private static void doInit() {
        Repository repo = null;
        try {
            repo = new Repository();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

    /**
     * Adder.
     * @param args string
     */
    private static void doAdd(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        String filename = args[1];
        Data data;
        try {
            data = new Data(filename);
        } catch (IllegalArgumentException e) {
            System.out.println("File does not exist.");
            System.exit(0);
            return;
        }
        HashMap<String, String> state = data.getHeadCommit().getState();
        File file = new File(filename);
        Blob newBlob = new Blob(Utils.readContentsAsString(file), filename);

        if (!state.containsKey(filename)) {
            data.putAddStageMap(filename, newBlob.getSha1());
            newBlob.callerSerialize();
        } else {
            Blob blob = data.getHeadCommit().getBlob(filename);
            if (newBlob.getSha1().equals(blob.getSha1())) {
                if (data.getAddStageMap().containsKey(filename)) {
                    data.removeAddStageMap(filename);
                }
            } else {
                if (data.getAddStageMap().containsKey(filename)) {
                    data.removeAddStageMap(filename);
                }
                data.putAddStageMap(filename, newBlob.getSha1());
                newBlob.callerSerialize();
            }

        }
        if (data.getRemoveStageSet().contains(filename)) {
            data.removeRemoveStageSet(filename);
        }

        System.exit(0);
    }
/**
 * commit.
 * @param args string
 */
    private static void doCommit(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[1].isBlank()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        Data data = new Data();

        HashMap<String, String> addStage = data.getAddStageMap();
        HashSet<String> removeStage = data.getRemoveStageSet();

        if (addStage.isEmpty() && removeStage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        Commit prevCommit = data.getHeadCommit();
        HashMap<String, String> oldState = prevCommit.getState();

        HashMap<String, String> newState = new HashMap<>();
        for (Map.Entry<String, String> pair : oldState.entrySet()) {
            newState.put(pair.getKey(), pair.getValue());
        }

        for (Map.Entry<String, String> pair : addStage.entrySet()) {
            newState.remove(pair.getKey());
            newState.put(pair.getKey(), pair.getValue());
        }

        for (String filename: removeStage) {
            newState.remove(filename);
        }

        data.clearStages();
        Commit commit = new Commit(prevCommit.getSha1(),
                null, args[1], newState);
        data.setBranchSha1(commit.getSha1());
        data.getHead().setCommitSha1(commit.getSha1());

        System.exit(0);

    }

    /**
     * check out Head.
     * @param args string
     */
    private static void doCheckoutHead(String... args) {
        String filename = args[2];
        if (!args[1].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (filename.isBlank()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        Data data = new Data(filename);

        if (!data.getHeadCommit().getState().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            Blob newBlob = data.getHeadCommit().getBlob(filename);
            newBlob.callerSerialize();
            Utils.writeContents(new File(filename), newBlob.getContents());
        }

        System.exit(0);
    }

    /**
     * check out commit.
     * @param args string
     */
    private static void doCheckoutCommit(String... args) {
        String filename = args[3];
        String commitId = args[1];
        if (!args[2].equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (filename.isBlank()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (commitId.isBlank()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        Data data = new Data();
        Commit commit = data.getCommit(commitId);

        if (!commit.getState().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        } else {
            Blob newBlob = commit.getBlob(filename);
            newBlob.callerSerialize();
            Utils.writeContents(new File(filename), newBlob.getContents());
        }

        System.exit(0);
    }

    /**
     * check out branch.
     * @param args string
     */
    private static void doCheckoutBranch(String... args) {
        String branchName = args[1];
        if (branchName.isBlank()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        Data data = new Data();
        if (data.getBranch().getName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        if (!Objects.requireNonNull(Utils.plainFilenamesIn(Paths.BRANCHES))
                .contains(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        File cwd = new File(".");
        String cwdString = cwd.getAbsolutePath().substring(0,
                cwd.getAbsolutePath().length() - 2);
        List<String> filesInDir = Utils.plainFilenamesIn(cwdString);

        for (String file : filesInDir) {
            if (!data.getAddStageMap().containsKey(file)
                    && !data.getHeadCommit().getState().containsKey(file)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String file : filesInDir) {
            File fileObj = new File(file);
            if (fileObj.isFile()) {
                Utils.restrictedDelete(file);
            }
        }

        Branch branch = data.getBranch(branchName);
        Commit commit = data.getCommit(branch.getSha1());
        for (String filename : commit.getState().keySet()) {
            Blob newBlob = commit.getBlob(filename);
            newBlob.callerSerialize();
            Utils.writeContents(new File(filename), newBlob.getContents());
        }

        data.clearStages();
        data.getHead().setCommitSha1(commit.getSha1());
        data.getHead().setName(branchName);

        System.exit(0);
    }

    /**
     * log function.
     */
    private static void doLog() {
        Data data = new Data();
        Commit commit = data.getHeadCommit();
        while (true) {
            System.out.println("===");
            System.out.println("commit " + commit.getSha1());
            System.out.println("Date: " + commit.getTimeStamp());
            System.out.println(commit.getCommitMessage());

            String parent1 = commit.getParent1();
            if (parent1 != null) {
                System.out.println();
                commit = data.getCommit(commit.getParent1());
            } else {
                break;
            }
        }

        System.exit(0);
    }

    /**
     * global log.
     */
    private static void doGlobalLog() {
        Data data = new Data();
        Commit commit;
        for (String filename : Objects.requireNonNull(
                Utils.plainFilenamesIn(Paths.COMMITS))) {
            commit = data.getCommit(filename);
            System.out.println("===");
            System.out.println("commit " + commit.getSha1());
            System.out.println("Date: " + commit.getTimeStamp());
            System.out.println(commit.getCommitMessage());
        }

        System.exit(0);
    }

    /**
     * do RM.
     * @param args string
     */
    private static void doRm(String... args) {
        String filename = args[1];
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (filename.isBlank()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        Data data = new Data();
        if (!data.getHeadCommit().getState().containsKey(filename)
                && !data.getAddStageMap().containsKey(filename)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (data.getAddStageMap().containsKey(filename)) {
            data.removeAddStageMap(filename);
        }

        if (data.getHeadCommit().getState().containsKey(filename)) {
            data.addRemoveStageSet(filename);
            Utils.restrictedDelete(filename);
        }

        System.exit(0);
    }

    /**
     * fider.
     * @param args string
     */
    private static void doFind(String... args) {
        String commitMessage = args[1];
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (commitMessage.isBlank()) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }

        Data data = new Data();
        Commit commit;
        boolean found = false;
        for (String filename : Objects.requireNonNull(
                Utils.plainFilenamesIn(Paths.COMMITS))) {
            commit = data.getCommit(filename);
            if (commit.getCommitMessage().equals(commitMessage)) {
                found = true;
                System.out.println(commit.getSha1());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }

        System.exit(0);
    }

    /**
     * do status.
     */
    private static void doStatus() {
        Data data = new Data();
        System.out.println("=== Branches ===");

        List<String> allBranches = Utils.plainFilenamesIn(Paths.BRANCHES);
        assert allBranches != null;
        Collections.sort(allBranches);
        System.out.println("*" + data.getBranch().getName());
        for (String branchName : allBranches) {
            if (!branchName.equals(data.getBranch().getName())) {
                System.out.println(branchName);
            }
        }

        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> filenames = new LinkedList<>(
                data.getAddStageMap().keySet());
        Collections.sort(filenames);
        for (String filename : filenames) {
            System.out.println(filename);
        }

        System.out.println();
        System.out.println("=== Removed Files ===");
        filenames = new LinkedList<>(data.getRemoveStageSet());
        Collections.sort(filenames);
        for (String filename : data.getRemoveStageSet()) {
            System.out.println(filename);
        }

        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");

        System.exit(0);
    }

    /**
     * branch do.
     * @param args string
     */
    private static void doBranch(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String newBranchName = args[1];

        List<String> branchNames = Utils.plainFilenamesIn(Paths.BRANCHES);
        assert branchNames != null;
        for (String branchName : branchNames) {
            if (branchName.equals(newBranchName)) {
                System.out.println("A branch with that name already exists.");
                System.exit(0);
            }
        }
        Data data = new Data();

        Branch newBranch = new Branch(
                newBranchName, data.getHead().setCommitSha1());

        System.exit(0);
    }

    /**
     * do RM branch.
     * @param args string
     */
    private static void doRmBranch(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String branchName = args[1];
        Data data = new Data();
        if (data.getBranch().getName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        Branch branch = data.getBranch(branchName);
        Utils.join(Paths.BRANCHES, branch.getName()).delete();

        System.exit(0);
    }

    /**
     * reset do.
     * @param args string
     */
    private static void doReset(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String commitId = args[1];
        Data data = new Data();
        Commit commit = data.getCommit(commitId);

        File cwd = new File(".");
        String cwdString = cwd.getAbsolutePath()
                .substring(0, cwd.getAbsolutePath().length() - 2);
        List<String> filesInDir = Utils.plainFilenamesIn(cwdString);

        for (String file : filesInDir) {
            if (!data.getAddStageMap().containsKey(file)
                    && !data.getHeadCommit().getState().containsKey(file)) {
                System.out.println("There is an untracked file in"
                        + " the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String file : filesInDir) {
            File fileObj = new File(file);
            if (fileObj.isFile()) {
                Utils.restrictedDelete(file);
            }
        }

        for (String filename : commit.getState().keySet()) {
            Blob newBlob = commit.getBlob(filename);
            newBlob.callerSerialize();
            Utils.writeContents(new File(filename), newBlob.getContents());
        }

        data.clearStages();
        data.getBranch().setSha1(commit.getSha1());
        data.getHead().setCommitSha1(commit.getSha1());

        System.exit(0);
    }

    /**
     * do merge .
     * @param args string
     */
    private static void doMerge(String... args) {
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        String branchName = args[1];
        Data data = new Data();
        if (branchName.equals(data.getBranch().getName())) {
            System.out.println("Cannot merge a branch with itself");
            System.exit(0);
        }
        Commit splitPoint = findSplitPoint(data, args);
        assert splitPoint != null;
        Branch fromBranch = data.getBranch(branchName);
        if (splitPoint.getSha1().equals(fromBranch.getSha1())) {
            System.out.println("Given branch "
                    + "is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitPoint.getSha1().equals(data.getBranch().getSha1())) {
            System.out.println("Current branch fast-forwarded.");
            doCheckoutBranch(args);
        }
        if (!data.getAddStageMap().isEmpty()
                || !data.getRemoveStageSet().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }


        System.exit(0);
    }

    /**
     * find split point.
     * @param data Data
     * @param args string array
     * @return split points
     */
    private static Commit findSplitPoint(Data data, String[] args) {
        Branch curBranch = data.getBranch();
        Commit curCommit = data.getCommit(curBranch.getSha1());
        LinkedList<String> curBranchPath = new LinkedList<>();
        while (true) {
            curBranchPath.add(curCommit.getSha1());
            String parent1 = curCommit.getParent1();
            if (parent1 != null) {
                curCommit = data.getCommit(curCommit.getParent1());
            } else {
                break;
            }
        }

        String fromBranchName = args[1];
        Branch fromBranch = data.getBranch(fromBranchName);
        curCommit = data.getCommit(fromBranch.getSha1());
        while (true) {
            if (curBranchPath.contains(curCommit.getSha1())) {
                return curCommit;
            }
            String parent1 = curCommit.getParent1();
            if (parent1 != null) {
                curCommit = data.getCommit(curCommit.getParent1());
            } else {
                break;
            }
        }
        return null;
    }
}
