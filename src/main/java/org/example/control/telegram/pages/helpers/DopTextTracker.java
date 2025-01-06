package org.example.control.telegram.pages.helpers;

import org.example.data.logger.LogType;
import org.example.data.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class DopTextTracker {
    private static final Map<Long, String> passportsAndNames = new HashMap<Long, String>();

    public static void SetArgsNeded(long id, String args) {
        passportsAndNames.put(id, args);
        Logger.put(passportsAndNames.toString(), LogType.DEBUG);
    }
    public static String GetArgsNeded(long id) {
        Logger.put(passportsAndNames.toString(), LogType.DEBUG);
        if (passportsAndNames.containsKey(id)) {
            var result = passportsAndNames.get(id);
            passportsAndNames.remove(id);
            return result;
        }
        else return null;
    }
}
