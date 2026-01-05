// 
// Decompiled by Procyon v0.6.0
// 

package etmaHandler;

import java.util.ArrayList;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Iterator;
import com.swabunga.spell.event.SpellCheckEvent;
import java.util.List;
import com.swabunga.spell.event.SpellCheckListener;

public class Suggest
{
    public static class SuggestionListener implements SpellCheckListener
    {
        public static List<String> spellOutputList;
        public static String wrongWord;
        public static boolean checkFlag;
        
        public void spellingError(final SpellCheckEvent event) {
            SuggestionListener.checkFlag = true;
            String spellOutput = "";
            String temp = "";
            SuggestionListener.wrongWord = "";
            spellOutput = spellOutput + "Misspelling: " + event.getInvalidWord();
            SuggestionListener.wrongWord = event.getInvalidWord();
            final List suggestions = event.getSuggestions();
            if (suggestions.isEmpty()) {
                spellOutput += "\nNo suggestions found.";
            }
            else {
                spellOutput += "\nSuggestions:";
                final Iterator i = suggestions.iterator();
                while (i.hasNext()) {
                    try {
                        temp = i.next().toString();
                        spellOutput += temp;
                        SuggestionListener.spellOutputList.add(temp);
                    }
                    catch (final Exception anException) {
                        System.out.println("Error " + anException);
                    }
                    if (i.hasNext()) {
                        spellOutput += ", ";
                    }
                }
                SuggestionListener.checkFlag = false;
            }
        }
        
        public static void addToDictionary(final String aString) {
            String dictString = loadDictString(EtmaHandlerJ.dictionaryPathPreferences, false);
            dictString += aString;
            saveDictString(EtmaHandlerJ.dictionaryPathPreferences, dictString);
        }
        
        public static String loadDictString(final String filePath, final boolean lineFeed) {
            final StringBuilder fhiContents = new StringBuilder(10000);
            final File aFile = new File(filePath);
            BufferedReader buffer = null;
            String currentLine = null;
            try {
                for (buffer = new BufferedReader(new FileReader(aFile)), currentLine = buffer.readLine(); currentLine != null; currentLine = buffer.readLine()) {
                    currentLine += "\r\n";
                    fhiContents.append(currentLine);
                }
            }
            catch (final Exception anException) {
                JOptionPane.showMessageDialog(null, "Please choose a dictionary, using the Preferences menu!");
            }
            finally {
                try {
                    buffer.close();
                }
                catch (final Exception ex) {}
            }
            return fhiContents.toString();
        }
        
        private static void saveDictString(final String filePath, final String fhiString) {
            final File aFile = new File(filePath);
            BufferedWriter buffer = null;
            final String currentLine = null;
            try {
                buffer = new BufferedWriter(new FileWriter(aFile));
                buffer.write(fhiString);
            }
            catch (final Exception anException) {
                JOptionPane.showMessageDialog(null, anException);
                try {
                    buffer.close();
                }
                catch (final Exception closeEx) {
                    JOptionPane.showMessageDialog(null, closeEx);
                }
            }
            finally {
                try {
                    buffer.close();
                }
                catch (final Exception anException2) {
                    JOptionPane.showMessageDialog(null, anException2);
                }
            }
        }
        
        static {
            SuggestionListener.spellOutputList = new ArrayList<String>();
            SuggestionListener.wrongWord = "";
        }
    }
}
