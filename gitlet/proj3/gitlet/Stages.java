package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

/**
 * the stages class.
 * @author Joshua Park
 */
public class Stages implements Serializable {
    /**
     * Mappings of filenames to their blobs.
     * These filenames are staged for addition.
     */
    private HashMap<String, String> _addStageMap;

    /**
     * Filenames that are staged for deletion.
     */
    private HashSet<String> _removeStageSet;

    /**
     * Initial commit constructor.
     **/
    Stages() {
        this._addStageMap = new HashMap<String, String>();
        this._removeStageSet = new HashSet<String>();
        serializeAddStage();
        serializeRemoveStage();
    }

    /**
     * Serialize the addition stage.
     */
    private void serializeAddStage() {
        Utils.writeObject(Paths.STAGEDFORADD, _addStageMap);
    }

    /**
     * Serialize the removal stage.
     */
    private void serializeRemoveStage() {
        Utils.writeObject(Paths.STAGEDFORREMOVE, _removeStageSet);
    }

    /**
     * stage map.
     * @return add stage map
     */
    public HashMap<String, String> getAddStageMap() {
        return _addStageMap;
    }

    /**
     * remove stage set.
     * @return remove stage set
     */
    public HashSet<String> getRemoveStageSet() {
        return _removeStageSet;
    }

    /**
     * add stage map.
     * @param key string
     * @param value string
     */
    public void putAddStageMap(String key, String value) {
        this._addStageMap.put(key, value);
        serializeAddStage();
    }

    /**
     * remove add stage.
     * @param key string
     */
    public void removeAddStageMap(String key) {
        this._addStageMap.remove(key);
        serializeAddStage();
    }

    /**
     * remove stage set.
     * @param key string
     */
    public void addRemoveStageSet(String key) {
        this._removeStageSet.add(key);
        serializeRemoveStage();
    }

    /**
     * remove stage set.
     * @param key string
     */
    public void removeRemoveStageSet(String key) {
        this._removeStageSet.remove(key);
        serializeRemoveStage();
    }

    /**
     * clear stages.
     */
    public void clearStages() {
        this.getAddStageMap().clear();
        this.getRemoveStageSet().clear();
        serializeAddStage();
        serializeRemoveStage();
    }
}
