package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The data class, deals with data.
 * @author Joshua Park
 */
public class Data {

    /**
     * File contents from file name.
     */
    private String _fileContents;

    /**
     * Get the branch of where HEAD points to.
     */
    private Branch _branch;

    /**
     * Head Pointer.
     */
    private Head _head;

    /**
     * Commit that Head points to.
     */
    private Commit _headCommit;

    /**
     * Mappings of filenames to their blob ids.
     * These filenames are staged for addition.
     */
    private HashMap<String, String> _addStageMap;

    /**
     * Filenames that are staged for deletion.
     */
    private HashSet<String> _removeStageSet;

    /**
     * Data constructor.
     * @param filename filename to get contents for
     */
    /**
     * The data constructor.
     * @param filename fileName
     */
    @SuppressWarnings("unchecked")
    Data(String filename) {
        _head = Utils.readObject(Paths.HEAD, Head.class);
        _headCommit = Utils.readObject(Utils.join(Paths.COMMITS,
                _head.setCommitSha1()), Commit.class);
        _branch = Utils.readObject(Utils.join(Paths.BRANCHES,
                _head.getName()), Branch.class);
        _fileContents = Utils.readContentsAsString(new File(filename));
        _addStageMap = Utils.readObject(Paths.STAGEDFORADD, HashMap.class);
        _removeStageSet = Utils.readObject(Paths.STAGEDFORREMOVE,
                HashSet.class);
    }

    /**
     * Data constructor.
     */
    @SuppressWarnings("unchecked")
    Data() {
        _head = Utils.readObject(Paths.HEAD, Head.class);
        _headCommit = Utils.readObject(Utils.join(Paths.COMMITS,
                _head.setCommitSha1()), Commit.class);
        _branch = Utils.readObject(Utils.join(Paths.BRANCHES,
                _head.getName()), Branch.class);
        _fileContents = null;
        _addStageMap = Utils.readObject(Paths.STAGEDFORADD, HashMap.class);
        _removeStageSet = Utils.readObject(
                Paths.STAGEDFORREMOVE, HashSet.class);
    }

    /**
     * head getter.
     * @return head
     */
    public Head getHead() {
        return _head;
    }

    /**
     * head getter.
     * @return head commit
     */
    public Commit getHeadCommit() {
        return _headCommit;
    }

    /**
     * branch getter.
     * @return branch
     */
    public Branch getBranch() {
        return _branch;
    }

    /**
     * file content Getter.
     * @return fileContents
     */
    public String getFileContents() {
        return _fileContents;
    }

    /**
     * Hashmap for data.
     * @return addStageMap
     */
    public HashMap<String, String> getAddStageMap() {
        return _addStageMap;
    }

    /**
     * get RemoveStageSet.
     * @return stage set
     */
    public HashSet<String> getRemoveStageSet() {
        return _removeStageSet;
    }

    /**
     * add stage.
     * @param key key string
     * @param value value string
     */
    public void putAddStageMap(String key, String value) {
        this._addStageMap.put(key, value);
        Utils.writeObject(Paths.STAGEDFORADD, _addStageMap);
    }

    /**
     * remove Add stage.
     * @param key key to remove
     */
    public void removeAddStageMap(String key) {
        this._addStageMap.remove(key);
        Utils.writeObject(Paths.STAGEDFORADD, _addStageMap);
    }

    /**
     * add Remove Stage Set.
     * @param key string
     */
    public void addRemoveStageSet(String key) {
        this._removeStageSet.add(key);
        Utils.writeObject(Paths.STAGEDFORREMOVE, _removeStageSet);
    }

    /**
     * clear stages.
     */
    public void clearStages() {
        this.getAddStageMap().clear();
        this.getRemoveStageSet().clear();
        Utils.writeObject(Paths.STAGEDFORADD, _addStageMap);
        Utils.writeObject(Paths.STAGEDFORREMOVE, _removeStageSet);
    }

    /**
     * remove Stage set.
     * @param key string
     */
    public void removeRemoveStageSet(String key) {
        this._removeStageSet.remove(key);
        Utils.writeObject(Paths.STAGEDFORREMOVE, _removeStageSet);
    }

    /**
     * set branch sha.
     * @param sha1 setter
     */
    public void setBranchSha1(String sha1) {
        this._branch.setSha1(sha1);
    }

    /**
     * get commit.
     * @param commitId checks if commit is there
     * @return the object
     */
    public Commit getCommit(String commitId) {
        List<String> filenames = Utils.plainFilenamesIn(Paths.COMMITS);
        assert filenames != null;
        int len = commitId.length();
        for (String filename : filenames) {
            if (filename.substring(0, len).equals(commitId)) {
                return Utils.readObject(
                        Utils.join(Paths.COMMITS, filename), Commit.class);
            }
        }
        System.out.println("No commit with that id exists.");
        System.exit(0);
        return null;
    }

    /**
     * branch getter.
     * @param branchName branchName
     * @return null or object
     */
    public Branch getBranch(String branchName) {
        List<String> branchNames = Utils.plainFilenamesIn(Paths.BRANCHES);
        assert branchNames != null;
        for (String name : branchNames) {
            if (name.equals(branchName)) {
                return Utils.readObject(Utils.join(
                        Paths.BRANCHES, branchName), Branch.class);
            }
        }
        System.out.println("A branch with that name does not exist.");
        System.exit(0);
        return null;
    }

}
