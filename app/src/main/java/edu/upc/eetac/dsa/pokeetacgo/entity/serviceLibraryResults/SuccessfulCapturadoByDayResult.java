package edu.upc.eetac.dsa.pokeetacgo.entity.serviceLibraryResults;

import java.util.Map;

/**
 * Created by ericmassip on 8/12/16.
 */
public class SuccessfulCapturadoByDayResult {
    public int capturadosOneDayAgo;
    public int capturadosTwoDaysAgo;
    public int capturadosThreeDaysAgo;
    public int capturadosFourDaysAgo;
    public int capturadosFiveDaysAgo;
    public int capturadosSixDaysAgo;
    public int capturadosSevenDaysAgo;

    public void setCapturadosByDay(Map capturadosByDay) {
        this.capturadosOneDayAgo = (int) capturadosByDay.get(1);
        this.capturadosTwoDaysAgo = (int) capturadosByDay.get(2);
        this.capturadosThreeDaysAgo = (int) capturadosByDay.get(3);
        this.capturadosFourDaysAgo = (int) capturadosByDay.get(4);
        this.capturadosFiveDaysAgo = (int) capturadosByDay.get(5);
        this.capturadosSixDaysAgo = (int) capturadosByDay.get(6);
        this.capturadosSevenDaysAgo = (int) capturadosByDay.get(7);
    }
}
