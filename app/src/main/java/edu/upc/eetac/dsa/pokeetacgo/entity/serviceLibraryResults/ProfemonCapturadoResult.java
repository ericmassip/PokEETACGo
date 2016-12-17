package edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults;

/**
 * Created by ericmassip on 8/12/16.
 */
public class ProfemonCapturadoResult {
    public int profemonId;
    public String name;
    public int level;

    public ProfemonCapturadoResult fillIntheFields(int profemonId, String name, int level) {
        this.profemonId = profemonId;
        this.name = name;
        this.level = level;
        return this;
    }
}
