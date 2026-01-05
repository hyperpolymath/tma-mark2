/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.swabunga.spell.event.SpellCheckEvent
 *  com.swabunga.spell.event.SpellCheckListener
 */
package etmaMonitor;

import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import etmaMonitor.EtmaMonitorJ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;

public class Suggest {

    public static class SuggestionListener
    implements SpellCheckListener {
        public static List<String> spellOutputList = new ArrayList<String>();
        public static String wrongWord = "";
        public static boolean checkFlag;

        public void spellingError(SpellCheckEvent event) {
            checkFlag = true;
            Object spellOutput = "";
            String temp = "";
            wrongWord = "";
            spellOutput = (String)spellOutput + "Misspelling: " + event.getInvalidWord();
            wrongWord = event.getInvalidWord();
            List suggestions = event.getSuggestions();
            if (suggestions.isEmpty()) {
                spellOutput = (String)spellOutput + "\nNo suggestions found.";
            } else {
                spellOutput = (String)spellOutput + "\nSuggestions:";
                Iterator i = suggestions.iterator();
                while (i.hasNext()) {
                    try {
                        temp = i.next().toString();
                        spellOutput = (String)spellOutput + temp;
                        spellOutputList.add(temp);
                    }
                    catch (Exception anException) {
                        System.out.println("Error " + anException);
                    }
                    if (!i.hasNext()) continue;
                    spellOutput = (String)spellOutput + ", ";
                }
                checkFlag = false;
            }
        }

        public static void addToDictionary(String aString) {
            Object dictString = SuggestionListener.loadDictString(EtmaMonitorJ.dictionaryPathPreferences, false);
            dictString = (String)dictString + aString;
            SuggestionListener.saveDictString(EtmaMonitorJ.dictionaryPathPreferences, (String)dictString);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public static String loadDictString(String filePath, boolean lineFeed) {
            StringBuilder fhiContents = new StringBuilder(10000);
            File aFile = new File(filePath);
            BufferedReader buffer = null;
            Object currentLine = null;
            try {
                buffer = new BufferedReader(new FileReader(aFile));
                currentLine = buffer.readLine();
                while (currentLine != null) {
                    currentLine = (String)currentLine + "\r\n";
                    fhiContents.append((String)currentLine);
                    currentLine = buffer.readLine();
                }
            }
            catch (Exception anException) {
                JOptionPane.showMessageDialog(null, "Please choose a dictionary, using the Preferences menu!");
            }
            finally {
                try {
                    buffer.close();
                }
                catch (Exception exception) {}
            }
            return fhiContents.toString();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private static void saveDictString(String filePath, String fhiString) {
            File aFile = new File(filePath);
            BufferedWriter buffer = null;
            Object currentLine = null;
            try {
                buffer = new BufferedWriter(new FileWriter(aFile));
                buffer.write(fhiString);
            }
            catch (Exception anException) {
                JOptionPane.showMessageDialog(null, anException);
            }
            finally {
                try {
                    buffer.close();
                }
                catch (Exception anException) {
                    JOptionPane.showMessageDialog(null, anException);
                }
            }
        }
    }
}
