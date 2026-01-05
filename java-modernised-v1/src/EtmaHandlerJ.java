// 
// Decompiled by Procyon v0.6.0
// 

package etmaHandler;

import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.awt.print.Printable;
import javax.swing.text.DefaultHighlighter;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.QuitResponse;
import java.awt.desktop.QuitEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.EventQueue;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.zip.ZipOutputStream;
import javax.swing.filechooser.FileSystemView;
import org.w3c.dom.Node;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.Comparator;
import java.net.MalformedURLException;
import java.io.InputStreamReader;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.TimerTask;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.util.Arrays;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.activation.DataSource;
import jakarta.mail.Multipart;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.Address;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.Session;
import java.util.Properties;
// Removed: import org.jdesktop.jdic.desktop.Message;
// Removed: import net.roydesign.io.DocumentFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.filechooser.FileFilter;
import javax.swing.JProgressBar;
import javax.swing.undo.CannotUndoException;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.Robot;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URI;
import javax.swing.JFileChooser;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.Container;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.TableModel;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import org.jdesktop.layout.GroupLayout;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Taskbar;
import java.awt.Desktop;
import com.swabunga.spell.event.WordTokenizer;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.event.SpellCheckListener;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.AbstractButton;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import javax.swing.text.Document;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.event.MouseListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.datatransfer.DataFlavor;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import javax.swing.ToolTipManager;
import javax.swing.Icon;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import javax.swing.table.TableCellEditor;
import javax.swing.DefaultCellEditor;
import java.util.TreeSet;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.util.HashSet;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.text.Highlighter;
import java.util.Timer;
import javax.swing.JMenu;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.engine.SpellDictionary;
import java.util.Set;
import java.awt.datatransfer.Transferable;
import javax.swing.undo.UndoManager;
import java.io.File;
import java.util.prefs.Preferences;
import java.util.List;
import javax.swing.JTextField;
import java.util.Map;
import javax.swing.JFrame;

public class EtmaHandlerJ extends JFrame
{
    public String thisVersion;
    public String latestVersion;
    public String[] xmlTagsStrings;
    public String[] xmlTagsStringsStaff;
    public String[] xmlTagsStringsReduced;
    public String[] xmlTagsStringsReducedStaff;
    public String[] xmlTagsStringsReducedQuestions;
    public String[] xmlTagsStringsReducedSubs;
    public int lString;
    public int lStringStaff;
    public int lStringQuestions;
    public Map<String, String> studentDetails;
    public Map<String, String> tutorDetails;
    public Map<String, String> submissionDetails;
    public Map<String, String> studentDetailsShort;
    public Map<String, String> tutorDetailsShort;
    public Map<String, String> submissionDetailsShort;
    public Map<String, JTextField> fieldNames1;
    public Map<String, JTextField> fieldNames2;
    private List<String> questionNumbers;
    public List<Integer> partStarts;
    public String outString;
    public String markString;
    public int numberOfQuestions;
    public List<Integer> numberOfParts;
    public Preferences ourRoot;
    public List thisLine;
    public String courseMenuPreferences;
    public String downloadsFolderPreferences;
    public String tmaMenuPreferences;
    public String userNamePreferences;
    public String passwordPreferences;
    public String smtpServerPreferences;
    public String yourEmailAddressPreferences;
    public boolean autoFillFlagPreferences;
    public boolean autoMp3Preferences;
    public boolean launchTmaListFlagPreferences;
    public boolean spellCheckFlagPreferences;
    public boolean suggestFlagPreferences;
    public boolean authenticationFlagPreferences;
    public boolean toolTipFlagPreferences;
    public boolean startupScreenFlagPreferences;
    public boolean checkClosureFlagPreferences;
    public boolean sizeWarnFlagPreferences;
    public int fontPreferences;
    public String[][] weightingsPreferences;
    public String[][] maxScoresPreferences;
    public boolean createMarkedPreferences;
    public double[] mainLocationPreferences;
    public boolean sizePreferences;
    public String commentBankFilePreferences;
    public String currentWpPreferences;
    public String currentEdPreferences;
    public String currentAudioPreferences;
    public String currentBrowserPreferences;
    public static String dictionaryPathPreferences;
    public String eTmaAddressPreferences;
    public String csvFilePreference;
    public boolean startedFlag;
    public boolean highlightedFlag;
    public int studentIndex;
    public int maxFileNameLength;
    public String markedStatus;
    public List<String> questionStatusList;
    public List<String> studentMarksList;
    public String newDownloadFolderPath;
    List<String> tmaNewFiles;
    List<String> tmaTempFiles;
    List<String> tmaTransFiles;
    List<String> tmaWeightings;
    List<String> tmaMaxScores;
    List<String> longFilenames;
    public String[] tmaNames;
    public Map<String, Integer> weightingsMap;
    public Map<String, Integer> maxScoresMap;
    public List<Integer> weightingsList;
    public List<Integer> maxScoresList;
    public boolean sortOrder;
    public int[] tmaListSizes;
    public int[] gradesListSizes;
    public int[] prevMarksListSizes;
    public boolean savedFlag;
    public int[] partScoresListSizes;
    public int toBeMarked;
    public String filter;
    public boolean displayFlag;
    public static boolean alertFlag;
    public List<Integer> redRows;
    public List<Integer> blueRows;
    public int passMark;
    public int fSize;
    public boolean size;
    public File[] pt3Files;
    public String[] emailRecipients;
    protected UndoManager undo;
    public Transferable clipBoard;
    public int xCoord;
    public int yCoord;
    public boolean tooHighFlag;
    public static String wordToAdd;
    public static int actionFlag;
    public String overallGrade;
    public String currentWpPath;
    public String currentEdPath;
    public String currentAudioPath;
    public String osName;
    public Map<String, String> wpMap;
    public Map<String, String> wpMap1;
    public File attachmentFile;
    public boolean attFlag;
    public int[] smallWindowSize;
    public int[] largeWindowSize;
    public final String[] MONTHLIST;
    public String currentStudentScript;
    public double tmaListLocX;
    public double tmaListLocY;
    public double tmaListWidth;
    public double tmaListHeight;
    public int cursorPos;
    public boolean startUp;
    public List<String> submissionList;
    public boolean readErrorFlag;
    public boolean entryErrorFlag;
    public String courseName;
    public String colorPreferences;
    public final String colorDefaultString = "219;219;219:255;255;255:204;255;255";
    public boolean[] colorDefaultsFlag;
    public int currentColorIndex;
    public final String[] LINUXFILEMANAGER;
    public String linuxWP;
    public String linuxAudio;
    public char char02;
    public String string02;
    public char char03;
    public String string03;
    public char wineacute;
    public String wineacuteString;
    public char rtn;
    public char lf;
    public char leftq;
    public String leftqString;
    public char rightq;
    public String rightqString;
    public String lfString;
    public String rtnString;
    public String courseDirectory;
    public Set<String> courseDirectoryList;
    public Boolean autoImportFlag;
    public String unzippedFilePath;
    public Boolean tmaListError;
    public JFrame messageWindow2;
    public JFrame messageWindow3;
    public SpellDictionary dictionary;
    public SpellChecker spellChecker;
    public String[] acceptableFiles;
    public Set<String> acceptableFilesSet;
    public String feedbackString;
    public String markedString;
    public String markedString1;
    public Set<String> allStudents;
    public Set<String> allTmas;
    public String returnsName;
    public String tempName;
    public String[] gridColor;
    public JMenu additionField;
    public Timer timer2;
    public Set<String> acceptableEnds;
    public String[] yearEnds;
    public String[] monthTags;
    public String pathToDownloadedFile;
    public boolean autoImportPreferences;
    public boolean globalFontsPreferences;
    public String zipFileName;
    public boolean foundItZip;
    public String fileToUnzip;
    public String desktopPath;
    public File distFile;
    public boolean checkReturnsFlagPreferences;
    public String parentName1;
    public boolean sortPreference;
    public int sortRow;
    public String dictionaryLocation;
    public String version1;
    public int maxAcceptFilename;
    public boolean isMonitorData;
    Highlighter.HighlightPainter myHighlightPainter;
    Highlighter.HighlightPainter blankHighlightPainter;
    private JMenuItem Developer;
    private JMenu Edit;
    private JMenu File;
    private JMenuItem Redo;
    private JMenuItem Undo;
    private JButton addAttachmentButton;
    private JCheckBox addInitialsFlag;
    private JTextField addRecip;
    private JTextField address_line1;
    private JTextField address_line2;
    private JTextField address_line3;
    private JTextField address_line4;
    private JTextField address_line5;
    private JTextField assgnmt_suffix;
    public JTextField audioPath;
    private JCheckBox authenticationFlag;
    private JCheckBox autoFillFlag;
    private JCheckBox autoImportFlag1;
    private JCheckBox autoMp3;
    private JButton backUp;
    private JMenuItem backUpMenu;
    private JButton bankComment;
    private JMenuItem bankCommentMenuItem;
    private JButton batchZip;
    public JTextField browserPath;
    private JButton browserSelect;
    private ButtonGroup buttonGroup1;
    private JCheckBox checkClosureFlag;
    private JCheckBox checkReturnsFlag;
    private JMenuItem checkSpelling;
    private JButton checkSpellingButton;
    private JButton checkTotals;
    private JMenuItem checkTotalsMenu;
    private JMenuItem checkUpdates;
    private JMenuItem chooseColor;
    private JButton closeCustomize;
    private JButton closePreferences;
    private JButton collectTmas;
    private JMenuItem collectTmasMenu;
    private JFrame colorFrame1;
    private JCheckBox colorRemove;
    private JComboBox colorWindowSelector;
    private JTextField commentBankEd;
    private JTextField commentBankFile;
    private JMenuItem comparePartScores;
    private JMenuItem copyText;
    private JComboBox courseList;
    private JTextField course_code;
    private JTextField course_version_num;
    private JMenuItem createFeedback;
    private JCheckBox createMarked;
    private JFrame custom;
    private JMenuItem customize;
    private JMenuItem cutText;
    private JCheckBox defaultFlag;
    private JButton deleteAttachment;
    public JTextField dictionaryPath;
    private JCheckBox directEntryFlag;
    private JMenuItem distributeDocument;
    private JCheckBox doubleClickFlag;
    private JTextField downloadsFolder;
    private JTextField e_tma_submission_date;
    private JTextField e_tma_submission_num;
    private JTextField email_address;
    private JMenuItem etmaHandlerHelp;
    private JButton etmaSite;
    private JMenuItem etmaSiteMenu;
    private JTextField etmasFolder;
    private JMenuItem exitMenuItem;
    private JButton exportGrades;
    private JButton exportGrades1;
    private JMenuItem exportMarksGrid;
    private JTextField fhiFileName;
    private JComboBox fontSize;
    private JTextField forenames;
    private JCheckBox globalFonts;
    private JFrame gradesSummary;
    private JTable gradesSummaryTable;
    private JTable gradesSummaryTable1;
    private JMenu help;
    private JFrame helpFrame;
    private JScrollPane helpScroll;
    private JCheckBox hideBackupEtmas;
    private JCheckBox hideBankComment;
    private JCheckBox hideECollectTmas;
    private JCheckBox hideEtmaSite;
    private JCheckBox hideListGrades;
    private JCheckBox hideListTmas;
    private JCheckBox hideOpenCommentBank;
    private JCheckBox hideOpenPreferences;
    private JCheckBox hideOpenReturnsFolder;
    private JCheckBox hideOpenTma;
    private JCheckBox hideOpenTmaFolder;
    private JCheckBox hideSavePt3;
    private JCheckBox hideSendEmail;
    private JCheckBox hideTestJs;
    private JCheckBox hideTrainingSite;
    private JCheckBox hideZipFiles;
    private JCheckBox highlightUnmarked;
    private JCheckBox ignoreCurrentTma;
    private JTextField initials;
    private JColorChooser jColorChooser1;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JLabel jLabel17;
    private JLabel jLabel18;
    private JLabel jLabel19;
    private JLabel jLabel2;
    private JLabel jLabel20;
    private JLabel jLabel21;
    private JLabel jLabel22;
    private JLabel jLabel23;
    private JLabel jLabel24;
    private JLabel jLabel25;
    private JLabel jLabel26;
    private JLabel jLabel27;
    private JLabel jLabel28;
    private JLabel jLabel29;
    private JLabel jLabel3;
    private JLabel jLabel30;
    private JLabel jLabel31;
    private JLabel jLabel32;
    private JLabel jLabel33;
    private JLabel jLabel34;
    private JLabel jLabel35;
    private JLabel jLabel36;
    private JLabel jLabel37;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItemCopy;
    private JMenuItem jMenuItemCut;
    private JMenuItem jMenuItemPaste;
    private JMenuItem jMenuItemRedo;
    private JMenuItem jMenuItemSelectAll;
    private JMenuItem jMenuItemUndo;
    private JMenuItem jMenuItemVersion;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPopupMenu jPopupMenu1;
    private JRadioButton jRadioButton1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JScrollPane jScrollPane4;
    private JScrollPane jScrollPane5;
    private JScrollPane jScrollPane6;
    private JScrollPane jScrollPane7;
    private JTextArea jTextArea1;
    private JTextArea jTextArea2;
    private JTextArea jTextArea3;
    private JTextPane jTextPane1;
    private JTextField jsTestFile;
    private JCheckBox lateSubmission;
    private JTextField late_submission_status;
    private JCheckBox launchTmaList;
    private JButton listAllScores;
    private JMenuItem listGrades;
    private JTable listOfTmas;
    private JMenuItem listTmasMenuItem;
    private JButton loadXMLAlt;
    private JFrame mailClient;
    private JPasswordField mailPassword;
    private JButton mailPreferences;
    private JFrame mailPreferencesFrame;
    private JTextField mailUserName;
    private JTextField marked_date;
    private JTable maxScoreTable1;
    private JTable maxScoreTable2;
    private JTable maxScoreTable3;
    private JTable maxScoreTable4;
    private JScrollPane maxScores1;
    private JScrollPane maxScores2;
    private JScrollPane maxScores3;
    private JScrollPane maxScores4;
    private JTextField max_assgnmt_score;
    private JTextField messageAddresses;
    private JScrollPane messageBody;
    private JTextField messageSubject;
    private JTextArea messageText;
    private JFrame messageWindow;
    private JButton missingSubmissions;
    private JButton moreDetails;
    private JButton openCommentBank;
    private JMenuItem openCommentBankMenu;
    private JButton openPreferences;
    private JButton openReturnsFolder;
    private JButton openTma;
    private JButton openTmaFolder;
    private JMenuItem openTmaFolderMenu;
    private JButton openTmaList;
    private JMenuItem openTmaMenu;
    private JTextField ouEtmaAddress;
    private JTextField ou_computer_user_name;
    private JTextField overall_grade_score;
    private JButton partScoresButton;
    private JFrame partScoresTable;
    private JMenuItem pasteText;
    private JTextField permitted_question_count;
    private JTextField personal_id;
    private JTextField postcode;
    private JFrame preferences;
    private JMenuItem preferencesMenu;
    private JTextField pres_code;
    private JTable prevMarks;
    private JComboBox previousPt3s;
    private JMenuItem printDoc;
    private JButton printGrades;
    private JButton printTmaList;
    private JTextField region_code;
    private JButton saveAddress;
    private JButton saveMailPreferences;
    private JButton savePt3;
    private JMenuItem savePt3MenuItem;
    private JButton saveWeightings;
    private JTextField score_update_allowed;
    private JButton selectAll;
    private JMenuItem selectAllText;
    private JButton selectDictionary;
    private JButton selectDownloadsFolder;
    private JButton sendButton;
    private JButton sendEmail;
    private JMenuItem sendEmailMenu;
    private JButton setAudioApp;
    private JButton setCommentBankEditor;
    private JButton setCommentBankFile;
    private JButton setEtmasFolder;
    private JButton setJsTestFile;
    private JButton setTmaWeightings;
    private JCheckBox showLatestFlag;
    private JMenu sites;
    private JCheckBox sizeWarnFlag;
    private JTextField smtpHost;
    private JCheckBox spellCheckFlag;
    private JFrame spellChooser;
    private JTextField staff_forenames;
    private JTextField staff_id;
    private JTextField staff_initials;
    private JTextField staff_surname;
    private JTextField staff_title;
    private JCheckBox startupScreenFlag;
    private JComboBox studentList;
    private JComboBox subNo;
    private JTextField submission_status;
    private JFrame submittedTmas;
    private JCheckBox suggestFlag;
    private JTextField surname;
    private JMenuItem testJsMenu;
    private JTextField title;
    private JComboBox tmaList;
    private JScrollPane tmaMarks;
    private JTable tmaNumbers;
    private JTable tmaScores;
    private JComboBox tmaSelectMenu;
    private JTextField toBeMarkedTmas;
    private JCheckBox toolTipFlag;
    private JTextField totalTmas;
    private JTextField total_question_count;
    private JButton trainingSite;
    private JMenuItem trainingSiteMenu;
    private JTextField tutor_comments;
    private JScrollPane tutor_comments_area;
    private JTextArea tutor_comments_input;
    private JMenuItem undoDistribution;
    private JTextField walton_received_date;
    private JFrame weightings;
    private JScrollPane weightings1;
    private JScrollPane weightings2;
    private JScrollPane weightings3;
    private JScrollPane weightings4;
    private JScrollPane weightingsHeaders;
    private JTable weightingsTable1;
    private JTable weightingsTable2;
    private JTable weightingsTable3;
    private JTable weightingsTable4;
    public JTextField wpPath;
    private JButton wpSelect;
    private JTextField yourEmailAddress;
    private JButton zipFiles;
    private JMenuItem zipFilesMenu;
    private JTextField zip_date;
    private JTextField zip_file;
    
    public List<String> getQuestionNumbers() {
        return this.questionNumbers;
    }
    
    public void setQuestionNumbers(final List<String> questionNumbers) {
        this.questionNumbers = questionNumbers;
    }
    
    public void findAcceptableEnds() {
        final boolean foundIt = false;
        this.zipFileName = this.makeZipFileName();
        final String thisYear;
        final String yearCode = thisYear = this.getDateAndTime().substring(2, 4);
        final String lastYear = this.adjustYear(this.getDateAndTime().substring(0, 4), -1);
        final String nextYear = this.adjustYear(this.getDateAndTime().substring(0, 4), 1);
        final String[] yearEndsAlt = { "", "", "" };
        yearEndsAlt[0] = lastYear;
        yearEndsAlt[1] = thisYear;
        yearEndsAlt[2] = nextYear;
        (this.acceptableEnds = new HashSet<String>()).add(this.zipFileName);
        this.acceptableEnds.add("1-99");
        for (int count = 0; count < yearEndsAlt.length; ++count) {
            for (int count2 = 0; count2 < this.monthTags.length; ++count2) {
                this.acceptableEnds.add("-" + yearEndsAlt[count] + this.monthTags[count2]);
            }
        }
    }
    
    public String makeZipFileName() {
        String nowTime = this.getDateAndTime();
        nowTime = nowTime.substring(0, 10);
        nowTime = nowTime.replace("_", "-");
        System.out.println(nowTime);
        return nowTime;
    }
    
    public String adjustYear(final String aYear, final int aNumber) {
        final String previousYear = "";
        final int thisYearNumber = Integer.parseInt(aYear);
        final int lastYearNumber = thisYearNumber + aNumber;
        final String lastYear = String.valueOf(lastYearNumber);
        return lastYear.substring(2, 4);
    }
    
    public void flashScreen() {
        (this.messageWindow2 = new JFrame("Welcome!")).setAlwaysOnTop(true);
        this.messageWindow2.setSize(430, 230);
        final JTextField lbl = new JTextField();
        final JTextField lbl2 = new JTextField();
        final JTextField lbl3 = new JTextField();
        final JLabel lbl4 = new JLabel();
        final Color messageColor = new Color(200, 255, 255);
        lbl.setFont(new Font("Times", 0, 28));
        lbl.setText("ETMA Handler for Mackintosh");
        lbl.setSize(430, 30);
        lbl.setLocation(0, 20);
        lbl.setVisible(true);
        lbl.setHorizontalAlignment(0);
        lbl.setBackground(messageColor);
        this.messageWindow2.add(lbl, 0);
        lbl3.setFont(new Font("Times", 0, 24));
        lbl3.setText("c MJ Hay 2008");
        lbl3.setSize(430, 30);
        lbl3.setLocation(0, 60);
        lbl3.setVisible(true);
        lbl3.setHorizontalAlignment(0);
        this.messageWindow2.add(lbl3, 1);
        lbl2.setFont(new Font("Times", 1, 18));
        lbl2.setText("Press 'return' to continue...");
        lbl2.setSize(430, 30);
        lbl2.setLocation(0, 90);
        lbl2.setVisible(true);
        lbl2.setHorizontalAlignment(0);
        this.messageWindow2.add(lbl2, 2);
        lbl4.setFont(new Font("Times", 2, 18));
        lbl4.setText("You can disable this start-up screen in the preferences!");
        lbl4.setSize(430, 30);
        lbl4.setVisible(true);
        lbl4.setHorizontalAlignment(0);
        lbl4.setAlignmentX(200.0f);
        lbl4.setVerticalAlignment(3);
        this.messageWindow2.add(lbl4, 3);
        this.messageWindow2.setLocationRelativeTo(null);
        this.messageWindow2.setVisible(true);
        JOptionPane.showMessageDialog(null, "");
        this.messageWindow2.setVisible(false);
    }
    
    public EtmaHandlerJ() {
        this.thisVersion = "5.4.2UN";
        this.latestVersion = "0.0";
        this.xmlTagsStrings = new String[] { "student_details", "tutor_details", "submission_details", "question_details", "ou_computer_user_name", "personal_id", "title", "initials", "forenames", "surname", "email_address", "address_line1", "address_line2", "address_line3", "address_line4", "address_line5", "postcode" };
        this.xmlTagsStringsStaff = new String[] { "staff_id", "staff_title", "staff_initials", "staff_forenames", "staff_surname", "region_code" };
        this.xmlTagsStringsReduced = new String[] { "ou_computer_user_name", "personal_id", "title", "initials", "forenames", "surname", "email_address", "address_line1", "address_line2", "address_line3", "address_line4", "address_line5", "postcode" };
        this.xmlTagsStringsReducedStaff = new String[] { "staff_id", "staff_title", "staff_initials", "staff_forenames", "staff_surname", "region_code" };
        this.xmlTagsStringsReducedQuestions = new String[] { "questn_part_desc", "student_question_part_score", "maximum_question_part_score" };
        this.xmlTagsStringsReducedSubs = new String[] { "course_code", "course_version_num", "pres_code", "assgnmt_suffix", "e_tma_submission_num", "e_tma_submission_date", "walton_received_date", "marked_date", "submission_status", "late_submission_status", "zip_date", "zip_file", "score_update_allowed", "overall_grade_score", "tutor_comments", "max_assgnmt_score", "total_question_count", "permitted_question_count" };
        this.lString = this.xmlTagsStringsReduced.length;
        this.lStringStaff = this.xmlTagsStringsReducedStaff.length;
        this.lStringQuestions = this.xmlTagsStringsReducedQuestions.length;
        this.studentDetails = new HashMap<String, String>();
        this.tutorDetails = new HashMap<String, String>();
        this.submissionDetails = new HashMap<String, String>();
        this.studentDetailsShort = new HashMap<String, String>();
        this.tutorDetailsShort = new HashMap<String, String>();
        this.submissionDetailsShort = new HashMap<String, String>();
        this.fieldNames1 = new HashMap<String, JTextField>();
        this.fieldNames2 = new HashMap<String, JTextField>();
        this.questionNumbers = new ArrayList<String>();
        this.partStarts = new ArrayList<Integer>();
        this.outString = "";
        this.markString = "";
        this.numberOfQuestions = 0;
        this.numberOfParts = new ArrayList<Integer>();
        this.ourRoot = Preferences.userNodeForPackage(this.getClass());
        this.thisLine = new ArrayList();
        this.weightingsPreferences = new String[15][4];
        this.maxScoresPreferences = new String[15][4];
        this.mainLocationPreferences = new double[2];
        this.sizePreferences = true;
        this.currentWpPreferences = "";
        this.currentEdPreferences = "";
        this.currentAudioPreferences = "";
        this.currentBrowserPreferences = "";
        this.csvFilePreference = "";
        this.startedFlag = false;
        this.highlightedFlag = true;
        this.maxFileNameLength = 0;
        this.questionStatusList = new ArrayList<String>();
        this.studentMarksList = new ArrayList<String>();
        this.newDownloadFolderPath = "";
        this.tmaNewFiles = new ArrayList<String>();
        this.tmaTempFiles = new ArrayList<String>();
        this.tmaTransFiles = new ArrayList<String>();
        this.tmaWeightings = new ArrayList<String>();
        this.tmaMaxScores = new ArrayList<String>();
        this.longFilenames = new ArrayList<String>();
        this.tmaNames = new String[] { "Course", "TMA00", "TMA01", "TMA02", "TMA03", "TMA04", "TMA05", "TMA06", "TMA07", "TMA08", "TMA09", "TMA10", "TMA11", "TMA12", "Pass Mark" };
        this.weightingsMap = new HashMap<String, Integer>();
        this.maxScoresMap = new HashMap<String, Integer>();
        this.weightingsList = new ArrayList<Integer>();
        this.maxScoresList = new ArrayList<Integer>();
        this.sortOrder = true;
        this.tmaListSizes = new int[] { 45, 60, 90, 65, 15, 30, 10, 25, 10, 10, 120, 8 };
        this.gradesListSizes = new int[] { 70, 70, 70, 42, 30, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 45, 45 };
        this.prevMarksListSizes = new int[] { 35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 0, 0, 40, 40 };
        this.savedFlag = true;
        this.partScoresListSizes = new int[] { 50, 70, 70, 50, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25 };
        this.toBeMarked = 0;
        this.filter = "All";
        this.displayFlag = true;
        this.redRows = new ArrayList<Integer>();
        this.blueRows = new ArrayList<Integer>();
        this.passMark = 0;
        this.fSize = 0;
        this.size = true;
        this.pt3Files = null;
        this.emailRecipients = new String[3];
        this.undo = new UndoManager();
        this.clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        this.xCoord = 0;
        this.yCoord = 0;
        this.tooHighFlag = false;
        this.overallGrade = "";
        this.currentWpPath = "System Default";
        this.currentEdPath = "System Default";
        this.currentAudioPath = "System Default";
        this.osName = "";
        this.wpMap = new HashMap<String, String>();
        this.wpMap1 = new HashMap<String, String>();
        this.attachmentFile = null;
        this.attFlag = false;
        this.smallWindowSize = new int[] { 870, 520 };
        this.largeWindowSize = new int[] { 870, 740 };
        this.MONTHLIST = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        this.currentStudentScript = "";
        this.tmaListLocX = 20.0;
        this.tmaListLocY = 20.0;
        this.tmaListWidth = 20.0;
        this.tmaListHeight = 20.0;
        this.startUp = true;
        this.submissionList = new ArrayList<String>();
        this.readErrorFlag = false;
        this.entryErrorFlag = false;
        this.courseName = "";
        this.colorPreferences = "219;219;219:255;255;255:204;255;255";
        this.colorDefaultsFlag = new boolean[] { true, true, true };
        this.currentColorIndex = 0;
        this.LINUXFILEMANAGER = new String[] { "nautilus", "konqueror" };
        this.linuxWP = "ooffice";
        this.linuxAudio = "ooffice";
        this.char02 = '\u0002';
        this.string02 = "" + this.char02;
        this.char03 = '\u0003';
        this.string03 = "" + this.char03;
        this.wineacute = '\u00e9';
        this.wineacuteString = "" + this.wineacute;
        this.rtn = '\r';
        this.lf = '\n';
        this.leftq = '\u00d2';
        this.leftqString = "" + this.leftq;
        this.rightq = '\u00d3';
        this.rightqString = "" + this.rightq;
        this.lfString = "" + this.lf;
        this.rtnString = "" + this.rtn;
        this.courseDirectory = "";
        this.courseDirectoryList = new HashSet<String>();
        this.autoImportFlag = false;
        this.unzippedFilePath = "";
        this.tmaListError = false;
        this.messageWindow2 = null;
        this.messageWindow3 = new JFrame();
        this.dictionary = null;
        this.spellChecker = null;
        this.acceptableFiles = new String[] { ".doc", ".rtf", ".pdf", "docx", ".mp3", ".MP3", ".py", ".PDF" };
        this.acceptableFilesSet = new HashSet<String>();
        this.feedbackString = "";
        this.markedString = "-MARKED";
        this.markedString1 = "";
        this.allStudents = new TreeSet<String>();
        this.allTmas = new TreeSet<String>();
        this.returnsName = "/returns/";
        this.tempName = "/temp/";
        this.gridColor = null;
        this.additionField = new JMenu("");
        this.acceptableEnds = new HashSet<String>();
        this.yearEnds = new String[] { "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30" };
        this.monthTags = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" };
        this.pathToDownloadedFile = "";
        this.autoImportPreferences = false;
        this.globalFontsPreferences = false;
        this.zipFileName = "";
        this.foundItZip = false;
        this.fileToUnzip = "";
        this.desktopPath = "";
        this.distFile = null;
        this.checkReturnsFlagPreferences = true;
        this.parentName1 = "";
        this.version1 = this.getClass().getPackage().getImplementationVersion();
        this.maxAcceptFilename = 59;
        this.isMonitorData = false;
        this.myHighlightPainter = new MyHighlightPainter(Color.yellow);
        this.blankHighlightPainter = new MyHighlightPainter(Color.white);
        this.initComponents();
        this.additionField = new JMenu("");
        this.late_submission_status.setVisible(false);
        this.openPreferences.setVisible(false);
        this.hideOpenPreferences.setVisible(false);
        this.jMenuItemVersion.setText("Version info: " + this.thisVersion);
        this.osName = System.getProperty("os.name");
        if (this.osName.equals("Mac OS X")) {
            this.getRootPane().putClientProperty("apple.awt.fullscreenable", true);
        }
        this.jMenuBar1.add(this.additionField);
        this.makeMap();
        try {
            this.desktopPath = System.getProperty("user.home") + "/Desktop";
            this.desktopPath = this.desktopPath.replace("\\", "/");
        }
        catch (final Exception e) {
            System.out.println("Exception caught =" + e.getMessage());
        }
        this.dictionaryLocation = System.getProperty("user.dir") + "/etmaDictionary.dic";
        this.findAcceptableEnds();
        final int keyModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        final TableColumn zipColumn = this.listOfTmas.getColumnModel().getColumn(11);
        final JCheckBox checkBox = new JCheckBox();
        this.setWpMap();
        this.setWpMapAlt();
        zipColumn.setCellEditor(new DefaultCellEditor(checkBox));
        checkBox.updateUI();
        this.tutor_comments.setVisible(false);
        this.moreDetails.setVisible(true);
        this.osName = System.getProperty("os.name");
        if (!this.osName.equals("Mac OS X")) {
            this.smallWindowSize[0] = 810;
            this.smallWindowSize[1] = 540;
            this.largeWindowSize[0] = this.smallWindowSize[0];
        }
        else {
            this.exitMenuItem.setVisible(false);
            this.preferencesMenu.setVisible(false);
            this.largeWindowSize[1] = 770;
        }
        if (this.osName.contains("Windows")) {
            this.returnsName = "\\returns\\";
            this.tempName = "\\temp\\";
        }
        this.setPreferences();
        this.setSizesOfPrevScoresList();
        try {
            final File aFile = new File(this.dictionaryPath.getText());
            this.dictionary = (SpellDictionary)new SpellDictionaryHashMap(aFile);
        }
        catch (final Exception ex) {}
        if (!this.startupScreenFlagPreferences) {
            this.flashScreen();
        }
        this.setResizable(true);
        for (int i = 0; i < this.tmaNames.length; ++i) {
            this.tmaNumbers.setValueAt(this.tmaNames[i], i, 0);
        }
        this.restoreHidePreferences();
        this.tmaScores.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.weightingsTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.weightingsTable2.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.weightingsTable3.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.weightingsTable4.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.maxScoreTable1.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.maxScoreTable2.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.maxScoreTable3.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.maxScoreTable4.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        this.autoMp3.setVisible(false);
        this.autoMp3.setSelected(false);
        if (this.etmasFolder.getText().equals("")) {
            final Object[] options = { "Yes", "Quit program" };
            final JFrame frame = null;
            final int n = JOptionPane.showOptionDialog(frame, "Before you can continue, you must select your 'etmas' folder.\nIt MUST be called 'etmas' exactly without the quotes.\nWould you like to do this now? ", "", 1, 3, null, options, options[0]);
            if (n == 1) {
                System.exit(0);
            }
            if (n == 0) {
                this.selectEtmasFolder();
            }
            JOptionPane.showMessageDialog(null, "The program will now quit. Please restart it!");
            System.exit(0);
        }
        else {
            final File testFile = new File(this.etmasFolder.getText());
            if (!testFile.exists()) {
                JOptionPane.showMessageDialog((Component)null, "Your etmas folder cannot be found!\nHave you moved or deleted it?\nIt should be at " + this.etmasFolder.getText() + "\n\nPlease re-locate it in the preferences before going any further, then quit and relaunch the Filehandler.");
                this.openPrefsWindow();
            }
        }
        this.buttonHider();
        this.tmaScores.getColumnModel().getColumn(2).setCellEditor(new GACellEditor(new JTextField()));
        this.weightingsTable1.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.weightingsTable2.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.weightingsTable3.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.weightingsTable4.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.maxScoreTable1.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.maxScoreTable2.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.maxScoreTable3.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        this.maxScoreTable4.getColumnModel().getColumn(0).setCellEditor(new GACellEditor(new JTextField()));
        ToolTipManager.sharedInstance().setEnabled(this.toolTipFlag.isSelected());
        final String tempCourseMenuPreference = this.courseMenuPreferences;
        final String tempTmaMenuPreference = this.tmaMenuPreferences;
        try {
            this.setupMenus(this.etmasFolder.getText(), this.courseList);
        }
        catch (final Exception anException) {
            System.out.println("Error:" + anException);
        }
        this.startedFlag = true;
        this.courseList.setSelectedItem(tempCourseMenuPreference);
        this.tmaList.setSelectedItem(tempTmaMenuPreference);
        this.subNo.setVisible(false);
        this.studentList.setVisible(false);
        this.loadXMLAlt.setVisible(false);
        this.fhiFileName.setVisible(false);
        this.savePt3.setEnabled(false);
        this.zipFiles.setEnabled(false);
        this.openTma.setEnabled(false);
        this.openTmaFolder.setEnabled(false);
        this.checkTotals.setEnabled(false);
        this.printDoc.addActionListener(new etmaPrinter(this, 0.8));
        this.listOfTmas.setSelectionMode(2);
        this.lateSubmission.setSelected(false);
        this.tutor_comments_input.setFont(new Font("Lucida Grande", 0, this.fontPreferences));
        final SecurityManager appsm = System.getSecurityManager();
        this.savePt3MenuItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        this.savePt3MenuItem.setAccelerator(KeyStroke.getKeyStroke(83, keyModifier));
        this.listTmasMenuItem.setAccelerator(KeyStroke.getKeyStroke(76, keyModifier));
        this.bankCommentMenuItem.setAccelerator(KeyStroke.getKeyStroke(66, keyModifier));
        this.checkSpelling.setAccelerator(KeyStroke.getKeyStroke(75, keyModifier));
        this.copyText.setAccelerator(KeyStroke.getKeyStroke(67, keyModifier));
        this.pasteText.setAccelerator(KeyStroke.getKeyStroke(86, keyModifier));
        this.cutText.setAccelerator(KeyStroke.getKeyStroke(88, keyModifier));
        this.printDoc.setAccelerator(KeyStroke.getKeyStroke(80, keyModifier));
        this.exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, keyModifier));
        this.Undo.setAccelerator(KeyStroke.getKeyStroke(90, keyModifier));
        this.Redo.setAccelerator(KeyStroke.getKeyStroke(89, keyModifier));
        this.preferencesMenu.setAccelerator(KeyStroke.getKeyStroke(74, keyModifier));
        this.openTmaMenu.setAccelerator(KeyStroke.getKeyStroke(79, keyModifier));
        this.openTmaFolderMenu.setAccelerator(KeyStroke.getKeyStroke(70, keyModifier));
        this.checkTotalsMenu.setAccelerator(KeyStroke.getKeyStroke(84, keyModifier));
        this.openCommentBankMenu.setAccelerator(KeyStroke.getKeyStroke(71, keyModifier));
        this.selectAllText.setAccelerator(KeyStroke.getKeyStroke(65, keyModifier));
        this.jMenuItemUndo.setAccelerator(KeyStroke.getKeyStroke(90, keyModifier));
        this.jMenuItemRedo.setAccelerator(KeyStroke.getKeyStroke(89, keyModifier));
        this.jMenuItemCopy.setAccelerator(KeyStroke.getKeyStroke(67, keyModifier));
        this.jMenuItemPaste.setAccelerator(KeyStroke.getKeyStroke(86, keyModifier));
        this.jMenuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(65, keyModifier));
        this.jMenuItemCut.setAccelerator(KeyStroke.getKeyStroke(88, keyModifier));
        try {
            appsm.checkExit(0);
        }
        catch (final Exception ex2) {}
        this.mainLocationPreferences[0] = this.ourRoot.getDouble("menuPreferences1", 30.0);
        this.mainLocationPreferences[1] = this.ourRoot.getDouble("menuPreferences2", 30.0);
        this.setLocation((int)this.mainLocationPreferences[0], (int)this.mainLocationPreferences[1]);
        this.setLocation((int)this.mainLocationPreferences[0], (int)this.mainLocationPreferences[1]);
        this.size = this.ourRoot.getBoolean("size", true);
        if (this.size) {
            this.setSize((int)this.ourRoot.getDouble("menuPreferences3", 830.0), (int)this.ourRoot.getDouble("smallWindowHeight", 512.0));
        }
        else {
            this.setSize((int)this.ourRoot.getDouble("menuPreferences3", 830.0), (int)this.ourRoot.getDouble("largeWindowHeight", 740.0));
        }
        this.undoCode();
        this.setupWeightings();
        final Transferable clipBoard1 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            final String s = (String)clipBoard1.getTransferData(DataFlavor.stringFlavor);
        }
        catch (final Exception ex3) {}
        this.JavaAwtDesktop();
        this.setProgIcon();
        final JTableHeader header = this.listOfTmas.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final TableColumnModel tcm = EtmaHandlerJ.this.listOfTmas.getColumnModel();
                final int vc = tcm.getColumnIndexAtX(e.getX());
                if (vc != 11) {
                    final DefaultTableModel model = (DefaultTableModel)EtmaHandlerJ.this.listOfTmas.getModel();
                    EtmaHandlerJ.this.sortAllRowsBy(model, vc, EtmaHandlerJ.this.sortOrder);
                    EtmaHandlerJ.this.getNewTmaRedRows(EtmaHandlerJ.this.listOfTmas.getRowCount());
                    try {
                        EtmaHandlerJ.this.setSizesOfTmaList();
                    }
                    catch (final Exception ex) {}
                    EtmaHandlerJ.this.sortPreference = EtmaHandlerJ.this.sortOrder;
                    EtmaHandlerJ.this.sortRow = vc;
                    EtmaHandlerJ.this.ourRoot.putBoolean("sortPreference", EtmaHandlerJ.this.sortPreference);
                    EtmaHandlerJ.this.ourRoot.putInt("sortRow", EtmaHandlerJ.this.sortRow);
                    EtmaHandlerJ.this.sortOrder = !EtmaHandlerJ.this.sortOrder;
                }
                else {
                    final Object[] options = { "Select All", "Select None", "Cancel" };
                    final JFrame frame = null;
                    final int n = JOptionPane.showOptionDialog(frame, "Quick Select:", "", 1, 3, null, options, options[0]);
                    final int nRow = EtmaHandlerJ.this.listOfTmas.getRowCount();
                    boolean selectAllFlag = true;
                    if (n == 1) {
                        selectAllFlag = false;
                    }
                    if (n != 2) {
                        for (int i = 0; i < nRow; ++i) {
                            if (!EtmaHandlerJ.this.listOfTmas.getValueAt(i, 0).equals("")) {
                                EtmaHandlerJ.this.listOfTmas.setValueAt(selectAllFlag, i, 11);
                            }
                        }
                    }
                }
            }
        });
        final JTableHeader header2 = this.gradesSummaryTable.getTableHeader();
        header2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                final TableColumnModel tcm = EtmaHandlerJ.this.gradesSummaryTable.getColumnModel();
                final int vc = tcm.getColumnIndexAtX(e.getX());
                final DefaultTableModel model = (DefaultTableModel)EtmaHandlerJ.this.gradesSummaryTable.getModel();
                EtmaHandlerJ.this.sortAllRowsBy(model, vc, EtmaHandlerJ.this.sortOrder);
                EtmaHandlerJ.this.getNewRedRows(EtmaHandlerJ.this.allStudents.size());
                try {
                    EtmaHandlerJ.this.setSizesOfGradesList();
                }
                catch (final Exception ex) {}
                EtmaHandlerJ.this.sortOrder = !EtmaHandlerJ.this.sortOrder;
            }
        });
        if (this.launchTmaList.isSelected()) {
            this.openList();
        }
        this.submittedTmas.setLocation((int)this.tmaListLocX, (int)this.tmaListLocY);
        this.startUp = false;
        if (this.checkReturnsFlag.isSelected()) {
            try {
                this.returnsFolderCheckEmpty();
            }
            catch (final Exception ex4) {}
        }
        if (!this.startupScreenFlagPreferences) {
            this.messageWindow2.setVisible(false);
        }
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
    }
    
    private void setPreferences() {
        this.colorDefaultsFlag[0] = this.ourRoot.getBoolean("colorDefaults0", true);
        this.colorDefaultsFlag[1] = this.ourRoot.getBoolean("colorDefaults1", true);
        this.colorDefaultsFlag[2] = this.ourRoot.getBoolean("colorDefaults2", true);
        this.directEntryFlag.setSelected(this.ourRoot.getBoolean("directEntryPreference", false));
        this.userNamePreferences = this.ourRoot.get("userNamePreferences", "");
        this.currentWpPreferences = this.ourRoot.get("currentWpPreferences", "System Default");
        this.currentEdPreferences = this.ourRoot.get("currentEdPreferences", "System Default");
        this.currentWpPath = this.ourRoot.get("currentWpPath", "System Default");
        this.currentEdPath = this.ourRoot.get("currentEdPath", "System Default");
        this.currentAudioPath = this.ourRoot.get("currentAudioPath", "System Default");
        this.toolTipFlagPreferences = this.ourRoot.getBoolean("toolTipFlagPreferences", true);
        this.checkClosureFlagPreferences = this.ourRoot.getBoolean("checkClosureFlagPreferences", false);
        this.spellCheckFlagPreferences = this.ourRoot.getBoolean("spellCheckFlagPreferences", false);
        this.suggestFlagPreferences = this.ourRoot.getBoolean("suggestFlagPreferences", false);
        if (!this.spellCheckFlagPreferences) {
            this.suggestFlagPreferences = false;
            this.suggestFlag.setSelected(false);
            this.suggestFlag.setEnabled(false);
        }
        this.doubleClickFlag.setSelected(this.ourRoot.getBoolean("doubleClickFlag", false));
        this.addInitialsFlag.setSelected(this.ourRoot.getBoolean("resizeFlag", true));
        this.showLatestFlag.setSelected(this.ourRoot.getBoolean("showLatestFlag", false));
        this.checkClosureFlag.setSelected(this.checkClosureFlagPreferences);
        this.spellCheckFlag.setSelected(this.spellCheckFlagPreferences);
        this.suggestFlag.setSelected(this.suggestFlagPreferences);
        this.toolTipFlag.setSelected(this.ourRoot.getBoolean("toolTipFlagPreferences", true));
        this.startupScreenFlag.setSelected(this.ourRoot.getBoolean("startupScreenPreferences", true));
        if (this.osName.equals("Mac OS X")) {
            this.startupScreenFlagPreferences = this.startupScreenFlag.isSelected();
        }
        else {
            this.startupScreenFlagPreferences = true;
        }
        this.autoImportPreferences = this.ourRoot.getBoolean("autoImportFlag", false);
        this.autoImportFlag1.setSelected(this.autoImportPreferences);
        this.wpPath.setText(this.ourRoot.get("currentWpPreferences", "System Default"));
        this.commentBankEd.setText(this.ourRoot.get("currentEdPreferences", "System Default"));
        this.audioPath.setText(this.ourRoot.get("currentAudioPreferences", "System Default"));
        this.browserPath.setText(this.ourRoot.get("currentBrowserPreferences", ""));
        this.currentBrowserPreferences = this.ourRoot.get("currentBrowserPreferences", "");
        this.passwordPreferences = this.ourRoot.get("passwordPreferences", "");
        this.smtpServerPreferences = this.ourRoot.get("smtpServerPreferences", "");
        EtmaHandlerJ.dictionaryPathPreferences = this.ourRoot.get("dictionaryPathPreferences", this.dictionaryLocation);
        this.dictionaryPath.setText(EtmaHandlerJ.dictionaryPathPreferences);
        this.yourEmailAddressPreferences = this.ourRoot.get("yourEmailAddressPreferences", "");
        this.authenticationFlagPreferences = this.ourRoot.getBoolean("authenticationFlagPreferences", true);
        this.launchTmaListFlagPreferences = this.ourRoot.getBoolean("launchTmaListFlagPreferences", true);
        this.launchTmaList.setSelected(this.launchTmaListFlagPreferences);
        this.autoFillFlagPreferences = this.ourRoot.getBoolean("autoFillFlagPreferences", false);
        this.autoFillFlag.setSelected(this.ourRoot.getBoolean("autoFillFlagPreferences", false));
        this.checkReturnsFlagPreferences = this.ourRoot.getBoolean("checkReturnsFlagPreferences", true);
        this.checkReturnsFlag.setSelected(this.ourRoot.getBoolean("checkReturnsFlagPreferences", true));
        this.globalFonts.setSelected(this.ourRoot.getBoolean("globalFonts", false));
        this.globalFontsPreferences = this.ourRoot.getBoolean("globalFonts", false);
        if (this.globalFontsPreferences) {
            this.setFonts(this.fontPreferences);
        }
        this.sortPreference = this.ourRoot.getBoolean("sortPreference", true);
        this.sortRow = this.ourRoot.getInt("sortRow", 2);
        this.sizeWarnFlag.setSelected(this.ourRoot.getBoolean("sizeWarnFlagPreferences", true));
        this.sizeWarnFlagPreferences = this.sizeWarnFlag.isSelected();
        this.mailUserName.setText(this.ourRoot.get("userNamePreferences", ""));
        this.mailPassword.setText(this.ourRoot.get("passwordPreferences", ""));
        this.yourEmailAddress.setText(this.ourRoot.get("yourEmailAddressPreferences", ""));
        this.tmaListLocX = this.ourRoot.getDouble("tmaListLocX", 20.0);
        this.tmaListLocY = this.ourRoot.getDouble("tmaListLocY", 20.0);
        this.smtpHost.setText(this.ourRoot.get("smtpServerPreferences", ""));
        this.authenticationFlag.setSelected(this.ourRoot.getBoolean("authenticationFlagPreferences", true));
        this.etmasFolder.setText(this.ourRoot.get("etmasFolder", ""));
        this.downloadsFolder.setText(this.ourRoot.get("downloadsFolder", this.desktopPath));
        this.commentBankFile.setText(this.ourRoot.get("commentBankFile", "Automatic"));
        this.createMarked.setSelected(this.ourRoot.getBoolean("createMarked", true));
        this.eTmaAddressPreferences = this.ourRoot.get("etmaAddress", "http://css3.open.ac.uk/etma/tutor/");
        this.ouEtmaAddress.setText(this.ourRoot.get("etmaAddress", "http://css3.open.ac.uk/etma/tutor/"));
        this.jsTestFile.setText(this.ourRoot.get("jsTestFile", ""));
        this.courseMenuPreferences = this.ourRoot.get("menuPreferences", "");
        this.mainLocationPreferences[0] = this.ourRoot.getInt("menuPreferences1", 0);
        this.mainLocationPreferences[1] = this.ourRoot.getInt("menuPreferences2", 0);
        this.tmaMenuPreferences = this.ourRoot.get("tmaMenuPreferences", "");
        this.downloadsFolderPreferences = this.ourRoot.get("downloadsFolder", this.desktopPath);
        this.createMarkedPreferences = this.ourRoot.getBoolean("createMarked", true);
        this.sizePreferences = this.ourRoot.getBoolean("size", true);
        this.fontPreferences = this.ourRoot.getInt("fontSize", 14);
        this.highlightedFlag = this.ourRoot.getBoolean("highlightedFlag", true);
        this.highlightUnmarked.setSelected(this.highlightedFlag);
        this.csvFilePreference = this.ourRoot.get("csvFilePreference", "");
        this.colorPreferences = this.ourRoot.get("colorPreferences", "255;255;255:255;255;255:204;255;255");
        this.setColors();
        this.colorRemove.setSelected(this.ourRoot.getBoolean("colorRemovePreferences", true));
        for (int i = 0; i < 15; ++i) {
            this.weightingsPreferences[i][0] = this.ourRoot.get("weightings<" + i + "><0>", "");
            this.weightingsTable1.setValueAt(this.weightingsPreferences[i][0], i, 0);
            this.weightingsPreferences[i][1] = this.ourRoot.get("weightings<" + i + "><1>", "");
            this.weightingsTable2.setValueAt(this.weightingsPreferences[i][1], i, 0);
            this.weightingsPreferences[i][2] = this.ourRoot.get("weightings<" + i + "><2>", "");
            this.weightingsTable3.setValueAt(this.weightingsPreferences[i][2], i, 0);
            this.weightingsPreferences[i][3] = this.ourRoot.get("weightings<" + i + "><3>", "");
            this.weightingsTable4.setValueAt(this.weightingsPreferences[i][3], i, 0);
        }
        for (int i = 0; i < 14; ++i) {
            this.maxScoresPreferences[i][0] = this.ourRoot.get("maxScores<" + i + "><0>", "");
            this.maxScoreTable1.setValueAt(this.maxScoresPreferences[i][0], i, 0);
            this.maxScoresPreferences[i][1] = this.ourRoot.get("maxScores<" + i + "><1>", "");
            this.maxScoreTable2.setValueAt(this.maxScoresPreferences[i][1], i, 0);
            this.maxScoresPreferences[i][2] = this.ourRoot.get("maxScores<" + i + "><2>", "");
            this.maxScoreTable3.setValueAt(this.maxScoresPreferences[i][2], i, 0);
            this.maxScoresPreferences[i][3] = this.ourRoot.get("maxScores<" + i + "><3>", "");
            this.maxScoreTable4.setValueAt(this.maxScoresPreferences[i][3], i, 0);
        }
        this.fontSize.setSelectedItem(String.valueOf(this.fontPreferences));
        this.maxScoreTable1.setValueAt("XXXXXXXXXXXXX", 14, 0);
        this.maxScoreTable2.setValueAt("XXXXXXXXXXXXX", 14, 0);
        this.maxScoreTable3.setValueAt("XXXXXXXXXXXXX", 14, 0);
        this.maxScoreTable4.setValueAt("XXXXXXXXXXXXX", 14, 0);
        this.smallWindowSize[1] = (int)this.ourRoot.getDouble("smallWindowHeight", 520.0);
        this.largeWindowSize[1] = (int)this.ourRoot.getDouble("largeWindowHeight", 740.0);
        this.size = this.ourRoot.getBoolean("size", true);
        if (this.size) {
            this.setSize(this.smallWindowSize[0], this.smallWindowSize[1]);
            this.moreDetails.setText("More details");
            this.moreDetails.setToolTipText("Shows the full Handler window");
        }
        else {
            this.setSize(this.largeWindowSize[0], this.largeWindowSize[1]);
            this.moreDetails.setText("Fewer details");
            this.moreDetails.setToolTipText("Shows the smaller Handler window");
        }
    }
    
    public boolean checkForDigit(final String aString) {
        boolean thereIsADigit = false;
        for (int strLen = aString.length(), i = 0; i < strLen; ++i) {
            final Character thisChar = aString.charAt(i);
            if (Character.isDigit(thisChar)) {
                thereIsADigit = true;
            }
        }
        return thereIsADigit;
    }
    
    public String getClipBoard() {
        String result = "";
        final Transferable clipBoard1 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            result = (String)clipBoard1.getTransferData(DataFlavor.stringFlavor);
        }
        catch (final Exception ex) {}
        return result;
    }
    
    public void highlight(final JTextArea textComp, final String pattern) {
        try {
            final Highlighter hilite = textComp.getHighlighter();
            final Document doc = textComp.getDocument();
            final String text = doc.getText(0, doc.getLength());
            for (int pos = 0; (pos = text.indexOf(pattern, pos)) >= 0; pos += pattern.length()) {
                hilite.addHighlight(pos, pos + pattern.length() - 1, this.myHighlightPainter);
            }
        }
        catch (final Exception ex) {}
    }
    
    public void removeSingleHighlight(final JTextArea textComp, final String pattern) {
        try {
            final Highlighter hilite = textComp.getHighlighter();
            final Document doc = textComp.getDocument();
            final String text = doc.getText(0, doc.getLength());
            for (int pos = 0; (pos = text.indexOf(pattern, pos)) >= 0; pos += pattern.length()) {
                hilite.addHighlight(pos, pos + pattern.length(), this.blankHighlightPainter);
            }
        }
        catch (final Exception ex) {}
    }
    
    public void removeHighlights(final JTextArea textComp) {
        final Highlighter hilite = textComp.getHighlighter();
        final Highlighter.Highlight[] hilites = hilite.getHighlights();
        for (int i = 0; i < hilites.length; ++i) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
    
    public void spellReplace() {
        String newWord = "";
        List<String> suggestions = new ArrayList<String>();
        String wrongWord = "";
        wrongWord = Suggest.SuggestionListener.wrongWord;
        Suggest.SuggestionListener.wrongWord = "";
        if (!wrongWord.equals("")) {
            Toolkit.getDefaultToolkit().beep();
            String newText = "";
            int thisOption = 0;
            boolean suggFlag = false;
            final ButtonGroup bgroup = new ButtonGroup();
            final JPanel radioPanel = new JPanel();
            suggestions = Suggest.SuggestionListener.spellOutputList;
            try {
                final int wwBegin = this.tutor_comments_input.getText().indexOf(wrongWord);
                final int wwEnd = wwBegin + wrongWord.length();
                this.tutor_comments_input.select(wwBegin, wwEnd);
            }
            catch (final Exception anException) {
                System.out.println("Error " + anException);
            }
            if (suggestions.size() > 0) {
                suggFlag = true;
            }
            if (suggFlag) {
                final JRadioButton[] thisButton = new JRadioButton[suggestions.size()];
                radioPanel.setLayout(new GridLayout(3, 1));
                for (int i = 0; i < suggestions.size(); ++i) {
                    bgroup.add(thisButton[i] = new JRadioButton(suggestions.get(i), false));
                    radioPanel.add(thisButton[i]);
                }
                radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Please select replacement spelling:"));
                final JFrame frame = null;
                final Object[] options = { "Ignore", "Add to dictionary", "Replace", "Type in replacement" };
                if (thisButton.length >= 1) {
                    thisButton[0].setSelected(true);
                }
                thisOption = JOptionPane.showOptionDialog((Component)frame, (Object)radioPanel, "Spelling Report: " + wrongWord, 1, 3, (Icon)null, options, options[0]);
                for (int j = 0; j < thisButton.length; ++j) {
                    if (thisButton[j].isSelected()) {
                        newWord = suggestions.get(j);
                    }
                }
                if (thisOption == 2) {
                    final String aPeriod = ".";
                    for (int k = 0; k < thisButton.length; ++k) {
                        if (thisButton[k].isSelected()) {
                            newWord = suggestions.get(k);
                            if (this.checkCase(wrongWord)) {
                                newWord = this.changeCase(newWord);
                            }
                            newText = this.tutor_comments_input.getText().replaceAll(" " + wrongWord, " " + newWord);
                            newText = this.fixPunctuation(newText, wrongWord, newWord);
                        }
                    }
                    if (!newWord.equals("")) {
                        this.tutor_comments_input.setText(newText);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "You haven't selected a replacement!");
                    }
                }
                if (thisOption == 1) {
                    Suggest.SuggestionListener.addToDictionary(wrongWord);
                }
                if (thisOption == 3) {
                    newWord = JOptionPane.showInputDialog("Please type in the amended word");
                    if (!newWord.equals("")) {
                        newText = this.tutor_comments_input.getText().replaceAll(" " + wrongWord, " " + newWord);
                        newText = this.fixPunctuation(newText, wrongWord, newWord);
                        this.tutor_comments_input.setText(newText);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "You haven't typed anything!", "", 2);
                    }
                }
            }
            else if (Suggest.SuggestionListener.checkFlag) {
                Suggest.SuggestionListener.checkFlag = false;
                final JFrame frame2 = null;
                radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "No suggestions"));
                final Object[] options2 = { "Ignore", "Add to dictionary", "Change" };
                thisOption = JOptionPane.showOptionDialog((Component)frame2, (Object)radioPanel, "Spelling Report: " + wrongWord, 1, 3, (Icon)null, options2, options2[0]);
                if (thisOption == 1) {
                    Suggest.SuggestionListener.addToDictionary(wrongWord);
                }
                if (thisOption == 2) {
                    newWord = JOptionPane.showInputDialog("Please type in the amended word");
                    if (!newWord.equals("")) {
                        newText = this.tutor_comments_input.getText().replaceAll(" " + wrongWord, " " + newWord);
                        newText = this.fixPunctuation(newText, wrongWord, newWord);
                        this.tutor_comments_input.setText(newText);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "You haven't typed anything!", "", 2);
                    }
                }
            }
            this.tutor_comments_input.setCaretPosition(Math.max(0, this.cursorPos + newWord.length() - wrongWord.length()));
            Suggest.SuggestionListener.spellOutputList.clear();
            Suggest.SuggestionListener.wrongWord = "";
        }
    }
    
    public boolean checkCase(final String aWord) {
        boolean isCapital = false;
        final int ascii = aWord.codePointAt(0);
        if (ascii > 64 && ascii < 97) {
            isCapital = true;
        }
        return isCapital;
    }
    
    public String changeCase(final String aWord) {
        String upperCase = "";
        int newAscii;
        final int ascii = newAscii = aWord.codePointAt(0);
        if (ascii > 96) {
            newAscii = ascii - 32;
        }
        final char firstCharacter = (char)newAscii;
        upperCase = firstCharacter + aWord.substring(1);
        return upperCase;
    }
    
    public String fixPunctuation(String aComment, final String aWord, final String bWord) {
        final char returnChar = '\r';
        final char linefeedChar = '\n';
        aComment = aComment.replace(" " + aWord, " " + bWord);
        aComment = aComment.replace(" " + aWord, " " + bWord);
        aComment = aComment.replace(" " + aWord, " " + bWord);
        aComment = aComment.replace(" " + aWord, " " + bWord);
        aComment = aComment.replace(" " + aWord, " " + bWord);
        aComment = aComment.replace(" " + aWord, " " + bWord);
        aComment = aComment.replace(" " + aWord + linefeedChar, " " + bWord + linefeedChar);
        aComment = aComment.replace(linefeedChar + aWord, linefeedChar + bWord);
        final int wordLength = aWord.length();
        final String existingFirstWord = aComment.substring(0, wordLength);
        if (existingFirstWord.equals(aWord)) {
            aComment = aComment.replaceFirst(aWord, bWord);
        }
        return aComment;
    }
    
    public void spellHighlight(final String wrongWord) {
        final List<String> suggestions = new ArrayList<String>();
        this.highlight(this.tutor_comments_input, " " + wrongWord);
    }
    
    public void wordReplace(String aWord, final String bWord, final JPanel aPanel, final JRadioButton[] aButton, final List<String> aList) {
        while (aWord.equals("")) {
            final JFrame frame = null;
            final Object[] options = { "Replace", "Add to dictionary", "Cancel" };
            final int n = JOptionPane.showOptionDialog((Component)frame, (Object)aPanel, "Spelling Report: " + bWord, 1, 3, (Icon)null, options, options[0]);
            if (n == 0) {
                for (int i = 0; i < aButton.length; ++i) {
                    if (aButton[i].isSelected()) {
                        aWord = aList.get(i);
                        final String newText = this.tutor_comments_input.getText().replaceAll(bWord, aWord);
                        this.tutor_comments_input.setText(newText);
                    }
                }
            }
            if (n == 1) {
                Suggest.SuggestionListener.addToDictionary(bWord);
                aWord = "*";
            }
            if (n == 2) {
                aWord = "*";
            }
        }
    }
    
    public void spellCheckComments() {
        final File aFile = new File(this.dictionaryPath.getText());
        String line = "";
        String textToCheck = this.tutor_comments_input.getText();
        textToCheck = textToCheck.replace(",", "");
        final String[] linesToCheck = textToCheck.split(" ");
        try {
            this.dictionary = (SpellDictionary)new SpellDictionaryHashMap(aFile);
            (this.spellChecker = new SpellChecker(this.dictionary)).addSpellCheckListener((SpellCheckListener)new Suggest.SuggestionListener());
            for (int i = 0; i < linesToCheck.length; ++i) {
                line = linesToCheck[i];
                this.spellChecker.checkSpelling((WordTokenizer)new StringWordTokenizer(line));
                this.spellReplace();
            }
            JOptionPane.showMessageDialog(null, "Spellcheck complete!");
        }
        catch (final Exception anException) {
            System.out.println("Error " + anException);
            JOptionPane.showMessageDialog(null, "Please choose a dictionary, using the Preferences menu!");
        }
        this.highlightAllErrors();
    }
    
    public void JavaAwtDesktop() {
        try {
            final Desktop desktop = Desktop.getDesktop();
            desktop.setAboutHandler(e -> JOptionPane.showMessageDialog((Component)null, "This is version " + this.thisVersion, "About " + this.getTitle(), 1));
            desktop.setPreferencesHandler(e -> this.openPrefsWindow());
            desktop.setQuitHandler((e, response) -> {
                this.exitRoutine();
                response.cancelQuit();
                return;
            });
            final Taskbar taskbar = Taskbar.getTaskbar();
            final BufferedImage image = ImageIO.read(this.getClass().getResource("/res/etmahandler.jpeg"));
            taskbar.setIconImage(image);
        }
        catch (final Exception ex) {}
    }
    
    public void setProgIcon() {
        try {
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/res/etmahandler.jpeg")));
            this.preferences.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
            this.partScoresTable.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
            this.submittedTmas.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
            this.gradesSummary.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
            this.colorFrame1.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
            this.custom.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
            this.helpFrame.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmahandler.jpeg")).getImage());
        }
        catch (final Exception ex) {}
    }
    
    private void saveLocation() {
        this.ourRoot.putDouble("menuPreferences1", this.getLocation().getX());
        this.ourRoot.putDouble("menuPreferences2", this.getLocation().getY());
        this.ourRoot.putDouble("tmaListLocX", this.submittedTmas.getLocation().getX());
        this.ourRoot.putDouble("tmaListLocY", this.submittedTmas.getLocation().getY());
        this.tmaListLocX = this.submittedTmas.getLocation().getX();
        this.tmaListLocY = this.submittedTmas.getLocation().getY();
        this.ourRoot.putDouble("menuPreferences3", this.getSize().getWidth());
        this.ourRoot.putDouble("menuPreferences4", this.getSize().getHeight());
        if (this.size) {
            this.smallWindowSize[1] = (int)this.getSize().getHeight();
            this.ourRoot.putDouble("smallWindowHeight", this.smallWindowSize[1]);
        }
        else {
            this.largeWindowSize[1] = (int)this.getSize().getHeight();
            this.ourRoot.putDouble("largeWindowHeight", this.largeWindowSize[1]);
        }
    }
    
    private void initComponents() {
        this.buttonGroup1 = new ButtonGroup();
        this.preferences = new JFrame();
        this.closePreferences = new JButton();
        this.setEtmasFolder = new JButton();
        this.etmasFolder = new JTextField();
        this.selectDownloadsFolder = new JButton();
        this.downloadsFolder = new JTextField();
        this.createMarked = new JCheckBox();
        this.setTmaWeightings = new JButton();
        this.setCommentBankFile = new JButton();
        this.commentBankFile = new JTextField();
        this.setJsTestFile = new JButton();
        this.jsTestFile = new JTextField();
        this.ouEtmaAddress = new JTextField();
        this.jLabel5 = new JLabel();
        this.saveAddress = new JButton();
        this.autoFillFlag = new JCheckBox();
        this.toolTipFlag = new JCheckBox();
        this.spellCheckFlag = new JCheckBox();
        this.selectDictionary = new JButton();
        this.dictionaryPath = new JTextField();
        this.wpSelect = new JButton();
        this.wpPath = new JTextField();
        this.browserSelect = new JButton();
        this.browserPath = new JTextField();
        this.checkClosureFlag = new JCheckBox();
        this.launchTmaList = new JCheckBox();
        this.directEntryFlag = new JCheckBox();
        this.doubleClickFlag = new JCheckBox();
        this.addInitialsFlag = new JCheckBox();
        this.showLatestFlag = new JCheckBox();
        this.startupScreenFlag = new JCheckBox();
        this.suggestFlag = new JCheckBox();
        this.setAudioApp = new JButton();
        this.audioPath = new JTextField();
        this.autoMp3 = new JCheckBox();
        this.setCommentBankEditor = new JButton();
        this.commentBankEd = new JTextField();
        this.sizeWarnFlag = new JCheckBox();
        this.autoImportFlag1 = new JCheckBox();
        this.globalFonts = new JCheckBox();
        this.checkReturnsFlag = new JCheckBox();
        this.jLabel37 = new JLabel();
        this.submittedTmas = new JFrame();
        this.jScrollPane1 = new JScrollPane();
        this.listOfTmas = new JTable();
        this.batchZip = new JButton();
        this.totalTmas = new JTextField();
        this.toBeMarkedTmas = new JTextField();
        this.jLabel20 = new JLabel();
        this.jLabel21 = new JLabel();
        this.tmaSelectMenu = new JComboBox();
        this.missingSubmissions = new JButton();
        this.printTmaList = new JButton();
        this.jLabel34 = new JLabel();
        this.selectAll = new JButton();
        this.highlightUnmarked = new JCheckBox();
        this.gradesSummary = new JFrame();
        this.jScrollPane3 = new JScrollPane();
        this.gradesSummaryTable = new JTable();
        this.printGrades = new JButton();
        this.jLabel33 = new JLabel();
        this.exportGrades = new JButton();
        this.ignoreCurrentTma = new JCheckBox();
        this.weightings = new JFrame();
        this.weightings1 = new JScrollPane();
        this.weightingsTable1 = new JTable();
        this.weightings2 = new JScrollPane();
        this.weightingsTable2 = new JTable();
        this.weightings3 = new JScrollPane();
        this.weightingsTable3 = new JTable();
        this.weightingsHeaders = new JScrollPane();
        this.tmaNumbers = new JTable();
        this.saveWeightings = new JButton();
        this.weightings4 = new JScrollPane();
        this.weightingsTable4 = new JTable();
        this.maxScores1 = new JScrollPane();
        this.maxScoreTable1 = new JTable();
        this.maxScores2 = new JScrollPane();
        this.maxScoreTable2 = new JTable();
        this.maxScores3 = new JScrollPane();
        this.maxScoreTable3 = new JTable();
        this.maxScores4 = new JScrollPane();
        this.maxScoreTable4 = new JTable();
        this.jScrollPane6 = new JScrollPane();
        this.jTextArea2 = new JTextArea();
        this.jScrollPane7 = new JScrollPane();
        this.jTextArea3 = new JTextArea();
        this.helpFrame = new JFrame();
        this.helpScroll = new JScrollPane();
        this.jTextPane1 = new JTextPane();
        this.mailClient = new JFrame();
        this.messageBody = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.messageSubject = new JTextField();
        this.messageAddresses = new JTextField();
        this.jLabel23 = new JLabel();
        this.jLabel24 = new JLabel();
        this.sendButton = new JButton();
        this.mailPreferences = new JButton();
        this.addAttachmentButton = new JButton();
        this.deleteAttachment = new JButton();
        this.addRecip = new JTextField();
        this.jLabel29 = new JLabel();
        this.jLabel3 = new JLabel();
        this.mailPreferencesFrame = new JFrame();
        this.mailUserName = new JTextField();
        this.smtpHost = new JTextField();
        this.mailPassword = new JPasswordField();
        this.authenticationFlag = new JCheckBox();
        this.jLabel25 = new JLabel();
        this.jLabel26 = new JLabel();
        this.jLabel27 = new JLabel();
        this.saveMailPreferences = new JButton();
        this.yourEmailAddress = new JTextField();
        this.jLabel28 = new JLabel();
        this.messageWindow = new JFrame();
        this.jScrollPane4 = new JScrollPane();
        this.messageText = new JTextArea();
        this.spellChooser = new JFrame();
        this.jPanel1 = new JPanel();
        this.jRadioButton1 = new JRadioButton();
        this.custom = new JFrame();
        this.jLabel35 = new JLabel();
        this.hideEtmaSite = new JCheckBox();
        this.hideListTmas = new JCheckBox();
        this.hideOpenTmaFolder = new JCheckBox();
        this.hideTrainingSite = new JCheckBox();
        this.hideSavePt3 = new JCheckBox();
        this.hideECollectTmas = new JCheckBox();
        this.hideOpenTma = new JCheckBox();
        this.hideOpenCommentBank = new JCheckBox();
        this.hideBankComment = new JCheckBox();
        this.hideZipFiles = new JCheckBox();
        this.hideBackupEtmas = new JCheckBox();
        this.hideSendEmail = new JCheckBox();
        this.hideOpenPreferences = new JCheckBox();
        this.hideTestJs = new JCheckBox();
        this.closeCustomize = new JButton();
        this.hideListGrades = new JCheckBox();
        this.colorRemove = new JCheckBox();
        this.hideOpenReturnsFolder = new JCheckBox();
        this.jLabel36 = new JLabel();
        this.colorFrame1 = new JFrame();
        this.jColorChooser1 = new JColorChooser();
        this.colorWindowSelector = new JComboBox();
        this.defaultFlag = new JCheckBox();
        this.partScoresTable = new JFrame();
        this.jScrollPane5 = new JScrollPane();
        this.gradesSummaryTable1 = new JTable();
        this.exportGrades1 = new JButton();
        this.jPopupMenu1 = new JPopupMenu();
        this.jMenuItemUndo = new JMenuItem();
        this.jMenuItemRedo = new JMenuItem();
        this.jMenuItemCopy = new JMenuItem();
        this.jMenuItemPaste = new JMenuItem();
        this.jMenuItemSelectAll = new JMenuItem();
        this.jMenuItemCut = new JMenuItem();
        this.tutor_comments_area = new JScrollPane();
        this.tutor_comments_input = new JTextArea();
        this.fhiFileName = new JTextField();
        this.zipFiles = new JButton();
        this.tmaMarks = new JScrollPane();
        this.tmaScores = new JTable();
        this.ou_computer_user_name = new JTextField();
        this.personal_id = new JTextField();
        this.title = new JTextField();
        this.initials = new JTextField();
        this.forenames = new JTextField();
        this.surname = new JTextField();
        this.address_line1 = new JTextField();
        this.address_line2 = new JTextField();
        this.address_line3 = new JTextField();
        this.address_line4 = new JTextField();
        this.address_line5 = new JTextField();
        this.postcode = new JTextField();
        this.email_address = new JTextField();
        this.staff_id = new JTextField();
        this.loadXMLAlt = new JButton();
        this.checkTotals = new JButton();
        this.staff_title = new JTextField();
        this.staff_initials = new JTextField();
        this.staff_forenames = new JTextField();
        this.overall_grade_score = new JTextField();
        this.staff_surname = new JTextField();
        this.region_code = new JTextField();
        this.course_code = new JTextField();
        this.course_version_num = new JTextField();
        this.pres_code = new JTextField();
        this.assgnmt_suffix = new JTextField();
        this.e_tma_submission_num = new JTextField();
        this.score_update_allowed = new JTextField();
        this.max_assgnmt_score = new JTextField();
        this.zip_file = new JTextField();
        this.zip_date = new JTextField();
        this.late_submission_status = new JTextField();
        this.submission_status = new JTextField();
        this.marked_date = new JTextField();
        this.walton_received_date = new JTextField();
        this.e_tma_submission_date = new JTextField();
        this.total_question_count = new JTextField();
        this.permitted_question_count = new JTextField();
        this.tutor_comments = new JTextField();
        this.savePt3 = new JButton();
        this.openPreferences = new JButton();
        this.tmaList = new JComboBox();
        this.courseList = new JComboBox();
        this.studentList = new JComboBox();
        this.openTmaList = new JButton();
        this.subNo = new JComboBox();
        this.collectTmas = new JButton();
        this.openTmaFolder = new JButton();
        this.openTma = new JButton();
        this.etmaSite = new JButton();
        this.trainingSite = new JButton();
        this.jScrollPane2 = new JScrollPane();
        this.prevMarks = new JTable();
        this.listAllScores = new JButton();
        this.openReturnsFolder = new JButton();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jLabel6 = new JLabel();
        this.jLabel7 = new JLabel();
        this.jLabel8 = new JLabel();
        this.jLabel9 = new JLabel();
        this.jLabel10 = new JLabel();
        this.jLabel11 = new JLabel();
        this.jLabel12 = new JLabel();
        this.jLabel13 = new JLabel();
        this.jLabel14 = new JLabel();
        this.jLabel15 = new JLabel();
        this.jLabel16 = new JLabel();
        this.jLabel17 = new JLabel();
        this.jLabel18 = new JLabel();
        this.jLabel19 = new JLabel();
        this.openCommentBank = new JButton();
        this.bankComment = new JButton();
        this.lateSubmission = new JCheckBox();
        this.fontSize = new JComboBox();
        this.moreDetails = new JButton();
        this.jLabel22 = new JLabel();
        this.previousPt3s = new JComboBox();
        this.sendEmail = new JButton();
        this.checkSpellingButton = new JButton();
        this.backUp = new JButton();
        this.jLabel30 = new JLabel();
        this.jLabel31 = new JLabel();
        this.jLabel32 = new JLabel();
        this.jPanel2 = new JPanel();
        this.partScoresButton = new JButton();
        this.jMenuBar1 = new JMenuBar();
        this.File = new JMenu();
        this.preferencesMenu = new JMenuItem();
        this.savePt3MenuItem = new JMenuItem();
        this.collectTmasMenu = new JMenuItem();
        this.listTmasMenuItem = new JMenuItem();
        this.openTmaMenu = new JMenuItem();
        this.openTmaFolderMenu = new JMenuItem();
        final JMenuItem openReturnsFolderMenu = new JMenuItem();
        this.listGrades = new JMenuItem();
        this.printDoc = new JMenuItem();
        this.zipFilesMenu = new JMenuItem();
        this.exportMarksGrid = new JMenuItem();
        this.createFeedback = new JMenuItem();
        this.exitMenuItem = new JMenuItem();
        this.Edit = new JMenu();
        this.Undo = new JMenuItem();
        this.Redo = new JMenuItem();
        this.copyText = new JMenuItem();
        this.pasteText = new JMenuItem();
        this.selectAllText = new JMenuItem();
        this.cutText = new JMenuItem();
        this.jMenu1 = new JMenu();
        this.checkTotalsMenu = new JMenuItem();
        this.bankCommentMenuItem = new JMenuItem();
        this.openCommentBankMenu = new JMenuItem();
        this.checkSpelling = new JMenuItem();
        this.sendEmailMenu = new JMenuItem();
        this.backUpMenu = new JMenuItem();
        this.chooseColor = new JMenuItem();
        this.customize = new JMenuItem();
        this.distributeDocument = new JMenuItem();
        this.undoDistribution = new JMenuItem();
        this.comparePartScores = new JMenuItem();
        this.checkUpdates = new JMenuItem();
        this.Developer = new JMenuItem();
        this.testJsMenu = new JMenuItem();
        this.sites = new JMenu();
        this.etmaSiteMenu = new JMenuItem();
        this.trainingSiteMenu = new JMenuItem();
        this.help = new JMenu();
        this.etmaHandlerHelp = new JMenuItem();
        this.jMenuItemVersion = new JMenuItem();
        this.preferences.setTitle("Preferences");
        this.preferences.setName("Preferences");
        this.closePreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.closePreferences.setText("Close preferences");
        this.closePreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.closePreferencesActionPerformed(evt);
            }
        });
        this.setEtmasFolder.setBackground(new Color(255, 0, 0));
        this.setEtmasFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.setEtmasFolder.setText("Select etmas folder");
        this.setEtmasFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.setEtmasFolderActionPerformed(evt);
            }
        });
        this.etmasFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.etmasFolder.setText("etmas Folder");
        this.selectDownloadsFolder.setBackground(new Color(0, 153, 0));
        this.selectDownloadsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.selectDownloadsFolder.setText("Select Downloads Folder");
        this.selectDownloadsFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.selectDownloadsFolderActionPerformed(evt);
            }
        });
        this.downloadsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.downloadsFolder.setText("downloads Folder");
        this.createMarked.setBackground(new Color(0, 153, 0));
        this.createMarked.setFont(new Font("Lucida Grande", 0, 10));
        this.createMarked.setSelected(true);
        this.createMarked.setText("Create '-MARKED' copy of Student's script");
        this.createMarked.setToolTipText("<html>If this is ticked, the first time you open a student's script,<br> a copy will be created with 'MARKED' appended to the filename.<BR> \nYou can then mark this copy and leave the student's submission untouched.</html>");
        this.createMarked.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.createMarkedMouseReleased(evt);
            }
        });
        this.setTmaWeightings.setBackground(new Color(255, 255, 0));
        this.setTmaWeightings.setFont(new Font("Lucida Grande", 0, 10));
        this.setTmaWeightings.setText("Set TMA Weightings");
        this.setTmaWeightings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.setTmaWeightingsActionPerformed(evt);
            }
        });
        this.setCommentBankFile.setBackground(new Color(102, 102, 255));
        this.setCommentBankFile.setFont(new Font("Lucida Grande", 0, 10));
        this.setCommentBankFile.setText("Set commentbank file");
        this.setCommentBankFile.setToolTipText("<html>To use this, first create an empty text file, probably in your course folder <br>within your etmas folder. Then set the path to that file, using this button.</html>");
        this.setCommentBankFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.setCommentBankFileActionPerformed(evt);
            }
        });
        this.commentBankFile.setFont(new Font("Lucida Grande", 0, 10));
        this.commentBankFile.setText("comment bank file");
        this.setJsTestFile.setBackground(new Color(102, 102, 255));
        this.setJsTestFile.setFont(new Font("Lucida Grande", 0, 10));
        this.setJsTestFile.setText("Set Browser for Collecting TMAs");
        this.setJsTestFile.setToolTipText("<html>To use this, create a textfile, with extension .htm or .html, <br>probably in your course folder within your etmas folder, <br>then set the path to it using this button.</html>");
        this.setJsTestFile.setActionCommand("Set Browser for Collecting TMAs");
        this.setJsTestFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.setJsTestFileActionPerformed(evt);
            }
        });
        this.jsTestFile.setFont(new Font("Lucida Grande", 0, 10));
        this.jsTestFile.setText("jsTestFile");
        this.ouEtmaAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.ouEtmaAddress.setText("jTextField1");
        this.jLabel5.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel5.setForeground(new Color(255, 0, 0));
        this.jLabel5.setText("eTMA site:default:http://css3.open.ac.uk/etma/tutor");
        this.saveAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.saveAddress.setText("Save");
        this.saveAddress.setToolTipText("You would only need to use this button if the OU changes the address of the etma site on Tutor Home.");
        this.saveAddress.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.saveAddressActionPerformed(evt);
            }
        });
        this.autoFillFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.autoFillFlag.setText("Autofill marks");
        this.autoFillFlag.setToolTipText("<html>If this is ticked, all partscores will be set as maximum scores.<br> <B>Use with care!</B><html>");
        this.autoFillFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.autoFillFlagActionPerformed(evt);
            }
        });
        this.toolTipFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.toolTipFlag.setText("Turn on Tooltips");
        this.toolTipFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.toolTipFlagActionPerformed(evt);
            }
        });
        this.spellCheckFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.spellCheckFlag.setSelected(true);
        this.spellCheckFlag.setText("Activate live spellcheck");
        this.spellCheckFlag.setToolTipText("<html>If this is ticked, the comments box will be checked for spelling errors as you type. <br>You must select a valid dictionary using 'Select spellcheck dictionary' above.</html>");
        this.spellCheckFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.spellCheckFlagActionPerformed(evt);
            }
        });
        this.selectDictionary.setBackground(new Color(102, 102, 255));
        this.selectDictionary.setFont(new Font("Lucida Grande", 0, 10));
        this.selectDictionary.setText("Select spellcheck dictionary");
        this.selectDictionary.setToolTipText("<html>You must select a valid dictionary (enclosed with the package),<br> if you want to use the spellchecker.</html>");
        this.selectDictionary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.selectDictionaryActionPerformed(evt);
            }
        });
        this.dictionaryPath.setEditable(false);
        this.dictionaryPath.setFont(new Font("Lucida Grande", 0, 10));
        this.dictionaryPath.setText("dictionaryPath");
        this.wpSelect.setBackground(new Color(0, 153, 0));
        this.wpSelect.setFont(new Font("Lucida Grande", 0, 10));
        this.wpSelect.setText("Select Word Processor");
        this.wpSelect.setToolTipText("<html>Select the word processor you want to use to read<br> and mark students' scripts, or select 'System Default'</html>");
        this.wpSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.wpSelectActionPerformed(evt);
            }
        });
        this.wpPath.setEditable(false);
        this.wpPath.setFont(new Font("Lucida Grande", 0, 10));
        this.wpPath.setText("wpPath");
        this.browserSelect.setBackground(new Color(102, 102, 255));
        this.browserSelect.setFont(new Font("Lucida Grande", 0, 10));
        this.browserSelect.setText("Select Javatest Browser");
        this.browserSelect.setToolTipText("<html>Select the browser you want to use to test Javascripts.<br> The default is 'Firefox'.</html>");
        this.browserSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.browserSelectActionPerformed(evt);
            }
        });
        this.browserPath.setEditable(false);
        this.browserPath.setFont(new Font("Lucida Grande", 0, 10));
        this.browserPath.setText("browserPath");
        this.checkClosureFlag.setBackground(new Color(255, 102, 0));
        this.checkClosureFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.checkClosureFlag.setText("Check for open scripts before zipping ");
        this.checkClosureFlag.setToolTipText("<html>If this is ticked, a check will be carried out before zipping files<br> to see if there are any open scripts which might not have been saved<br>. It takes a few moments.</html>");
        this.checkClosureFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkClosureFlagActionPerformed(evt);
            }
        });
        this.launchTmaList.setBackground(new Color(0, 153, 0));
        this.launchTmaList.setFont(new Font("Lucida Grande", 0, 10));
        this.launchTmaList.setSelected(true);
        this.launchTmaList.setText("Open TMA List on launch");
        this.launchTmaList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.launchTmaListActionPerformed(evt);
            }
        });
        this.directEntryFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.directEntryFlag.setText("Enable direct entry of totals");
        this.directEntryFlag.setToolTipText("<html>Allows you to enter totals without the part scores.<br> Only check this on the <B>rare</B> occasions you need to do this, otherwise leave alone.</html>");
        this.directEntryFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.directEntryFlagActionPerformed(evt);
            }
        });
        this.doubleClickFlag.setBackground(new Color(0, 153, 0));
        this.doubleClickFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.doubleClickFlag.setSelected(true);
        this.doubleClickFlag.setText("Double-click to open PT3");
        this.doubleClickFlag.setToolTipText("<html>If you prefer to <B>double-click</B> the list of students<br> to open a PT3, tick this box. The default is single-click.</html>");
        this.doubleClickFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.doubleClickFlagActionPerformed(evt);
            }
        });
        this.addInitialsFlag.setBackground(new Color(0, 153, 0));
        this.addInitialsFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.addInitialsFlag.setText("Add initials to MARKED script");
        this.addInitialsFlag.setToolTipText("");
        this.addInitialsFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.addInitialsFlagActionPerformed(evt);
            }
        });
        this.showLatestFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.showLatestFlag.setText("Show only latest submissions");
        this.showLatestFlag.setToolTipText("<html>If ticked, only the latest submission from a student <br>for the selected TMA will be shown.</html>");
        this.showLatestFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.showLatestFlagActionPerformed(evt);
            }
        });
        this.startupScreenFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.startupScreenFlag.setText("Disable startup screen");
        this.startupScreenFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.startupScreenFlagActionPerformed(evt);
            }
        });
        this.suggestFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.suggestFlag.setSelected(true);
        this.suggestFlag.setText("Suggest corrections");
        this.suggestFlag.setToolTipText("<html>Tick this box if you want the live spellcheck to<br> suggest corrections for misspellings as you type.</html>");
        this.suggestFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.suggestFlagActionPerformed(evt);
            }
        });
        this.setAudioApp.setBackground(new Color(255, 255, 255));
        this.setAudioApp.setFont(new Font("Lucida Grande", 0, 10));
        this.setAudioApp.setText("Set Audio App");
        this.setAudioApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.setAudioAppActionPerformed(evt);
            }
        });
        this.audioPath.setEditable(false);
        this.audioPath.setFont(new Font("Lucida Grande", 0, 10));
        this.audioPath.setText("wpPath");
        this.autoMp3.setFont(new Font("Lucida Grande", 0, 10));
        this.autoMp3.setText("Auto open MP3");
        this.autoMp3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.autoMp3ActionPerformed(evt);
            }
        });
        this.setCommentBankEditor.setBackground(new Color(255, 255, 255));
        this.setCommentBankEditor.setFont(new Font("Lucida Grande", 0, 10));
        this.setCommentBankEditor.setText("Set commentbank editor");
        this.setCommentBankEditor.setToolTipText("<html>To use this, select the text editor you want to use<br> to manage your comment bank file<br> (eg TextEdit, GEdit or NotePad)</html>");
        this.setCommentBankEditor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.setCommentBankEditorActionPerformed(evt);
            }
        });
        this.commentBankEd.setFont(new Font("Lucida Grande", 0, 10));
        this.commentBankEd.setText("comment bank editor");
        this.sizeWarnFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.sizeWarnFlag.setSelected(true);
        this.sizeWarnFlag.setText("Warn if student's folder is large");
        this.sizeWarnFlag.setToolTipText("<html>Tick this box if you want the program to warn you<br> if the student's folder is over about 8MB</html>");
        this.sizeWarnFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.sizeWarnFlagActionPerformed(evt);
            }
        });
        this.autoImportFlag1.setFont(new Font("Lucida Grande", 0, 10));
        this.autoImportFlag1.setText("AutoImport");
        this.autoImportFlag1.setToolTipText("<html>If ticked, downloaded eTMA folder will be imported without further intervention.<br>\n Browser must be set to autounzip, and the downloads folder must be set in the filehandler preferences <br>\n(second top button) to your browser's download location.</html>");
        this.autoImportFlag1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.autoImportFlag1ActionPerformed(evt);
            }
        });
        this.globalFonts.setFont(new Font("Lucida Grande", 0, 10));
        this.globalFonts.setText("Global fontsize");
        this.globalFonts.setToolTipText("If ticked, changes to the font size will affect both the comments box and a number of other components.");
        this.globalFonts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.globalFontsActionPerformed(evt);
            }
        });
        this.checkReturnsFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.checkReturnsFlag.setText("Check returns folder for old zipped files on launch");
        this.checkReturnsFlag.setToolTipText("<html>If ticked, downloaded eTMA folder will be imported without further intervention. <br>Browser must be set to autounzip, \nand the downloads folder<br> must be set in the filehandler preferences<br> (second top button) to your browser's download location.</html>");
        this.checkReturnsFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkReturnsFlagActionPerformed(evt);
            }
        });
        final GroupLayout preferencesLayout = new GroupLayout(this.preferences.getContentPane());
        this.preferences.getContentPane().setLayout((LayoutManager)preferencesLayout);
        preferencesLayout.setHorizontalGroup((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.setCommentBankEditor, -1, -1, 32767).add(413, 413, 413)).add(2, (GroupLayout.Group)preferencesLayout.createSequentialGroup().add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.setAudioApp, -2, 169, -2).add(18, 18, 18).add((Component)this.audioPath, -2, 162, -2)).add((Component)this.setTmaWeightings, -2, 169, -2)).addPreferredGap(0, -1, 32767).add((Component)this.autoMp3).add(109, 109, 109)).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((GroupLayout.Group)preferencesLayout.createParallelGroup(2, false).add(1, (Component)this.selectDictionary, -1, -1, 32767).add(1, (Component)this.selectDownloadsFolder, -1, -1, 32767).add(1, (Component)this.setCommentBankFile, -1, -1, 32767).add(1, (Component)this.setEtmasFolder, -1, -1, 32767).add(1, (Component)this.setJsTestFile, -1, -1, 32767).add(1, (Component)this.wpSelect, -1, -1, 32767).add(1, (Component)this.browserSelect, -2, 178, -2)).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(15, 15, 15).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1, false).add((Component)this.dictionaryPath).add((Component)this.browserPath).add((Component)this.wpPath).add((Component)this.etmasFolder).add((Component)this.commentBankFile).add((Component)this.downloadsFolder).add((Component)this.jsTestFile).add((Component)this.commentBankEd, -2, 304, -2))).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(18, 18, 18).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((Component)this.jLabel5).add((Component)this.ouEtmaAddress, -2, 216, -2)).addPreferredGap(0).add((Component)this.saveAddress)))).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(2).add(1, (Component)this.autoFillFlag).add(1, (Component)this.addInitialsFlag).add(1, (Component)this.spellCheckFlag).add((Component)this.suggestFlag)).add((Component)this.toolTipFlag)).addPreferredGap(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1, false).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.sizeWarnFlag).add(18, 18, 18).add((Component)this.autoImportFlag1).add(18, 18, 18).add((Component)this.globalFonts)).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add(2, (GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.directEntryFlag).add(38, 38, 38).add((Component)this.showLatestFlag)).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.createMarked).addPreferredGap(0).add((Component)this.closePreferences)).add(2, (GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.launchTmaList).add(69, 69, 69).add((Component)this.doubleClickFlag)).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.checkClosureFlag).addPreferredGap(0).add((Component)this.startupScreenFlag)))))).add((Component)this.checkReturnsFlag)).add((Component)this.jLabel37).addContainerGap(831, 32767)));
        preferencesLayout.setVerticalGroup((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(510, 510, 510).add((Component)this.jLabel37).addContainerGap(-1, 32767)).add(2, (GroupLayout.Group)preferencesLayout.createSequentialGroup().add(0, 34, 32767).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.etmasFolder, -2, -1, -2).add((Component)this.setEtmasFolder)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.downloadsFolder, -2, -1, -2).add((Component)this.selectDownloadsFolder)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.commentBankFile, -2, -1, -2).add((Component)this.setCommentBankFile)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.setJsTestFile).add((Component)this.jsTestFile, -2, -1, -2)).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.selectDictionary).add((Component)this.dictionaryPath, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.wpPath, -2, -1, -2).add((Component)this.wpSelect)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.browserPath, -2, -1, -2).add((Component)this.browserSelect)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.setCommentBankEditor).add((Component)this.commentBankEd, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((Component)this.setAudioApp).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.audioPath, -2, -1, -2).add((Component)this.autoMp3))).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.jLabel5)).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(19, 19, 19).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.setTmaWeightings).add((Component)this.ouEtmaAddress, -2, -1, -2).add((Component)this.saveAddress)))).add(10, 10, 10).add((Component)this.checkReturnsFlag).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.checkClosureFlag).add((Component)this.startupScreenFlag).add((Component)this.toolTipFlag)).add(8, 8, 8).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.autoFillFlag).add((Component)this.doubleClickFlag).add((Component)this.launchTmaList)).add(8, 8, 8).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.directEntryFlag).add((Component)this.addInitialsFlag).add((Component)this.showLatestFlag)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.createMarked).add((Component)this.spellCheckFlag).add((Component)this.closePreferences)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.suggestFlag).add((Component)this.sizeWarnFlag).add((Component)this.autoImportFlag1).add((Component)this.globalFonts))));
        this.submittedTmas.setTitle("Current TMAs");
        this.submittedTmas.setFocusTraversalPolicyProvider(true);
        this.submittedTmas.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                EtmaHandlerJ.this.submittedTmasWindowClosing(evt);
            }
        });
        this.jScrollPane1.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        this.jScrollPane1.setHorizontalScrollBarPolicy(31);
        this.jScrollPane1.setAlignmentX(10.0f);
        this.jScrollPane1.setAlignmentY(10.0f);
        this.jScrollPane1.setCursor(new Cursor(0));
        this.jScrollPane1.setFont(new Font("Lucida Grande", 0, 10));
        this.jScrollPane1.setMinimumSize(new Dimension(1200, 600));
        this.jScrollPane1.setPreferredSize(new Dimension(1200, 600));
        this.jScrollPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.jScrollPane1MouseReleased(evt);
            }
        });
        this.listOfTmas.setBackground(new Color(204, 255, 255));
        this.listOfTmas.setFont(new Font("Lucida Grande", 0, 11));
        this.listOfTmas.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null } }, new String[] { "PI", "Forenames", "Surname", "Oucu", "Score", "Status", "Sub", "Cse", "Pres", "TMA", "Date Submitted", "Zip" }) {
            Class[] types = { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class };
            boolean[] canEdit = { false, false, false, false, false, false, false, false, false, false, false, true };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.listOfTmas.setAutoResizeMode(0);
        this.listOfTmas.setAutoscrolls(false);
        this.listOfTmas.setFocusTraversalPolicyProvider(true);
        this.listOfTmas.setMaximumSize(new Dimension(1050, 2000));
        this.listOfTmas.setMinimumSize(new Dimension(700, 2000));
        this.listOfTmas.setOpaque(false);
        this.listOfTmas.setPreferredSize(new Dimension(750, 2000));
        this.listOfTmas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.listOfTmasMouseReleased(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.listOfTmas);
        this.batchZip.setBackground(new Color(255, 102, 0));
        this.batchZip.setFont(new Font("Lucida Grande", 0, 10));
        this.batchZip.setText("Batch Zip");
        this.batchZip.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.batchZipActionPerformed(evt);
            }
        });
        this.totalTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.totalTmas.setText("jTextField1");
        this.toBeMarkedTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.toBeMarkedTmas.setText("jTextField1");
        this.jLabel20.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel20.setText("Number of TMAs");
        this.jLabel21.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel21.setText("To be marked");
        this.tmaSelectMenu.setFont(new Font("Lucida Grande", 0, 10));
        this.tmaSelectMenu.setModel(new DefaultComboBoxModel(new String[] { "All", "Unmarked", "Marked", "Zipped" }));
        this.tmaSelectMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.tmaSelectMenuActionPerformed(evt);
            }
        });
        this.missingSubmissions.setFont(new Font("Lucida Grande", 0, 10));
        this.missingSubmissions.setText("Missing submissions");
        this.missingSubmissions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.missingSubmissionsActionPerformed(evt);
            }
        });
        this.printTmaList.setFont(new Font("Lucida Grande", 0, 10));
        this.printTmaList.setText("Print list");
        this.printTmaList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.printTmaListActionPerformed(evt);
            }
        });
        this.jLabel34.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel34.setForeground(new Color(255, 51, 51));
        this.jLabel34.setText("Click on any row to open that PT3; click on any heading to sort by that column.");
        this.selectAll.setBackground(new Color(255, 102, 0));
        this.selectAll.setFont(new Font("Lucida Grande", 0, 10));
        this.selectAll.setText("Select all/none to Zip");
        this.selectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.selectAllActionPerformed(evt);
            }
        });
        this.highlightUnmarked.setFont(new Font("Lucida Grande", 0, 10));
        this.highlightUnmarked.setText("Highlight unmarked TMAs");
        this.highlightUnmarked.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.highlightUnmarkedActionPerformed(evt);
            }
        });
        final GroupLayout submittedTmasLayout = new GroupLayout(this.submittedTmas.getContentPane());
        this.submittedTmas.getContentPane().setLayout((LayoutManager)submittedTmasLayout);
        submittedTmasLayout.setHorizontalGroup((GroupLayout.Group)submittedTmasLayout.createParallelGroup(1).add((GroupLayout.Group)submittedTmasLayout.createSequentialGroup().add(31, 31, 31).add((GroupLayout.Group)submittedTmasLayout.createParallelGroup(1).add((GroupLayout.Group)submittedTmasLayout.createSequentialGroup().add((GroupLayout.Group)submittedTmasLayout.createParallelGroup(1).add((Component)this.jLabel34).add((Component)this.highlightUnmarked)).add(164, 164, 164).add((Component)this.selectAll)).add((GroupLayout.Group)submittedTmasLayout.createSequentialGroup().add(47, 47, 47).add((Component)this.jLabel20).addPreferredGap(0).add((Component)this.totalTmas, -2, 30, -2).addPreferredGap(1).add((Component)this.jLabel21).add(18, 18, 18).add((Component)this.toBeMarkedTmas, -2, 24, -2).add(35, 35, 35).add((Component)this.tmaSelectMenu, -2, 100, -2).add(18, 18, 18).add((Component)this.missingSubmissions).addPreferredGap(1).add((Component)this.printTmaList).addPreferredGap(0).add((Component)this.batchZip)).add((Component)this.jScrollPane1, -2, 778, -2)).addContainerGap(1108, 32767)));
        submittedTmasLayout.setVerticalGroup((GroupLayout.Group)submittedTmasLayout.createParallelGroup(1).add((GroupLayout.Group)submittedTmasLayout.createSequentialGroup().add(45, 45, 45).add((GroupLayout.Group)submittedTmasLayout.createParallelGroup(2).add((Component)this.selectAll).add((GroupLayout.Group)submittedTmasLayout.createSequentialGroup().add((Component)this.highlightUnmarked).addPreferredGap(0).add((Component)this.jLabel34))).addPreferredGap(0).add((Component)this.jScrollPane1, -2, 400, -2).addPreferredGap(0).add((GroupLayout.Group)submittedTmasLayout.createParallelGroup(3).add((Component)this.totalTmas, -2, -1, -2).add((Component)this.jLabel20).add((Component)this.jLabel21).add((Component)this.toBeMarkedTmas, -2, -1, -2).add((Component)this.tmaSelectMenu, -2, 22, -2).add((Component)this.missingSubmissions).add((Component)this.printTmaList).add((Component)this.batchZip)).addContainerGap(145, 32767)));
        this.gradesSummaryTable.setFont(new Font("Lucida Grande", 0, 10));
        this.gradesSummaryTable.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null } }, new String[] { "PID", "Forenames", "Surname", "oucu", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "Ave" }) {
            Class[] types = { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Double.class };
            boolean[] canEdit = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.gradesSummaryTable.setGridColor(new Color(102, 153, 255));
        this.gradesSummaryTable.setRowSelectionAllowed(false);
        this.gradesSummaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.gradesSummaryTableMouseReleased(evt);
            }
        });
        this.jScrollPane3.setViewportView(this.gradesSummaryTable);
        this.printGrades.setFont(new Font("Lucida Grande", 0, 10));
        this.printGrades.setText("Print Grades");
        this.printGrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.printGradesActionPerformed(evt);
            }
        });
        this.jLabel33.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel33.setForeground(new Color(255, 0, 0));
        this.jLabel33.setText("Click on any mark to open the corresponding PT3; click on any heading to sort by that column.");
        this.exportGrades.setFont(new Font("Lucida Grande", 0, 10));
        this.exportGrades.setText("Export Grades");
        this.exportGrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.exportGradesActionPerformed(evt);
            }
        });
        this.ignoreCurrentTma.setFont(new Font("Lucida Grande", 0, 10));
        this.ignoreCurrentTma.setText("Show averages without current TMA");
        this.ignoreCurrentTma.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.ignoreCurrentTmaActionPerformed(evt);
            }
        });
        final GroupLayout gradesSummaryLayout = new GroupLayout(this.gradesSummary.getContentPane());
        this.gradesSummary.getContentPane().setLayout((LayoutManager)gradesSummaryLayout);
        gradesSummaryLayout.setHorizontalGroup((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(1).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(1).add(2, (Component)this.jScrollPane3, -1, 809, 32767).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(2).add((Component)this.ignoreCurrentTma).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(2).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add((Component)this.jLabel33).add(119, 119, 119)).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add((Component)this.exportGrades).addPreferredGap(1))).add((Component)this.printGrades))))).addContainerGap()));
        gradesSummaryLayout.setVerticalGroup((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(1).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add(38, 38, 38).add((Component)this.jScrollPane3, -1, 263, 32767).addPreferredGap(0).add((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(2).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add((Component)this.jLabel33).addPreferredGap(0, 28, 32767)).add((GroupLayout.Group)gradesSummaryLayout.createSequentialGroup().add((GroupLayout.Group)gradesSummaryLayout.createParallelGroup(3).add((Component)this.exportGrades).add((Component)this.printGrades)).addPreferredGap(0))).add((Component)this.ignoreCurrentTma).addContainerGap()));
        this.weightings.setDefaultCloseOperation(0);
        this.weightings.setTitle("TMA Weightings");
        this.weightings.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                EtmaHandlerJ.this.weightingsWindowClosing(evt);
            }
        });
        this.weightingsTable1.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable1.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Weightings for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.weightingsTable1.setGridColor(new Color(0, 51, 255));
        this.weightingsTable1.setSelectionBackground(new Color(255, 255, 255));
        this.weightingsTable1.setSelectionForeground(new Color(0, 0, 0));
        this.weightings1.setViewportView(this.weightingsTable1);
        this.weightingsTable2.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable2.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Weightings for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.weightingsTable2.setGridColor(new Color(0, 51, 255));
        this.weightingsTable2.setSelectionBackground(new Color(255, 255, 255));
        this.weightingsTable2.setSelectionForeground(new Color(0, 0, 0));
        this.weightings2.setViewportView(this.weightingsTable2);
        this.weightingsTable3.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable3.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Weightings for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.weightingsTable3.setGridColor(new Color(0, 51, 255));
        this.weightingsTable3.setSelectionBackground(new Color(255, 255, 255));
        this.weightingsTable3.setSelectionForeground(new Color(0, 0, 0));
        this.weightings3.setViewportView(this.weightingsTable3);
        this.weightingsHeaders.setVerticalScrollBarPolicy(21);
        this.tmaNumbers.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.blue));
        this.tmaNumbers.setFont(new Font("Lucida Grande", 0, 10));
        this.tmaNumbers.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.tmaNumbers.setGridColor(new Color(0, 51, 255));
        this.tmaNumbers.setSelectionBackground(new Color(255, 255, 255));
        this.tmaNumbers.setSelectionForeground(new Color(0, 0, 0));
        this.weightingsHeaders.setViewportView(this.tmaNumbers);
        this.saveWeightings.setFont(new Font("Lucida Grande", 0, 10));
        this.saveWeightings.setText("Save");
        this.saveWeightings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.saveWeightingsActionPerformed(evt);
            }
        });
        this.weightingsTable4.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable4.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Weightings for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.weightingsTable4.setGridColor(new Color(0, 51, 255));
        this.weightingsTable4.setSelectionBackground(new Color(255, 255, 255));
        this.weightingsTable4.setSelectionForeground(new Color(0, 0, 0));
        this.weightings4.setViewportView(this.weightingsTable4);
        this.maxScoreTable1.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        this.maxScoreTable1.setFont(new Font("Lucida Grande", 0, 10));
        this.maxScoreTable1.setForeground(Color.red);
        this.maxScoreTable1.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Max score for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.maxScoreTable1.setGridColor(new Color(0, 51, 255));
        this.maxScoreTable1.setSelectionBackground(new Color(255, 255, 255));
        this.maxScoreTable1.setSelectionForeground(new Color(0, 0, 0));
        this.maxScores1.setViewportView(this.maxScoreTable1);
        this.maxScoreTable2.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        this.maxScoreTable2.setFont(new Font("Lucida Grande", 0, 10));
        this.maxScoreTable2.setForeground(Color.red);
        this.maxScoreTable2.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Max Score for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.maxScoreTable2.setGridColor(new Color(0, 51, 255));
        this.maxScoreTable2.setSelectionBackground(new Color(255, 255, 255));
        this.maxScoreTable2.setSelectionForeground(new Color(0, 0, 0));
        this.maxScores2.setViewportView(this.maxScoreTable2);
        this.maxScoreTable3.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        this.maxScoreTable3.setFont(new Font("Lucida Grande", 0, 10));
        this.maxScoreTable3.setForeground(Color.red);
        this.maxScoreTable3.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Max Score for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.maxScoreTable3.setGridColor(new Color(0, 51, 255));
        this.maxScoreTable3.setSelectionBackground(new Color(255, 255, 255));
        this.maxScoreTable3.setSelectionForeground(new Color(0, 0, 0));
        this.maxScores3.setViewportView(this.maxScoreTable3);
        this.maxScoreTable4.setBorder(BorderFactory.createLineBorder(Color.red, 2));
        this.maxScoreTable4.setFont(new Font("Lucida Grande", 0, 10));
        this.maxScoreTable4.setForeground(Color.red);
        this.maxScoreTable4.setModel(new DefaultTableModel(new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null } }, new String[] { "Max Score for" }) {
            Class[] types = { String.class };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.maxScoreTable4.setGridColor(new Color(0, 51, 255));
        this.maxScoreTable4.setSelectionBackground(new Color(255, 255, 255));
        this.maxScoreTable4.setSelectionForeground(new Color(0, 0, 0));
        this.maxScores4.setViewportView(this.maxScoreTable4);
        this.jTextArea2.setColumns(20);
        this.jTextArea2.setRows(5);
        this.jTextArea2.setText("If the maximum score for any assignment\nis not 100, enter the course code carefully \nat the top of a \"Max score for\" area and enter \nthe maximum score against the appropriate\nTMA number. You don't need to enter any 100s ");
        this.jScrollPane6.setViewportView(this.jTextArea2);
        this.jTextArea3.setColumns(20);
        this.jTextArea3.setRows(5);
        this.jTextArea3.setText("If all assignments aren't equally weighted\n enter the course code carefully (eg TU100-12B)\nat the top of a \"Weightings for\" area and enter \neach weighting against the appropriate\nTMA number. Blanks are treated as zeros.\nYou can also enter the overall pass mark.");
        this.jScrollPane7.setViewportView(this.jTextArea3);
        final GroupLayout weightingsLayout = new GroupLayout(this.weightings.getContentPane());
        this.weightings.getContentPane().setLayout((LayoutManager)weightingsLayout);
        weightingsLayout.setHorizontalGroup((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add(2, (GroupLayout.Group)weightingsLayout.createSequentialGroup().addContainerGap().add((Component)this.weightingsHeaders, -2, 58, -2).addPreferredGap(0).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add(2, (GroupLayout.Group)weightingsLayout.createSequentialGroup().add((Component)this.weightings1, -2, 101, -2).addPreferredGap(0)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(21, 21, 21).add((Component)this.saveWeightings).add(15, 15, 15))).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1, false).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.maxScores1, -2, 101, -2).addPreferredGap(0).add((Component)this.weightings2, -2, 101, -2).addPreferredGap(0).add((Component)this.maxScores2, -2, 101, -2)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(3, 3, 3).add((Component)this.jScrollPane7))).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(10, 10, 10).add((Component)this.weightings3, -2, 101, -2).addPreferredGap(0).add((Component)this.maxScores3, -2, 101, -2).addPreferredGap(0).add((Component)this.weightings4, -2, 101, -2).addPreferredGap(0).add((Component)this.maxScores4, -2, 101, -2)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(18, 18, 18).add((Component)this.jScrollPane6, -2, 323, -2))).add(648, 648, 648)));
        weightingsLayout.setVerticalGroup((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(55, 55, 55).add((GroupLayout.Group)weightingsLayout.createParallelGroup(2, false).add(1, (Component)this.weightings1, -1, 260, 32767).add((Component)this.weightingsHeaders, -2, 245, -2)).add(7, 7, 7)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(57, 57, 57).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((Component)this.maxScores1, -1, 265, 32767).add((Component)this.weightings2, -1, 265, 32767).add(2, (Component)this.maxScores2, -1, 265, 32767).add((Component)this.weightings3, -1, 265, 32767).add((Component)this.maxScores4, -1, 265, 32767).add((Component)this.weightings4, -1, 265, 32767).add((Component)this.maxScores3, -1, 265, 32767)))).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.saveWeightings)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().addPreferredGap(1).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((Component)this.jScrollPane6, -2, -1, -2).add((Component)this.jScrollPane7, -2, 106, -2)))).add(1072, 1072, 1072)));
        this.helpFrame.setTitle("Help");
        this.jTextPane1.setEditable(false);
        this.jTextPane1.setFont(new Font("Lucida Grande", 0, 10));
        this.jTextPane1.setText("Welcome to the Mac FileHandler (Java Version)!\nGETTING STARTED:\n\n(Please note that this program has at present been tested on MAC OS 10.4.4 (Tiger) only\n\nIMPORTANT: Although the setup procedures are, I hope, reasonably straightforward, you are strongly advised to thoroughly test your setup before using it \"live\", either with dummy TMAs from students, or with TMAs from the eTMA training site. See also the disclaimer below. If you get error messages, or something just doesn't work, it will almost certainly be because you haven't set the preferences correctly!\n\nIt is also worthwhile (at least initially) after zipping the files for final return to unzip a copy first to check that it does contain what you expect! There may be a problem if the students TMA file(s) are themselves within extra nested folders  take particular care in this situation. \n\n\n1. \tCreate (or use an existing) an empty folder called \"etmas\" without the quotes, somewhere on your hard disk (or your local iDisk - that should also work!). Alternatively, to try out the package, move the supplied etmas folder to somewhere convenient and use that, at least initially. In this case, go to step 4, and miss out step 5 . Step 5 is only required once you have downloaded more TMAs from the OU site and want to add them to the etmas folder.\n\n2.\tInside this folder create a folder called \"returns\" (if you forget to do this, one will be created the first time you try to save and zip files to return!)\n\n3.\tDownload eTMAs from the OU site, ensuring that the file setting is  \"Standard Zip\". Set zipped files to be automatically expanded with Stuffit Expander or an equivalent, or unzip manually.\n\n4.\tLaunch eTMAHandlerJ.  Select the File:Preferences menu and the \"etmas folder\"  tag.  Navigate to your \"etmas\" folder (not any TMA folders inside it) and select \"Choose.\n\nNext, using \"Select Downloads Folder\", select your folder for files you will be downloading from the OU website  (this will generally be wherever your browser downloads usually go - IMPORTANT: do not set it to, or anywhere inside, your etmas folder. You can set other preferences now, or come back to them later.\n\n5.\tRelaunch, click the button \"Insert TMAs\" , and navigate to your downloaded TMA files folder (the complete folder, not one of the internal ones - it should begin with the course code. DON'T select any of the folders inside the complete folder - this can cause chaos!). New TMAs should now have been automagically transferred into the appropriate places in the \"etmas\" folder you created earlier.  Unless you know what you're doing, don't manually move files into your \"etmas\" folder  this should have happened automatically!\n\n6.\tSelect the course number, and then the TMA number from the pull down menu. Then click on the List TMAs button, and, if you've set up correctly, a summary window should appear with details of all students who have submitted the selected TMA.  Click on the appropriate name in the TMA List window and the PT3 for that student will be displayed. You can then open and mark the assignment itself (see step 7), enter the marks and comments and save the PT3 and the marked or part-marked file(s) for later zipping.\n\nYou can enter part scores within a question part in the form \"1+3+4\". On leaving the selected cell  the string will be replaced by the total (in this case 8). This will happen anyway on saving the PT3.\n\nYou can move between cells of the scores grid using the up- and down-arrow keys \n\n7. The first time you select the student's file (probably a .doc or .rtf), a duplicate file in the same folder will be created with \"-MARKED\" appended to the title.  This will be the one that will be opened at that point ; the original file will remain untouched.  You can disable this feature in the Preferences if you wish. On future occasions you will be given the option of selecting a different file if you wish. If you want to duplicate the file again (eg as backup), you could use \"Save as...\" , or alternatively quit the program and navigate to the appropriate folder  dont use a filename which includes the word MARKED. The students' files open with your default text-editor (probably Word).\n\n8.\tThe link to the OU etmas site is set by default to http://css3.open.ac.uk/etma/tutor/ . If necessary, this can be changed in the General preferences.\n\n9.\tSaving and zipping: Use the \"Save PT3\" button frequently to avoid data loss.  The \"Check Totals\" button will probably not be needed but can be used at any stage if you suspect something has gone wrong with the auto-calculation of totals. There are two ways of zipping;\na)\tClicking the \"Save and Zip\" button will create a new zipped file for the currently open PT3. There will also be the option to add to existing zip file, but this is not undoable, ie individual students files within the existing zip file can't easily be amended once you've done this.\nb) \tOn the open \"List TMAs\" window,  click on the \"Zip\" in the last columns and use shift-click or command-click to select the students whose files you wish to be zipped. Then press the \"Batch zip\" button. The routine does not save the currently open PT3, so ensure that this, and any associated student's files, have been saved first. \n\n10.\tThere is a facility for setting up and accessing an item bank of comments. Create a  blank file using your text editor (eg TextEdit or NotePad) anywhere, but probably in the root 'etmas' folder for convenience. Call it what you like, and set the path to it in the preferences. Then any text copied to the clipboard will be added to that file if you press the Bank Comment button, and can be accessed by pressing Open Comment Bank. The Test JS button operates similarly  select the students coding in the TMA script, copy, and press the button. Smart quotes in Word, which interfere with Javascript, will be replaced by straight ones in the text (but the students copy will remain untouched). If you are editing/debugging the copied script in Text Edit, remember to \"select\" and \"Copy\" it all again before you test it in the browser (the routine operates with data from the clipboard!\n\n11.\tPrevious scores for the current student are automatically shown. If you enter in the appropriate TMA weightings in the preferences, the weighted average for all TMAs submitted, including the current one,  will be displayed in the far right hand cell.  The average is based on all the TMAs expected to be submitted to date (non-submissions are counted as zero). As soon as the next TMA is submitted by one student (ie the TMA folder is created for that TMA) this becomes the \"expected number\" of TMAs, so other students averages will be depressed(!) until they have also been given a mark for that TMA.\n\n12.   \tYou can also list all the grades for your students using the \"Grades summary\" button. If you enter the overall pass mark in the bottom cell of the TMA weightings preferences, any students currently scoring less than the pass mark will be asterisked \"**\" and highlighted.  You can also click on any assignment score to open the  corresponding PT3 info.\n\n13.\tMissing Submissions: this is a relatively new enhancement of the List TMAs window, which will compare the current list of submitted TMAs with your OU student list and indicate any students yet to submit.  You should first download (if you havent already!) the student list from your Student info web page  it will be a file beginning with extract and  ending in .csv. This file is probably best kept in the respective course folder of your etmas file, but in fact can be kept anywhere. Then click the Missing Submissions button on the List TMAs page, and select the appropriate downloaded student file when prompted (you should only have to do this the first time). If all goes well, a list of missing students for that TMA will be displayed. You can replace the file (removing the previous one) if you download a new list, eg following student withdrawals or additions. If the file name is different you will be asked to select it the first time you use the Missing Submissions button.\n\nOther functions are, I hope, self-explanatory or obvious!  \n\nGood luck! Comments questions and suggestions most welcome!\nMike Hay (mike@hayfamily.co.uk) Tutor Number 00516109\n\n\nTroubleshooting:\nA.\tMake sure you have selected the entire etmas folder in the etmas preference tab, not any of the contained folders. The file path in that tab should end in etmas:. Also make sure that you have not set the \"Downloaded TMAs\" to your \"etmas\" folder\nB.\tSimilarly, make sure when moving downloaded TMAs to the etmas file that you select the entire downloaded (fully unzipped) folder (the name should begin with the course code). If something seems to have gone wrong, try removing the appropriate files from the etmas folder, rename the downloaded file back to its original name and try again!\nC.\tThe program caters for 60 entries in the marks grid, including the question totalsI hope this is adequate.\nD.\t Double-check that your folder preferences are set correctly. I suggest  that on a Mac initially you use the hard disk for all your storage rather than the iDisk until youre clear what youre doing.\n\nE.\tThe OU has now slightly changed the way it suggests users structure their folders. They use a downloads folder within the etmas folder  my program does not. So when using the Filehandler, all the course etma folders should appear immediately within the etmas folder, not within any subfolders. If you use the Collect TMAs button, this should happen automatically, but be aware that this is slightly different from the format recommended in the latest eTMA Training Manual.  Any reference within this ReadMe to downloads therefore refers to wherever your browser puts downloaded files. \n\n\nIMPORTANT DISCLAIMER: this program has been produced by me for my own convenience, but is offered freely to OU tutors to mark eTMAs. It is offered \"as is\", and you use it entirely at your own risk. I take no responsibility whatsoever for any loss or damage however caused, or any consequential loss or damage to Open University systems, or to students' systems or progress with their studies. It is not supported or recommended in any way by the Open University. Your attention is drawn again to the importance of initial testing, either with students' dummy TMAs, or using TMAs from the eTMA training site.\n\nMJHay 29 July 2007\n\n\n\n\n\n\n\n\n");
        this.helpScroll.setViewportView(this.jTextPane1);
        final GroupLayout helpFrameLayout = new GroupLayout(this.helpFrame.getContentPane());
        this.helpFrame.getContentPane().setLayout((LayoutManager)helpFrameLayout);
        helpFrameLayout.setHorizontalGroup((GroupLayout.Group)helpFrameLayout.createParallelGroup(1).add((GroupLayout.Group)helpFrameLayout.createSequentialGroup().add(54, 54, 54).add((Component)this.helpScroll, -2, 443, -2).addContainerGap(118, 32767)));
        helpFrameLayout.setVerticalGroup((GroupLayout.Group)helpFrameLayout.createParallelGroup(1).add(2, (GroupLayout.Group)helpFrameLayout.createSequentialGroup().add(31, 31, 31).add((Component)this.helpScroll, -1, 397, 32767).addContainerGap()));
        this.mailClient.setTitle("Mail Client");
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setFont(new Font("Lucida Grande", 0, 10));
        this.jTextArea1.setRows(5);
        this.messageBody.setViewportView(this.jTextArea1);
        this.messageSubject.setFont(new Font("Lucida Grande", 0, 10));
        this.messageAddresses.setEditable(false);
        this.messageAddresses.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel23.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel23.setText("To:");
        this.jLabel24.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel24.setText("Subject:");
        this.sendButton.setFont(new Font("Lucida Grande", 0, 10));
        this.sendButton.setText("Send");
        this.sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.sendButtonActionPerformed(evt);
            }
        });
        this.mailPreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.mailPreferences.setText("Mail preferences");
        this.mailPreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.mailPreferencesActionPerformed(evt);
            }
        });
        this.addAttachmentButton.setFont(new Font("Lucida Grande", 0, 10));
        this.addAttachmentButton.setText("Add attachment");
        this.addAttachmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.addAttachmentButtonActionPerformed(evt);
            }
        });
        this.deleteAttachment.setFont(new Font("Lucida Grande", 0, 10));
        this.deleteAttachment.setText("Remove attachment");
        this.deleteAttachment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.deleteAttachmentActionPerformed(evt);
            }
        });
        this.addRecip.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel29.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel29.setText("Additional Recipient:");
        this.jLabel3.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel3.setForeground(new Color(255, 0, 0));
        this.jLabel3.setText("(A copy will be sent to you automatically)");
        final GroupLayout mailClientLayout = new GroupLayout(this.mailClient.getContentPane());
        this.mailClient.getContentPane().setLayout((LayoutManager)mailClientLayout);
        mailClientLayout.setHorizontalGroup((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)mailClientLayout.createParallelGroup(1, false).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().add((Component)this.jLabel23).addPreferredGap(0).add((Component)this.messageAddresses)).add((Component)this.messageBody).add(2, (GroupLayout.Group)mailClientLayout.createSequentialGroup().add((Component)this.jLabel24).add(27, 27, 27).add((Component)this.messageSubject)).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().add((Component)this.jLabel29).addPreferredGap(0).add((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((Component)this.jLabel3).add((Component)this.addRecip, -2, 347, -2)))).add(15, 15, 15).add((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((Component)this.addAttachmentButton).add((Component)this.sendButton).add((Component)this.deleteAttachment).add((Component)this.mailPreferences)).addContainerGap(462, 32767)));
        mailClientLayout.setVerticalGroup((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)mailClientLayout.createParallelGroup(1, false).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.sendButton).addPreferredGap(0).add((Component)this.addAttachmentButton)).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().add((GroupLayout.Group)mailClientLayout.createParallelGroup(3).add((Component)this.jLabel23).add((Component)this.messageAddresses, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)mailClientLayout.createParallelGroup(3).add((Component)this.jLabel29, -2, 13, -2).add((Component)this.addRecip, -2, -1, -2)).addPreferredGap(0, -1, 32767).add((Component)this.jLabel3))).add(7, 7, 7).add((GroupLayout.Group)mailClientLayout.createParallelGroup(3).add((Component)this.messageSubject, -2, -1, -2).add((Component)this.jLabel24, -2, 13, -2).add((Component)this.deleteAttachment)).add((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addPreferredGap(0, 8, 32767).add((Component)this.messageBody, -2, 360, -2).addContainerGap()).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.mailPreferences).addContainerGap()))));
        this.mailPreferencesFrame.setTitle("Mail Preferences");
        this.mailUserName.setFont(new Font("Lucida Grande", 0, 10));
        this.mailUserName.setText("jTextField1");
        this.smtpHost.setFont(new Font("Lucida Grande", 0, 10));
        this.smtpHost.setText("jTextField2");
        this.mailPassword.setFont(new Font("Lucida Grande", 0, 10));
        this.mailPassword.setText("jPasswordField1");
        this.authenticationFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.authenticationFlag.setSelected(true);
        this.authenticationFlag.setText("Server requires authentication");
        this.authenticationFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.authenticationFlagActionPerformed(evt);
            }
        });
        this.jLabel25.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel25.setText("Username:");
        this.jLabel26.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel26.setText("Password");
        this.jLabel27.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel27.setText("SMTP Server");
        this.saveMailPreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.saveMailPreferences.setText("Save Mail Preferences");
        this.saveMailPreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.saveMailPreferencesActionPerformed(evt);
            }
        });
        this.yourEmailAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.yourEmailAddress.setText("jTextField2");
        this.jLabel28.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel28.setText("Your email address");
        final GroupLayout mailPreferencesFrameLayout = new GroupLayout(this.mailPreferencesFrame.getContentPane());
        this.mailPreferencesFrame.getContentPane().setLayout((LayoutManager)mailPreferencesFrameLayout);
        mailPreferencesFrameLayout.setHorizontalGroup((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(1).add((GroupLayout.Group)mailPreferencesFrameLayout.createSequentialGroup().add(46, 46, 46).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(2).add((Component)this.saveMailPreferences).add((GroupLayout.Group)mailPreferencesFrameLayout.createSequentialGroup().add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(2).add((Component)this.jLabel25).add((Component)this.jLabel26).add((Component)this.jLabel27).add((Component)this.jLabel28)).addPreferredGap(0).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(1, false).add((Component)this.authenticationFlag).add((Component)this.mailUserName, -1, 237, 32767).add((Component)this.mailPassword).add((Component)this.smtpHost).add(2, (Component)this.yourEmailAddress)))).add(151, 151, 151)));
        mailPreferencesFrameLayout.setVerticalGroup((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(1).add((GroupLayout.Group)mailPreferencesFrameLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.jLabel25).add((Component)this.mailUserName, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.jLabel26).add((Component)this.mailPassword, -2, -1, -2)).add(21, 21, 21).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.jLabel27).add((Component)this.smtpHost, -2, -1, -2)).addPreferredGap(0).add((Component)this.authenticationFlag).add(13, 13, 13).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.yourEmailAddress, -2, -1, -2).add((Component)this.jLabel28)).add(16, 16, 16).add((Component)this.saveMailPreferences).addContainerGap(110, 32767)));
        this.messageWindow.setTitle("Backing up..... please wait.....");
        this.messageWindow.setAlwaysOnTop(true);
        this.messageWindow.setEnabled(false);
        this.messageWindow.setFocusable(false);
        this.messageWindow.setFocusableWindowState(false);
        this.messageWindow.setResizable(false);
        this.messageText.setColumns(20);
        this.messageText.setRows(5);
        this.jScrollPane4.setViewportView(this.messageText);
        final GroupLayout messageWindowLayout = new GroupLayout(this.messageWindow.getContentPane());
        this.messageWindow.getContentPane().setLayout((LayoutManager)messageWindowLayout);
        messageWindowLayout.setHorizontalGroup((GroupLayout.Group)messageWindowLayout.createParallelGroup(1).add((GroupLayout.Group)messageWindowLayout.createSequentialGroup().add(59, 59, 59).add((Component)this.jScrollPane4, -2, -1, -2).addContainerGap(97, 32767)));
        messageWindowLayout.setVerticalGroup((GroupLayout.Group)messageWindowLayout.createParallelGroup(1).add((GroupLayout.Group)messageWindowLayout.createSequentialGroup().add(50, 50, 50).add((Component)this.jScrollPane4, -2, -1, -2).addContainerGap(166, 32767)));
        this.jRadioButton1.setText("jRadioButton1");
        final GroupLayout jPanel1Layout = new GroupLayout((Container)this.jPanel1);
        this.jPanel1.setLayout((LayoutManager)jPanel1Layout);
        jPanel1Layout.setHorizontalGroup((GroupLayout.Group)jPanel1Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel1Layout.createSequentialGroup().add(9, 9, 9).add((Component)this.jRadioButton1).addContainerGap(52, 32767)));
        jPanel1Layout.setVerticalGroup((GroupLayout.Group)jPanel1Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel1Layout.createSequentialGroup().addContainerGap().add((Component)this.jRadioButton1).addContainerGap(202, 32767)));
        final GroupLayout spellChooserLayout = new GroupLayout(this.spellChooser.getContentPane());
        this.spellChooser.getContentPane().setLayout((LayoutManager)spellChooserLayout);
        spellChooserLayout.setHorizontalGroup((GroupLayout.Group)spellChooserLayout.createParallelGroup(1).add((GroupLayout.Group)spellChooserLayout.createSequentialGroup().add(78, 78, 78).add((Component)this.jPanel1, -2, -1, -2).addContainerGap(54, 32767)));
        spellChooserLayout.setVerticalGroup((GroupLayout.Group)spellChooserLayout.createParallelGroup(1).add((GroupLayout.Group)spellChooserLayout.createSequentialGroup().add(31, 31, 31).add((Component)this.jPanel1, -2, -1, -2).addContainerGap(50, 32767)));
        this.custom.setDefaultCloseOperation(0);
        this.custom.setTitle("Customise");
        this.custom.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                EtmaHandlerJ.this.customWindowClosing(evt);
            }
        });
        this.jLabel35.setFont(new Font("Lucida Grande", 0, 12));
        this.jLabel35.setForeground(new Color(255, 51, 51));
        this.jLabel35.setHorizontalAlignment(0);
        this.jLabel35.setText("Tick the buttons you wish to hide");
        this.hideEtmaSite.setFont(new Font("Lucida Grande", 0, 10));
        this.hideEtmaSite.setText("eTMA site");
        this.hideListTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.hideListTmas.setText("List TMAs");
        this.hideOpenTmaFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenTmaFolder.setText("Open TMA Folder");
        this.hideTrainingSite.setFont(new Font("Lucida Grande", 0, 10));
        this.hideTrainingSite.setText("Training Site");
        this.hideSavePt3.setFont(new Font("Lucida Grande", 0, 10));
        this.hideSavePt3.setText("Save PT3");
        this.hideECollectTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.hideECollectTmas.setText("Collect TMAs");
        this.hideOpenTma.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenTma.setText("Open TMA");
        this.hideOpenCommentBank.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenCommentBank.setText("Open Comment Bank");
        this.hideBankComment.setFont(new Font("Lucida Grande", 0, 10));
        this.hideBankComment.setText("Bank Comment");
        this.hideZipFiles.setFont(new Font("Lucida Grande", 0, 10));
        this.hideZipFiles.setText("Zip Files");
        this.hideBackupEtmas.setFont(new Font("Lucida Grande", 0, 10));
        this.hideBackupEtmas.setText("Backup eTMAs");
        this.hideSendEmail.setFont(new Font("Lucida Grande", 0, 10));
        this.hideSendEmail.setText("Send Email");
        this.hideOpenPreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenPreferences.setText("Open Preferences");
        this.hideOpenPreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.hideOpenPreferencesActionPerformed(evt);
            }
        });
        this.hideTestJs.setFont(new Font("Lucida Grande", 0, 10));
        this.hideTestJs.setSelected(true);
        this.hideTestJs.setText("Spelling");
        this.hideTestJs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.hideTestJsActionPerformed(evt);
            }
        });
        this.closeCustomize.setFont(new Font("Lucida Grande", 0, 10));
        this.closeCustomize.setText("Close");
        this.closeCustomize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.closeCustomizeActionPerformed(evt);
            }
        });
        this.hideListGrades.setFont(new Font("Lucida Grande", 0, 10));
        this.hideListGrades.setText("List Grades");
        this.colorRemove.setText("Remove colour background from all main window buttons");
        this.colorRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.colorRemoveActionPerformed(evt);
            }
        });
        this.hideOpenReturnsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenReturnsFolder.setText("Open Returns Folder");
        this.jLabel36.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel36.setForeground(new Color(255, 51, 51));
        this.jLabel36.setText("(To restore the colours, untick the box and relaunch the program)");
        final GroupLayout customLayout = new GroupLayout(this.custom.getContentPane());
        this.custom.getContentPane().setLayout((LayoutManager)customLayout);
        customLayout.setHorizontalGroup((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().add((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().add(28, 28, 28).add((Component)this.colorRemove)).add((GroupLayout.Group)customLayout.createSequentialGroup().add(62, 62, 62).add((Component)this.jLabel36)).add((GroupLayout.Group)customLayout.createParallelGroup(2).add((GroupLayout.Group)customLayout.createSequentialGroup().add(8, 8, 8).add((Component)this.closeCustomize).addPreferredGap(0).add((Component)this.jLabel35, -2, 253, -2)).add(1, (GroupLayout.Group)customLayout.createSequentialGroup().add(39, 39, 39).add((GroupLayout.Group)customLayout.createParallelGroup(1).add((Component)this.hideEtmaSite, -2, 122, -2).add((Component)this.hideListTmas, -2, 122, -2).add((Component)this.hideOpenTmaFolder, -2, 122, -2).add((Component)this.hideTrainingSite, -2, 122, -2).add((Component)this.hideSavePt3, -2, 122, -2).add((Component)this.hideECollectTmas, -2, 122, -2).add((Component)this.hideOpenTma, -2, 122, -2).add((Component)this.hideListGrades, -2, 122, -2)).add(40, 40, 40).add((GroupLayout.Group)customLayout.createParallelGroup(1).add((Component)this.hideOpenReturnsFolder, -2, 122, -2).add((Component)this.hideTestJs, -2, 122, -2).add((Component)this.hideOpenCommentBank, -2, 141, -2).add((Component)this.hideZipFiles, -2, 122, -2).add((Component)this.hideBankComment, -2, 122, -2).add((Component)this.hideSendEmail, -2, 122, -2).add((Component)this.hideBackupEtmas, -2, 122, -2).add((Component)this.hideOpenPreferences, -2, 122, -2))))).addContainerGap(138, 32767)));
        customLayout.setVerticalGroup((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.jLabel35).add((Component)this.closeCustomize)).add(16, 16, 16).add((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().add((Component)this.hideEtmaSite).add(13, 13, 13).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideListTmas).add((Component)this.hideZipFiles))).add((Component)this.hideOpenCommentBank)).add(15, 15, 15).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideOpenTmaFolder).add((Component)this.hideBankComment)).add(15, 15, 15).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideTrainingSite).add((Component)this.hideBackupEtmas)).add(8, 8, 8).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideSavePt3).add((Component)this.hideSendEmail)).add(14, 14, 14).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideECollectTmas).add((Component)this.hideTestJs)).add(15, 15, 15).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideOpenTma).add((Component)this.hideOpenReturnsFolder)).add(21, 21, 21).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideListGrades).add((Component)this.hideOpenPreferences)).addPreferredGap(1).add((Component)this.colorRemove).addPreferredGap(0).add((Component)this.jLabel36, -1, 37, 32767).addContainerGap()));
        this.colorFrame1.setTitle("Colours");
        this.colorFrame1.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                EtmaHandlerJ.this.colorFrame1WindowClosing(evt);
            }
        });
        this.colorWindowSelector.setFont(new Font("Lucida Grande", 0, 10));
        this.colorWindowSelector.setModel(new DefaultComboBoxModel(new String[] { "Select a window:", "Main Window", "Comments Window", "Scores Grid" }));
        this.colorWindowSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.colorWindowSelectorActionPerformed(evt);
            }
        });
        this.defaultFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.defaultFlag.setText("Use default");
        this.defaultFlag.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.defaultFlagActionPerformed(evt);
            }
        });
        final GroupLayout colorFrame1Layout = new GroupLayout(this.colorFrame1.getContentPane());
        this.colorFrame1.getContentPane().setLayout((LayoutManager)colorFrame1Layout);
        colorFrame1Layout.setHorizontalGroup((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((GroupLayout.Group)colorFrame1Layout.createSequentialGroup().addContainerGap().add((Component)this.jColorChooser1, -2, -1, -2).add(15, 15, 15).add((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((Component)this.colorWindowSelector, -2, 155, -2).add((Component)this.defaultFlag)).add(41, 41, 41)));
        colorFrame1Layout.setVerticalGroup((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((GroupLayout.Group)colorFrame1Layout.createSequentialGroup().add((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((Component)this.jColorChooser1, -2, -1, -2).add((GroupLayout.Group)colorFrame1Layout.createSequentialGroup().add(37, 37, 37).add((Component)this.colorWindowSelector, -2, -1, -2).add(21, 21, 21).add((Component)this.defaultFlag))).addContainerGap(-1, 32767)));
        this.partScoresTable.setTitle("Partscores");
        this.gradesSummaryTable1.setFont(new Font("Lucida Grande", 0, 10));
        this.gradesSummaryTable1.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null } }, new String[] { "Sub No", "Forenames", "Surname", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" }) {
            Class[] types = { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class, Object.class };
            boolean[] canEdit = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.gradesSummaryTable1.setGridColor(new Color(102, 153, 255));
        this.gradesSummaryTable1.setRowSelectionAllowed(false);
        this.gradesSummaryTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.gradesSummaryTable1MouseReleased(evt);
            }
        });
        this.jScrollPane5.setViewportView(this.gradesSummaryTable1);
        this.exportGrades1.setFont(new Font("Lucida Grande", 0, 10));
        this.exportGrades1.setText("Export");
        this.exportGrades1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.exportGrades1ActionPerformed(evt);
            }
        });
        final GroupLayout partScoresTableLayout = new GroupLayout(this.partScoresTable.getContentPane());
        this.partScoresTable.getContentPane().setLayout((LayoutManager)partScoresTableLayout);
        partScoresTableLayout.setHorizontalGroup((GroupLayout.Group)partScoresTableLayout.createParallelGroup(1).add((GroupLayout.Group)partScoresTableLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)partScoresTableLayout.createParallelGroup(1).add((GroupLayout.Group)partScoresTableLayout.createSequentialGroup().add((Component)this.exportGrades1).add(0, 721, 32767)).add((Component)this.jScrollPane5, -1, 796, 32767)).addContainerGap()));
        partScoresTableLayout.setVerticalGroup((GroupLayout.Group)partScoresTableLayout.createParallelGroup(1).add(2, (GroupLayout.Group)partScoresTableLayout.createSequentialGroup().addContainerGap(16, 32767).add((Component)this.jScrollPane5, -2, 393, -2).addPreferredGap(0).add((Component)this.exportGrades1).addContainerGap()));
        this.jMenuItemUndo.setText("Undo");
        this.jMenuItemUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemUndoActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemUndo);
        this.jMenuItemRedo.setText("Redo");
        this.jMenuItemRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemRedoActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemRedo);
        this.jMenuItemCopy.setText("Copy");
        this.jMenuItemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemCopyActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemCopy);
        this.jMenuItemPaste.setText("Paste");
        this.jMenuItemPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemPasteActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemPaste);
        this.jMenuItemSelectAll.setText("Select All");
        this.jMenuItemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemSelectAllActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemSelectAll);
        this.jMenuItemCut.setText("Cut");
        this.jMenuItemCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemCutActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemCut);
        this.setDefaultCloseOperation(0);
        this.setTitle("File Handler");
        this.setCursor(new Cursor(0));
        this.setMinimumSize(new Dimension(730, 573));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent evt) {
                EtmaHandlerJ.this.formWindowClosing(evt);
            }
            
            @Override
            public void windowOpened(final WindowEvent evt) {
                EtmaHandlerJ.this.formWindowOpened(evt);
            }
        });
        this.tutor_comments_area.setBorder(BorderFactory.createBevelBorder(0));
        this.tutor_comments_area.setViewportBorder(BorderFactory.createBevelBorder(0));
        this.tutor_comments_input.setColumns(20);
        this.tutor_comments_input.setFont(new Font("Lucida Grande", 0, 10));
        this.tutor_comments_input.setLineWrap(true);
        this.tutor_comments_input.setRows(5);
        this.tutor_comments_input.setTabSize(4);
        this.tutor_comments_input.setBorder(BorderFactory.createBevelBorder(0));
        this.tutor_comments_input.setMinimumSize(new Dimension(0, 0));
        this.tutor_comments_input.setName("fhiFileName");
        this.tutor_comments_input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent evt) {
                EtmaHandlerJ.this.tutor_comments_inputFocusGained(evt);
            }
        });
        this.tutor_comments_input.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent evt) {
                EtmaHandlerJ.this.tutor_comments_inputMousePressed(evt);
            }
            
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.tutor_comments_inputMouseReleased(evt);
            }
        });
        this.tutor_comments_input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent evt) {
                EtmaHandlerJ.this.tutor_comments_inputKeyPressed(evt);
            }
            
            @Override
            public void keyReleased(final KeyEvent evt) {
                EtmaHandlerJ.this.tutor_comments_inputKeyReleased(evt);
            }
        });
        this.tutor_comments_area.setViewportView(this.tutor_comments_input);
        this.fhiFileName.setFont(new Font("Lucida Grande", 0, 10));
        this.zipFiles.setBackground(new Color(255, 102, 0));
        this.zipFiles.setFont(new Font("Lucida Grande", 0, 10));
        this.zipFiles.setText("Zip Files");
        this.zipFiles.setToolTipText("Saves and zips the current student's files. The zipped file will be found in the returns folder. It is then ready to return to MK using the browser.");
        this.zipFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.zipFilesActionPerformed(evt);
            }
        });
        this.tmaMarks.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.tmaMarks.setViewportBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.tmaScores.setBackground(new Color(204, 255, 255));
        this.tmaScores.setFont(new Font("Lucida Grande", 0, 10));
        this.tmaScores.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null }, { null, null, null, null, null, null } }, new String[] { "Qu No", "Part No", "Score", "Max", "Totals", "Qn Max" }) {
            Class[] types = { String.class, String.class, String.class, String.class, String.class, String.class };
            boolean[] canEdit = { false, false, true, false, false, false };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.tmaScores.setCellSelectionEnabled(true);
        this.tmaScores.setGridColor(new Color(153, 153, 153));
        this.tmaScores.setIntercellSpacing(new Dimension(2, 2));
        this.tmaScores.setName("TMA Scores");
        this.tmaScores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(final MouseEvent evt) {
                EtmaHandlerJ.this.tmaScoresMouseExited(evt);
            }
            
            @Override
            public void mouseReleased(final MouseEvent evt) {
                EtmaHandlerJ.this.tmaScoresMouseReleased(evt);
            }
        });
        this.tmaScores.addInputMethodListener(new InputMethodListener() {
            @Override
            public void caretPositionChanged(final InputMethodEvent evt) {
            }
            
            @Override
            public void inputMethodTextChanged(final InputMethodEvent evt) {
                EtmaHandlerJ.this.tmaScoresInputMethodTextChanged(evt);
            }
        });
        this.tmaScores.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent evt) {
                EtmaHandlerJ.this.tmaScoresKeyPressed(evt);
            }
            
            @Override
            public void keyReleased(final KeyEvent evt) {
                EtmaHandlerJ.this.tmaScoresKeyReleased(evt);
            }
        });
        this.tmaMarks.setViewportView(this.tmaScores);
        this.ou_computer_user_name.setEditable(false);
        this.ou_computer_user_name.setFont(new Font("Lucida Grande", 0, 10));
        this.ou_computer_user_name.setToolTipText("The student's OU User Name.");
        this.ou_computer_user_name.setName("ou_computer_user_name");
        this.personal_id.setEditable(false);
        this.personal_id.setFont(new Font("Lucida Grande", 0, 10));
        this.personal_id.setToolTipText("The student's PID");
        this.personal_id.setName("personal_id");
        this.title.setEditable(false);
        this.title.setFont(new Font("Lucida Grande", 0, 10));
        this.title.setToolTipText("The student's Title");
        this.title.setName("title");
        this.initials.setEditable(false);
        this.initials.setFont(new Font("Lucida Grande", 0, 10));
        this.initials.setToolTipText("All the student's initials.");
        this.initials.setName("initials");
        this.forenames.setEditable(false);
        this.forenames.setFont(new Font("Lucida Grande", 1, 10));
        this.forenames.setForeground(new Color(255, 51, 51));
        this.forenames.setToolTipText("Student's forename");
        this.forenames.setName("forenames");
        this.surname.setEditable(false);
        this.surname.setFont(new Font("Lucida Grande", 1, 10));
        this.surname.setForeground(new Color(255, 51, 51));
        this.surname.setToolTipText("Student's surname");
        this.surname.setName("surname");
        this.address_line1.setEditable(false);
        this.address_line1.setFont(new Font("Lucida Grande", 0, 10));
        this.address_line1.setName("address_line1");
        this.address_line2.setEditable(false);
        this.address_line2.setFont(new Font("Lucida Grande", 0, 10));
        this.address_line2.setName("address_line2");
        this.address_line3.setEditable(false);
        this.address_line3.setFont(new Font("Lucida Grande", 0, 10));
        this.address_line3.setName("address_line3");
        this.address_line4.setEditable(false);
        this.address_line4.setFont(new Font("Lucida Grande", 0, 10));
        this.address_line4.setName("address_line4");
        this.address_line5.setEditable(false);
        this.address_line5.setFont(new Font("Lucida Grande", 0, 10));
        this.address_line5.setName("address_line5");
        this.postcode.setEditable(false);
        this.postcode.setFont(new Font("Lucida Grande", 0, 10));
        this.postcode.setToolTipText("The student's postcode.");
        this.postcode.setName("postcode");
        this.email_address.setEditable(false);
        this.email_address.setFont(new Font("Lucida Grande", 0, 10));
        this.email_address.setName("email_address");
        this.staff_id.setEditable(false);
        this.staff_id.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_id.setName("staff_id");
        this.loadXMLAlt.setFont(new Font("Lucida Grande", 0, 10));
        this.loadXMLAlt.setText("LoadPT3");
        this.loadXMLAlt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.loadXMLAltActionPerformed(evt);
            }
        });
        this.checkTotals.setFont(new Font("Lucida Grande", 0, 10));
        this.checkTotals.setText("Check totals");
        this.checkTotals.setToolTipText("Use this if you think the totals are incorrect. Shouldn't generally be necessary!");
        this.checkTotals.setName("Calculate Totals");
        this.checkTotals.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkTotalsActionPerformed(evt);
            }
        });
        this.staff_title.setEditable(false);
        this.staff_title.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_title.setName("staff_id");
        this.staff_initials.setEditable(false);
        this.staff_initials.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_initials.setName("staff_id");
        this.staff_forenames.setEditable(false);
        this.staff_forenames.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_forenames.setName("staff_id");
        this.overall_grade_score.setEditable(false);
        this.overall_grade_score.setFont(new Font("Lucida Grande", 1, 10));
        this.overall_grade_score.setToolTipText("Total score for this TMA.");
        this.overall_grade_score.setName("staff_id");
        this.staff_surname.setEditable(false);
        this.staff_surname.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_surname.setName("staff_id");
        this.region_code.setEditable(false);
        this.region_code.setFont(new Font("Lucida Grande", 0, 10));
        this.region_code.setName("staff_id");
        this.course_code.setEditable(false);
        this.course_code.setFont(new Font("Lucida Grande", 0, 10));
        this.course_code.setName("staff_id");
        this.course_version_num.setEditable(false);
        this.course_version_num.setFont(new Font("Lucida Grande", 0, 10));
        this.course_version_num.setName("staff_id");
        this.pres_code.setEditable(false);
        this.pres_code.setFont(new Font("Lucida Grande", 0, 10));
        this.pres_code.setName("staff_id");
        this.assgnmt_suffix.setEditable(false);
        this.assgnmt_suffix.setFont(new Font("Lucida Grande", 0, 10));
        this.assgnmt_suffix.setName("staff_id");
        this.e_tma_submission_num.setEditable(false);
        this.e_tma_submission_num.setFont(new Font("Lucida Grande", 0, 10));
        this.e_tma_submission_num.setName("address_line3");
        this.score_update_allowed.setEditable(false);
        this.score_update_allowed.setFont(new Font("Lucida Grande", 0, 10));
        this.score_update_allowed.setName("address_line4");
        this.max_assgnmt_score.setEditable(false);
        this.max_assgnmt_score.setFont(new Font("Lucida Grande", 0, 10));
        this.max_assgnmt_score.setName("address_line5");
        this.zip_file.setEditable(false);
        this.zip_file.setFont(new Font("Lucida Grande", 0, 10));
        this.zip_file.setName("postcode");
        this.zip_date.setEditable(false);
        this.zip_date.setFont(new Font("Lucida Grande", 0, 10));
        this.zip_date.setName("staff_id");
        this.late_submission_status.setEditable(false);
        this.late_submission_status.setFont(new Font("Lucida Grande", 0, 10));
        this.late_submission_status.setName("address_line3");
        this.late_submission_status.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.late_submission_statusActionPerformed(evt);
            }
        });
        this.submission_status.setEditable(false);
        this.submission_status.setFont(new Font("Lucida Grande", 0, 10));
        this.submission_status.setToolTipText("Shows whether the TMA is unmarked, marked or zipped.");
        this.submission_status.setName("address_line4");
        this.marked_date.setEditable(false);
        this.marked_date.setFont(new Font("Lucida Grande", 0, 10));
        this.marked_date.setName("address_line5");
        this.walton_received_date.setEditable(false);
        this.walton_received_date.setFont(new Font("Lucida Grande", 0, 10));
        this.walton_received_date.setName("postcode");
        this.e_tma_submission_date.setEditable(false);
        this.e_tma_submission_date.setFont(new Font("Lucida Grande", 0, 10));
        this.e_tma_submission_date.setName("staff_id");
        this.total_question_count.setEditable(false);
        this.total_question_count.setFont(new Font("Lucida Grande", 0, 10));
        this.total_question_count.setName("address_line5");
        this.permitted_question_count.setEditable(false);
        this.permitted_question_count.setFont(new Font("Lucida Grande", 0, 10));
        this.permitted_question_count.setName("address_line5");
        this.tutor_comments.setFont(new Font("Lucida Grande", 0, 10));
        this.tutor_comments.setName("address_line5");
        this.savePt3.setBackground(new Color(255, 102, 0));
        this.savePt3.setFont(new Font("Lucida Grande", 0, 10));
        this.savePt3.setText("SavePT3");
        this.savePt3.setToolTipText("Save the current PT3 (alt- or command-S is a shortcut)");
        this.savePt3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.savePt3ActionPerformed(evt);
            }
        });
        this.openPreferences.setBackground(new Color(255, 0, 0));
        this.openPreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.openPreferences.setText("Open preferences");
        this.openPreferences.setName("Open Preferences");
        this.openPreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openPreferencesActionPerformed(evt);
            }
        });
        this.tmaList.setFont(new Font("Lucida Grande", 0, 10));
        this.tmaList.setModel(new DefaultComboBoxModel(new String[] { "Select TMA No" }));
        this.tmaList.setToolTipText("Select the number of the TMA you wish to mark");
        this.tmaList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent evt) {
                EtmaHandlerJ.this.tmaListItemStateChanged(evt);
            }
        });
        this.tmaList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.tmaListActionPerformed(evt);
            }
        });
        this.courseList.setFont(new Font("Lucida Grande", 0, 10));
        this.courseList.setModel(new DefaultComboBoxModel(new String[] { "Select module" }));
        this.courseList.setToolTipText("Select the Course Code (eg M150-07J)");
        this.courseList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent evt) {
                EtmaHandlerJ.this.courseListItemStateChanged(evt);
            }
        });
        this.courseList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.courseListActionPerformed(evt);
            }
        });
        this.studentList.setFont(new Font("Lucida Grande", 0, 10));
        this.studentList.setModel(new DefaultComboBoxModel(new String[] { "No courses" }));
        this.studentList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.studentListActionPerformed(evt);
            }
        });
        this.openTmaList.setBackground(new Color(0, 153, 0));
        this.openTmaList.setFont(new Font("Lucida Grande", 0, 10));
        this.openTmaList.setText("List TMAs");
        this.openTmaList.setToolTipText("Lists the Students who have submitted the currently selected TMA. Click on any name to open the PT3.");
        this.openTmaList.setName("Open Preferences");
        this.openTmaList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openTmaListActionPerformed(evt);
            }
        });
        this.subNo.setFont(new Font("Lucida Grande", 0, 10));
        this.subNo.setModel(new DefaultComboBoxModel(new String[] { "No courses" }));
        this.subNo.setInheritsPopupMenu(true);
        this.subNo.setName("No Courses");
        this.collectTmas.setBackground(new Color(0, 153, 0));
        this.collectTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.collectTmas.setText("Import TMAs");
        this.collectTmas.setToolTipText("<html>This button transfers files from your newly downloaded folder to the correct place in your 'etmas' folder.<br>\nSelect the course folder that you've just downloaded (probably on your desktop),<br>\nThe program will do the rest!");
        this.collectTmas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.collectTmasActionPerformed(evt);
            }
        });
        this.openTmaFolder.setBackground(new Color(0, 153, 0));
        this.openTmaFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.openTmaFolder.setText("Open TMA Folder");
        this.openTmaFolder.setToolTipText("Open's the folder containing the student's files for the currently selected TMA");
        this.openTmaFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openTmaFolderActionPerformed(evt);
            }
        });
        this.openTma.setBackground(new Color(0, 153, 0));
        this.openTma.setFont(new Font("Lucida Grande", 0, 10));
        this.openTma.setText("Open TMA");
        this.openTma.setToolTipText("Opens the Student's script. Disabled until a PT3 is loaded.");
        this.openTma.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openTmaActionPerformed(evt);
            }
        });
        this.etmaSite.setBackground(new Color(0, 153, 0));
        this.etmaSite.setFont(new Font("Lucida Grande", 0, 10));
        this.etmaSite.setText(" eTMA site");
        this.etmaSite.setToolTipText("<html>Go to the OU eTMA site to download new TMAs.<br>\n If you want to re-download older TMAs,<br>\nmake sure the left hand pop-up is set to 'collected' or 'returned\"");
        this.etmaSite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.etmaSiteActionPerformed(evt);
            }
        });
        this.trainingSite.setBackground(new Color(153, 0, 153));
        this.trainingSite.setFont(new Font("Lucida Grande", 0, 10));
        this.trainingSite.setText("Training Site");
        this.trainingSite.setToolTipText("Go to the OU eTMA Training site.");
        this.trainingSite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.trainingSiteActionPerformed(evt);
            }
        });
        this.prevMarks.setBackground(new Color(204, 255, 255));
        this.prevMarks.setFont(new Font("Lucida Grande", 0, 10));
        this.prevMarks.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null, null, null, null, null, null, null, null, null, null }, { null, null, null, null, null, null, null, null, null, null, null, null, null, null } }, new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "" }) {
            Class[] types = { String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };
            boolean[] canEdit = { false, false, false, false, false, false, false, false, false, false, false, false, false, false };
            
            @Override
            public Class getColumnClass(final int columnIndex) {
                return this.types[columnIndex];
            }
            
            @Override
            public boolean isCellEditable(final int rowIndex, final int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.prevMarks.setToolTipText("<HTML>\"Ave\" is the weighted average of the tutorials submitted <BR>(nil submissions are counted as zero).<BR><BR>\n\"Mod %\" is the percentage of the total marks available for the whole module achieved so far.</HTML>");
        this.prevMarks.setCellSelectionEnabled(true);
        this.prevMarks.setGridColor(new Color(153, 153, 153));
        this.jScrollPane2.setViewportView(this.prevMarks);
        this.listAllScores.setBackground(new Color(255, 255, 0));
        this.listAllScores.setFont(new Font("Lucida Grande", 0, 10));
        this.listAllScores.setText("List Grades");
        this.listAllScores.setToolTipText("List all the grades for students taking the currently selected course.");
        this.listAllScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.listAllScoresActionPerformed(evt);
            }
        });
        this.openReturnsFolder.setBackground(new Color(255, 102, 0));
        this.openReturnsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.openReturnsFolder.setText("Open Returns Folder");
        this.openReturnsFolder.setToolTipText("Opens the folder where the zipped files are kept to be returned to MK.");
        this.openReturnsFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openReturnsFolderActionPerformed(evt);
            }
        });
        this.jLabel1.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel1.setText("<HTML>Prev <BR>Scores:</HTML>");
        this.jLabel2.setBackground(new Color(255, 0, 0));
        this.jLabel2.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel2.setForeground(new Color(255, 0, 51));
        this.jLabel2.setText("Total");
        this.jLabel4.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel4.setText("Region");
        this.jLabel6.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel6.setText("Module");
        this.jLabel7.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel7.setText("Version");
        this.jLabel8.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel8.setText("Pres Code");
        this.jLabel9.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel9.setText("TMA No");
        this.jLabel10.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel10.setText("Sub No");
        this.jLabel11.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel11.setText("Sub Date:");
        this.jLabel12.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel12.setText("M'kd Date");
        this.jLabel13.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel13.setText("Total Qns");
        this.jLabel14.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel14.setText("Permitted");
        this.jLabel15.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel15.setText("Zip file");
        this.jLabel16.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel16.setText("Zip date");
        this.jLabel17.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel17.setText("Update allowed");
        this.jLabel18.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel18.setText("Max score");
        this.jLabel19.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel19.setText("W'ltn Rec'd");
        this.openCommentBank.setBackground(new Color(102, 102, 255));
        this.openCommentBank.setFont(new Font("Lucida Grande", 0, 10));
        this.openCommentBank.setText("Open comment bank");
        this.openCommentBank.setToolTipText("Opens the comment bank file defined in the Preferences.");
        this.openCommentBank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openCommentBankActionPerformed(evt);
            }
        });
        this.bankComment.setBackground(new Color(102, 102, 255));
        this.bankComment.setFont(new Font("Lucida Grande", 0, 10));
        this.bankComment.setText("Bank comment");
        this.bankComment.setToolTipText("Copies text from the clipboard to the comment bank file.");
        this.bankComment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.bankCommentActionPerformed(evt);
            }
        });
        this.lateSubmission.setFont(new Font("Lucida Grande", 0, 10));
        this.lateSubmission.setText("Late submission");
        this.lateSubmission.setToolTipText("Only tick this box if you are awarding the student a zero score because of late submission. Use with care!");
        this.lateSubmission.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.lateSubmissionActionPerformed(evt);
            }
        });
        this.fontSize.setFont(new Font("Lucida Grande", 0, 10));
        this.fontSize.setModel(new DefaultComboBoxModel(new String[] { "8", "10", "11", "12", "13", "14", "16", "18", "20" }));
        this.fontSize.setToolTipText("Select a font size for the tutor's comments box.");
        this.fontSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.fontSizeActionPerformed(evt);
            }
        });
        this.moreDetails.setBackground(new Color(255, 0, 0));
        this.moreDetails.setFont(new Font("Lucida Grande", 0, 10));
        this.moreDetails.setText("More details");
        this.moreDetails.setToolTipText("Shows the full Handler window");
        this.moreDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.moreDetailsActionPerformed(evt);
            }
        });
        this.jLabel22.setFont(new Font("Lucida Grande", 1, 10));
        this.jLabel22.setText("Tutor's comments:");
        this.previousPt3s.setFont(new Font("Lucida Grande", 0, 10));
        this.previousPt3s.setModel(new DefaultComboBoxModel(new String[] { "Previous PT3s" }));
        this.previousPt3s.setToolTipText("Opens any previous PT3s for this student in your browser.");
        this.previousPt3s.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.previousPt3sActionPerformed(evt);
            }
        });
        this.sendEmail.setBackground(new Color(102, 102, 255));
        this.sendEmail.setFont(new Font("Lucida Grande", 0, 10));
        this.sendEmail.setText("Send email");
        this.sendEmail.setToolTipText("Sends an email to this student, with a choice of methods.");
        this.sendEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.sendEmailActionPerformed(evt);
            }
        });
        this.checkSpellingButton.setBackground(new Color(102, 102, 255));
        this.checkSpellingButton.setFont(new Font("Lucida Grande", 0, 10));
        this.checkSpellingButton.setText("Spelling");
        this.checkSpellingButton.setToolTipText("Select and copy a script from the student's Word file; press this putton, and the file will be run as a script.");
        this.checkSpellingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkSpellingButtonActionPerformed(evt);
            }
        });
        this.backUp.setBackground(new Color(255, 0, 0));
        this.backUp.setFont(new Font("Lucida Grande", 0, 10));
        this.backUp.setText("Backup eTMAs");
        this.backUp.setToolTipText("Makes a backup copy of your etmas folder, in the same location as the etmas folder. May take a minute or two.");
        this.backUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.backUpActionPerformed(evt);
            }
        });
        this.jLabel30.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel30.setText("Font Size:");
        this.jLabel31.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel31.setText("Module::");
        this.jLabel32.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel32.setText("TMA:");
        final GroupLayout jPanel2Layout = new GroupLayout((Container)this.jPanel2);
        this.jPanel2.setLayout((LayoutManager)jPanel2Layout);
        jPanel2Layout.setHorizontalGroup((GroupLayout.Group)jPanel2Layout.createParallelGroup(1).add(0, 0, 32767));
        jPanel2Layout.setVerticalGroup((GroupLayout.Group)jPanel2Layout.createParallelGroup(1).add(0, 0, 32767));
        this.partScoresButton.setBackground(new Color(255, 102, 0));
        this.partScoresButton.setFont(new Font("Lucida Grande", 0, 10));
        this.partScoresButton.setText("Partscores");
        this.partScoresButton.setToolTipText("Displays a list of the partscores for each question for every student.");
        this.partScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.partScoresButtonActionPerformed(evt);
            }
        });
        this.File.setText("File");
        this.preferencesMenu.setForeground(new Color(255, 0, 0));
        this.preferencesMenu.setText("Preferences");
        this.preferencesMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.preferencesMenuActionPerformed(evt);
            }
        });
        this.File.add(this.preferencesMenu);
        this.savePt3MenuItem.setText("Save PT3");
        this.savePt3MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.savePt3MenuItemActionPerformed(evt);
            }
        });
        this.File.add(this.savePt3MenuItem);
        this.collectTmasMenu.setText("Import TMAs");
        this.collectTmasMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.collectTmasMenuActionPerformed(evt);
            }
        });
        this.File.add(this.collectTmasMenu);
        this.listTmasMenuItem.setText("List TMAs");
        this.listTmasMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.listTmasMenuItemActionPerformed(evt);
            }
        });
        this.File.add(this.listTmasMenuItem);
        this.openTmaMenu.setText("Open TMA script");
        this.openTmaMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openTmaMenuActionPerformed(evt);
            }
        });
        this.File.add(this.openTmaMenu);
        this.openTmaFolderMenu.setText("Open TMA Folder");
        this.openTmaFolderMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openTmaFolderMenuActionPerformed(evt);
            }
        });
        this.File.add(this.openTmaFolderMenu);
        openReturnsFolderMenu.setText("Open Returns Folder");
        openReturnsFolderMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openReturnsFolderMenuActionPerformed(evt);
            }
        });
        this.File.add(openReturnsFolderMenu);
        this.listGrades.setText("List Grades");
        this.listGrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.listGradesActionPerformed(evt);
            }
        });
        this.File.add(this.listGrades);
        this.printDoc.setText("Print main window");
        this.File.add(this.printDoc);
        this.zipFilesMenu.setText("Zip Files");
        this.zipFilesMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.zipFilesMenuActionPerformed(evt);
            }
        });
        this.File.add(this.zipFilesMenu);
        this.exportMarksGrid.setText("Export Marks Grid");
        this.exportMarksGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.exportMarksGridActionPerformed(evt);
            }
        });
        this.File.add(this.exportMarksGrid);
        this.createFeedback.setAccelerator(KeyStroke.getKeyStroke(70, 512));
        this.createFeedback.setText("Create/Open feedback script");
        this.createFeedback.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.createFeedbackActionPerformed(evt);
            }
        });
        this.File.add(this.createFeedback);
        this.exitMenuItem.setText("Exit");
        this.exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.exitMenuItemActionPerformed(evt);
            }
        });
        this.File.add(this.exitMenuItem);
        this.jMenuBar1.add(this.File);
        this.Edit.setText("Edit");
        this.Undo.setText("Undo");
        this.Undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.UndoActionPerformed(evt);
            }
        });
        this.Edit.add(this.Undo);
        this.Redo.setText("Redo");
        this.Redo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.RedoActionPerformed(evt);
            }
        });
        this.Edit.add(this.Redo);
        this.copyText.setText("Copy");
        this.copyText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.copyTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.copyText);
        this.pasteText.setText("Paste");
        this.pasteText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.pasteTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.pasteText);
        this.selectAllText.setText("Select All");
        this.selectAllText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.selectAllTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.selectAllText);
        this.cutText.setText("Cut");
        this.cutText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.cutTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.cutText);
        this.jMenuBar1.add(this.Edit);
        this.jMenu1.setText("Tools");
        this.checkTotalsMenu.setText("Check Totals");
        this.checkTotalsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkTotalsMenuActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.checkTotalsMenu);
        this.bankCommentMenuItem.setText("Bank Comment");
        this.bankCommentMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.bankCommentMenuItemActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.bankCommentMenuItem);
        this.openCommentBankMenu.setText("Open comment bank");
        this.openCommentBankMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.openCommentBankMenuActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.openCommentBankMenu);
        this.checkSpelling.setText("Check Spelling");
        this.checkSpelling.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkSpellingActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.checkSpelling);
        this.sendEmailMenu.setText("Send email...");
        this.sendEmailMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.sendEmailMenuActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.sendEmailMenu);
        this.backUpMenu.setText("Backup etmas Folder...");
        this.backUpMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.backUpMenuActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.backUpMenu);
        this.chooseColor.setText("Choose colours");
        this.chooseColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.chooseColorActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.chooseColor);
        this.customize.setText("Customise...");
        this.customize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.customizeActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.customize);
        this.distributeDocument.setText("Distribute handout...");
        this.distributeDocument.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.distributeDocumentActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.distributeDocument);
        this.undoDistribution.setText("Undo distribution...");
        this.undoDistribution.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.undoDistributionActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.undoDistribution);
        this.comparePartScores.setText("Compare part scores");
        this.comparePartScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.comparePartScoresActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.comparePartScores);
        this.checkUpdates.setText("Check for updates");
        this.checkUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.checkUpdatesActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.checkUpdates);
        this.Developer.setText("Word count (PT3)");
        this.Developer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.WordCountActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.Developer);
        this.testJsMenu.setText("Developer");
        this.testJsMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.testJsMenuActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.testJsMenu);
        this.jMenuBar1.add(this.jMenu1);
        this.sites.setText("Sites");
        this.etmaSiteMenu.setText("Go to etma Site");
        this.etmaSiteMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.etmaSiteMenuActionPerformed(evt);
            }
        });
        this.sites.add(this.etmaSiteMenu);
        this.trainingSiteMenu.setText("Go to Training Site");
        this.trainingSiteMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.trainingSiteMenuActionPerformed(evt);
            }
        });
        this.sites.add(this.trainingSiteMenu);
        this.jMenuBar1.add(this.sites);
        this.help.setText("Help");
        this.help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.helpActionPerformed(evt);
            }
        });
        this.etmaHandlerHelp.setText("EtmaHandler Help");
        this.etmaHandlerHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.etmaHandlerHelpActionPerformed(evt);
            }
        });
        this.help.add(this.etmaHandlerHelp);
        this.jMenuItemVersion.setText("Version info");
        this.jMenuItemVersion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                EtmaHandlerJ.this.jMenuItemVersionActionPerformed(evt);
            }
        });
        this.help.add(this.jMenuItemVersion);
        this.jMenuBar1.add(this.help);
        this.setJMenuBar(this.jMenuBar1);
        final GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createParallelGroup(1).add(2, (GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add(2, (GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.title, -2, 274, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.staff_id, -2, 104, -2).add((Component)this.staff_surname, -2, 104, -2).add((Component)this.staff_forenames, -2, 104, -2).add((Component)this.personal_id, -2, 102, -2).add((Component)this.staff_title, -2, 103, -2).add(2, (Component)this.staff_initials, -2, 104, -2))).add((GroupLayout.Group)layout.createParallelGroup(2).add(1, (GroupLayout.Group)layout.createParallelGroup(2, false).add(1, (Component)this.postcode).add(1, (Component)this.address_line5).add(1, (Component)this.address_line4).add(1, (Component)this.address_line3).add(1, (Component)this.address_line2, -1, 212, 32767)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2, false).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.address_line1, -2, 129, -2).addPreferredGap(0, -1, 32767).add((Component)this.openPreferences)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.initials, -2, 35, -2).add(18, 18, 18).add((Component)this.email_address, -2, 209, -2))).addPreferredGap(0).add((Component)this.sendEmail)))).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(1).add((Component)this.jLabel18).addPreferredGap(0).add((Component)this.max_assgnmt_score, -2, 46, -2).addPreferredGap(0).add((Component)this.jLabel17).addPreferredGap(0).add((Component)this.score_update_allowed, -2, 27, -2).addPreferredGap(0).add((Component)this.jLabel7).addPreferredGap(0).add((Component)this.course_version_num, -2, 24, -2).add(2, 2, 2)).add((GroupLayout.Group)layout.createSequentialGroup().add(16, 16, 16).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel4).addPreferredGap(1).add((Component)this.region_code, -2, 46, -2).addPreferredGap(0).add((Component)this.jLabel6).add(4, 4, 4).add((Component)this.course_code).add(18, 18, 18).add((Component)this.jLabel9).addPreferredGap(0).add((Component)this.assgnmt_suffix, -2, 40, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel16).add((Component)this.jLabel19).add((Component)this.jLabel15).add((Component)this.jLabel11)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((Component)this.e_tma_submission_date).add((Component)this.zip_file).add((Component)this.zip_date).add((Component)this.walton_received_date, -2, 218, -2))).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel12).addPreferredGap(0).add((Component)this.marked_date, -2, 222, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel13).addPreferredGap(0).add((Component)this.total_question_count, -2, 33, -2).add(18, 18, 18).add((Component)this.jLabel14).addPreferredGap(0).add((Component)this.permitted_question_count, -2, 35, -2).addPreferredGap(1).add((Component)this.jLabel8).addPreferredGap(1).add((Component)this.pres_code, -2, 46, -2))).add(0, 0, 32767)))))).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tmaMarks, -2, 310, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel1, -2, 32, -2).addPreferredGap(0).add((Component)this.jScrollPane2, -2, 0, 32767)).add((Component)this.tutor_comments_area)))).add(18, 18, 18)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(47, 47, 47).add((Component)this.bankComment).addPreferredGap(0).add((Component)this.openCommentBank).addPreferredGap(0).add((Component)this.checkSpellingButton, -2, 104, -2).addPreferredGap(1).add((Component)this.partScoresButton).add(36, 36, 36).add((Component)this.openReturnsFolder)).add((GroupLayout.Group)layout.createSequentialGroup().add(7, 7, 7).add((Component)this.jLabel10).addPreferredGap(1).add((Component)this.e_tma_submission_num, -2, 24, -2).addPreferredGap(0).add((Component)this.jLabel2).addPreferredGap(0).add((Component)this.overall_grade_score, -2, 40, -2).add(1, 1, 1).add((Component)this.checkTotals).add(16, 16, 16).add((Component)this.submission_status, -2, 85, -2).add(8, 8, 8).add((Component)this.jLabel22, -2, 113, -2).addPreferredGap(0).add((Component)this.jLabel30).addPreferredGap(0).add((Component)this.fontSize, -2, -1, -2))).addContainerGap()))).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(8, 8, 8).add((Component)this.jLabel32).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.courseList, -2, 114, -2).addPreferredGap(1).add((Component)this.etmaSite).addPreferredGap(0).add((Component)this.collectTmas).addPreferredGap(0).add((Component)this.openTmaFolder)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tmaList, -2, 79, -2).add(48, 48, 48).add((Component)this.openTmaList).addPreferredGap(1).add((Component)this.openTma).add(18, 18, 18).add((Component)this.trainingSite))).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.savePt3).addPreferredGap(0).add((Component)this.moreDetails)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.zipFiles).addPreferredGap(1).add((Component)this.lateSubmission)))).add((GroupLayout.Group)layout.createSequentialGroup().add(4, 4, 4).add((Component)this.jLabel31)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.ou_computer_user_name, -2, 89, -2).addPreferredGap(0).add((Component)this.forenames, -2, 117, -2).addPreferredGap(0).add((Component)this.surname, -2, 149, -2).addPreferredGap(1).add((Component)this.previousPt3s, -2, -1, -2).addPreferredGap(0).add((Component)this.listAllScores).addPreferredGap(0).add((Component)this.backUp))).add(17, 17, 17))).add((GroupLayout.Group)layout.createSequentialGroup().add(319, 319, 319).add((Component)this.late_submission_status, -2, 23, -2).add(44, 44, 44).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.subNo, -2, 10, -2).add((GroupLayout.Group)layout.createParallelGroup(2, false).add(1, (Component)this.studentList, 0, 0, 32767).add(1, (Component)this.tutor_comments, -2, -1, -2))).add(32, 32, 32).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.fhiFileName, -2, 16, -2).add((GroupLayout.Group)layout.createSequentialGroup().add(17, 17, 17).add((Component)this.loadXMLAlt, -2, 30, -2))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.courseList, -2, 23, -2).add((Component)this.jLabel31).add((Component)this.openTmaFolder).add((Component)this.etmaSite).add((Component)this.collectTmas).add((Component)this.moreDetails).add((Component)this.savePt3)).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(4, 4, 4).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.openTma).add((Component)this.openTmaList, -2, 31, -2).add((Component)this.tmaList, -2, 23, -2).add((Component)this.zipFiles).add((Component)this.lateSubmission).add((Component)this.jLabel32, -2, 23, -2))).add(2, (GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0).add((Component)this.trainingSite))).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(9, 9, 9).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.previousPt3s, -2, 24, -2).add((Component)this.surname, -2, -1, -2).add((Component)this.forenames, -2, -1, -2).add((Component)this.ou_computer_user_name, -2, -1, -2))).add((GroupLayout.Group)layout.createSequentialGroup().add(6, 6, 6).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.listAllScores).add((Component)this.backUp, -2, 26, -2)))).addPreferredGap(0).add((Component)this.openReturnsFolder)).add((GroupLayout.Group)layout.createSequentialGroup().add(41, 41, 41).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.partScoresButton).add((Component)this.checkSpellingButton, -2, 25, -2).add((Component)this.openCommentBank).add((Component)this.bankComment)))).add(6, 6, 6).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(7, 7, 7).add((GroupLayout.Group)layout.createParallelGroup(2).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel30).add((Component)this.fontSize, -2, 24, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel22).add(1, 1, 1)))).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.checkTotals).add((Component)this.submission_status, -2, -1, -2).add((Component)this.overall_grade_score, -2, -1, -2).add((Component)this.jLabel2).add((Component)this.jLabel10).add((Component)this.e_tma_submission_num, -2, -1, -2))).add(10, 10, 10).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tmaMarks, -2, 358, -2).add(18, 18, 18).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel6).add((Component)this.jLabel4).add((Component)this.region_code, -2, -1, -2).add((Component)this.course_code, -2, -1, -2).add((Component)this.jLabel9).add((Component)this.assgnmt_suffix, -2, -1, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.title, -2, 19, -2).add((Component)this.personal_id, -2, -1, -2)).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(6, 6, 6).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.pres_code, -2, 19, -2).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel13).add((Component)this.total_question_count, -2, -1, -2).add((Component)this.jLabel14).add((Component)this.permitted_question_count, -2, -1, -2).add((Component)this.sendEmail, -2, 25, -2).add((Component)this.jLabel8, -2, 13, -2))).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.marked_date, -2, -1, -2).add((Component)this.jLabel12)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel16).add((Component)this.zip_date, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel19).add((Component)this.walton_received_date, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.zip_file, -2, -1, -2).add((Component)this.jLabel15, -2, 14, -2).add((Component)this.staff_forenames, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel11).add((Component)this.e_tma_submission_date, -2, -1, -2).add((Component)this.staff_surname, -2, -1, -2).add((Component)this.address_line5, -2, -1, -2))).add((GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.initials, -2, -1, -2).add((Component)this.email_address, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.address_line1, -2, -1, -2).add((Component)this.openPreferences)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.address_line2, -2, -1, -2).add((Component)this.staff_title, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.address_line3, -2, -1, -2).add((Component)this.staff_initials, -2, 19, -2)).addPreferredGap(0).add((Component)this.address_line4, -2, -1, -2).add(35, 35, 35).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.postcode, -2, -1, -2).add((Component)this.staff_id, -2, -1, -2).add((Component)this.jLabel18).add((Component)this.max_assgnmt_score, -2, -1, -2).add((Component)this.jLabel17).add((Component)this.score_update_allowed, -2, -1, -2).add((Component)this.jLabel7).add((Component)this.course_version_num, -2, -1, -2))))))).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tutor_comments_area, -2, 308, -2).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0).add((Component)this.jLabel1, -2, -1, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(6, 6, 6).add((Component)this.jScrollPane2, -2, 54, -2))))).addPreferredGap(0, -1, 32767).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(5, 5, 5).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.fhiFileName, -2, -1, -2).add((Component)this.late_submission_status, -2, -1, -2))).add((GroupLayout.Group)layout.createSequentialGroup().add(34, 34, 34).add((Component)this.loadXMLAlt)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.studentList, -2, 14, -2).add(21, 21, 21).add((Component)this.tutor_comments, -2, -1, -2).add(13, 13, 13).add((Component)this.subNo, -2, 18, -2)))));
        this.pack();
    }
    
    private void exportMarksGridActionPerformed(final ActionEvent evt) {
        this.exportMarksGrid(this.tmaScores, "marksGrid", 0);
    }
    
    private void showLatestFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("showLatestFlag", this.showLatestFlag.isSelected());
    }
    
    private void addInitialsFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("resizeFlag", this.addInitialsFlag.isSelected());
    }
    
    private void doubleClickFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("doubleClickFlag", this.doubleClickFlag.isSelected());
    }
    
    private void weightingsWindowClosing(final WindowEvent evt) {
        this.saveWeightings();
    }
    
    private void directEntryFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("directEntryPreference", this.directEntryFlag.isSelected());
    }
    
    private void customizeActionPerformed(final ActionEvent evt) {
        this.restoreHidePreferences();
        this.custom.setSize(470, 450);
        this.custom.setVisible(true);
    }
    
    public void restoreHidePreferences() {
        final JCheckBox[] customBoxes = { this.hideEtmaSite, this.hideListTmas, this.hideOpenTmaFolder, this.hideTrainingSite, this.hideSavePt3, this.hideECollectTmas, this.hideOpenTma, this.hideOpenCommentBank, this.hideZipFiles, this.hideBankComment, this.hideBackupEtmas, this.hideSendEmail, this.hideOpenPreferences, this.hideTestJs, this.hideListGrades, this.hideOpenReturnsFolder };
        final String buttonPref = this.ourRoot.get("hidingPreferences", "false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false|");
        final String[] buttonPref2 = buttonPref.split(",");
        Boolean buttonStatus = false;
        for (int i = 0; i < customBoxes.length; ++i) {
            // Bounds check to prevent ArrayIndexOutOfBoundsException
            if (i < buttonPref2.length && buttonPref2[i].equals("true")) {
                buttonStatus = true;
            }
            else {
                buttonStatus = false;
            }
            customBoxes[i].setSelected(buttonStatus);
        }
    }
    
    public void exportMarksGrid(final JTable marksTable, String marksFile, final int headerNo) {
        final char returnChar = '\r';
        final char tabChar = '\t';
        marksFile = marksFile + this.course_code.getText() + "-" + this.pres_code.getText();
        if (headerNo == 0) {
            marksFile = marksFile + "-TMA" + this.assgnmt_suffix.getText();
        }
        final String[] tableHeader = { "Qn No" + tabChar + "PartNo" + tabChar + "Score" + tabChar + "Max" + tabChar + "Totals" + tabChar + "Qn Max" + returnChar, "PID" + tabChar + "Forenames" + tabChar + "Surname" + tabChar + "oucu" + tabChar + "00" + tabChar + "01" + tabChar + "02" + tabChar + "03" + tabChar + "04" + tabChar + "05" + tabChar + "06" + tabChar + "07" + tabChar + "08" + tabChar + "09" + tabChar + "10" + tabChar + "11" + tabChar + "Ave" + tabChar + "Mod %" + returnChar };
        String gridFile = "";
        String currentEntry = "";
        if (headerNo == 0) {
            gridFile = gridFile + this.forenames.getText() + " " + this.surname.getText() + " " + this.ou_computer_user_name.getText() + returnChar;
        }
        gridFile += tableHeader[headerNo];
        final int nRows = marksTable.getRowCount();
        final int nColumns = marksTable.getColumnCount();
        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nColumns; ++j) {
                currentEntry = (String)marksTable.getValueAt(i, j);
                try {
                    currentEntry.length();
                }
                catch (final Exception anException) {
                    currentEntry = "";
                }
                gridFile = gridFile + currentEntry + tabChar;
            }
            gridFile += returnChar;
        }
        final File bFile = new File(this.downloadsFolder.getText());
        final String aFolder = bFile.getParent();
        final JFileChooser _fileChooser = new JFileChooser(aFolder);
        _fileChooser.setFileSelectionMode(1);
        _fileChooser.setDialogTitle("Please select folder to save the exported file to:");
        final int path = _fileChooser.showOpenDialog(null);
        final File aFile = _fileChooser.getSelectedFile();
        final String aFolderName = aFile.getPath();
        String aFileName = aFolderName + "/" + marksFile + ".txt";
        File cFile = new File(aFileName);
        int subScript = 1;
        String marksFile2 = marksFile;
        while (cFile.exists()) {
            marksFile2 = marksFile + "." + subScript;
            aFileName = aFolderName + "/" + marksFile2 + ".txt";
            cFile = new File(aFileName);
            ++subScript;
        }
        this.saveFhiString(aFileName, gridFile);
        JOptionPane.showMessageDialog((Component)null, "File has been saved to " + aFolderName + "/" + marksFile2 + ".txt\nas a tab-delimited text file.");
    }
    
    private void testJsMenuActionPerformed(final ActionEvent evt) {
    }
    
    private void zipFilesMenuActionPerformed(final ActionEvent evt) {
        this.zipper();
    }
    
    private void checkTotalsMenuActionPerformed(final ActionEvent evt) {
        this.calculateTotals();
    }
    
    private void openCommentBankMenuActionPerformed(final ActionEvent evt) {
        this.commentBankOpener();
    }
    
    private void openTmaMenuActionPerformed(final ActionEvent evt) {
        this.tmaScriptOpener();
    }
    
    private void collectTmasMenuActionPerformed(final ActionEvent evt) {
        this.tmaCollector();
    }
    
    private void openReturnsFolderMenuActionPerformed(final ActionEvent evt) {
        this.returnsFolderOpener();
    }
    
    private void openTmaFolderMenuActionPerformed(final ActionEvent evt) {
        this.tmaFolderOpener();
    }
    
    private void listGradesActionPerformed(final ActionEvent evt) {
        this.openGradesListAlt();
    }
    
    private void trainingSiteMenuActionPerformed(final ActionEvent evt) {
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        final String myURI = "http://etma-training.open.ac.uk/etma/tutor/etmat_training_signon.asp";
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }
    
    private void etmaSiteMenuActionPerformed(final ActionEvent evt) {
        this.openMainEtmaSite();
    }
    
    private void colorWindowSelectorActionPerformed(final ActionEvent evt) {
        this.saveColors(this.currentColorIndex);
        final int selNo = this.colorWindowSelector.getSelectedIndex();
        this.currentColorIndex = selNo;
        if (selNo > 0) {
            if (this.colorDefaultsFlag[selNo - 1]) {
                this.defaultFlag.setSelected(true);
            }
            else {
                this.defaultFlag.setSelected(false);
            }
        }
    }
    
    public void buttonHider() {
        final JCheckBox[] customBoxes = { this.hideEtmaSite, this.hideListTmas, this.hideOpenTmaFolder, this.hideTrainingSite, this.hideSavePt3, this.hideECollectTmas, this.hideOpenTma, this.hideOpenCommentBank, this.hideZipFiles, this.hideBankComment, this.hideBackupEtmas, this.hideSendEmail, this.hideOpenPreferences, this.hideTestJs, this.hideListGrades, this.hideOpenReturnsFolder };
        this.etmaSite.setVisible(!this.hideEtmaSite.isSelected());
        this.openTmaList.setVisible(!this.hideListTmas.isSelected());
        this.openTmaFolder.setVisible(!this.hideOpenTmaFolder.isSelected());
        this.trainingSite.setVisible(!this.hideTrainingSite.isSelected());
        this.savePt3.setVisible(!this.hideSavePt3.isSelected());
        this.collectTmas.setVisible(!this.hideECollectTmas.isSelected());
        this.openTma.setVisible(!this.hideOpenTma.isSelected());
        this.openCommentBank.setVisible(!this.hideOpenCommentBank.isSelected());
        this.zipFiles.setVisible(!this.hideZipFiles.isSelected());
        this.bankComment.setVisible(!this.hideBankComment.isSelected());
        this.backUp.setVisible(!this.hideBackupEtmas.isSelected());
        this.sendEmail.setVisible(!this.hideSendEmail.isSelected());
        this.checkSpellingButton.setVisible(!this.hideTestJs.isSelected());
        this.listAllScores.setVisible(!this.hideListGrades.isSelected());
        this.openReturnsFolder.setVisible(!this.hideOpenReturnsFolder.isSelected());
        final Color bgColor = this.checkTotals.getBackground();
        if (this.colorRemove.isSelected()) {
            this.etmaSite.setBackground(bgColor);
            this.openTmaList.setBackground(bgColor);
            this.openTmaFolder.setBackground(bgColor);
            this.trainingSite.setBackground(bgColor);
            this.savePt3.setBackground(bgColor);
            this.collectTmas.setBackground(bgColor);
            this.openCommentBank.setBackground(bgColor);
            this.zipFiles.setBackground(bgColor);
            this.bankComment.setBackground(bgColor);
            this.backUp.setBackground(bgColor);
            this.sendEmail.setBackground(bgColor);
            this.openPreferences.setBackground(bgColor);
            this.checkSpellingButton.setBackground(bgColor);
            this.listAllScores.setBackground(bgColor);
            this.openTma.setBackground(bgColor);
            this.openReturnsFolder.setBackground(bgColor);
            this.moreDetails.setBackground(bgColor);
            this.setEtmasFolder.setBackground(bgColor);
            this.selectDownloadsFolder.setBackground(bgColor);
            this.setCommentBankFile.setBackground(bgColor);
            this.setJsTestFile.setBackground(bgColor);
            this.setTmaWeightings.setBackground(bgColor);
            this.selectDictionary.setBackground(bgColor);
            this.wpSelect.setBackground(bgColor);
            this.browserSelect.setBackground(bgColor);
            this.createMarked.setBackground(bgColor);
            this.checkClosureFlag.setBackground(bgColor);
            this.launchTmaList.setBackground(bgColor);
            this.doubleClickFlag.setBackground(bgColor);
            this.addInitialsFlag.setBackground(bgColor);
            this.setCommentBankEditor.setBackground(bgColor);
            this.setAudioApp.setBackground(bgColor);
            this.partScoresButton.setBackground(bgColor);
            this.selectAll.setBackground(bgColor);
            this.batchZip.setBackground(bgColor);
            this.listOfTmas.setBackground(Color.white);
        }
        String buttonPref = "";
        for (int i = 0; i < customBoxes.length; ++i) {
            buttonPref += customBoxes[i].isSelected();
        }
        this.ourRoot.putBoolean("colorRemovePreferences", this.colorRemove.isSelected());
        this.ourRoot.put("hidingPreferences", buttonPref);
    }
    
    private void defaultFlagActionPerformed(final ActionEvent evt) {
        final int selNo = this.colorWindowSelector.getSelectedIndex();
        if (selNo > 0) {
            this.colorDefaultsFlag[selNo - 1] = this.defaultFlag.isSelected();
            this.ourRoot.putBoolean("colorDefaults0", this.colorDefaultsFlag[0]);
            this.ourRoot.putBoolean("colorDefaults1", this.colorDefaultsFlag[1]);
            this.ourRoot.putBoolean("colorDefaults2", this.colorDefaultsFlag[2]);
        }
    }
    
    private void colorFrame1WindowClosing(final WindowEvent evt) {
        this.saveColors(this.colorWindowSelector.getSelectedIndex());
        this.colorFrame1.setVisible(false);
        if (!this.startUp) {
            JOptionPane.showMessageDialog(null, "You may have to relaunch for the colour changes to come into effect.", "", 1);
        }
        System.out.println(this.colorWindowSelector.getSelectedIndex());
    }
    
    public void setColors() {
        final String[] allColors = this.colorPreferences.split(":");
        final String[] mainColor = allColors[0].split(";");
        final String[] commentsColor = allColors[1].split(";");
        this.gridColor = allColors[2].split(";");
        if (!this.colorDefaultsFlag[0]) {
            this.setBackground(new Color(Integer.parseInt(mainColor[0]), Integer.parseInt(mainColor[1]), Integer.parseInt(mainColor[2])));
        }
        if (!this.colorDefaultsFlag[1]) {
            this.tutor_comments_input.setBackground(new Color(Integer.parseInt(commentsColor[0]), Integer.parseInt(commentsColor[1]), Integer.parseInt(commentsColor[2])));
        }
        if (!this.colorDefaultsFlag[2]) {
            this.tmaScores.setBackground(new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2])));
        }
    }
    
    public void saveColors(final int selNo) {
        Color newColor = null;
        String[] allColors = null;
        String[] mainColor = null;
        String[] commentsColor = null;
        this.gridColor = null;
        if (selNo > 0) {
            if (!this.colorDefaultsFlag[selNo - 1]) {
                newColor = this.jColorChooser1.getSelectionModel().getSelectedColor();
            }
            else {
                allColors = "219;219;219:255;255;255:204;255;255".split(":");
                final String[] tempString = allColors[selNo - 1].split(";");
                final int redColor = Integer.parseInt(tempString[0]);
                final int blueColor = Integer.parseInt(tempString[1]);
                final int greenColor = Integer.parseInt(tempString[2]);
                newColor = new Color(redColor, blueColor, greenColor);
            }
            if (selNo == 1) {
                final JFrame aFrame = this;
                aFrame.setBackground(newColor);
            }
            if (selNo == 2) {
                final JTextArea aFrame2 = this.tutor_comments_input;
                aFrame2.setBackground(newColor);
            }
            if (selNo == 3) {
                final JTable aFrame3 = this.tmaScores;
                aFrame3.setBackground(newColor);
            }
            final int redColor2 = newColor.getRed();
            final int blueColor2 = newColor.getBlue();
            final int greenColor2 = newColor.getGreen();
            allColors = this.colorPreferences.split(":");
            mainColor = allColors[0].split(";");
            commentsColor = allColors[1].split(";");
            this.gridColor = allColors[2].split(";");
            allColors[selNo - 1] = redColor2 + ";" + greenColor2 + ";" + blueColor2;
            this.colorPreferences = allColors[0] + ":" + allColors[1] + ":" + allColors[2];
            this.ourRoot.put("colorPreferences", this.colorPreferences);
        }
    }
    
    private void chooseColorActionPerformed(final ActionEvent evt) {
        this.colorFrame1.setVisible(true);
        this.colorFrame1.setSize(650, 400);
        this.currentColorIndex = this.colorWindowSelector.getSelectedIndex();
        System.out.println(this.currentColorIndex);
    }
    
    private void gradesSummaryTableMouseReleased(final MouseEvent evt) {
        this.openSelectedScore();
    }
    
    private void tutor_comments_inputFocusGained(final FocusEvent evt) {
    }
    
    private void submittedTmasWindowClosing(final WindowEvent evt) {
        this.saveLocation();
    }
    
    private void formWindowClosing(final WindowEvent evt) {
        this.exitRoutine();
    }
    
    private void formWindowOpened(final WindowEvent evt) {
        if (this.launchTmaList.isSelected() && !this.tmaListError) {
            this.submittedTmas.setVisible(true);
        }
        this.submittedTmas.toFront();
        this.tmaListError = false;
    }
    
    private void launchTmaListActionPerformed(final ActionEvent evt) {
        this.launchTmaListFlagPreferences = this.launchTmaList.isSelected();
        this.ourRoot.putBoolean("launchTmaListFlagPreferences", this.launchTmaList.isSelected());
    }
    
    private void checkClosureFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("checkClosureFlagPreferences", this.checkClosureFlag.isSelected());
        this.checkClosureFlagPreferences = this.checkClosureFlag.isSelected();
    }
    
    private void browserSelectActionPerformed(final ActionEvent evt) {
        this.setBrowserPreferences();
    }
    
    public void setBrowserPreferences() {
        this.ourRoot.put("currentBrowserPreferences", this.currentBrowserPreferences);
        final JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentBrowserPreferences = JOptionPane.showInputDialog(null, "Please enter the short (launch) name of your browser (eg 'firefox').");
            }
            else {
                _fileChooser.setDialogTitle("Please select application to test students'Javascripts ");
                final int path = _fileChooser.showOpenDialog(null);
                final File aFile = _fileChooser.getSelectedFile();
                this.currentBrowserPreferences = aFile.getPath();
            }
            this.browserPath.setText(this.currentBrowserPreferences);
            this.ourRoot.put("currentBrowserPreferences", this.currentBrowserPreferences);
        }
        catch (final Exception ex) {}
    }
    
    public void setWpPreferences() {
        this.ourRoot.put("currentWpPreferences", this.currentWpPreferences);
        final JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentWpPath = JOptionPane.showInputDialog(null, "Please enter the short(launch) name of your Word Processor (eg'ooffice')\n or leave blank for System Default");
                if (this.currentWpPath.equals("")) {
                    this.currentWpPath = "System Default";
                }
            }
            else {
                final Object[] options = { "Default", "Select other" };
                final JFrame frame = null;
                final int n = JOptionPane.showOptionDialog(frame, "Do you want to use the system default word processor?", "Default WP", 1, 3, null, options, options[0]);
                if (n == 0) {
                    this.currentWpPath = "System Default";
                    this.currentWpPreferences = "System Default";
                }
                else {
                    _fileChooser.setDialogTitle("Please select application to open students' scripts: ");
                    final int path = _fileChooser.showOpenDialog(null);
                    final File aFile = _fileChooser.getSelectedFile();
                    this.currentWpPath = aFile.getPath();
                }
            }
            JOptionPane.showMessageDialog((Component)null, "Word processor path set to " + this.currentWpPath);
            this.wpPath.setText(this.currentWpPath);
            this.ourRoot.put("currentWpPreferences", this.currentWpPath);
        }
        catch (final Exception ex) {}
    }
    
    private void tutor_comments_inputKeyReleased(final KeyEvent evt) {
        final KeyStroke thisKey = KeyStroke.getKeyStrokeForEvent(evt);
        final KeyStroke commandS = KeyStroke.getKeyStroke(83, 4, true);
        final int thisCode = thisKey.getKeyCode();
        if (this.spellCheckFlag.isSelected() && (thisCode == 32 || thisCode == 10)) {
            if (this.suggestFlag.isSelected()) {
                this.liveSpellCheck();
            }
            this.removeHighlights(this.tutor_comments_input);
            this.highlightAllErrors();
        }
        if (!commandS.equals(thisKey)) {
            this.savedFlag = false;
        }
    }
    
    public void highlightAllErrors() {
        final File aFile = new File(this.dictionaryPath.getText());
        final String[] allWords = this.tutor_comments_input.getText().split(" ");
        final int numWords = allWords.length;
        try {
            this.dictionary = (SpellDictionary)new SpellDictionaryHashMap(aFile);
            this.spellChecker = new SpellChecker(this.dictionary);
            for (int i = 0; i <= numWords - 1; ++i) {
                final Suggest.SuggestionListener sl = new Suggest.SuggestionListener();
                Suggest.SuggestionListener.wrongWord = "";
                Suggest.SuggestionListener.spellOutputList.clear();
                this.spellChecker.addSpellCheckListener((SpellCheckListener)sl);
                this.spellChecker.checkSpelling((WordTokenizer)new StringWordTokenizer(allWords[i]));
                final String wrongWord = Suggest.SuggestionListener.wrongWord;
                this.spellChecker.removeSpellCheckListener((SpellCheckListener)sl);
                if (!wrongWord.equals("")) {
                    this.spellHighlight(wrongWord);
                    this.highlight(this.tutor_comments_input, " " + wrongWord);
                }
            }
        }
        catch (final Exception anException) {
            System.out.println("Error " + anException);
        }
    }
    
    public void liveSpellCheck() {
        this.cursorPos = this.tutor_comments_input.getCaretPosition();
        if (this.cursorPos > 1) {
            final String currentText = this.tutor_comments_input.getText().substring(0, this.cursorPos);
            final String lastCharacter = currentText.substring(currentText.length() - 1);
            if (lastCharacter.equals(" ")) {
                String[] wordList = null;
                wordList = currentText.split(" ");
                final String lastWord = wordList[wordList.length - 1];
                try {
                    final Suggest.SuggestionListener sl = new Suggest.SuggestionListener();
                    Suggest.SuggestionListener.wrongWord = "";
                    this.spellChecker.addSpellCheckListener((SpellCheckListener)sl);
                    this.spellChecker.checkSpelling((WordTokenizer)new StringWordTokenizer(lastWord));
                    this.spellReplace();
                    final String wrongWord = Suggest.SuggestionListener.wrongWord;
                    this.spellChecker.removeSpellCheckListener((SpellCheckListener)sl);
                    if (!wrongWord.equals("")) {}
                }
                catch (final Exception anException) {
                    System.out.println("Error " + anException);
                }
            }
        }
    }
    
    private void deleteAttachmentActionPerformed(final ActionEvent evt) {
        this.attFlag = false;
        String message = this.jTextArea1.getText();
        final String attString = "Attachment: " + this.attachmentFile.getName();
        message = message.replaceAll(attString, "");
        this.jTextArea1.setText(message);
    }
    
    private void addAttachmentButtonActionPerformed(final ActionEvent evt) {
        this.addAttachment();
    }
    
    private void backUpMenuActionPerformed(final ActionEvent evt) {
        this.backUp();
    }
    
    private void sendEmailMenuActionPerformed(final ActionEvent evt) {
        this.chooseEmailMethod();
    }
    
    private void wpSelectActionPerformed(final ActionEvent evt) {
        this.setWpPreferences();
    }
    
    public void setWpMap() {
        this.wpMap.put("Microsoft Word", "com.microsoft.Word");
        this.wpMap.put("NeoOffice.app", "org.neooffice.NeoOffice");
        this.wpMap.put("AppleWorks 6.app", "com.apple.appleworks");
        this.wpMap.put("AppleWorks.app", "com.apple.appleworks");
        this.wpMap.put("TextEdit.app", "e");
        this.wpMap.put("OpenOffice.org 2.3.app", "org.openoffice.script");
        this.wpMap.put("OpenOffice.org 2.2.app", "org.openoffice.script");
        this.wpMap.put("OpenOffice.org 2.4.app", "org.openoffice.script");
        this.wpMap.put("OpenOffice.org.app", "org.openoffice.script");
        this.wpMap.put("Pages.app", "com.apple.iWork.Pages");
        this.wpMap.put("Netscape.app", "com.netscape.mozilla");
        this.wpMap.put("Firefox.app", "org.mozilla.firefox");
        this.wpMap.put("Safari.app", "com.apple.Safari");
        this.wpMap.put("Internet Explorer.app", "com.microsoft.explorer");
        this.wpMap.put("NavigatorX.app", "org.netscape.navigator");
        this.wpMap.put("Navigator.app", "org.netscape.navigator");
        this.wpMap.put("Camino.app", "org.mozilla.camino");
    }
    
    public void setWpMapAlt() {
        this.wpMap1.put("Microsoft Word", "");
        this.wpMap1.put("MicrosoftWord", "");
        this.wpMap1.put("NeoOffice.app", "/Contents/MacOS/soffice.bin");
        this.wpMap1.put("AppleWorks 6.app", "/Contents/MacOS/AppleWorks 6");
        this.wpMap1.put("AppleWorks.app", "/Contents/MacOS/AppleWorks 6");
        this.wpMap1.put("TextEdit.app", "/Contents/MacOS/TextEdit.app");
        this.wpMap1.put("OpenOffice.org 2.3.app", "/Contents/MacOS/soffice.bin");
        this.wpMap1.put("OpenOffice.org 2.2.app", "/Contents/MacOS/soffice.bin");
        this.wpMap1.put("OpenOffice.org 2.4.app", "/Contents/MacOS/soffice.bin");
        this.wpMap1.put("OpenOffice.org.app", "/Contents/MacOS/soffice.bin");
        this.wpMap1.put("Pages.app", "/Contents/MacOS/Pages");
        this.wpMap1.put("Netscape.app", "com.netscape.mozilla");
        this.wpMap1.put("Firefox.app", "org.mozilla.firefox");
        this.wpMap1.put("Safari.app", "com.apple.Safari");
        this.wpMap1.put("Internet Explorer.app", "com.microsoft.explorer");
        this.wpMap1.put("NavigatorX.app", "org.netscape.navigator");
        this.wpMap1.put("Navigator.app", "org.netscape.navigator");
        this.wpMap1.put("Camino.app", "org.mozilla.camino");
    }
    
    private void selectDictionaryActionPerformed(final ActionEvent evt) {
        final JFileChooser _fileChooser = new JFileChooser(this.etmasFolder.getText());
        _fileChooser.setDialogTitle("Please select the dictionary file for the Spellchecker");
        final int path = _fileChooser.showOpenDialog(null);
        final File aFile = _fileChooser.getSelectedFile();
        this.dictionaryPath.setText(aFile.getPath());
        EtmaHandlerJ.dictionaryPathPreferences = this.dictionaryPath.getText();
        this.ourRoot.put("dictionaryPathPreferences", aFile.getPath());
    }
    
    private void spellCheckFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("spellCheckFlagPreferences", this.spellCheckFlag.isSelected());
        if (!this.spellCheckFlag.isSelected()) {
            this.suggestFlag.setSelected(false);
            this.suggestFlag.setEnabled(false);
        }
        else {
            this.suggestFlag.setEnabled(true);
        }
    }
    
    private void checkSpellingActionPerformed(final ActionEvent evt) {
        this.savedFlag = false;
        this.spellCheckComments();
    }
    
    private void backUpActionPerformed(final ActionEvent evt) {
        this.backUp();
    }
    
    public void copyDirectory(final File sourceLocation, final File targetLocation) throws IOException {
        this.messageWindow.setSize(400, 10);
        this.messageWindow.setVisible(true);
        this.messageText.setVisible(true);
        this.messageWindow.setTitle("Please wait.... backing up....");
        this.messageWindow.setLocation(300, 300);
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            final String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; ++i) {
                this.copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        }
        else {
            final InputStream in = new FileInputStream(sourceLocation);
            final OutputStream out = new FileOutputStream(targetLocation);
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }
    
    public void backUp() {
        final List<String> errorList = new ArrayList<String>();
        final File aFile = new File(this.etmasFolder.getText());
        final JFileChooser _fileChooser = new JFileChooser();
        _fileChooser.setFileSelectionMode(1);
        _fileChooser.setDialogTitle("Choose backup location:");
        final int path = _fileChooser.showOpenDialog(null);
        final String newLocation = _fileChooser.getSelectedFile().getPath() + "/etmasBackup_" + this.getDateAndTime();
        final File bFile = new File(newLocation);
        final Object[] options = { "Backup", "Cancel" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to backup?\nThis could take a couple of minutes.", "Backup", 1, 3, null, options, options[1]);
        if (n == 0) {
            try {
                this.copyDirectory(aFile, bFile);
                this.messageWindow.setVisible(false);
                JOptionPane.showMessageDialog((Component)null, "Backup successful.\nThe backup folder is at " + newLocation);
            }
            catch (final Exception anException) {
                this.messageWindow.setVisible(false);
                JOptionPane.showMessageDialog(null, "Backup failed", "", 0);
            }
        }
    }
    
    private void selectAllTextActionPerformed(final ActionEvent evt) {
        final Component tempComp = this.getFocusOwner();
        try {
            final JTextField tempComp2 = (JTextField)tempComp;
            tempComp2.selectAll();
        }
        catch (final Exception anException) {
            this.tutor_comments_input.selectAll();
        }
    }
    
    public static void sendKey(final int ctrlValue, final int keyValue) {
        Robot robot = null;
        try {
            robot = new Robot();
        }
        catch (final Throwable e) {
            System.out.println("Could not create new robot.");
            e.printStackTrace();
            System.exit(0);
        }
        if (ctrlValue != -1) {
            robot.keyPress(ctrlValue);
        }
        robot.keyPress(keyValue);
        robot.keyRelease(keyValue);
        if (ctrlValue != -1) {
            robot.keyRelease(ctrlValue);
        }
    }
    
    private void pasteTextActionPerformed(final ActionEvent evt) {
        final int keyModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        String textToBePasted = this.getClipBoard();
        textToBePasted = textToBePasted.replaceAll(this.rtnString, this.lfString);
        final int pastedTextLength = textToBePasted.length();
        final int cursorPos1 = this.tutor_comments_input.getSelectionStart();
        final int textLength = this.tutor_comments_input.getText().length();
        final String firstSlice = this.tutor_comments_input.getText().substring(0, cursorPos1);
        String lastSlice = "";
        try {
            lastSlice = this.tutor_comments_input.getText().substring(cursorPos1, textLength);
        }
        catch (final Exception ex) {}
        this.tutor_comments_input.setText(firstSlice + textToBePasted + lastSlice);
        this.tutor_comments_input.setSelectionStart(cursorPos1 + pastedTextLength);
        this.tutor_comments_input.setSelectionEnd(cursorPos1 + pastedTextLength);
    }
    
    private void copyTextActionPerformed(final ActionEvent evt) {
        String selectedText = "";
        final Component tempComp = this.getFocusOwner();
        try {
            final JTextField tempComp2 = (JTextField)tempComp;
            selectedText = tempComp2.getSelectedText();
        }
        catch (final Exception anException) {
            selectedText = this.tutor_comments_input.getSelectedText();
        }
        final StringSelection textToCopy = new StringSelection(selectedText);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }
    
    private void RedoActionPerformed(final ActionEvent evt) {
        try {
            this.undo.redo();
        }
        catch (final CannotUndoException ex) {
            System.out.println("Unable to undo: " + ex);
            ex.printStackTrace();
        }
    }
    
    private void UndoActionPerformed(final ActionEvent evt) {
        try {
            this.undo.undo();
        }
        catch (final CannotUndoException ex) {
            System.out.println("Unable to undo: " + ex);
            ex.printStackTrace();
        }
    }
    
    private void toolTipFlagActionPerformed(final ActionEvent evt) {
        this.toolTipFlagPreferences = this.toolTipFlag.isSelected();
        this.ourRoot.putBoolean("toolTipFlagPreferences", this.toolTipFlag.isSelected());
        ToolTipManager.sharedInstance().setEnabled(this.toolTipFlag.isSelected());
    }
    
    private void autoFillFlagActionPerformed(final ActionEvent evt) {
        this.autoFillFlagPreferences = this.autoFillFlag.isSelected();
        this.ourRoot.putBoolean("autoFillFlagPreferences", this.autoFillFlag.isSelected());
    }
    
    private void authenticationFlagActionPerformed(final ActionEvent evt) {
    }
    
    private void saveMailPreferencesActionPerformed(final ActionEvent evt) {
        this.ourRoot.put("userNamePreferences", this.mailUserName.getText());
        this.ourRoot.put("yourEmailAddressPreferences", this.yourEmailAddress.getText());
        final char[] passwordTemp = this.mailPassword.getPassword();
        String passwordString = "";
        for (int i = 0; i < passwordTemp.length; ++i) {
            passwordString += passwordTemp[i];
        }
        this.ourRoot.put("passwordPreferences", passwordString);
        this.passwordPreferences = passwordString;
        this.ourRoot.put("smtpServerPreferences", this.smtpHost.getText());
        this.ourRoot.putBoolean("authenticationFlagPreferences", this.authenticationFlag.isSelected());
        this.mailPreferencesFrame.setVisible(false);
    }
    
    private void mailPreferencesActionPerformed(final ActionEvent evt) {
        this.mailPreferencesFrame.setVisible(true);
        this.mailPreferencesFrame.setSize(400, 230);
    }
    
    private void sendButtonActionPerformed(final ActionEvent evt) {
        final String testPreferences = this.smtpHost.getText();
        if (testPreferences.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter details in 'Mail Preferences'");
        }
        else {
            try {
                this.postMail(this.emailRecipients, this.messageSubject.getText(), this.jTextArea1.getText(), "michael.hay@btinternet.com");
                JOptionPane.showMessageDialog(null, "Email has been sent!");
                this.attFlag = false;
            }
            catch (final Exception anException) {
                JOptionPane.showMessageDialog((Component)null, "Email has NOT been sent! \n" + anException, "", 0);
            }
        }
    }
    
    public void progressBar(final int currValue, final JProgressBar pb) {
        pb.setIndeterminate(false);
        pb.setName("Unzip Progress");
        pb.setValue(currValue);
        pb.setVisible(true);
        pb.setStringPainted(true);
        pb.setBorderPainted(true);
        this.messageWindow3.toFront();
        pb.repaint();
        pb.paintImmediately(0, 0, 300, 100);
        this.messageWindow3.repaint();
    }
    
    public JProgressBar progressBarCreate(final int max, final int min, final String barName) {
        this.messageWindow3.setVisible(true);
        this.messageWindow3.setSize(300, 100);
        this.messageWindow3.setLocation(500, 300);
        final JProgressBar pb = new JProgressBar(max, min);
        this.messageWindow3.setTitle(barName);
        this.messageWindow3.add(pb);
        this.messageWindow3.validate();
        return pb;
    }
    
    public boolean unZipAlt() {
        this.courseDirectory = "";
        this.courseDirectoryList.clear();
        String unzippedFileParent = "";
        boolean nullFlag = false;
        boolean zipFlag = true;
        int path = 0;
        final JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
        final ZipFilter filter1 = new ZipFilter();
        _fileChooser.setFileFilter(filter1);
        _fileChooser.setDialogTitle("Please select a downloaded file to unzip:eg 2008-02-21_2117.zip");
        File aFile = null;
        if (!this.foundItZip) {
            path = _fileChooser.showOpenDialog(null);
            aFile = _fileChooser.getSelectedFile();
        }
        else {
            aFile = new File(this.fileToUnzip);
        }
        if (path == 1) {
            zipFlag = false;
        }
        else {
            unzippedFileParent = aFile.getParent();
            try {
                aFile.getName();
                this.unzippedFilePath = aFile.getPath();
                unzippedFileParent = aFile.getParent();
            }
            catch (final Exception anException) {
                nullFlag = true;
            }
            this.messageWindow.setSize(400, 10);
            this.messageWindow.setTitle("Please wait.... unzipping....");
            this.messageWindow.setLocation(300, 300);
            this.messageWindow.setAlwaysOnTop(true);
            JProgressBar pb = null;
            try {
                int numberOfFiles = 0;
                final ZipFile zf = new ZipFile(aFile.getPath());
                final int zfSize = zf.size();
                pb = this.progressBarCreate(0, zfSize, "Unzipping progress");
                final Enumeration zipEnum = zf.entries();
                String dir = new String(aFile.getParent());
                if (dir.charAt(dir.length() - 1) != '/') {
                    dir = dir;
                }
                while (zipEnum.hasMoreElements()) {
                    final ZipEntry item = (ZipEntry) zipEnum.nextElement();
                    System.out.println(item);
                    ++numberOfFiles;
                    this.progressBar(numberOfFiles, pb);
                    this.messageWindow.setTitle("Please wait.... unzipping.... file " + numberOfFiles + " of " + zfSize);
                    if (item.isDirectory()) {
                        final String dirPath = dir + item.getName();
                        String reducedPath = dirPath.replace(unzippedFileParent, "");
                        reducedPath = reducedPath.replaceFirst("/", "");
                        reducedPath = reducedPath.replaceFirst("/", "");
                        final File newdir = new File(dirPath);
                        if (this.courseDirectory.equals("")) {
                            this.courseDirectory = newdir.getPath();
                        }
                        if (!reducedPath.contains("/") && !reducedPath.contains("\\")) {
                            this.courseDirectoryList.add(newdir.getPath());
                        }
                        newdir.mkdir();
                    }
                    else {
                        try {
                            final String newfile = dir + item.getName();
                            final InputStream is = zf.getInputStream(item);
                            final FileOutputStream fos = new FileOutputStream(newfile);
                            int ch;
                            while ((ch = is.read()) != -1) {
                                fos.write(ch);
                            }
                            is.close();
                            fos.close();
                        }
                        catch (final Exception anException2) {
                            System.out.println("Mike error " + anException2);
                        }
                    }
                }
                zf.close();
                this.messageWindow.setVisible(false);
                if (!this.foundItZip) {
                    JOptionPane.showMessageDialog((Component)null, zfSize + " files unzipped to " + dir);
                    this.messageWindow3.remove(pb);
                }
            }
            catch (final Exception e) {
                this.messageWindow.setVisible(false);
                System.err.println(e);
                if (!nullFlag && !this.foundItZip) {
                    JOptionPane.showMessageDialog(null, "This file has not unzipped properly!");
                }
                zipFlag = false;
            }
            this.messageWindow3.setVisible(false);
            try {
                this.messageWindow3.remove(pb);
            }
            catch (final Exception ex) {}
        }
        return zipFlag;
    }
    
    private void saveAddressActionPerformed(final ActionEvent evt) {
        this.ourRoot.put("etmaAddress", this.ouEtmaAddress.getText());
    }
    
    private void etmaHandlerHelpActionPerformed(final ActionEvent evt) {
        this.helpFrame.setSize(600, 700);
        this.helpFrame.setVisible(true);
    }
    
    private void helpActionPerformed(final ActionEvent evt) {
        this.helpFrame.setVisible(true);
    }
    
    private void setJsTestFileActionPerformed(final ActionEvent evt) {
        final Object[] options = { "Default", "Select other" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, "Do you want to use the system default Browser", "Default WP", 1, 3, null, options, options[0]);
        if (n == 0) {
            this.jsTestFile.setText("System Default");
            this.ourRoot.put("jsTestFile", "System Default");
        }
        else {
            if (this.osName.equals("Mac OS X")) {
                final JFileChooser fileChooser = new JFileChooser("/Applications");
            }
            else {
                final JFileChooser fileChooser2 = new JFileChooser("/Applications");
            }
            final JFileChooser _fileChooser = new JFileChooser("/Applications");
            _fileChooser.setDialogTitle("Please select the browser to download your TMAs (Mac only)");
            final int path = _fileChooser.showOpenDialog(null);
            final File aFile = _fileChooser.getSelectedFile();
            this.jsTestFile.setText(aFile.getPath());
            this.ourRoot.put("jsTestFile", aFile.getPath());
        }
    }
    
    public void javaScriptTester() {
        final String jsPath = this.jsTestFile.getText();
        final char leftDouble = '\u00d2';
        final char rightDouble = '\u00d3';
        final char leftSingle = '\u00d4';
        final char rightSingle = '\u00d5';
        final char single = '\'';
        final char doubleQ = '\"';
        String runString = "";
        String jsText = this.getClipBoard();
        jsText = jsText.replace(leftDouble, doubleQ);
        jsText = jsText.replace(rightDouble, doubleQ);
        jsText = jsText.replace(leftSingle, single);
        jsText = jsText.replace(rightSingle, single);
        jsText = jsText.replace('\u2019', single);
        jsText = jsText.replace('\u2018', single);
        jsText = jsText.replace('\u201c', doubleQ);
        jsText = jsText.replace('\u201d', doubleQ);
        final Object[] options = { "Test", "Edit", "Cancel" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, "Test, or open for editing?", "", 1, 3, null, options, options[0]);
        this.saveFhiString(jsPath, jsText);
        final Runtime thisRun = Runtime.getRuntime();
        final String[] cmd = { null, jsPath };
        if (n == 0) {
            runString = this.browserOpenString(jsPath);
            cmd[0] = this.browserPath.getText();
        }
        String newPath = "";
        if (n == 1) {
            newPath = jsPath.replace(".html", ".txt");
            final File aFile = new File(jsPath);
            final File bFile = new File(newPath);
            aFile.renameTo(bFile);
            runString = "open " + newPath;
            cmd[0] = "";
        }
        try {
            if (this.osName.equals("Mac OS X")) {
                // Modernized: using Runtime.exec instead of JDIC DocumentFile
                if (n == 0) {
                    final String[] cmd2 = { "open", "-a", this.browserPath.getText(), jsPath };
                    thisRun.exec(cmd2);
                }
                if (n == 1) {
                    final String[] cmd2 = { "open", newPath };
                    thisRun.exec(cmd2);
                }
            }
            if (this.osName.contains("Windows")) {
                thisRun.exec(cmd);
            }
            if (this.osName.contains("Linux")) {
                thisRun.exec(cmd);
            }
        }
        catch (final Exception ex) {}
    }
    
    private void checkSpellingButtonActionPerformed(final ActionEvent evt) {
        this.savedFlag = false;
        this.spellCheckComments();
    }
    
    private void sendEmailActionPerformed(final ActionEvent evt) {
        this.chooseEmailMethod();
    }
    
    public void chooseEmailMethod() {
        final Object[] options = { "OWA on the web", "Apple Mail.app" };
        final JFrame frame = null;
        int n = 0;
        if (this.osName.equals("Mac OS X")) {
            n = JOptionPane.showOptionDialog(frame, "Send using OWA, or with Apple Mail.app ", "Send Email", 1, 3, null, options, options[0]);
            if (n == 0) {
                this.sendEmailMethodAlt();
            }
            else {
                this.sendEmailMethodAlt2();
            }
        }
        else {
            this.sendEmailMethodAlt();
        }
    }
    
    public void sendEmailMethod() {
        this.mailClient.setVisible(true);
        this.mailClient.setSize(650, 500);
        final String testPreferences = this.smtpHost.getText();
        if (this.attFlag && this.jTextArea1.getText().equals("")) {
            this.jTextArea1.setText("Attachment: " + this.attachmentFile.getName());
        }
        if (testPreferences.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter details in 'Mail Preferences'");
        }
        this.emailRecipients[0] = this.email_address.getText();
        this.emailRecipients[1] = this.ou_computer_user_name.getText() + "@my.open.ac.uk";
        this.messageAddresses.setText(this.emailRecipients[0] + ", " + this.emailRecipients[1]);
    }
    
    public void sendEmailMethodAlt() {
        final String htmlString1 = "<HTML><HEAD><script language=\"JavaScript1.2\">function mailThisUrl(){window.location = \"mailto:";
        final String htmlString2 = "\"}mailThisUrl();//window.close();</script></HEAD><BODY></BODY></HTML>";
        this.emailRecipients[0] = this.email_address.getText();
        this.emailRecipients[1] = this.ou_computer_user_name.getText() + "@my.open.ac.uk";
        final String subjectString = "TMA" + this.assgnmt_suffix.getText() + "%20" + this.course_code.getText();
        if (this.emailRecipients[1].equals("")) {
            this.outString = htmlString1 + this.emailRecipients[1] + subjectString + htmlString2;
        }
        else {
            this.outString = htmlString1 + this.emailRecipients[0] + ", " + this.emailRecipients[1] + subjectString + htmlString2;
        }
        final String htmlFilename = this.etmasFolder.getText() + "/temp.html";
        this.putFile(htmlFilename, this.outString);
        final String myURI;
        final String thisUrl = myURI = "https://outlook.office.com/owa/?path=/mail/action/compose&to=" + this.emailRecipients[0] + "&subject=" + subjectString;
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }
    
    public void sendEmailMethodAlt2() {
        this.emailRecipients[0] = this.email_address.getText();
        this.emailRecipients[1] = this.ou_computer_user_name.getText() + "@my.open.ac.uk";
        final String subject = "TMA" + this.assgnmt_suffix.getText() + " " + this.course_code.getText();
        try {
            // Modernized: using java.awt.Desktop mailto URI instead of JDIC
            final String mailtoUri = "mailto:" + this.emailRecipients[0] +
                "?subject=" + java.net.URLEncoder.encode(subject, "UTF-8").replace("+", "%20");
            java.awt.Desktop.getDesktop().mail(new java.net.URI(mailtoUri));
        }
        catch (final Exception ex) {
            System.err.println("Failed to open mail client: " + ex.getMessage());
        }
    }
    
    public void addAttachment() {
        this.attFlag = false;
        final JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
        _fileChooser.setDialogTitle("Please select the file to be attached:");
        final int path = _fileChooser.showOpenDialog(null);
        final File aFile = _fileChooser.getSelectedFile();
        this.attachmentFile = aFile;
        this.attFlag = true;
        String message = this.jTextArea1.getText();
        message = message + "\n\nAttachment: " + this.attachmentFile.getName();
        this.jTextArea1.setText(message);
    }
    
    public void postMail(final String[] recipients, final String subject, final String message, final String from) throws MessagingException {
        final boolean debug = false;
        final Properties props = new Properties();
        props.put("mail.smtp.host", this.smtpHost.getText());
        if (this.authenticationFlag.isSelected()) {
            props.put("mail.smtp.auth", "true");
        }
        else {
            props.put("mail.smtp.auth", "false");
        }
        final Session session = Session.getDefaultInstance(props, (Authenticator)null);
        session.setDebug(debug);
        final jakarta.mail.Message msg = (jakarta.mail.Message)new MimeMessage(session);
        final InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom((Address)addressFrom);
        final String extraAddress = this.addRecip.getText();
        int numRecip = 2;
        if (!extraAddress.equals("")) {
            numRecip = 3;
            recipients[2] = extraAddress;
        }
        final InternetAddress[] addressTo = new InternetAddress[numRecip];
        for (int i = 0; i < numRecip; ++i) {
            if (!recipients[i].equals("")) {
                addressTo[i] = new InternetAddress(recipients[i]);
            }
        }
        msg.setRecipients(jakarta.mail.Message.RecipientType.TO, (Address[])addressTo);
        msg.setSubject(subject);
        final Address ccAddress = (Address)new InternetAddress(this.yourEmailAddress.getText());
        msg.addRecipient(jakarta.mail.Message.RecipientType.CC, ccAddress);
        if (this.attFlag) {
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            this.jTextArea1.setText(message);
            messageBodyPart.setText(message);
            final Multipart multipart = (Multipart)new MimeMultipart();
            multipart.addBodyPart((BodyPart)messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            final DataSource source = (DataSource)new FileDataSource(this.attachmentFile);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(this.attachmentFile.getName());
            multipart.addBodyPart((BodyPart)messageBodyPart);
            msg.setContent(multipart);
            msg.saveChanges();
        }
        else {
            msg.saveChanges();
            final Address myAddress = (Address)new InternetAddress(this.yourEmailAddress.getText());
            msg.setFrom(myAddress);
            msg.setContent((Object)message, "text/plain");
        }
        final Transport transport = session.getTransport("smtp");
        transport.connect(this.smtpHost.getText(), this.mailUserName.getText(), this.passwordPreferences);
        transport.sendMessage(msg, msg.getAllRecipients());
    }
    
    private String addEntries(String sumString) {
        double thisTotal = 0.0;
        sumString = sumString.replace("+", ",");
        final String[] sumElements = sumString.split(",");
        for (int i = 0; i < sumElements.length; ++i) {
            thisTotal += Double.parseDouble(sumElements[i]);
        }
        String thisTotalString = String.valueOf(thisTotal);
        thisTotalString = thisTotalString.replace(".0", "");
        return thisTotalString;
    }
    
    private void previousPt3sActionPerformed(final ActionEvent evt) {
        final File aFile = new File(this.fhiFileName.getText());
        String parentName = aFile.getParent();
        final File bFile = new File(parentName);
        parentName = bFile.getParent();
        final String thisPt3 = (String)this.previousPt3s.getSelectedItem();
        if (thisPt3.endsWith("All")) {
            this.displayAllPt3s();
        }
        else {
            String pt3String = "";
            final Runtime thisRun = Runtime.getRuntime();
            try {
                if (this.osName.equals("Mac OS X") || this.osName.equals("Darwin")) {
                    pt3String = "file://" + parentName + "/" + thisPt3;
                    if (!System.getProperty("java.version").startsWith("1.6.")) {
                        final String[] cmd = { "open", pt3String };
                        thisRun.exec(cmd);
                    }
                    else {
                        final String[] cmd = { "open", pt3String };
                        thisRun.exec(cmd);
                    }
                }
                if (this.osName.contains("Windows")) {
                    try {
                        if (!thisPt3.equals("Previous PT3s")) {
                            pt3String = parentName + "\\" + thisPt3;
                            thisRun.exec("explorer.exe " + pt3String);
                        }
                    }
                    catch (final Exception ex) {}
                }
                if (this.osName.contains("Linux")) {
                    final URL ppt3 = null;
                    String browser = "firefox";
                    try {
                        if (!thisPt3.equals("Previous PT3s")) {
                            pt3String = parentName + "/" + thisPt3;
                            if (!this.currentBrowserPreferences.equals("")) {
                                browser = this.currentBrowserPreferences;
                            }
                            final String[] cmd2 = { browser, pt3String };
                            thisRun.exec(cmd2);
                        }
                    }
                    catch (final Exception anException) {
                        this.progNotFound(browser);
                    }
                }
            }
            catch (final Exception ex2) {}
        }
    }
    
    public void displayAllPt3s() {
        BufferedReader buffer = null;
        final StringBuilder sb = new StringBuilder(10000);
        String textOfAllFiles = "";
        for (int numberOfPt3s = this.previousPt3s.getItemCount(), i = 1; i < numberOfPt3s - 1; ++i) {
            final String fileString = "";
            final String thisPt3 = (String) this.previousPt3s.getItemAt(i);
            final File aFile = new File(this.parentName1 + "/" + thisPt3);
            try {
                buffer = new BufferedReader(new FileReader(aFile));
                for (String currentLine = buffer.readLine(); currentLine != null; currentLine = buffer.readLine()) {
                    sb.append(currentLine);
                }
            }
            catch (final Exception ex) {}
            finally {
                try {
                    buffer.close();
                }
                catch (final Exception ex2) {}
            }
        }
        textOfAllFiles = sb.toString();
        textOfAllFiles = textOfAllFiles.replaceAll("width=\"100%\" align=\"left\"", "");
        final File aFile2 = new File(this.parentName1 + "/allText.htm");
        BufferedWriter bufferOut = null;
        try {
            bufferOut = new BufferedWriter(new FileWriter(aFile2));
            bufferOut.write(textOfAllFiles);
        }
        catch (final Exception ex3) {}
        finally {
            try {
                bufferOut.close();
            }
            catch (final Exception ex4) {}
        }
        String pt3String = "";
        final Runtime thisRun = Runtime.getRuntime();
        try {
            if (this.osName.equals("Mac OS X")) {
                pt3String = "file://" + this.parentName1 + "/allText.htm";
                final String[] cmd = { "open", pt3String };
                thisRun.exec(cmd);
            }
            if (this.osName.contains("Windows")) {
                try {
                    pt3String = this.parentName1 + "\\allText.htm";
                    thisRun.exec("explorer.exe " + pt3String);
                }
                catch (final Exception ex5) {}
            }
            if (this.osName.contains("Linux")) {
                final URL ppt3 = null;
                String browser = "firefox";
                try {
                    pt3String = this.parentName1 + "/allText.htm";
                    if (!this.currentBrowserPreferences.equals("")) {
                        browser = this.currentBrowserPreferences;
                    }
                    final String[] cmd2 = { browser, pt3String };
                    thisRun.exec(cmd2);
                }
                catch (final Exception anException) {
                    this.progNotFound(browser);
                }
            }
        }
        catch (final Exception ex6) {}
    }
    
    public long calculateDirectorySize(final File aFile) {
        long fileSize = 0L;
        final File[] listFiles;
        final File[] listOfFiles = listFiles = aFile.listFiles();
        for (final File thisFile : listFiles) {
            fileSize += thisFile.length();
        }
        return fileSize;
    }
    
    private void setupPreviousPt3s() {
        String parentName = "";
        while (this.previousPt3s.getItemCount() > 1) {
            this.previousPt3s.removeItemAt(1);
        }
        try {
            final File aFile = new File(this.fhiFileName.getText());
            parentName = aFile.getParent();
            final File bFile = new File(parentName);
            parentName = bFile.getParent();
            this.parentName1 = parentName;
            final File cFile = new File(parentName);
            final File[] pt3Files1 = cFile.listFiles();
            Arrays.sort(pt3Files1);
            for (int i = 0; i < pt3Files1.length; ++i) {
                if (pt3Files1[i].getName().contains("PT3-")) {
                    this.previousPt3s.addItem(pt3Files1[i].getName());
                }
            }
            this.previousPt3s.addItem("All");
        }
        catch (final Exception ex) {}
    }
    
    private void missingSubmissionsActionPerformed(final ActionEvent evt) {
        final List<String> csvPidList = new ArrayList<String>();
        final List<String> csvPidList2 = new ArrayList<String>();
        final List<String> csvPidList3 = new ArrayList<String>();
        final List<String> tablePidList = new ArrayList<String>();
        String missingReport = "The following students have not yet submitted this TMA:\n";
        final String pathToCourseFolder = this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem();
        final String[] fileList = new File(pathToCourseFolder).list();
        String csvFilePath = "";
        String extractName = "";
        for (int i = 0; i < fileList.length; ++i) {
            if (fileList[i].contains("extract") || fileList[i].contains("students-detail")) {
                extractName = fileList[i];
            }
        }
        if (extractName.equals("")) {
            JOptionPane.showMessageDialog(null, "No student list was found for this module.\nYou should download it and put it inside the module folder, \nwhich is inside the etmas folder\n(next to the TMA number folders).\nIt must begin with 'extract'.");
        }
        else {
            boolean missingFlag = false;
            csvFilePath = pathToCourseFolder + "/" + extractName;
            final String csvDetails = this.loadFhiString(csvFilePath, true);
            final String[] csvLine = csvDetails.split("\r");
            String[] csvCells = null;
            for (int j = 1; j < csvLine.length; ++j) {
                try {
                    csvCells = csvLine[j].split(",");
                    final String csvPid = csvCells[0].replace("\n", "");
                    if (!csvPid.equals("")) {
                        csvPidList.add(csvPid);
                    }
                    final String csvPid2 = csvCells[1].replace("\n", "");
                    if (!csvPid2.equals("")) {
                        csvPidList2.add(csvPid2);
                    }
                    final String csvPid3 = csvCells[2].replace("\n", "");
                    if (!csvPid3.equals("")) {
                        csvPidList3.add(csvPid3);
                    }
                }
                catch (final Exception ex) {}
            }
            final int nStud = this.studentList.getItemCount();
            for (int nRow = this.listOfTmas.getRowCount(), k = 0; k < nRow; ++k) {
                tablePidList.add((String)this.listOfTmas.getValueAt(k, 0));
            }
            for (int k = 0; k < csvPidList.size(); ++k) {
                if (!tablePidList.contains(csvPidList.get(k))) {
                    missingFlag = true;
                    missingReport = missingReport + (String)csvPidList.get(k) + "," + (String)csvPidList3.get(k) + "," + (String)csvPidList2.get(k);
                }
            }
            if (!missingFlag) {
                missingReport = "All TMAs have been submitted";
            }
            final String missingReport2 = missingReport;
            final JFrame iFRAME = new JFrame();
            iFRAME.setAlwaysOnTop(true);
            iFRAME.setDefaultCloseOperation(2);
            iFRAME.setLocationRelativeTo(null);
            iFRAME.requestFocus();
            final JPanel contentPane = new JPanel();
            final JTextArea ta = new JTextArea(20, 40);
            final JScrollPane scroll = new JScrollPane(ta, 22, 32);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            ta.setEditable(false);
            ta.setText(missingReport);
            ta.setCaretPosition(0);
            contentPane.add(scroll);
            final JButton export = new JButton("Export to CSV");
            export.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final File bFile = new File(EtmaHandlerJ.this.downloadsFolder.getText());
                    final String aFolder = bFile.getParent();
                    final JFileChooser _fileChooser = new JFileChooser(aFolder);
                    _fileChooser.setFileSelectionMode(1);
                    _fileChooser.setDialogTitle("Please select folder to save the exported file to:");
                    final int path = _fileChooser.showOpenDialog(iFRAME);
                    final File aFile = _fileChooser.getSelectedFile();
                    final String aFolderName = aFile.getPath();
                    final String aFileName = "Unsubmitted assignments " + EtmaHandlerJ.this.courseList.getSelectedItem() + " TMA" + EtmaHandlerJ.this.tmaList.getSelectedItem() + " " + EtmaHandlerJ.this.getDateAndTime() + ".csv";
                    EtmaHandlerJ.this.tmaList.getSelectedItem();
                    EtmaHandlerJ.this.saveFhiString(aFolderName + "/" + aFileName, missingReport2);
                    JOptionPane.showMessageDialog((Component)iFRAME, "File has been saved to " + aFolderName + "/" + aFileName + "\nas a csv file.");
                    EtmaHandlerJ.this.toFront();
                }
            });
            export.setVisible(true);
            contentPane.add(export);
            JOptionPane.showMessageDialog(null, contentPane, "Missing Submissions", 1);
        }
    }
    
    private void moreDetailsActionPerformed(final ActionEvent evt) {
        this.size = this.ourRoot.getBoolean("size", true);
        if (this.size) {
            this.smallWindowSize[1] = this.getHeight();
            this.ourRoot.putDouble("smallWindowHeight", this.getHeight());
            this.largeWindowSize[1] = (int)this.ourRoot.getDouble("largeWindowHeight", 740.0);
            this.smallWindowSize[0] = this.getWidth();
            this.ourRoot.putDouble("smallWindowWidth", this.getWidth());
            this.setSize(this.largeWindowSize[0] = this.smallWindowSize[0], this.largeWindowSize[1]);
            this.moreDetails.setText("Fewer details");
            this.moreDetails.setToolTipText("Shows the smaller Handler window");
            this.ourRoot.putBoolean("size", false);
            this.size = false;
        }
        else {
            this.largeWindowSize[1] = this.getHeight();
            this.ourRoot.putDouble("largeWindowHeight", this.getHeight());
            this.smallWindowSize[1] = (int)this.ourRoot.getDouble("smallWindowHeight", 510.0);
            this.largeWindowSize[0] = this.getWidth();
            this.ourRoot.putDouble("largeWindowWidth", this.getWidth());
            this.setSize(this.smallWindowSize[0] = this.largeWindowSize[0], this.smallWindowSize[1]);
            this.moreDetails.setText("More details");
            this.moreDetails.setToolTipText("Shows the full Handler window");
            this.ourRoot.putBoolean("size", true);
            this.size = true;
        }
    }
    
    private void fontSizeActionPerformed(final ActionEvent evt) {
        try {
            this.fSize = Integer.parseInt((String)this.fontSize.getSelectedItem());
            this.tutor_comments_input.setFont(new Font("Lucida Grande", 0, this.fSize));
            if (this.globalFontsPreferences) {
                this.setFonts(this.fSize);
            }
            this.ourRoot.putInt("fontSize", this.fSize);
        }
        catch (final Exception ex) {}
    }
    
    public void setFonts(final int fontSize) {
        this.tmaScores.setFont(new Font("Lucida Grande", 0, fontSize));
        this.listOfTmas.setFont(new Font("Lucida Grande", 0, fontSize));
        this.courseList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.tmaList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.forenames.setFont(new Font("Lucida Grande", 0, fontSize));
        this.surname.setFont(new Font("Lucida Grande", 0, fontSize));
        this.ou_computer_user_name.setFont(new Font("Lucida Grande", 0, fontSize));
        this.gradesSummaryTable.setFont(new Font("Lucida Grande", 0, fontSize));
        this.etmaSite.setFont(new Font("Lucida Grande", 0, fontSize));
        this.collectTmas.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTmaList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTma.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTmaList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTmaFolder.setFont(new Font("Lucida Grande", 0, fontSize));
        this.trainingSite.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openCommentBank.setFont(new Font("Lucida Grande", 0, fontSize));
        this.bankComment.setFont(new Font("Lucida Grande", 0, fontSize));
        this.savePt3.setFont(new Font("Lucida Grande", 0, fontSize));
        this.previousPt3s.setFont(new Font("Lucida Grande", 0, fontSize));
        this.listAllScores.setFont(new Font("Lucida Grande", 0, fontSize));
        this.checkSpellingButton.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openReturnsFolder.setFont(new Font("Lucida Grande", 0, fontSize));
        this.zipFiles.setFont(new Font("Lucida Grande", 0, fontSize));
        this.backUp.setFont(new Font("Lucida Grande", 0, fontSize));
        this.gradesSummaryTable.setFont(new Font("Lucida Grande", 0, fontSize));
        this.moreDetails.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openReturnsFolder.setFont(new Font("Lucida Grande", 0, fontSize));
        this.overall_grade_score.setFont(new Font("Lucida Grande", 0, fontSize));
        this.submission_status.setFont(new Font("Lucida Grande", 0, fontSize));
        this.e_tma_submission_num.setFont(new Font("Lucida Grande", 0, fontSize));
        this.lateSubmission.setFont(new Font("Lucida Grande", 0, fontSize));
        this.jLabel22.setFont(new Font("Lucida Grande", 0, fontSize));
        this.prevMarks.setFont(new Font("Lucida Grande", 0, fontSize));
        this.region_code.setFont(new Font("Lucida Grande", 0, fontSize));
        this.course_code.setFont(new Font("Lucida Grande", 0, fontSize));
        this.assgnmt_suffix.setFont(new Font("Lucida Grande", 0, fontSize));
        this.personal_id.setFont(new Font("Lucida Grande", 0, fontSize));
        this.email_address.setFont(new Font("Lucida Grande", 0, fontSize));
        this.pres_code.setFont(new Font("Lucida Grande", 0, fontSize));
        this.total_question_count.setFont(new Font("Lucida Grande", 0, fontSize));
        this.permitted_question_count.setFont(new Font("Lucida Grande", 0, fontSize));
        this.marked_date.setFont(new Font("Lucida Grande", 0, fontSize));
        this.e_tma_submission_date.setFont(new Font("Lucida Grande", 0, fontSize));
        this.sendEmail.setFont(new Font("Lucida Grande", 0, fontSize));
    }
    
    public static void setUIFont(final FontUIResource f) {
        final Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            final Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
    
    private void lateSubmissionActionPerformed(final ActionEvent evt) {
        if (this.lateSubmission.isSelected()) {
            final Object[] options = { "Mark as Late", "Don't mark as late" };
            final JFrame frame = null;
            final int n = JOptionPane.showOptionDialog(frame, "Are you sure? This will mean\r that the student gets a zero score.\nYou should explain this on the PT3.", "Late Submission", 1, 3, null, options, options[1]);
            if (n == 0) {
                this.late_submission_status.setText("Y");
            }
            if (n == 1) {
                this.late_submission_status.setText("N");
                this.lateSubmission.setSelected(false);
            }
        }
        else {
            this.late_submission_status.setText("N");
            this.lateSubmission.setSelected(false);
        }
        this.calculateTotals();
    }
    
    private void tmaSelectMenuActionPerformed(final ActionEvent evt) {
        this.filter = (String)this.tmaSelectMenu.getSelectedItem();
        this.openList();
    }
    
    private void bankCommentMenuItemActionPerformed(final ActionEvent evt) {
        this.bankComment();
    }
    
    private void bankCommentActionPerformed(final ActionEvent evt) {
        this.bankComment();
    }
    
    private void bankComment() {
        String bankPath = this.commentBankFile.getText();
        if (bankPath.equals("Automatic")) {
            bankPath = this.createCommentBank();
        }
        if (bankPath.equals("")) {
            JOptionPane.showMessageDialog(null, "Please create a blank Text File in your module folder \nand set the path to it in Preferences or Options\nor set the preference to 'Automatic' and open a PT3!");
        }
        else {
            final String currentBank = this.loadFhiString(bankPath, true);
            final String newComment = this.getClipBoard();
            final String newBank = currentBank + "\r\n----------------\r\n" + newComment;
            this.saveFhiString(bankPath, newBank);
        }
    }
    
    private void listTmasMenuItemActionPerformed(final ActionEvent evt) {
        this.openList();
    }
    
    private void savePt3MenuItemActionPerformed(final ActionEvent evt) {
        final Timer timer1 = new Timer();
        final ScheduleRunner task1 = new ScheduleRunner();
        timer1.schedule(task1, 1000L);
        this.saveDetails();
        JOptionPane.showMessageDialog(null, "PT3 saved!");
    }
    
    public void progNotFound(final String aString) {
        JOptionPane.showMessageDialog((Component)null, "Sorry, the program '" + aString + "' doesn't seem to exist!\nIf you have set it in Preferences, make sure you've set it to the launcher name.", "", 0);
    }
    
    public void commentBankOpener() {
        final File fhiFile = new File(this.fhiFileName.getText());
        String commentBank = this.commentBankFile.getText();
        String edPath = this.commentBankEd.getText();
        if (commentBank.equals("Automatic")) {
            commentBank = this.createCommentBank();
        }
        System.out.println(commentBank);
        if (commentBank.equals("")) {
            JOptionPane.showMessageDialog(null, "Please create a blank Text File in your module folder \nand set the path to it in Preferences or Options\nor set the Preference to 'Automatic' and open a PT3!");
        }
        else {
            final File cbFile = new File(commentBank);
            final Runtime thisRun = Runtime.getRuntime();
            if (this.osName.contains("Mac OS X") || this.osName.contains("Darwin")) {
                if (this.commentBankEd.getText().equals("System Default")) {
                    try {
                        final String[] cmd = { "open", commentBank };
                        thisRun.exec(cmd);
                    }
                    catch (final Exception ex) {}
                }
                else {
                    try {
                        final String tmaFiletoOpenMac = commentBank.replace("/", ":");
                        final String appPathMac = this.commentBankEd.getText().replace("/", ":");
                        final String argsString = "tell application \"Finder\" to copy name of startup disk to std\ntell application \"Finder\" to open file (std &\"" + tmaFiletoOpenMac + "\")using (std &\"" + appPathMac + "\")";
                        final String[] args = { "osascript", "-e", argsString };
                        thisRun.exec(args);
                    }
                    catch (final Exception ex2) {}
                }
            }
            if (this.osName.contains("Linux")) {
                if (edPath.equals("System Default")) {
                    edPath = "gedit";
                }
                try {
                    if (!edPath.equals(null)) {
                        this.linuxWP = edPath;
                    }
                    final String[] cmd = { this.linuxWP, commentBank };
                    thisRun.exec(cmd);
                }
                catch (final Exception e) {
                    this.progNotFound(this.linuxWP);
                }
            }
            else {
                try {
                    if (this.osName.contains("Windows")) {
                        if (this.commentBankEd.getText().equals("System Default") || this.commentBankEd.getText().equals("")) {
                            thisRun.exec("explorer.exe " + cbFile.getPath());
                        }
                        else {
                            final String[] cmd2 = { this.commentBankEd.getText(), cbFile.getPath() };
                            thisRun.exec(cmd2);
                        }
                    }
                    else if (!this.osName.contains("Mac OS X")) {
                        // Modernized: using java.awt.Desktop instead of JDIC DocumentFile
                        if (this.commentBankEd.getText().equals("System Default")) {
                            java.awt.Desktop.getDesktop().open(cbFile);
                        }
                        else {
                            final String[] cmd3 = { this.commentBankEd.getText(), cbFile.getPath() };
                            thisRun.exec(cmd3);
                        }
                    }
                }
                catch (final Exception e) {
                    JOptionPane.showMessageDialog(null, "Please create a blank Text File in your module folder \nand set the path to it in Preferences or Options!");
                    System.out.println(e);
                }
            }
        }
    }
    
    private void openCommentBankActionPerformed(final ActionEvent evt) {
        this.commentBankOpener();
    }
    
    private void setCommentBankFileActionPerformed(final ActionEvent evt) {
        final Object[] options = { "Automatic", "Manual selection", "Cancel" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, "Choose location automatically?\n(If so, make sure you have a PT3 open!)", "", 1, 3, null, options, options[0]);
        if (n == 0) {
            this.commentBankFile.setText("Automatic");
            this.ourRoot.put("commentBankFile", "Automatic");
            JOptionPane.showMessageDialog((Component)null, "Commentbank file is at " + this.createCommentBank());
        }
        if (n == 1) {
            final JFileChooser _fileChooser = new JFileChooser(this.etmasFolder.getText());
            _fileChooser.setDialogTitle("Please select a text file to be your Comment Bank");
            final int path = _fileChooser.showOpenDialog(null);
            final File aFile = _fileChooser.getSelectedFile();
            this.commentBankFile.setText(aFile.getPath());
            this.ourRoot.put("commentBankFile", aFile.getPath());
        }
        if (n == 2) {}
    }
    
    private void preferencesMenuActionPerformed(final ActionEvent evt) {
        this.openPrefsWindow();
    }
    
    public void colorSetter(final JTable aTable, final List<Integer> aRow, final List<Integer> bRow, final Color aColor, final Color bColor, final boolean inBold) {
        aTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
                final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                final Font font = comp.getFont();
                if (aRow.contains(row)) {
                    comp.setBackground(aColor);
                    if (inBold) {
                        comp.setFont(font.deriveFont(1));
                    }
                }
                else if (bRow.contains(row)) {
                    comp.setBackground(bColor);
                    if (inBold) {
                        comp.setFont(font.deriveFont(1));
                    }
                }
                else {
                    comp.setBackground(new Color(Integer.parseInt(EtmaHandlerJ.this.gridColor[0]), Integer.parseInt(EtmaHandlerJ.this.gridColor[1]), Integer.parseInt(EtmaHandlerJ.this.gridColor[2])));
                    comp.setFont(font.deriveFont(0));
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });
    }
    
    public void returnsFolderOpener() {
        final String returnsFolder = this.etmasFolder.getText() + this.returnsName;
        try {
            this.calculateTotals();
        }
        catch (final Exception ex) {}
        try {
            Desktop.getDesktop().open(new File(returnsFolder));
        }
        catch (final Exception ex2) {}
    }
    
    private void returnsFolderCheckEmpty() {
        final File currentReturnsFolder = new File(this.etmasFolder.getText() + "/returns");
        final File[] returnsFolderContents = currentReturnsFolder.listFiles();
        String todaysDate = this.makeZipFileName().substring(0, 10);
        todaysDate = todaysDate.replace("-", "_");
        final List<File> oldZipFiles = new ArrayList<File>();
        for (int i = 0; i < returnsFolderContents.length; ++i) {
            if (!returnsFolderContents[i].getName().contains(todaysDate) && returnsFolderContents[i].getName().contains(".zip")) {
                oldZipFiles.add(returnsFolderContents[i]);
            }
        }
        if (oldZipFiles.size() > 0) {
            final Object[] options = { "Delete", "Don't delete" };
            final JFrame frame = null;
            final int n = JOptionPane.showOptionDialog((Component)frame, "There are " + oldZipFiles.size() + " zipped files in your returns folder which were zipped before today.\nThey may be no longer needed - do you want to delete them?\n(You can turn this alert off in the preferences)", "", 1, 3, (Icon)null, options, options[1]);
            if (n == 0) {
                for (final File dFile : oldZipFiles) {
                    dFile.delete();
                }
                JOptionPane.showMessageDialog(null, "Files  deleted");
            }
            else {
                JOptionPane.showMessageDialog(null, "Files not deleted");
            }
        }
    }
    
    private void openReturnsFolderActionPerformed(final ActionEvent evt) {
        this.returnsFolderOpener();
    }
    
    private void jScrollPane1MouseReleased(final MouseEvent evt) {
    }
    
    private void saveWeightingsActionPerformed(final ActionEvent evt) {
        this.saveWeightings();
    }
    
    public void checkWeightingsCodes() {
        final List<String> courses = new ArrayList<String>();
        final List<String> weightedCourses = new ArrayList<String>();
        final List<String> maxScoreCourses = new ArrayList<String>();
        for (int nCourses = this.courseList.getItemCount(), i = 0; i < nCourses; ++i) {
            courses.add((String) this.courseList.getItemAt(i));
        }
        weightedCourses.add((String)this.weightingsTable1.getValueAt(0, 0));
        weightedCourses.add((String)this.weightingsTable2.getValueAt(0, 0));
        weightedCourses.add((String)this.weightingsTable3.getValueAt(0, 0));
        weightedCourses.add((String)this.weightingsTable4.getValueAt(0, 0));
        maxScoreCourses.add((String)this.maxScoreTable1.getValueAt(0, 0));
        maxScoreCourses.add((String)this.maxScoreTable2.getValueAt(0, 0));
        maxScoreCourses.add((String)this.maxScoreTable3.getValueAt(0, 0));
        maxScoreCourses.add((String)this.maxScoreTable4.getValueAt(0, 0));
        final List<String> coursesMissing = new ArrayList<String>();
        boolean coursesOK = true;
        for (int j = 0; j < weightedCourses.size(); ++j) {
            if (!courses.contains(weightedCourses.get(j)) && !weightedCourses.get(j).equals("")) {
                coursesMissing.add(weightedCourses.get(j));
                coursesOK = false;
            }
        }
        for (int j = 0; j < maxScoreCourses.size(); ++j) {
            if (!courses.contains(maxScoreCourses.get(j)) && !maxScoreCourses.get(j).equals("")) {
                coursesMissing.add(maxScoreCourses.get(j));
                coursesOK = false;
            }
        }
        if (!coursesOK) {
            JOptionPane.showMessageDialog((Component)null, "The following courses are not in your module list and may be mistyped:\n" + coursesMissing);
        }
    }
    
    public void saveWeightings() {
        this.checkWeightingsCodes();
        for (int i = 0; i < 15; ++i) {
            try {
                this.ourRoot.put("weightings<" + i + "><0>", (String)this.weightingsTable1.getValueAt(i, 0));
            }
            catch (final Exception ex) {}
            try {
                this.ourRoot.put("weightings<" + i + "><1>", (String)this.weightingsTable2.getValueAt(i, 0));
            }
            catch (final Exception ex2) {}
            try {
                this.ourRoot.put("weightings<" + i + "><2>", (String)this.weightingsTable3.getValueAt(i, 0));
            }
            catch (final Exception ex3) {}
            try {
                this.ourRoot.put("weightings<" + i + "><3>", (String)this.weightingsTable4.getValueAt(i, 0));
            }
            catch (final Exception ex4) {}
            try {
                this.ourRoot.put("maxScores<" + i + "><0>", (String)this.maxScoreTable1.getValueAt(i, 0));
            }
            catch (final Exception ex5) {}
            try {
                this.ourRoot.put("maxScores<" + i + "><1>", (String)this.maxScoreTable2.getValueAt(i, 0));
            }
            catch (final Exception ex6) {}
            try {
                this.ourRoot.put("maxScores<" + i + "><2>", (String)this.maxScoreTable3.getValueAt(i, 0));
            }
            catch (final Exception ex7) {}
            try {
                this.ourRoot.put("maxScores<" + i + "><3>", (String)this.maxScoreTable4.getValueAt(i, 0));
            }
            catch (final Exception ex8) {}
        }
        this.weightings.setVisible(false);
    }
    
    private void setTmaWeightingsActionPerformed(final ActionEvent evt) {
        this.preferences.setVisible(false);
        this.weightings.setSize(1100, 500);
        this.weightings.setVisible(true);
    }
    
    private void exitMenuItemActionPerformed(final ActionEvent evt) {
        this.exitRoutine();
    }
    
    public void exitRoutine() {
        final Object[] options = { "Save PT3 and Quit", "Quit without saving PT3", "Cancel" };
        final JFrame frame = null;
        this.saveLocation();
        if (!this.surname.getText().equals("")) {
            final int n = JOptionPane.showOptionDialog((Component)frame, (Object)"Are you sure you want to quit?", "Quitting " + this.getTitle(), 1, 3, (Icon)null, options, options[0]);
            if (n == 1) {
                try {
                    this.timer2.cancel();
                }
                catch (final Exception ex) {}
                System.exit(0);
            }
            if (n == 0) {
                try {
                    this.saveDetails();
                    try {
                        this.timer2.cancel();
                    }
                    catch (final Exception ex2) {}
                }
                catch (final Exception anException) {
                    JOptionPane.showMessageDialog(null, "PT3 NOT saved!");
                }
                System.exit(0);
            }
            if (n == 2) {
                this.setVisible(true);
            }
        }
        else {
            final Object[] options2 = { "Quit", "Cancel" };
            final int n2 = JOptionPane.showOptionDialog((Component)frame, (Object)"Are you sure you want to quit?", "Quitting " + this.getTitle(), 1, 3, (Icon)null, options2, options2[0]);
            if (n2 == 0) {
                try {
                    this.timer2.cancel();
                }
                catch (final Exception ex3) {}
                System.exit(0);
            }
        }
    }
    
    public void clearZipTicks() {
    }
    
    private void batchZipActionPerformed(final ActionEvent evt) {
        this.longFilenames.clear();
        final int[] rowsSelected = this.getSelectedFiles();
        if (rowsSelected.length == 0) {
            this.messageWindow.setVisible(false);
            JOptionPane.showMessageDialog(null, "Please select  files to be zipped by ticking the last column!");
        }
        else {
            boolean continueFlag = true;
            if (this.osName.equals("Mac OS X")) {
                continueFlag = this.checkScriptClosure();
            }
            if (this.osName.contains("Windows")) {
                final File f = new File(this.currentStudentScript);
                continueFlag = this.checkScriptClosureWindows(f.getName());
            }
            if (this.osName.contains("Linux")) {
                continueFlag = this.checkScriptClosureLinux(this.currentStudentScript);
            }
            if (continueFlag) {
                String course = "";
                String oucu = "";
                String pres = "";
                String tmaNo = "";
                String subNo1 = "";
                String zipFileName1 = "";
                String pid = "";
                String status = "";
                int numberZipped = 0;
                boolean notReadyFlag = false;
                boolean success1 = false;
                boolean success2 = false;
                final String etmasBase = this.etmasFolder.getText();
                String fhiString = "";
                final File tempBase = new File(etmasBase + this.tempName);
                final File returnsBase = new File(etmasBase + this.returnsName);
                String tmaFolderPath = "";
                int studNo = 0;
                this.messageWindow.setSize(400, 10);
                this.messageWindow.setVisible(true);
                this.messageWindow.setTitle("Please wait.... zipping files....");
                this.messageWindow.setLocation(300, 300);
                try {
                    tempBase.mkdir();
                }
                catch (final Exception ex) {}
                try {
                    returnsBase.mkdir();
                }
                catch (final Exception ex2) {}
                for (studNo = 0; studNo < rowsSelected.length; ++studNo) {
                    oucu = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 3);
                    pres = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 8);
                    course = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 7);
                    tmaNo = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 9);
                    subNo1 = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 6);
                    pid = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 0);
                    status = (String)this.listOfTmas.getValueAt(rowsSelected[studNo], 5);
                    course = course + "-" + pres;
                    tmaFolderPath = etmasBase + "/" + course + "/" + tmaNo + "/" + oucu + "/" + subNo1;
                    fhiString = this.loadFhiString(tmaFolderPath + course + "-" + tmaNo + "-" + subNo1 + "-" + oucu + ".fhi", false);
                    boolean noCommentFlag = false;
                    if (fhiString.contains("<tutor_comments></tutor_comments")) {
                        noCommentFlag = true;
                    }
                    if ((status.equals("Marked") || status.equals("Zipped")) && !noCommentFlag) {
                        ++numberZipped;
                        if (this.ou_computer_user_name.getText().equals(oucu)) {
                            this.submission_status.setText("Zipped");
                        }
                        fhiString = this.changeStatus(fhiString, "submission_status", "Zipped");
                        fhiString = this.changeStatus(fhiString, "zip_date", this.getDateAndTime());
                        fhiString = this.changeStatus(fhiString, "zip_file", this.getZipfileName2(subNo1));
                        fhiString = fhiString.replaceAll("&", "&amp;");
                        this.saveFhiString(tmaFolderPath + course + "-" + tmaNo + "-" + subNo1 + "-" + oucu + ".fhi", fhiString);
                        final File aFile = new File(tmaFolderPath);
                        zipFileName1 = pid + "-" + course + "-" + tmaNo + "-" + oucu + "-" + subNo1;
                        if (!this.osName.contains("Windows")) {
                            success1 = this.zipDirectory(aFile, this.etmasFolder.getText() + this.tempName + zipFileName1, 4);
                        }
                        else {
                            success1 = this.zipDirectory(aFile, this.etmasFolder.getText() + "\\temp\\" + zipFileName1, 4);
                        }
                        this.listOfTmas.setValueAt("Zipped", rowsSelected[studNo], 5);
                    }
                    else {
                        notReadyFlag = true;
                    }
                }
                if (this.maxFileNameLength > this.maxAcceptFilename) {
                    String longFileNameString = "";
                    for (String aFilename : this.longFilenames) {
                        longFileNameString += aFilename;
                    }
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog((Component)null, "At least one of your filenames looks too long!\n (" + this.maxFileNameLength + " characters).\n" + longFileNameString, "", 2);
                }
                final File thisFile = new File(this.etmasFolder.getText() + this.tempName);
                final String fileToBeReturned = this.etmasFolder.getText() + this.returnsName + this.getZipfileName2(subNo1);
                final String nameOfFileToBeReturned = this.getZipfileName2(subNo1);
                success2 = this.zipDirectory(thisFile, this.etmasFolder.getText() + this.returnsName + this.getZipfileName2(subNo1), 3);
                this.messageWindow.setVisible(false);
                if (numberZipped > 0) {
                    if (success1 && success2) {
                        String errorMessage;
                        if (notReadyFlag) {
                            errorMessage = "WARNING: Some files were not zipped, since they aren't marked or they have no tutor comments!";
                        }
                        else {
                            errorMessage = "All files have been successfully zipped!";
                        }
                        this.zip_date.setText(this.getDateAndTime());
                        if (this.osName.contains("Windows")) {
                            copyString(fileToBeReturned + ".zip");
                        }
                        else {
                            copyString(nameOfFileToBeReturned + ".zip");
                        }
                        this.returnTmas(errorMessage);
                    }
                    else {
                        fhiString = this.loadFhiString(tmaFolderPath + course + "-" + tmaNo + "-" + subNo1 + "-" + oucu + ".fhi", false);
                        fhiString = this.changeStatus(fhiString, "submission_status", "");
                        fhiString = this.changeStatus(fhiString, "zip_date", "");
                        fhiString = this.changeStatus(fhiString, "zip_file", "");
                        this.saveFhiString(tmaFolderPath + course + "-" + tmaNo + "-" + subNo1 + "-" + oucu + ".fhi", fhiString);
                        this.listOfTmas.setValueAt("marked", rowsSelected[studNo], 5);
                        JOptionPane.showMessageDialog(null, "Error - files have NOT been zipped!");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "There were no completed TMAs selected to be zipped!\nMake sure a mark is entered for every part of each question \nand that the tutor comment box is not empty.", "Alert", 2);
                }
                deleteDir(tempBase);
            }
        }
    }
    
    private String changeStatus(final String mainString, String startTag, final String newValue) {
        startTag = "<" + startTag;
        final String endTag = startTag.replace("<", "</");
        int tagPos1 = mainString.indexOf(startTag);
        tagPos1 += startTag.length();
        final int tagPos2 = mainString.indexOf(endTag);
        final String chunk1 = mainString.substring(0, tagPos1);
        final String chunk2 = mainString.substring(tagPos2, mainString.length());
        final String tempString = mainString.substring(tagPos1, tagPos2);
        final String newMainString = chunk1 + newValue + chunk2;
        return newMainString;
    }
    
    public int[] getSelectedFiles() {
        final List<Integer> fileList1 = new ArrayList<Integer>();
        final List<Boolean> fileBool1 = new ArrayList<Boolean>();
        fileList1.clear();
        Integer selFile = 0;
        final Integer selFile2 = 0;
        final int nRow = this.listOfTmas.getRowCount();
        final int[] selectedRows = this.listOfTmas.getSelectedRows();
        Boolean zipSelect1 = false;
        for (selFile = 0; selFile < nRow; ++selFile) {
            zipSelect1 = (Boolean)this.listOfTmas.getValueAt(selFile, 11);
            try {
                if (zipSelect1) {
                    fileList1.add(selFile);
                }
            }
            catch (final Exception ex) {}
        }
        final int[] selectedRows2 = new int[fileList1.size()];
        for (int i = 0; i < fileList1.size(); ++i) {
            selectedRows2[i] = fileList1.get(i);
        }
        return selectedRows2;
    }
    
    public void openSelectedScore() {
        try {
            boolean loadFlag = true;
            if (!this.savedFlag) {
                final Object[] options = { "Save PT3", "Don't save", "Cancel" };
                final JFrame frame = null;
                final int n = JOptionPane.showOptionDialog(frame, "Do you want to save the current TMA first?", "Current TMA may not be saved!", 1, 3, null, options, options[0]);
                if (n == 0) {
                    this.saveDetails();
                    loadFlag = true;
                }
                if (n == 1) {
                    this.savedFlag = true;
                    loadFlag = true;
                }
                if (n == 2) {
                    this.savedFlag = false;
                    loadFlag = false;
                }
            }
            if (loadFlag) {
                final int rowNo = this.gradesSummaryTable.getSelectedRow();
                final int colNo = this.gradesSummaryTable.getSelectedColumn();
                final String oucu = (String)this.gradesSummaryTable.getValueAt(rowNo, 3);
                final TableColumn col = this.gradesSummaryTable.getColumnModel().getColumn(colNo);
                final String tmaNo = (String)col.getHeaderValue();
                final String courseName1 = (String)this.courseList.getSelectedItem();
                final String pathToOucu = this.etmasFolder.getText() + "/" + courseName1 + "/" + tmaNo + "/" + oucu;
                final String subNo1 = Collections.max((Collection<? extends String>)this.getListOfSubmissions(pathToOucu));
                final String fhiFileName1 = courseName1 + "-" + tmaNo + "-" + subNo1 + "-" + oucu + ".fhi";
                final String fhiFilePath = pathToOucu + "/" + subNo1 + "/" + fhiFileName1;
                this.loadPT3(fhiFilePath);
                this.setupPreviousPt3s();
                this.tmaList.setSelectedItem(tmaNo);
                this.gradesSummary.setVisible(false);
            }
        }
        catch (final Exception ex) {}
    }
    
    public void getListOfAllStudents() {
        this.allStudents.clear();
        this.allTmas.clear();
        final String courseFolderPath = this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem();
        final File courseFolderFile = new File(courseFolderPath);
        final File[] tmaFolders = courseFolderFile.listFiles();
        for (int i = 0; i < tmaFolders.length; ++i) {
            final File thisFile = tmaFolders[i];
            final String thisFileName = thisFile.getName();
            final int lenThisFileName = thisFileName.length();
            if (thisFile.isDirectory() && lenThisFileName < 3) {
                this.allTmas.add(thisFile.getName());
                final File[] studentsForThisTma = thisFile.listFiles();
                for (int j = 0; j < studentsForThisTma.length; ++j) {
                    if (studentsForThisTma[j].isDirectory()) {
                        this.allStudents.add(studentsForThisTma[j].getName());
                    }
                }
            }
        }
    }
    
    public void openPartScores() {
        final String aTma = this.tmaList.getSelectedItem().toString();
        this.partScoresTable.setVisible(true);
        this.gradesSummaryTable1.setVisible(true);
        this.partScoresTable.setSize(800, 500);
        this.gradesSummaryTable1.setSize(800, 500);
        final String submissionNo = this.e_tma_submission_num.getText();
        int rowNo = 0;
        this.getListOfAllStudents();
        final int nRow = this.gradesSummaryTable1.getRowCount();
        final int nColumn = this.gradesSummaryTable1.getColumnCount();
        for (int r = 0; r < nRow; ++r) {
            for (int c = 0; c < nColumn; ++c) {
                this.gradesSummaryTable1.setValueAt("", r, c);
            }
        }
        this.gradesSummaryTable1.setValueAt("Qn No:", 0, 3);
        this.gradesSummaryTable1.setValueAt("Part:", 1, 3);
        this.gradesSummaryTable1.setValueAt("Max:", 2, 3);
        for (String aStudent : this.allStudents) {
            for (int sn = 1; sn < 5; ++sn) {
                final String snString = Integer.toString(sn);
                String fhiName = this.courseList.getSelectedItem() + "-" + aTma + "-" + snString + "-" + aStudent + ".fhi";
                fhiName = this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + aTma + "/" + aStudent + "/" + snString + "/" + fhiName;
                this.setSizesOfPartScoresList();
                File dataFile = new File(fhiName);
                dataFile = this.tagClean(dataFile);
                this.getShortDetails("student_details", this.studentDetailsShort, dataFile);
                final boolean errorFlag = this.getPartMarks(dataFile, rowNo + 3);
                if (!errorFlag) {
                    String entry = this.studentDetailsShort.get("forenames");
                    if (!entry.equals("")) {
                        this.gradesSummaryTable1.setValueAt(entry, rowNo + 3, 1);
                    }
                    entry = this.studentDetailsShort.get("surname");
                    if (!entry.equals("")) {
                        this.gradesSummaryTable1.setValueAt(entry, rowNo + 3, 2);
                    }
                    entry = this.studentDetailsShort.get("personal_id");
                    if (!entry.equals("")) {
                        this.gradesSummaryTable1.setValueAt(snString, rowNo + 3, 0);
                    }
                    entry = this.studentDetailsShort.get("ou_computer_user_name");
                    if (!entry.equals("")) {
                        this.gradesSummaryTable1.setValueAt("", rowNo + 3, 3);
                    }
                    this.getDetails("submission_details", this.submissionDetails, dataFile);
                    ++rowNo;
                }
            }
        }
    }
    
    public boolean getPartMarks(File aFile, final int aRow) {
        boolean errorFlag = false;
        aFile = this.tagClean(aFile);
        this.setSizesOfPartScoresList();
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final org.w3c.dom.Document doc = docBuilder.parse(aFile);
            this.getQuestionNumbers().clear();
            this.partStarts.clear();
            this.numberOfParts.clear();
            doc.getDocumentElement().normalize();
            final NodeList questionBlock = doc.getElementsByTagName("question_details");
            final NodeList listOfQuestions = questionBlock.item(0).getChildNodes();
            this.numberOfQuestions = listOfQuestions.getLength();
            final List<String> maxScores = new ArrayList<String>();
            final List<String> questionScores = new ArrayList<String>();
            final List<String> qpCount = new ArrayList<String>();
            final List<String> studQnScores = new ArrayList<String>();
            String questionNumber = "";
            String maxScore = "";
            String qpc = "";
            String studQnScore = "";
            Element questionElement = null;
            String questionScore = "";
            for (int questionNo = 0; questionNo < this.numberOfQuestions; ++questionNo) {
                try {
                    questionElement = (Element)listOfQuestions.item(questionNo);
                    questionNumber = questionElement.getAttribute("question_number");
                    maxScore = questionElement.getElementsByTagName("maximum_question_score").item(0).getTextContent();
                    maxScores.add(maxScore);
                    questionScore = questionElement.getElementsByTagName("student_question_score").item(0).getTextContent();
                    questionScores.add(questionScore);
                    qpc = questionElement.getElementsByTagName("question_parts_count").item(0).getTextContent();
                    qpCount.add(qpc);
                    studQnScore = questionElement.getElementsByTagName("student_question_score").item(0).getTextContent();
                    studQnScores.add(studQnScore);
                    this.questionNumbers.add(questionNumber);
                }
                catch (final Exception ex) {}
            }
            int columnNo = 0;
            for (int i = 0; i < this.numberOfQuestions; ++i) {
                this.gradesSummaryTable1.setValueAt(this.getQuestionNumbers().get(i), 0, columnNo + 4);
                if (qpCount.get(i).equals("0")) {
                    this.partStarts.add(columnNo);
                    this.gradesSummaryTable1.setValueAt(maxScores.get(i), 2, columnNo + 4);
                    this.gradesSummaryTable1.setValueAt(questionScores.get(i), aRow, columnNo + 4);
                    ++columnNo;
                }
                else {
                    this.partStarts.add(columnNo);
                    final NodeList listOfQuestionParts = listOfQuestions.item(i).getChildNodes();
                    final int totalQuestionParts = listOfQuestionParts.getLength();
                    String partScore = "";
                    String partNumber = "";
                    String partMax = "";
                    for (int partNo = 3; partNo < totalQuestionParts; ++partNo) {
                        final Element partElement = (Element)listOfQuestionParts.item(partNo);
                        final String questionPartId = partElement.getAttribute("part_id");
                        partScore = partElement.getElementsByTagName("student_question_part_score").item(0).getTextContent();
                        partMax = partElement.getElementsByTagName("maximum_question_part_score").item(0).getTextContent();
                        partNumber = partElement.getElementsByTagName("questn_part_desc").item(0).getTextContent();
                        this.gradesSummaryTable1.setValueAt(partScore, aRow, columnNo + 4);
                        this.gradesSummaryTable1.setValueAt(partNumber, 1, columnNo + 4);
                        this.gradesSummaryTable1.setValueAt(partMax, 2, columnNo + 4);
                        ++columnNo;
                    }
                }
            }
            this.partStarts.add(columnNo);
        }
        catch (final Exception anException) {
            errorFlag = true;
        }
        try {
            this.calculateTotals();
        }
        catch (final Exception ex2) {}
        return errorFlag;
    }
    
    public void openGradesListAlt() {
        this.getListOfAllStudents();
        final Double[] totalMarks = new Double[22];
        final Integer[] numberOfMarks = new Integer[22];
        for (int i = 0; i < 22; ++i) {
            totalMarks[i] = 0.0;
            numberOfMarks[i] = 0;
        }
        this.gradesSummary.setVisible(true);
        this.gradesSummary.setSize(800, 400);
        this.gradesSummary.setLocation(50, 50);
        this.gradesSummaryTable.setSize(800, 400);
        this.gradesSummaryTable.setAutoResizeMode(0);
        this.gradesSummary.setTitle("Grades for course " + this.courseList.getSelectedItem());
        final Color glbgColor = new Color(200, 255, 255);
        final int nRow = this.gradesSummaryTable.getRowCount();
        final int nColumn = this.gradesSummaryTable.getColumnCount();
        for (int r = 0; r < nRow; ++r) {
            for (int c = 0; c < nColumn; ++c) {
                this.gradesSummaryTable.setValueAt(null, r, c);
                if (r == 16) {
                    this.gradesSummaryTable.setBackground(glbgColor);
                }
            }
        }
        this.redRows.clear();
        this.blueRows.clear();
        int rowNo = 0;
        int colNo = 0;
        for (String aStudent : this.allStudents) {
            for (String aTma : this.allTmas) {
                String fhiName = this.courseList.getSelectedItem() + "-" + aTma + "-1-" + aStudent + ".fhi";
                fhiName = this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + aTma + "/" + aStudent + "/1/" + fhiName;
                File dataFile = new File(fhiName);
                dataFile = this.tagClean(dataFile);
                this.getShortDetails("student_details", this.studentDetailsShort, dataFile);
                String entry = this.studentDetailsShort.get("forenames");
                if (!entry.equals("")) {
                    this.gradesSummaryTable.setValueAt(entry, rowNo, 1);
                }
                entry = this.studentDetailsShort.get("surname");
                if (!entry.equals("")) {
                    this.gradesSummaryTable.setValueAt(entry, rowNo, 2);
                }
                entry = this.studentDetailsShort.get("personal_id");
                if (!entry.equals("")) {
                    this.gradesSummaryTable.setValueAt(entry, rowNo, 0);
                }
                entry = this.studentDetailsShort.get("ou_computer_user_name");
                if (!entry.equals("")) {
                    this.gradesSummaryTable.setValueAt(entry, rowNo, 3);
                }
            }
            ++rowNo;
        }
        rowNo = 0;
        colNo = 0;
        for (final String aStudent : this.allStudents) {
            this.getPreviousScores((String)this.courseList.getSelectedItem(), aStudent, false, rowNo);
            for (int j = 0; j < this.studentMarksList.size(); ++j) {
                String tempMark = this.studentMarksList.get(j);
                if (tempMark.equals("")) {
                    tempMark = "*";
                }
                this.gradesSummaryTable.setValueAt(tempMark, rowNo, j + 4);
            }
            ++rowNo;
            final JTableHeader tmaListHeader = new JTableHeader();
            for (int noOfMarks = this.studentMarksList.size(), k = 0; k < noOfMarks; ++k) {
                String currentMark = this.studentMarksList.get(k);
                try {
                    totalMarks[k + 4] += Double.parseDouble(currentMark);
                    final Integer[] array = numberOfMarks;
                    final int n = k + 4;
                    final Integer n2 = array[n];
                    ++array[n];
                }
                catch (final Exception ex) {}
                if (currentMark.equals("")) {
                    currentMark = "*";
                }
            }
        }
        this.gradesSummaryTable.setValueAt("AVERAGE", this.allStudents.size(), 0);
        String thisAverage = "";
        for (int l = 4; l < 18; ++l) {
            double thisAverageDouble = totalMarks[l] / numberOfMarks[l];
            thisAverageDouble = Math.round(thisAverageDouble * 10.0) / 10.0;
            thisAverage = String.valueOf(thisAverageDouble);
            if (!thisAverage.equals("0.0")) {
                this.gradesSummaryTable.setValueAt(thisAverage, this.allStudents.size(), l);
            }
        }
        double aTotal = 0.0;
        int numMarks = 0;
        double thisAverageDouble2 = 0.0;
        for (int m = 0; m < this.allStudents.size(); ++m) {
            final double thisMark = (double)this.gradesSummaryTable.getValueAt(m, 16);
            final String currentMark2 = (String)this.gradesSummaryTable.getValueAt(m, this.allTmas.size() + 3);
            if (!currentMark2.equals("*")) {
                aTotal += thisMark;
                ++numMarks;
            }
            thisAverageDouble2 = aTotal / numMarks;
            thisAverageDouble2 = Math.round(thisAverageDouble2 * 10.0) / 10.0;
            thisAverage = String.valueOf(thisAverageDouble2);
        }
        this.gradesSummaryTable.setValueAt(thisAverageDouble2, this.allStudents.size(), 16);
        try {
            this.calculateTotals();
        }
        catch (final Exception ex2) {}
        this.setSizesOfGradesList();
    }
    
    public void openGradesList() {
        final Double[] totalMarks = new Double[22];
        final Integer[] numberOfMarks = new Integer[22];
        for (int i = 0; i < 22; ++i) {
            totalMarks[i] = 0.0;
            numberOfMarks[i] = 0;
        }
        this.gradesSummary.setVisible(true);
        this.gradesSummary.setSize(800, 400);
        this.gradesSummary.setLocation(50, 50);
        this.gradesSummaryTable.setSize(800, 400);
        this.gradesSummaryTable.setAutoResizeMode(0);
        this.gradesSummary.setTitle("Grades for module " + this.courseList.getSelectedItem());
        final int nRow = this.gradesSummaryTable.getRowCount();
        final int nColumn = this.gradesSummaryTable.getColumnCount();
        for (int r = 0; r < nRow; ++r) {
            for (int c = 0; c < nColumn; ++c) {
                this.gradesSummaryTable.setValueAt("", r, c);
            }
        }
        this.redRows.clear();
        this.makeTmaTable(1);
        final int numberOfStudents = this.studentList.getItemCount();
        for (int studNo = 0; studNo < numberOfStudents; ++studNo) {
            this.gradesSummaryTable.setValueAt(this.listOfTmas.getValueAt(studNo, 0), studNo, 0);
            this.gradesSummaryTable.setValueAt(this.listOfTmas.getValueAt(studNo, 1), studNo, 1);
            this.gradesSummaryTable.setValueAt(this.listOfTmas.getValueAt(studNo, 2), studNo, 2);
            this.gradesSummaryTable.setValueAt(this.listOfTmas.getValueAt(studNo, 3), studNo, 3);
            this.getPreviousScores((String)this.listOfTmas.getValueAt(studNo, 7) + "-" + (String)this.listOfTmas.getValueAt(studNo, 8), (String)this.listOfTmas.getValueAt(studNo, 3), false, studNo);
            final JTableHeader tmaListHeader = new JTableHeader();
            for (int noOfMarks = this.studentMarksList.size(), j = 0; j < noOfMarks; ++j) {
                String currentMark = this.studentMarksList.get(j);
                try {
                    totalMarks[j + 4] += Double.parseDouble(currentMark);
                    final Integer[] array = numberOfMarks;
                    final int n = j + 4;
                    final Integer n2 = array[n];
                    ++array[n];
                }
                catch (final Exception ex) {}
                if (currentMark.equals("")) {
                    currentMark = "*";
                }
                this.gradesSummaryTable.setValueAt(currentMark, studNo, j + 4);
            }
            try {
                String thisMark = (String)this.gradesSummaryTable.getValueAt(studNo, 16);
                final String asterisks = "**";
                thisMark = thisMark.replace(asterisks, "");
                totalMarks[16] += Double.parseDouble(thisMark);
                final Integer[] array2 = numberOfMarks;
                final Integer n3 = array2[16];
                ++array2[16];
            }
            catch (final Exception ex2) {}
        }
        this.setSizesOfGradesList();
        this.gradesSummaryTable.setValueAt("AVERAGE", numberOfStudents, 0);
        String thisAverage = "";
        for (int k = 4; k < 18; ++k) {
            thisAverage = String.valueOf(totalMarks[k] / numberOfMarks[k]);
            if (!thisAverage.equals("NaN")) {
                this.gradesSummaryTable.setValueAt(thisAverage, numberOfStudents, k);
            }
        }
        try {
            this.calculateTotals();
        }
        catch (final Exception ex3) {}
    }
    
    private void listAllScoresActionPerformed(final ActionEvent evt) {
        this.openGradesListAlt();
    }
    
    private void resize(final JTable table, final int newSize, final int colNo) {
        table.setAutoResizeMode(0);
        final int vColIndex = colNo;
        final TableColumn col = table.getColumnModel().getColumn(vColIndex);
        final int width = newSize;
        col.setPreferredWidth(width);
        if (table.equals(this.gradesSummaryTable) && colNo > 3) {
            col.setHeaderValue(this.prevMarks.getValueAt(0, colNo - 4));
        }
    }
    
    private void setSizesOfTmaList() {
        for (int i = 0; i < this.tmaListSizes.length; ++i) {
            this.resize(this.listOfTmas, this.tmaListSizes[i], i);
        }
    }
    
    private void setSizesOfPrevScoresList() {
        for (int i = 0; i < this.prevMarksListSizes.length; ++i) {
            this.resize(this.prevMarks, this.prevMarksListSizes[i], i);
        }
    }
    
    private void setSizesOfGradesList() {
        for (int i = 0; i < this.gradesListSizes.length; ++i) {
            this.resize(this.gradesSummaryTable, this.gradesListSizes[i], i);
        }
    }
    
    private void setSizesOfPartScoresList() {
        for (int i = 0; i < this.partScoresListSizes.length; ++i) {
            this.resize(this.gradesSummaryTable1, this.partScoresListSizes[i], i);
        }
    }
    
    private void sizeColumns(final JTable table) {
        this.gradesSummaryTable.setAutoResizeMode(0);
        final TableColumnModel columns = table.getColumnModel();
        final TableModel data = table.getModel();
        final int margin = columns.getColumnMargin() * 2;
        final int columnCount = columns.getColumnCount();
        final int rowCount = data.getRowCount();
        for (int col = 0; col < columnCount; ++col) {
            final TableColumn column = columns.getColumn(col);
            final int modelCol = column.getModelIndex();
            int width = 0;
            for (int row = 0; row < rowCount; ++row) {
                final TableCellRenderer r = table.getCellRenderer(row, col);
                final int w = r.getTableCellRendererComponent(table, data.getValueAt(row, modelCol), false, false, row, col).getPreferredSize().width;
                if (w > width) {
                    width = w;
                }
            }
        }
    }
    
    private void createMarkedMouseReleased(final MouseEvent evt) {
        this.ourRoot.putBoolean("createMarked", this.createMarked.isSelected());
    }
    
    private void setupWeightings() {
        for (int i = 0; i < 15; ++i) {
            this.tmaWeightings.add("1");
            this.tmaMaxScores.add("100");
        }
    }
    
    private void selectDownloadsFolderActionPerformed(final ActionEvent evt) {
        boolean okFlag = false;
        while (!okFlag) {
            final JFileChooser _fileChooser = new JFileChooser();
            _fileChooser.setFileSelectionMode(1);
            _fileChooser.setDialogTitle("Please select your usual Browser Downloads folder");
            final int path = _fileChooser.showOpenDialog(null);
            final File aFile = _fileChooser.getSelectedFile();
            final String thisPath = aFile.getPath();
            if (!thisPath.contains(this.etmasFolder.getText())) {
                okFlag = true;
                this.downloadsFolder.setText(thisPath);
                this.ourRoot.put("downloadsFolder", aFile.getPath());
            }
            else {
                JOptionPane.showMessageDialog(null, "It's not a good idea to download directly into your 'etmas' folder!\nPlease choose another location:");
            }
        }
    }
    
    public static boolean deleteDir(final File dir) {
        if (dir.isDirectory()) {
            final String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) {
                final boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    
    private void trainingSiteActionPerformed(final ActionEvent evt) {
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        final String myURI = "http://etma-training.open.ac.uk/etma/tutor/etmat_training_signon.asp";
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }
    
    public void openMainEtmaSite() {
        final Runtime thisRun = Runtime.getRuntime();
        final String appPath = this.jsTestFile.getText();
        System.out.println(appPath);
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        final String myURI = this.ouEtmaAddress.getText();
        try {
            if (!this.osName.equals("Mac OS X")) {
                Desktop.getDesktop().browse(new URI(myURI));
            }
            else {
                final String tmaFiletoOpenMac = myURI.replace("/", ":");
                final String appPathMac = appPath.replace("/", ":");
                String argsString = "";
                if (appPath.equals("") || appPath.equals("System Default")) {
                    argsString = "tell application \"Finder\" to open location \"" + myURI;
                    argsString = argsString.replace("\":", "\"");
                }
                else {
                    argsString = "tell application +\"" + appPathMac + "\"to open location \"" + myURI;
                    argsString = argsString.replace("\":", "\"");
                }
                final String[] args = { "osascript", "-e", argsString };
                thisRun.exec(args);
                System.out.println(argsString);
            }
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }
    
    public void openMainEtmaSite2() {
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        final String myURI = this.ouEtmaAddress.getText();
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }
    
    public void startImportTimer() {
        try {
            this.timer2.cancel();
        }
        catch (final Exception ex) {}
        this.timer2 = new Timer();
        final ScheduleRunner2 task2 = new ScheduleRunner2();
        this.timer2.schedule(task2, 5000L, 5000L);
    }
    
    private void etmaSiteActionPerformed(final ActionEvent evt) {
        this.openMainEtmaSite();
    }
    
    public void openURL(final String ouUrl) {
        final Runtime thisRun = Runtime.getRuntime();
        String browser = "";
        try {
            if (this.osName.equals("Mac OS X")) {
                final String[] cmd = { "open", ouUrl };
                thisRun.exec(cmd);
            }
            if (this.osName.contains("Windows")) {
                thisRun.exec("explorer.exe " + ouUrl);
            }
            if (this.osName.contains("Linux")) {
                browser = "firefox";
                if (!this.currentBrowserPreferences.equals("")) {
                    browser = this.currentBrowserPreferences;
                }
                thisRun.exec(new String[] { browser, ouUrl });
            }
        }
        catch (final Exception e) {
            this.progNotFound(browser);
        }
    }
    
    public File cleanFileName(final File aFile) {
        int maxLength = 40;
        File bFile = null;
        if (!aFile.isDirectory()) {
            final String parentPath = aFile.getParent();
            String oldPath = aFile.getName();
            if (oldPath.contains("-MARKED")) {
                maxLength = 50;
            }
            final int opLen = oldPath.length();
            if (opLen > maxLength) {
                final String firstPart = oldPath.substring(0, 15);
                final String lastPart = oldPath.substring(opLen - 15, opLen);
                oldPath = firstPart + lastPart;
                JOptionPane.showMessageDialog(null, "The filename is too long. It has now been truncated\n to keep it within the OU limit.");
            }
            String newPath = parentPath + "/" + oldPath.replaceAll(" ", "_");
            newPath = newPath.replaceAll(",", "_");
            bFile = new File(newPath);
            aFile.renameTo(bFile);
        }
        return bFile;
    }
    
    public void linuxOpen(final String fileName) {
        final int numberOfManagers = this.LINUXFILEMANAGER.length;
        boolean managerFound = false;
        for (int i = 0; i < numberOfManagers; ++i) {
            if (!managerFound) {
                try {
                    final Runtime thisRun = Runtime.getRuntime();
                    final String[] cmd = { this.LINUXFILEMANAGER[i], fileName };
                    thisRun.exec(cmd);
                    managerFound = true;
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    public List listAllFiles(final List<File> fileList) {
        final List<String> fileListString = null;
        List<File> innerFileList = null;
        for (final File aFile : fileList) {
            fileListString.add(aFile.getPath());
            if (aFile.isDirectory()) {
                innerFileList = Arrays.asList(aFile.listFiles());
                for (final File bFile : innerFileList) {
                    fileListString.add(bFile.getPath());
                }
            }
        }
        return fileList;
    }
    
    public void tmaScriptOpener() {
        String initials = "";
        List<File> fileList = new ArrayList<File>();
        List<File> innerFileList = new ArrayList<File>();
        List<File> innerFileList2 = new ArrayList<File>();
        final List<String> fileListString = new ArrayList<String>();
        final DocumentFilter filter1 = new DocumentFilter();
        Boolean fileExists = false;
        String fileToOpen = null;
        final File fhiFile = new File(this.fhiFileName.getText());
        if (this.addInitialsFlag.isSelected()) {
            initials = this.forenames.getText().substring(0, 1) + this.getMiddleInitial(this.forenames.getText()) + this.surname.getText().substring(0, 1);
        }
        this.markedString = "-MARKED";
        this.markedString1 = this.markedString;
        final Boolean createFlag = this.ourRoot.getBoolean("createMarked", true);
        try {
            this.calculateTotals();
        }
        catch (final Exception ex) {}
        final String tmaFolder = fhiFile.getParent();
        final File tmaFolderFile = new File(tmaFolder);
        fileList = Arrays.asList(tmaFolderFile.listFiles());
        for (final File aFile : fileList) {
            fileListString.add(aFile.getPath());
            if (aFile.isDirectory()) {
                innerFileList = Arrays.asList(aFile.listFiles());
                for (final File bFile : innerFileList) {
                    fileListString.add(bFile.getPath());
                    if (bFile.isDirectory()) {
                        innerFileList2 = Arrays.asList(bFile.listFiles());
                        for (final File cFile : innerFileList2) {
                            fileListString.add(cFile.getPath());
                        }
                    }
                }
            }
        }
        final boolean mp3ExistsFlag = false;
        this.acceptableFilesSet.clear();
        this.acceptableFilesSet.add(".doc");
        this.acceptableFilesSet.add(".rtf");
        this.acceptableFilesSet.add(".docx");
        this.acceptableFilesSet.add(".pdf");
        this.acceptableFilesSet.add(".py");
        this.acceptableFilesSet.add(".jar");
        this.acceptableFilesSet.add(".mp3");
        this.acceptableFilesSet.add(".MP3");
        this.acceptableFilesSet.add(".wav");
        this.acceptableFilesSet.add(".WAV");
        for (final String aFileName : fileListString) {
            final File aTempFile = new File(aFileName);
            final String fileShortName = aTempFile.getName();
            final String firstChar = fileShortName.substring(0, 1);
            if (aFileName.contains(this.markedString) && !fileExists && !fileShortName.startsWith("~$") && createFlag && !firstChar.equals(".")) {
                fileExists = true;
                final JFrame frame = null;
                int n = 0;
                final Object[] options = { "Marked", "Other", "Cancel" };
                if (mp3ExistsFlag) {
                    options[0] = "MP3";
                    n = JOptionPane.showOptionDialog(frame, "MP3 file or a different one?", "", 1, 3, null, options, options[0]);
                }
                else {
                    n = JOptionPane.showOptionDialog(frame, "Marked file or a different one?", "", 1, 3, null, options, options[0]);
                }
                if (n == 0) {
                    File xFile = new File(aFileName);
                    xFile = this.cleanFileName(xFile);
                    fileToOpen = xFile.getPath();
                }
                if (n == 1) {
                    final JFileChooser _fileChooser = new JFileChooser(tmaFolder);
                    _fileChooser.setFileFilter(filter1);
                    _fileChooser.setDialogTitle("Please select a script - probably a '.doc' file:");
                    final int path = _fileChooser.showOpenDialog(null);
                    File aFile2 = _fileChooser.getSelectedFile();
                    aFile2 = this.cleanFileName(aFile2);
                    fileToOpen = aFile2.getPath();
                }
                if (fileToOpen.contains(".fhi")) {
                    JOptionPane.showMessageDialog(null, "This is a system file - it is not a student script! Please try again.", "", 0);
                }
                else if (fileToOpen.contains(".mp3") || fileToOpen.contains(".MP3")) {
                    this.mp3Opener(fileToOpen);
                }
                else {
                    this.tmaOpener(fileToOpen);
                }
            }
        }
        if (!fileExists || !createFlag) {
            final JFileChooser _fileChooser2 = new JFileChooser(tmaFolder);
            _fileChooser2.setFileFilter(filter1);
            if (!mp3ExistsFlag) {
                _fileChooser2.setDialogTitle("Please select your student's script - probably a '.doc' file:");
            }
            else {
                _fileChooser2.setDialogTitle("Please select your student's MP3 file:");
            }
            final int path2 = _fileChooser2.showOpenDialog(null);
            File aFile3 = _fileChooser2.getSelectedFile();
            aFile3 = this.cleanFileName(aFile3);
            String fileToCreate;
            final String studentTma = fileToCreate = aFile3.getPath();
            final String studentTmaParent = aFile3.getParent();
            final String studentTmaName = aFile3.getName();
            final int extPosition = studentTmaName.lastIndexOf(".");
            String extension = "";
            try {
                extension = studentTmaName.substring(extPosition, studentTmaName.length());
            }
            catch (final Exception ex2) {}
            if (extension.equals(".fhi")) {
                JOptionPane.showMessageDialog(null, "This is a system file - it is not a student script! Please try again.", "", 0);
            }
            else {
                String otherPart = studentTmaName;
                try {
                    otherPart = studentTmaName.substring(0, extPosition);
                }
                catch (final Exception ex3) {}
                if (createFlag) {
                    if (extension.equals(".mp3") || extension.equals(".MP3")) {
                        otherPart = "Feedback" + otherPart;
                    }
                    fileToCreate = studentTmaParent + "/" + initials + otherPart + this.markedString1 + extension;
                    final File bFile2 = new File(fileToCreate);
                    try {
                        this.makeCopy(aFile3, bFile2);
                    }
                    catch (final Exception anException) {
                        JOptionPane.showMessageDialog((Component)null, "Error3 " + anException);
                    }
                }
                if (fileToCreate.contains(".mp3") || fileToCreate.contains(".MP3")) {
                    this.mp3Opener(fileToCreate);
                }
                else {
                    this.tmaOpener(fileToCreate);
                }
            }
        }
    }
    
    private void openTmaActionPerformed(final ActionEvent evt) {
        this.tmaScriptOpener();
    }
    
    public String getMiddleInitial(final String aString) {
        String middleInitial = "";
        try {
            if (aString.contains(" ")) {
                final int spacePos = aString.indexOf(" ");
                middleInitial = aString.substring(spacePos + 1, spacePos + 2);
            }
        }
        catch (final Exception ex) {}
        return middleInitial;
    }
    
    public String browserOpenString(final String fileToOpen) {
        String openString = "";
        final String appPath = this.browserPath.getText();
        String appName = "";
        String bundleId = "";
        int slashPos = 0;
        if (this.osName.equals("Mac OS X")) {
            slashPos = appPath.lastIndexOf("/");
            appName = appPath.substring(slashPos + 1, appPath.length());
            bundleId = this.wpMap.get(appName);
            if (appName.equals("TextEdit.app")) {
                openString = "open -e " + fileToOpen;
            }
            else {
                openString = "open -b " + bundleId + " file://" + fileToOpen;
            }
        }
        else {
            openString = appPath;
        }
        return openString;
    }
    
    public void tmaOpener(final String tmaFiletoOpen) {
        final String appPath = this.wpPath.getText();
        final String appName = "";
        final String bundleId = "";
        final String openString = "";
        final int slashPos = 0;
        this.currentStudentScript = tmaFiletoOpen;
        final File tmaFile = new File(tmaFiletoOpen);
        final long folderSize = this.calculateDirectorySize(tmaFile.getParentFile());
        if (folderSize > 9000000L && this.sizeWarnFlagPreferences) {
            final long folderSizeKB = folderSize / 1000L;
            final long folderSizeMB = folderSize / 1000000L;
            JOptionPane.showMessageDialog((Component)null, "This student's folder is about " + folderSizeMB + "MB!\nIt may be rather big to return. You should if possible only return the MARKED file,\n and remove the original file from the student's folder before zipping.\n Use the button 'Open TMA Folder' to see what's there!\nYou can disable this warning in etmaHandlerJ-> Preferences.");
        }
        try {
            final Runtime thisRun = Runtime.getRuntime();
            if (this.osName.equals("Mac OS X")) {
                if (this.wpPath.getText().equals("System Default")) {
                    try {
                        final String[] cmd = { "open", tmaFiletoOpen };
                        thisRun.exec(cmd);
                    }
                    catch (final Exception ex) {}
                }
                else {
                    final String tmaFiletoOpenMac = tmaFiletoOpen.replace("/", ":");
                    final String appPathMac = appPath.replace("/", ":");
                    final String argsString = "tell application \"Finder\" to copy name of startup disk to std\ntell application \"Finder\" to open file (std &\"" + tmaFiletoOpenMac + "\")using (std &\"" + appPathMac + "\")";
                    final String[] args = { "osascript", "-e", argsString };
                    thisRun.exec(args);
                }
            }
            if (this.osName.contains("Darwin")) {
                try {
                    final String[] cmd = { "open", tmaFiletoOpen };
                    thisRun.exec(cmd);
                }
                catch (final Exception ex2) {}
            }
            if (this.osName.contains("Windows")) {
                final String tmaFiletoOpenWindows = tmaFiletoOpen.replace('/', '\\');
                if (this.currentWpPreferences.equals("System Default") || this.currentWpPreferences.equals("")) {
                    thisRun.exec("explorer.exe " + tmaFiletoOpenWindows);
                }
                else {
                    final String[] cmd2 = { appPath, tmaFiletoOpenWindows };
                    thisRun.exec(cmd2);
                }
            }
            if (this.osName.contains("Linux") && !this.wpPath.getText().equals("")) {
                this.linuxWP = this.wpPath.getText();
                try {
                    final String[] cmd = { this.linuxWP, tmaFiletoOpen };
                    if (!this.linuxWP.equals("System Default") && !this.linuxWP.equals("")) {
                        thisRun.exec(cmd);
                    }
                    else {
                        Desktop.getDesktop().open(new File(tmaFiletoOpen));
                    }
                }
                catch (final Exception anException) {
                    this.progNotFound(this.linuxWP);
                }
            }
        }
        catch (final Exception anException2) {
            JOptionPane.showMessageDialog(null, anException2);
        }
    }
    
    public void mp3Opener(final String tmaFiletoOpen) {
        final String appPath = this.audioPath.getText();
        final String appName = "";
        final String bundleId = "";
        final String openString = "";
        final int slashPos = 0;
        this.currentStudentScript = tmaFiletoOpen;
        final String extension = tmaFiletoOpen.substring(tmaFiletoOpen.lastIndexOf("."));
        if (extension.equals(".mp3")) {
            try {
                final Runtime thisRun = Runtime.getRuntime();
                if (this.osName.equals("Mac OS X")) {
                    if (this.audioPath.getText().equals("System Default")) {
                        final String[] cmd = { "open", tmaFiletoOpen };
                        thisRun.exec(cmd);
                    }
                    else {
                        final String tmaFiletoOpenMac = tmaFiletoOpen.replace("/", ":");
                        final String appPathMac = this.audioPath.getText().replace("/", ":");
                        final String argsString = "tell application \"Finder\" to copy name of startup disk to std\ntell application \"Finder\" to open file (std &\"" + tmaFiletoOpenMac + "\")using (std &\"" + appPathMac + "\")";
                        final String[] args = { "osascript", "-e", argsString };
                        System.out.println(argsString);
                        System.out.println(appPath);
                        thisRun.exec(args);
                    }
                }
                if (this.osName.contains("Windows")) {
                    if (this.currentAudioPreferences.equals("System Default") || this.currentAudioPreferences.equals("")) {
                        thisRun.exec("explorer.exe " + tmaFiletoOpen);
                    }
                    else {
                        final String[] cmd = { appPath, tmaFiletoOpen };
                        thisRun.exec(cmd);
                    }
                }
                if (this.osName.contains("Linux")) {
                    if (!this.audioPath.getText().equals("")) {
                        this.linuxAudio = this.audioPath.getText();
                    }
                    try {
                        final String[] cmd = { this.linuxAudio, tmaFiletoOpen };
                        thisRun.exec(cmd);
                    }
                    catch (final Exception anException) {
                        this.progNotFound(this.linuxAudio);
                    }
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    private void makeCopy(final File src, final File dst) throws IOException {
        final InputStream in = new FileInputStream(src);
        final OutputStream out = new FileOutputStream(dst);
        final byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    public void tmaFolderOpener() {
        final File fhiFile = new File(this.fhiFileName.getText());
        final String tmaFolder = fhiFile.getParent();
        try {
            Desktop.getDesktop().open(new File(tmaFolder));
        }
        catch (final Exception ex) {}
        try {
            this.calculateTotals();
        }
        catch (final Exception ex2) {}
    }
    
    private void openTmaFolderActionPerformed(final ActionEvent evt) {
        this.tmaFolderOpener();
    }
    
    public void tmaCollector() {
        try {
            this.calculateTotals();
        }
        catch (final Exception ex) {}
        boolean zipFlag = true;
        final Object[] options = { "Yes", "No", "Cancel" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, "Has the downloaded file from the OU site been Unzipped?", "", 1, 3, null, options, options[0]);
        if (n == 1) {
            zipFlag = this.unZipAlt();
            this.autoImportFlag = true;
        }
        if (n != 2 && zipFlag) {
            if (this.submittedTmas.isVisible()) {
                this.saveLocation();
            }
            this.collectTmas();
        }
        this.courseList.setSelectedItem(this.courseName);
        if (this.submittedTmas.isVisible()) {
            this.openList();
        }
    }
    
    private void collectTmasActionPerformed(final ActionEvent evt) {
        this.tmaCollector();
    }
    
    private boolean getAllFiles(final File curDir) {
        final File[] listFiles;
        final File[] filesList = listFiles = curDir.listFiles();
        for (final File f : listFiles) {
            if (f.isDirectory()) {
                this.getAllFiles(f);
            }
            if (f.isFile()) {
                System.out.println(f.getName());
                if (f.getName().equals("monitor.fhi")) {
                    this.isMonitorData = true;
                }
            }
        }
        return this.isMonitorData;
    }
    
    private void collectTmas() {
        final List<String> errorList = new ArrayList<String>();
        File aFile = null;
        boolean duplicateFlag = false;
        this.isMonitorData = false;
        if (this.pathToDownloadedFile.equals("")) {
            if (!this.autoImportFlag || this.courseDirectoryList.size() == 0) {
                final JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
                _fileChooser.setFileSelectionMode(1);
                _fileChooser.setDialogTitle("Please select downloaded modulefolder (eg 'M150-06J') :");
                final int path = _fileChooser.showOpenDialog(null);
                aFile = _fileChooser.getSelectedFile();
                this.courseDirectoryList.add(aFile.getPath());
                this.isMonitorData = this.getAllFiles(aFile);
            }
            else {
                aFile = new File(this.courseDirectory);
                this.isMonitorData = this.getAllFiles(aFile);
            }
        }
        try {
            this.isMonitorData = this.getAllFiles(aFile);
        }
        catch (final Exception ex) {}
        System.out.println(this.isMonitorData);
        for (String tempDirectory : this.courseDirectoryList) {
            aFile = new File(tempDirectory);
            boolean continueFlag = true;
            if (aFile.getPath().contains(this.etmasFolder.getText())) {
                JOptionPane.showMessageDialog(null, "It looks as though your downloaded file is in your etmas folder.\nYou must move it elsewhere before you import it.", "", 0);
                continueFlag = false;
            }
            else if (!aFile.getName().contains("-")) {
                JOptionPane.showMessageDialog(null, "This is not a correctly named module code folder!", "", 0);
                continueFlag = false;
            }
            else {
                final String folderName = aFile.getName();
                final String[] hyphenParts = folderName.split("-");
                final int hyphenCount = hyphenParts.length;
                final String[] spaceParts = folderName.split(" ");
                final int spaceCount = spaceParts.length;
                if (hyphenCount > 2 || spaceCount > 1 || (folderName.contains(".") & this.osName.equals("Mac OS X"))) {
                    JOptionPane.showMessageDialog((Component)null, aFile.getName() + " looks like a duplicate module folder in the same location, with a suffix added!\nIf this is the case, delete it, move or import the other properly named folder\nand download the last batch of  TMA(s) from the OU site again.", "", 0);
                    final Object[] options = { "Cancel", "Yes, I want to go on" };
                    final JFrame frame = null;
                    final int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to continue importing this suspect folder?", "", 1, 3, null, options, options[0]);
                    if (n == 0) {
                        continueFlag = false;
                    }
                }
                if (this.isMonitorData) {
                    JOptionPane.showMessageDialog(null, "This is a MONITORING Data File, not one for Marking!\nYou probably need to download the monitor filehandler.", "", 0);
                    continueFlag = false;
                }
                if (!continueFlag) {
                    continue;
                }
                File dFile = null;
                this.courseName = aFile.getName();
                final String oldPathName = aFile.getParent();
                final String fixedFilename = this.fixDuplicate(this.courseName);
                final String newPathName = this.etmasFolder.getText();
                if (this.osName.contains("Windows")) {
                    dFile = new File(newPathName + "\\" + fixedFilename);
                }
                else {
                    dFile = new File(newPathName + "/" + fixedFilename);
                }
                try {
                    dFile.mkdir();
                }
                catch (final Exception e) {
                    System.out.println("Error " + e);
                }
                this.tmaNewFiles.clear();
                this.tmaTempFiles.clear();
                this.tmaTransFiles.clear();
                this.getAllFiles(aFile.getPath());
                this.tmaTempFiles.addAll(this.tmaNewFiles);
                String tempString = "";
                for (String aString : this.tmaTempFiles) {
                    tempString = aString.replace(oldPathName + "/" + this.courseName, newPathName + "/" + fixedFilename);
                    if (this.osName.contains("Windows")) {
                        tempString = aString.replace(oldPathName + "\\" + this.courseName, newPathName + "\\" + fixedFilename);
                    }
                    this.tmaTransFiles.add(tempString);
                }
                for (int numberOfFiles = this.tmaNewFiles.size(), i = 0; i < numberOfFiles; ++i) {
                    final File bFile = new File(this.tmaNewFiles.get(i));
                    final File cFile = new File(this.tmaTransFiles.get(i));
                    if (!cFile.exists()) {
                        try {
                            if (bFile.isDirectory()) {
                                cFile.mkdir();
                            }
                            this.makeCopy(bFile, cFile);
                        }
                        catch (final Exception anException) {
                            System.out.println("Error1 " + anException);
                        }
                    }
                    else if (!cFile.isDirectory() && !cFile.getName().equals(".DS_Store")) {
                        duplicateFlag = true;
                        errorList.add("\n" + cFile.getPath());
                    }
                }
                if (!duplicateFlag) {
                    JOptionPane.showMessageDialog((Component)null, "New files and folders for module " + aFile.getName() + " were transferred successfully! \nYou may discard the 'old' module folder which will be in the Trash or on the Desktop.");
                }
                else {
                    final Object[] options2 = { "Yes", "No" };
                    final JFrame frame2 = null;
                    final int n2 = JOptionPane.showOptionDialog((Component)frame2, "There may be some duplicate files for module " + aFile.getName() + "  which haven't been replaced!\nDo you want to see a list of these?", "Duplicate files", 1, 3, (Icon)null, options2, options2[0]);
                    if (n2 == 0) {
                        JOptionPane.showMessageDialog((Component)null, "Here are the duplicate files: (Press 'return' to continue)\n" + errorList);
                    }
                }
                File xFile = null;
                if (this.osName.equals("Mac OS X")) {
                    final String trashPath = this.findTrash(aFile.getPath());
                    xFile = new File(trashPath);
                }
                else {
                    xFile = new File(aFile.getPath() + "old" + this.getDateAndTime());
                }
                aFile.renameTo(xFile);
                if (!this.launchTmaList.isSelected()) {
                    continue;
                }
                this.openList();
            }
        }
        try {
            this.setupMenus(this.etmasFolder.getText(), this.courseList);
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem(), this.tmaList);
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem(), this.studentList);
        }
        catch (final Exception anException2) {
            System.out.println("Error:" + anException2);
        }
        File yFile = null;
        File zFile = null;
        if (!this.unzippedFilePath.equals("")) {
            zFile = new File(this.unzippedFilePath);
            if (!this.unzippedFilePath.contains("Imported")) {
                this.unzippedFilePath = this.unzippedFilePath.replace(".zip", "");
                yFile = new File(this.unzippedFilePath + "-Imported.zip");
                zFile.renameTo(yFile);
            }
        }
        this.courseDirectory = "";
        this.courseDirectoryList.clear();
        this.unzippedFilePath = "";
        this.autoImportFlag = false;
        this.pathToDownloadedFile = "";
    }
    
    public String fixDuplicate(final String aFilename) {
        String newFilename = aFilename;
        final int fileLength = aFilename.length();
        final String penultChar = aFilename.substring(fileLength - 2, fileLength - 1);
        if (penultChar.equals("-") || penultChar.equals(" ")) {
            newFilename = aFilename.substring(0, fileLength - 2);
        }
        return newFilename;
    }
    
    private void getAllFiles(final String dir2add) {
        final File thisDir = new File(dir2add);
        final String[] dirList = thisDir.list();
        for (int i = 0; i < dirList.length; ++i) {
            final File f = new File(thisDir, dirList[i]);
            if (f.isDirectory()) {
                final String filePath = f.getPath();
                this.tmaNewFiles.add(filePath);
                this.getAllFiles(filePath);
            }
            else {
                this.tmaNewFiles.add(f.getPath());
            }
        }
    }
    
    public String findTrash(final String aPath) {
        String aTrashPath = "";
        final String[] pathBits = aPath.split("/");
        final int pathLength = pathBits.length;
        if (pathBits[1].equals("Users")) {
            aTrashPath = "/" + pathBits[1] + "/" + pathBits[2] + "/.Trash/" + pathBits[pathLength - 1] + "_" + this.getDateAndTime();
        }
        else {
            aTrashPath = aPath + "old" + this.getDateAndTime();
        }
        return aTrashPath;
    }
    
    public String createCommentBank() {
        final String tmaNo = this.assgnmt_suffix.getText();
        if (tmaNo.equals("")) {
            JOptionPane.showMessageDialog(null, "You must open a student's PT3 for this module first!");
            return "";
        }
        final String tmaFolderPath = this.etmasFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + tmaNo;
        final String commentFileName = "commentBank" + this.course_code.getText() + "-" + this.pres_code.getText() + tmaNo + ".txt";
        final File aFile = new File(tmaFolderPath + "/" + commentFileName);
        try {
            aFile.createNewFile();
        }
        catch (final Exception ex) {}
        return tmaFolderPath + "/" + commentFileName;
    }
    
    private void tmaScoresMouseExited(final MouseEvent evt) {
    }
    
    private void tmaScoresMouseReleased(final MouseEvent evt) {
        if (this.statusChangeAgree()) {
            this.totalEntry();
            this.additionField.setText("");
            this.submission_status.setText("Unmarked");
            this.checkStatus();
            this.calculateTotals();
        }
    }
    
    private void loadXMLAltActionPerformed(final ActionEvent evt) {
        final String fhiName = this.courseList.getSelectedItem() + "-" + this.tmaList.getSelectedItem() + "-" + this.subNo.getSelectedItem() + "-" + this.studentList.getSelectedItem() + ".fhi";
        final String fhiPath = this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.studentList.getSelectedItem() + "/" + this.subNo.getSelectedItem() + "/" + fhiName;
        this.loadPT3(fhiPath);
    }
    
    public String loadFhiString(final String filePath, final boolean lineFeed) {
        String fhiContents = "";
        final File aFile = new File(filePath);
        BufferedReader buffer = null;
        String currentLine = null;
        try {
            for (buffer = new BufferedReader(new FileReader(aFile)), currentLine = buffer.readLine(); currentLine != null; currentLine += "\r\n", fhiContents += currentLine, currentLine = buffer.readLine()) {}
        }
        catch (final Exception ex) {}
        finally {
            try {
                buffer.close();
            }
            catch (final Exception ex2) {}
        }
        return fhiContents;
    }
    
    private void saveFhiString(final String filePath, final String fhiString) {
        final File aFile = new File(filePath);
        BufferedWriter buffer = null;
        final String currentLine = null;
        try {
            buffer = new BufferedWriter(new FileWriter(aFile));
            buffer.write(fhiString);
        }
        catch (final Exception anException) {
            JOptionPane.showMessageDialog(null, anException);
        }
        finally {
            try {
                buffer.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    private void disableScoresEditing(final JTable aTable) {
        for (int nRows = aTable.getRowCount(), i = 10; i < nRows; ++i) {
            aTable.isCellEditable(i, 2);
        }
    }
    
    public boolean isCellEditable(final int row, final int column) {
        return false;
    }
    
    public String loadVersion(final String aURL) {
        try {
            final URL url = new URL(aURL);
            final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            this.latestVersion = "";
            String str;
            while ((str = in.readLine()) != null) {
                this.latestVersion += str;
            }
            in.close();
        }
        catch (final MalformedURLException ex) {}
        catch (final IOException ex2) {}
        try {
            String[] latestVersionElements = this.latestVersion.split("<latestversion>");
            final String secondBit = latestVersionElements[1];
            latestVersionElements = secondBit.split("</latestversion>");
            this.latestVersion = latestVersionElements[0];
            latestVersionElements = this.latestVersion.split("</span>");
            this.latestVersion = latestVersionElements[1];
            latestVersionElements = this.latestVersion.split(">");
            this.latestVersion = latestVersionElements[1];
            System.out.println(this.latestVersion);
        }
        catch (final Exception e) {
            JOptionPane.showMessageDialog(null, "No update information available - please check on the website.");
            this.latestVersion = "0.0";
        }
        if (!this.latestVersion.equals(this.thisVersion) && !this.latestVersion.equals("0.0")) {
            final Object[] options = { "Yes", "No" };
            final JFrame frame = null;
            final int n = JOptionPane.showOptionDialog((Component)frame, "A new version of the filehandler (version " + this.latestVersion + ") is available at \nhttp://www.hayfamily.co.uk/etmahandlerpage.html\nYou have version " + this.thisVersion + ".\nWould you like to go to the filehandler web page now?", "", 1, 3, (Icon)null, options, options[0]);
            if (n == 0) {
                final String myURI = "http://s376541606.websitehome.co.uk/etmahandlerpage.html";
                try {
                    Desktop.getDesktop().browse(new URI(myURI));
                }
                catch (final Exception e2) {
                    JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
                }
            }
            if (n == 1) {}
        }
        else if (!this.latestVersion.equals("0.0")) {
            JOptionPane.showMessageDialog((Component)null, "You have the latest version of the filehandler (version " + this.thisVersion);
        }
        else {
            JOptionPane.showMessageDialog(null, "Update check hasn't worked.\nDo you have an internet connection?");
        }
        return this.latestVersion;
    }
    
    private File loadPT3(final String aPathname) {
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 70; ++j) {
                this.tmaScores.setValueAt(null, j, i);
            }
        }
        this.clearFields();
        this.attFlag = false;
        this.jTextArea1.setText("");
        File dataFile = new File(aPathname);
        this.fhiFileName.setText(aPathname);
        this.ampersandClean(dataFile);
        dataFile = this.tagClean(dataFile);
        final JTextField[] xmlTagsFileNames = { this.ou_computer_user_name, this.personal_id, this.title, this.initials, this.forenames, this.surname, this.email_address, this.address_line1, this.address_line2, this.address_line3, this.address_line4, this.address_line5, this.postcode };
        final JTextField[] xmlTagsFileNamesStaff = { this.staff_id, this.staff_title, this.staff_initials, this.staff_forenames, this.staff_surname, this.region_code };
        final JTextField[] xmlTagsFileNamesSubs = { this.course_code, this.course_version_num, this.pres_code, this.assgnmt_suffix, this.e_tma_submission_num, this.e_tma_submission_date, this.walton_received_date, this.marked_date, this.submission_status, this.late_submission_status, this.zip_date, this.zip_file, this.score_update_allowed, this.overall_grade_score, this.tutor_comments, this.max_assgnmt_score, this.total_question_count, this.permitted_question_count };
        try {
            this.getDetails("student_details", this.studentDetails, dataFile);
            this.putIntoFields(xmlTagsFileNames, this.xmlTagsStringsReduced, this.studentDetails);
            this.getDetails("tutor_details", this.tutorDetails, dataFile);
            this.putIntoFields(xmlTagsFileNamesStaff, this.xmlTagsStringsReducedStaff, this.tutorDetails);
            this.getDetails("submission_details", this.submissionDetails, dataFile);
            this.putIntoFields(xmlTagsFileNamesSubs, this.xmlTagsStringsReducedSubs, this.submissionDetails);
            this.getMarks3(dataFile);
        }
        catch (final Exception anException) {
            JOptionPane.showMessageDialog((Component)null, "There seems to be a problem reading the PT3 file. " + anException);
        }
        this.tutor_comments_input.setWrapStyleWord(true);
        this.tutor_comments_input.setText(this.submissionDetails.get("tutor_comments"));
        this.disableScoresEditing(this.tmaScores);
        this.savePt3.setEnabled(true);
        this.zipFiles.setEnabled(true);
        this.openTma.setEnabled(true);
        this.openTmaFolder.setEnabled(true);
        this.checkTotals.setEnabled(true);
        this.openReturnsFolder.setEnabled(true);
        try {
            this.getPreviousScores(this.course_code.getText() + "-" + this.pres_code.getText(), this.ou_computer_user_name.getText(), true, 0);
        }
        catch (final Exception anException) {
            System.out.println("Error1 " + anException);
        }
        this.calculateTotals();
        dataFile = this.tagRestore(dataFile);
        if (this.spellCheckFlag.isSelected()) {
            this.highlightAllErrors();
        }
        return dataFile;
    }
    
    public void getThisWeighting(final JTable aTable) {
        for (int i = 1; i < 15; ++i) {
            final String tempStr = (String)aTable.getValueAt(i, 0);
            try {
                this.weightingsList.add(Integer.parseInt(String.valueOf(tempStr)));
                this.weightingsMap.put((String)this.tmaNumbers.getValueAt(i, 0), Integer.parseInt(String.valueOf(tempStr)));
            }
            catch (final Exception anException) {
                this.weightingsList.add(0);
                this.weightingsMap.put((String)this.tmaNumbers.getValueAt(i, 0), 0);
            }
        }
    }
    
    public void getThisMaxScore(final JTable aTable) {
        for (int i = 1; i < 15; ++i) {
            final String tempStr = (String)aTable.getValueAt(i, 0);
            try {
                this.maxScoresList.add(Integer.parseInt(String.valueOf(tempStr)));
                this.maxScoresMap.put((String)this.tmaNumbers.getValueAt(i, 0), Integer.parseInt(String.valueOf(tempStr)));
            }
            catch (final Exception anException) {
                this.maxScoresList.add(100);
                this.maxScoresMap.put((String)this.tmaNumbers.getValueAt(i, 0), 0);
            }
        }
    }
    
    private Map<String, Integer> getWeightings(final String aCourse) {
        this.weightingsList.clear();
        this.weightingsMap.clear();
        if (this.weightingsTable1.getValueAt(0, 0).equals(aCourse)) {
            this.getThisWeighting(this.weightingsTable1);
        }
        else if (this.weightingsTable2.getValueAt(0, 0).equals(aCourse)) {
            this.getThisWeighting(this.weightingsTable2);
        }
        else if (this.weightingsTable3.getValueAt(0, 0).equals(aCourse)) {
            this.getThisWeighting(this.weightingsTable3);
        }
        else if (this.weightingsTable4.getValueAt(0, 0).equals(aCourse)) {
            this.getThisWeighting(this.weightingsTable4);
        }
        else {
            for (int i = 1; i < 15; ++i) {
                this.weightingsList.add(1);
                this.weightingsMap.put((String)this.tmaNumbers.getValueAt(i, 0), 1);
            }
        }
        return this.weightingsMap;
    }
    
    private Map<String, Integer> getMaxScores(final String aCourse) {
        this.maxScoresList.clear();
        this.maxScoresMap.clear();
        if (this.maxScoreTable1.getValueAt(0, 0).equals(aCourse)) {
            this.getThisMaxScore(this.maxScoreTable1);
        }
        else if (this.maxScoreTable2.getValueAt(0, 0).equals(aCourse)) {
            this.getThisMaxScore(this.maxScoreTable2);
        }
        else if (this.maxScoreTable3.getValueAt(0, 0).equals(aCourse)) {
            this.getThisMaxScore(this.maxScoreTable3);
        }
        else if (this.maxScoreTable4.getValueAt(0, 0).equals(aCourse)) {
            this.getThisMaxScore(this.maxScoreTable4);
        }
        else {
            for (int i = 1; i < 15; ++i) {
                this.maxScoresList.add(100);
                this.maxScoresMap.put((String)this.tmaNumbers.getValueAt(i, 0), 1);
            }
        }
        return this.maxScoresMap;
    }
    
    public void sortAllRowsBy(final DefaultTableModel model, final int colIndex, final boolean ascending) {
        final Vector data = model.getDataVector();
        Collections.sort((List<Object>)data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
    }
    
    private void getPreviousScores(final String courseCode, final String oucu, final boolean longFlag, final int studNo) {
        final Map<String, Integer> theseWeightings = this.getWeightings(courseCode);
        final Map<String, Integer> theseMaxScores = this.getMaxScores(courseCode);
        this.studentMarksList.clear();
        List<File> studentFolders = new ArrayList<File>();
        int totalMarks = 0;
        int totalSubmitted = 0;
        int average = 0;
        double average2 = 0.0;
        int averageM = 0;
        double average1M = 0.0;
        final NumberFormat decForm = new DecimalFormat("##0.00");
        this.passMark = 0;
        try {
            this.passMark = theseWeightings.get("Pass Mark");
        }
        catch (final Exception ex) {}
        this.prevMarks.setValueAt("Ave", 0, 12);
        int cellCount = 0;
        final String courseFolderPath = this.etmasFolder.getText() + "/" + courseCode;
        final File courseFolder = new File(courseFolderPath);
        studentFolders = Arrays.asList(courseFolder.listFiles());
        Collections.sort(studentFolders);
        String thisMark = "";
        int fileCount = 0;
        String latestTma = "";
        for (fileCount = 0; fileCount < studentFolders.size(); ++fileCount) {
            final File aFile = studentFolders.get(fileCount);
            if (aFile.isDirectory()) {
                final String aFileName = aFile.getName();
                if (aFileName.compareTo(latestTma) > 0) {
                    latestTma = aFileName;
                }
            }
        }
        for (fileCount = 0; fileCount < studentFolders.size(); ++fileCount) {
            thisMark = "";
            final File aFile = studentFolders.get(fileCount);
            final String aFileName = aFile.getName();
            if (aFile.isDirectory() && aFileName.length() < 3) {
                final int aWeighting = theseWeightings.get("TMA" + aFileName);
                int aMaxScore = theseMaxScores.get("TMA" + aFileName);
                if (aMaxScore == 0 || aMaxScore == 1) {
                    aMaxScore = 100;
                }
                this.prevMarks.setValueAt(aFile.getName(), 0, cellCount);
                try {
                    thisMark = this.getStudentsMarks(oucu, courseCode, aFile);
                }
                catch (final Exception ex2) {}
                Label_0421: {
                    if (aFileName.equals(latestTma)) {
                        if (this.ignoreCurrentTma.isSelected()) {
                            break Label_0421;
                        }
                    }
                    try {
                        totalMarks += Integer.parseInt(thisMark) * aWeighting * 100 / aMaxScore;
                    }
                    catch (final Exception ex3) {}
                    totalSubmitted += aWeighting;
                }
                if (longFlag) {
                    this.prevMarks.setValueAt(thisMark, 1, cellCount);
                }
                this.studentMarksList.add(thisMark);
                ++cellCount;
            }
        }
        try {
            average = totalMarks / totalSubmitted;
            average2 = totalMarks / (double)totalSubmitted;
            average2 = Math.round(average2 * 10.0) / 10.0;
        }
        catch (final Exception anException) {
            average = 0;
            average2 = 0.0;
        }
        try {
            averageM = totalMarks / 100;
            average1M = totalMarks / 100.0;
            average1M = Math.round(average1M * 10.0) / 10.0;
        }
        catch (final Exception anException) {
            averageM = 0;
            average1M = 0.0;
        }
        String averageString = "";
        if (longFlag) {
            this.prevMarks.setValueAt(average, 1, 12);
        }
        if (!longFlag) {
            if (average2 < this.passMark) {
                averageString = String.valueOf(average2) + "**";
                this.redRows.add(studNo);
                this.colorSetter(this.gradesSummaryTable, this.redRows, this.blueRows, Color.PINK, Color.YELLOW, true);
            }
            else {
                averageString = String.valueOf(average2);
            }
            this.gradesSummaryTable.setValueAt(average2, studNo, 16);
            this.gradesSummaryTable.setValueAt(average1M, studNo, 17);
        }
    }
    
    private void avePreviousScores(final String courseCode) {
        final Map<String, Integer> theseWeightings = this.getWeightings(courseCode);
        final Map<String, Integer> theseMaxScores = this.getMaxScores(courseCode);
        int totalMarks = 0;
        int totalSubmitted = 0;
        int average = 0;
        double average2 = 0.0;
        int averageM = 0;
        double average1M = 0.0;
        this.prevMarks.setValueAt("Ave", 0, 12);
        this.prevMarks.setValueAt("Mod %", 0, 13);
        final int cellCount = 0;
        String thisMark = "";
        int aWeighting = 0;
        int aMaxScore = 100;
        for (int i = 0; i < 16; ++i) {
            thisMark = "";
            try {
                aWeighting = theseWeightings.get("TMA" + this.prevMarks.getValueAt(0, i));
                aMaxScore = theseMaxScores.get("TMA" + this.prevMarks.getValueAt(0, i));
                if (aMaxScore == 0 || aMaxScore == 1) {
                    aMaxScore = 100;
                }
            }
            catch (final Exception ex) {}
            try {
                thisMark = (String)this.prevMarks.getValueAt(1, i);
            }
            catch (final Exception ex2) {}
            try {
                totalMarks += Integer.parseInt(thisMark) * aWeighting * 100 / aMaxScore;
            }
            catch (final Exception ex3) {}
            totalSubmitted += aWeighting;
            aWeighting = 0;
            aMaxScore = 100;
        }
        try {
            average = totalMarks / totalSubmitted;
            average2 = totalMarks / (double)totalSubmitted;
            average2 = Math.round(average2 * 10.0) / 10.0;
        }
        catch (final Exception anException) {
            average = 0;
            average2 = 0.0;
        }
        if (average < this.passMark) {
            final String averageS = Integer.toString(average);
            this.prevMarks.setValueAt(averageS, 1, 12);
        }
        else {
            this.prevMarks.setValueAt(average, 1, 12);
        }
        try {
            averageM = totalMarks / 100;
            average1M = totalMarks / 100.0;
            average1M = Math.round(average2 * 10.0) / 10.0;
        }
        catch (final Exception anException) {
            averageM = 0;
            average1M = 0.0;
        }
        this.prevMarks.setValueAt(averageM, 1, 13);
    }
    
    private void getNewRedRows(final int nStudents) {
        this.redRows.clear();
        this.blueRows.clear();
        for (int i = 0; i < nStudents; ++i) {
            final double studAve = (double)this.gradesSummaryTable.getValueAt(i, 16);
            if (studAve < this.passMark) {
                this.redRows.add(i);
            }
            this.colorSetter(this.gradesSummaryTable, this.redRows, this.blueRows, Color.PINK, Color.YELLOW, true);
        }
    }
    
    private void getNewTmaRedRows(final int nStudents) {
        this.redRows.clear();
        this.blueRows.clear();
        for (int i = 0; i < nStudents; ++i) {
            final String testString = (String)this.listOfTmas.getValueAt(i, 5);
            if (testString.equals("Unmarked")) {
                this.redRows.add(i);
            }
            if (testString.equals("Marked")) {
                this.blueRows.add(i);
            }
            boolean inBold = false;
            Color aColor = new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2]));
            Color bColor = new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2]));
            if (this.highlightUnmarked.isSelected()) {
                aColor = Color.PINK;
                bColor = Color.YELLOW;
                inBold = true;
            }
            this.colorSetter(this.listOfTmas, this.redRows, this.blueRows, aColor, bColor, inBold);
        }
    }
    
    private String getStudentsMarks(final String oucu, final String courseCode, final File bFile) {
        List<File> subFolderList = new ArrayList<File>();
        String thisMark = "";
        String subNo1 = "1";
        final String filePath = bFile.getPath();
        final String thisFile = filePath + "/" + oucu;
        final File subFolders = new File(thisFile);
        subFolderList = Arrays.asList(subFolders.listFiles());
        for (final File aFile : subFolderList) {
            if (aFile.isDirectory()) {
                subNo1 = aFile.getName();
            }
        }
        final String nextFile = thisFile + subNo1 + "/" + courseCode + "-" + bFile.getName() + "-" + subNo1 + "-" + oucu + ".fhi";
        thisMark = this.getIndividualMark(nextFile);
        return thisMark;
    }
    
    private String getIndividualMark(final String fhiName) {
        this.thisLine.clear();
        final File dataFile = new File(fhiName);
        this.getShortDetails("student_details", this.studentDetailsShort, dataFile);
        this.getShortDetails("tutor_details", this.tutorDetailsShort, dataFile);
        this.getShortDetails("submission_details", this.submissionDetailsShort, dataFile);
        this.overall_grade_score.setText("");
        this.ampersandCleanAlt(dataFile);
        return this.submissionDetailsShort.get("overall_grade_score");
    }
    
    private File getFile() {
        final JFileChooser _fileChooser = new JFileChooser();
        final JMenuItem openItem = new JMenuItem("Open...");
        final int path = _fileChooser.showOpenDialog(null);
        final File aFile = _fileChooser.getSelectedFile();
        this.fhiFileName.setText(aFile.getPath());
        return aFile;
    }
    
    private void putFile1(final String pathname, final String fixedString) {
        if (this.osName.equals("Mac OS X")) {}
        final File aFile = new File(pathname);
        OutputStreamWriter buffer = null;
        try {
            buffer = new OutputStreamWriter(new FileOutputStream(aFile), "ISO8859_1");
            buffer.flush();
            buffer.write(fixedString);
        }
        catch (final Exception anException) {
            System.out.println("Error: " + anException);
            try {
                buffer.close();
            }
            catch (final Exception closeEx1) {
                System.out.println("Error: " + closeEx1);
            }
        }
        finally {
            try {
                buffer.close();
            }
            catch (final Exception anException2) {
                System.out.println("Error: " + anException2);
            }
        }
    }

    private void putFile(final String pathname, String fixedString) {
        if (this.osName.equals("Mac OS X")) {
            fixedString = this.fixAccents(this.outString);
        }
        final File aFile = new File(pathname);
        OutputStreamWriter buffer = null;
        try {
            buffer = new OutputStreamWriter(new FileOutputStream(aFile), "ISO8859_1");
            buffer.flush();
            buffer.write(fixedString);
        }
        catch (final Exception anException) {
            System.out.println("Error: " + anException);
            try {
                buffer.close();
            }
            catch (final Exception closeEx2) {
                System.out.println("Error: " + closeEx2);
            }
        }
        finally {
            try {
                buffer.close();
            }
            catch (final Exception anException2) {
                System.out.println("Error: " + anException2);
            }
        }
    }
    
    private void clearFields() {
        for (final String aTag : this.fieldNames1.keySet()) {
            this.studentDetails.clear();
            this.tutorDetails.clear();
            this.submissionDetails.clear();
            this.fieldNames1.get(aTag).setText("");
            this.tutor_comments_input.setText("");
        }
        int nRow = this.prevMarks.getRowCount();
        int nCol = this.prevMarks.getColumnCount();
        for (int i = 0; i < nRow; ++i) {
            for (int j = 0; j < nCol; ++j) {
                this.prevMarks.setValueAt("", i, j);
            }
        }
        nRow = this.tmaScores.getRowCount();
        nCol = this.tmaScores.getColumnCount();
        for (int i = 0; i < nRow; ++i) {
            for (int j = 0; j < nCol; ++j) {
                this.tmaScores.setValueAt("", i, j);
            }
        }
    }
    
    private void makeMap() {
        final int lString1 = this.xmlTagsStringsReduced.length;
        final int lStringStaff1 = this.xmlTagsStringsReducedStaff.length;
        final int lStringSubs = this.xmlTagsStringsReducedSubs.length;
        final JTextField[] xmlTagsFileNames = { this.ou_computer_user_name, this.personal_id, this.title, this.initials, this.forenames, this.surname, this.email_address, this.address_line1, this.address_line2, this.address_line3, this.address_line4, this.address_line5, this.postcode };
        final JTextField[] xmlTagsFileNamesStaff = { this.staff_id, this.staff_title, this.staff_initials, this.staff_forenames, this.staff_surname, this.region_code };
        final JTextField[] xmlTagsFileNamesSubs = { this.course_code, this.course_version_num, this.pres_code, this.assgnmt_suffix, this.e_tma_submission_num, this.e_tma_submission_date, this.walton_received_date, this.marked_date, this.submission_status, this.late_submission_status, this.zip_date, this.zip_file, this.score_update_allowed, this.overall_grade_score, this.tutor_comments, this.max_assgnmt_score, this.total_question_count, this.permitted_question_count };
        for (int i = 0; i < lString1; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsReduced[i], xmlTagsFileNames[i]);
        }
        for (int i = 0; i < lStringStaff1; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsReducedStaff[i], xmlTagsFileNamesStaff[i]);
        }
        for (int i = 0; i < lStringSubs; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsReducedSubs[i], xmlTagsFileNamesSubs[i]);
        }
    }
    
    private void putIntoFields(final JTextField[] aField, final String[] aString, final Map<String, String> aMap) {
        for (int lField = aField.length, i = 0; i < lField; ++i) {
            (aField[i] = this.fieldNames1.get(aString[i])).setText(aMap.get(aString[i]));
        }
    }
    
    private void putMarks() {
        this.markString = "<question_details>";
        for (int nQuest = this.getQuestionNumbers().size(), questNo = 0; questNo < nQuest; ++questNo) {
            this.markString = this.markString + "<question question_number=\"" + (String)this.getQuestionNumbers().get(questNo) + "\">";
            this.constructQuestionDetails(questNo);
        }
        this.markString += "</question_details>";
        this.outString += this.markString;
    }
    
    private void constructQuestionDetails(final int aQuestion) {
        final String maxQuestion = (String)this.tmaScores.getValueAt(this.partStarts.get(aQuestion), 5);
        final String studQuestion = (String)this.tmaScores.getValueAt(this.partStarts.get(aQuestion), 4);
        this.markString = this.markString + "<maximum_question_score>" + maxQuestion + "</maximum_question_score>";
        this.markString = this.markString + "<student_question_score>" + studQuestion + "</student_question_score>";
        int nParts = 0;
        for (int i = 0; i < this.numberOfQuestions; ++i) {
            nParts = this.partStarts.get(i + 1) - this.partStarts.get(i);
            if (nParts == 1) {
                nParts = 0;
            }
            this.numberOfParts.add(nParts);
        }
        this.markString = this.markString + "<question_parts_count>" + this.numberOfParts.get(aQuestion) + "</question_parts_count>";
        if (this.numberOfParts.get(aQuestion) > 0) {
            this.constructQuestionPartDetails(aQuestion);
        }
        this.markString += "</question>";
    }
    
    private void constructQuestionPartDetails(final int aQuestion) {
        final StringBuilder thisPartStringSb = new StringBuilder(10000);
        String thisPartString = "";
        int partPosition = this.partStarts.get(aQuestion);
        for (int partNo = 0; partNo < this.numberOfParts.get(aQuestion); ++partNo) {
            thisPartStringSb.append("<question_part part_id=\"" + String.valueOf(partNo + 1) + "\">");
            thisPartStringSb.append("<maximum_question_part_score>" + this.tmaScores.getValueAt(partPosition, 3) + "</maximum_question_part_score>");
            thisPartStringSb.append("<student_question_part_score>" + this.tmaScores.getValueAt(partPosition, 2) + "</student_question_part_score>");
            thisPartStringSb.append("<questn_part_desc>" + this.tmaScores.getValueAt(partPosition, 1) + "</questn_part_desc>");
            thisPartStringSb.append("</question_part>");
            ++partPosition;
        }
        thisPartString = thisPartStringSb.toString();
        this.markString += thisPartString;
    }
    
    public String fixAccents(String commentsString) {
        final char cedilla = ']';
        final String stringCedilla = "" + cedilla;
        final String[] macAccents = { "a" };
        final String[] winAccents = { "a" };
        for (int i = 0; i < macAccents.length; ++i) {
            final String commentsStringOld = commentsString;
            commentsString = commentsString.replaceAll(macAccents[i], winAccents[i]);
            if (!commentsStringOld.equals(commentsString)) {}
        }
        return commentsString;
    }
    
    public String unFixAccents(String commentsString) {
        final char[] macAccents = { '\u00e9' };
        final char[] winAccents = { '\u00e9' };
        for (int i = 0; i < macAccents.length; ++i) {
            final char oldChar = macAccents[i];
            final char newChar = winAccents[i];
            commentsString = commentsString.replace(newChar, oldChar);
        }
        return commentsString;
    }
    
    public String replaceFunnyCharacters(final String commentsString) {
        String newString = commentsString;
        final String[] funnyCharactersMac = { "\u2022", "\u00df", "\u03c0", "\u2202", "µ", "©", "\u02da", "\u221a", "<", ">", "\u201c", "\u201d", "\u2018", "\u2019", "\u2013" };
        final String[] boringCharactersMac = { "*", "ss", "pi ", "delta ", "mu ", "Copyright", "degrees", "root", this.string02, this.string03, "\"", "\"", "'", "'", "-" };
        final String[] funnyCharactersWin = { "<", ">", "\u201c", "\u201d", "\u2018", "\u2019", "\u2013" };
        final String[] boringCharactersWin = { this.string02, this.string03, "\"", "\"", "'", "'", "-" };
        final String[] funnyCharactersLin = { "<", ">", "\u201c", "\u201d", "\u2018", "\u2019", "\u2013" };
        final String[] boringCharactersLin = { this.string02, this.string03, "\"", "\"", "'", "'", "-" };
        if (this.osName.equals("Mac OS X")) {
            for (int i = 0; i < funnyCharactersMac.length; ++i) {
                newString = newString.replaceAll(funnyCharactersMac[i], boringCharactersMac[i]);
            }
        }
        if (this.osName.contains("Windows")) {
            for (int i = 0; i < funnyCharactersWin.length; ++i) {
                newString = newString.replaceAll(funnyCharactersWin[i], boringCharactersWin[i]);
            }
            if (this.osName.contains("Linux")) {
                for (int i = 0; i < funnyCharactersLin.length; ++i) {
                    newString = newString.replaceAll(funnyCharactersLin[i], boringCharactersLin[i]);
                }
            }
        }
        return newString;
    }
    
    private void putDetails(final Map aMap, final String[] aString, final String bString) {
        final StringBuilder outStringSb = new StringBuilder(1000);
        outStringSb.append(this.outString);
        outStringSb.append(this.addStartTag(bString));
        final int lMap = aMap.size();
        for (int lString1 = aString.length, i = 0; i < lString1; ++i) {
            final String thisTag = aString[i];
            if (thisTag.equals("tutor_comments")) {
                this.submissionDetails.put("tutor_comments", this.tutor_comments_input.getText());
                final String commentsText = this.tutor_comments_input.getText();
                final String newText = this.replaceFunnyCharacters(commentsText);
                this.submissionDetails.put("tutor_comments", newText);
                this.tutor_comments_input.setText(newText);
            }
            else {
                aMap.put(thisTag, this.fieldNames1.get(thisTag).getText());
            }
            final Object thisEntry = aMap.get(thisTag);
            final String thisEntryString = (String)thisEntry;
            final String thisString = this.addStartTag(thisTag) + thisEntry + this.addEndTag(thisTag);
            outStringSb.append(thisString);
        }
        outStringSb.append(this.addEndTag(bString));
        this.outString = outStringSb.toString();
    }
    
    private void getDetails(final String aString, final Map aMap, File aFile) {
        try {
            this.ampersandClean(aFile);
            aFile = this.tagClean(aFile);
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = null;
            doc = docBuilder.parse(aFile);
            doc.getDocumentElement().normalize();
            final NodeList listOfPersons = doc.getElementsByTagName(aString);
            final Node firstPersonNode = listOfPersons.item(0);
            final NodeList listOfStudentDetails = listOfPersons.item(0).getChildNodes();
            final int numberOfStudentItems = listOfStudentDetails.getLength();
            if (firstPersonNode.getNodeType() == 1) {
                for (int i = 0; i < numberOfStudentItems; ++i) {
                    try {
                        final Element studentElement = (Element)listOfStudentDetails.item(i);
                        final String thisDetail = studentElement.getNodeName();
                        try {
                            final NodeList detailList = listOfStudentDetails.item(i).getChildNodes();
                            aMap.put(thisDetail, detailList.item(0).getNodeValue());
                        }
                        catch (final Exception e) {
                            aMap.put(thisDetail, "");
                        }
                        aFile = this.tagClean(aFile);
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        catch (final Exception ex2) {}
    }
    
    public void openPt3() {
        boolean loadFlag = true;
        this.saveLocation();
        this.studentIndex = this.listOfTmas.getSelectedRow();
        String oucu = (String)this.listOfTmas.getValueAt(this.studentIndex, 3);
        if (oucu.equals("")) {
            this.listOfTmas.setValueAt(false, this.studentIndex, 11);
        }
        else {
            try {
                if (this.listOfTmas.getSelectedColumn() != 11) {
                    if (!this.savedFlag) {
                        final Object[] options = { "Save PT3", "Don't save", "Cancel" };
                        final JFrame frame = null;
                        final int n = JOptionPane.showOptionDialog(frame, "Do you want to save the current TMA first?", "Current TMA may not be saved!", 1, 3, null, options, options[0]);
                        if (n == 0) {
                            this.saveDetails();
                            loadFlag = true;
                        }
                        if (n == 1) {
                            this.savedFlag = true;
                            loadFlag = true;
                        }
                        if (n == 2) {
                            this.savedFlag = false;
                            loadFlag = false;
                        }
                    }
                    if (loadFlag) {
                        this.savedFlag = false;
                        this.studentIndex = this.listOfTmas.getSelectedRow();
                        oucu = (String)this.listOfTmas.getValueAt(this.studentIndex, 3);
                        final String courseCode = (String)this.listOfTmas.getValueAt(this.studentIndex, 7);
                        final String presCode = (String)this.listOfTmas.getValueAt(this.studentIndex, 8);
                        final String subNoString = (String)this.listOfTmas.getValueAt(this.studentIndex, 6);
                        final String assNo = (String)this.listOfTmas.getValueAt(this.studentIndex, 9);
                        this.overallGrade = (String)this.listOfTmas.getValueAt(this.studentIndex, 4);
                        final String fhiName = courseCode + "-" + presCode + "-" + assNo + "-" + subNoString + "-" + oucu + ".fhi";
                        final String fhiPath = this.etmasFolder.getText() + "/" + courseCode + "-" + presCode + "/" + assNo + "/" + oucu + "/" + subNoString + "/" + fhiName;
                        this.submittedTmas.setVisible(false);
                        this.loadPT3(fhiPath);
                        this.setupPreviousPt3s();
                        this.lateSubmission.setSelected(this.late_submission_status.getText().equals("Y"));
                    }
                    this.submittedTmas.setVisible(false);
                }
            }
            catch (final Exception anException) {
                this.savedFlag = true;
            }
        }
        this.savedFlag = true;
    }
    
    private void listOfTmasMouseReleased(final MouseEvent evt) {
        final int numClick = evt.getClickCount();
        int clicksNeeded = 0;
        if (this.doubleClickFlag.isSelected()) {
            clicksNeeded = 1;
        }
        if (numClick > clicksNeeded) {
            this.openPt3();
        }
        else {
            final int[] selectedRows = this.listOfTmas.getSelectedRows();
            for (int j = 0; j < this.listOfTmas.getRowCount(); ++j) {
                if (this.listOfTmas.getValueAt(j, 0).equals("")) {
                    this.listOfTmas.setValueAt(false, j, 11);
                }
            }
        }
    }
    
    private void courseListItemStateChanged(final ItemEvent evt) {
        try {
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem(), this.studentList);
        }
        catch (final Exception ex) {}
    }
    
    private void tmaListItemStateChanged(final ItemEvent evt) {
        try {
            this.tmaMenuPreferences = (String)this.tmaList.getSelectedItem();
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem(), this.studentList);
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.studentList.getSelectedItem(), this.subNo);
            this.ourRoot.put("tmaMenuPreferences", this.tmaMenuPreferences);
        }
        catch (final Exception anException) {
            this.ourRoot.put("tmaMenuPreferences", this.tmaMenuPreferences);
        }
    }
    
    private void studentListActionPerformed(final ActionEvent evt) {
        try {
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.studentList.getSelectedItem(), this.subNo);
        }
        catch (final Exception ex) {}
    }
    
    private void openTmaListActionPerformed(final ActionEvent evt) {
        this.openList();
    }
    
    public String fixDate(final String oldDate) {
        String newDate = "";
        final String[] dateElements = oldDate.split("-");
        final String month1 = dateElements[1];
        final String dayOfMonth = dateElements[0];
        String monthNumber = "";
        final String restOfIt = dateElements[2];
        for (int i = 0; i < 12; ++i) {
            if (month1.equals(this.MONTHLIST[i])) {
                monthNumber = String.valueOf(i + 1);
            }
        }
        if (monthNumber.length() == 1) {
            monthNumber = "0" + monthNumber;
        }
        final String year1 = restOfIt.substring(0, 4);
        final String time1 = restOfIt.substring(5, 13);
        newDate = year1 + "-" + monthNumber + "-" + dayOfMonth + "  " + time1;
        return newDate;
    }
    
    private void openList() {
        this.toBeMarked = 0;
        this.tmaListError = false;
        this.setSizesOfTmaList();
        this.submittedTmas.setTitle("Current TMAs for module " + this.courseList.getSelectedItem());
        this.listOfTmas.clearSelection();
        final int nRow = this.listOfTmas.getRowCount();
        final int nColumn = this.listOfTmas.getColumnCount();
        for (int r = 0; r < nRow; ++r) {
            for (int c = 0; c < nColumn - 1; ++c) {
                this.listOfTmas.setValueAt("", r, c);
            }
            this.listOfTmas.setValueAt(false, r, 11);
        }
        final String labelText = " on any row to open that PT3; click on any heading to sort by that column.";
        String clickCommand = "Click";
        if (this.doubleClickFlag.isSelected()) {
            clickCommand = "Double-click";
        }
        this.jLabel34.setText(clickCommand + labelText);
        try {
            this.makeTmaTable(1);
            if (!this.startUp) {
                this.submittedTmas.setVisible(true);
            }
            this.submittedTmas.setSize(830, 620);
            this.submittedTmas.setLocation((int)this.tmaListLocX, (int)this.tmaListLocY);
            if (this.late_submission_status.getText().equals("Y")) {
                this.lateSubmission.setSelected(true);
            }
            else {
                this.lateSubmission.setSelected(false);
            }
        }
        catch (final Exception anException) {
            JOptionPane.showMessageDialog(null, "Please select a valid TMA number for this Course, or,\nIf you haven't yet downloaded any eTMAs, click 'eTMA Site'.\nWhen you've downloaded the TMAs, click 'Import TMAs',\nwhich will move them to the correct place in the 'etmas' folder. ");
            this.tmaListError = true;
        }
        try {
            this.calculateTotals();
        }
        catch (final Exception ex) {}
        final TableColumnModel tcm = this.listOfTmas.getColumnModel();
        final DefaultTableModel model = (DefaultTableModel)this.listOfTmas.getModel();
        this.sortAllRowsBy(model, this.sortRow, this.sortPreference);
        try {
            this.setSizesOfTmaList();
        }
        catch (final Exception ex2) {}
        this.getNewTmaRedRows(this.listOfTmas.getRowCount());
        this.listOfTmas.setVisible(false);
        this.listOfTmas.setVisible(true);
    }
    
    public List<String> getListOfSubmissions(final String aPathName) {
        this.submissionList.clear();
        final boolean showLatest = this.showLatestFlag.isSelected();
        final File aFile = new File(aPathName);
        final String[] fileList = aFile.list();
        Arrays.sort(fileList);
        final List<String> holdingList = new ArrayList<String>();
        for (int i = 0; i < fileList.length; ++i) {
            final String tempPath = aPathName + "/" + fileList[i];
            final File tempFile = new File(tempPath);
            if (tempFile.isDirectory()) {
                holdingList.add(fileList[i]);
            }
        }
        int startList = 0;
        if (showLatest) {
            startList = holdingList.size() - 1;
        }
        for (int j = startList; j < holdingList.size(); ++j) {
            this.submissionList.add(holdingList.get(j));
        }
        return this.submissionList;
    }
    
    private void makeTmaTable(final int version) {
        final int numberOfStudents = this.studentList.getItemCount();
        int toBeMarked = 0;
        int studentCrashNumber = 0;
        String studentCrashName = "";
        int adjustedStudentNo = 0;
        this.redRows.clear();
        this.blueRows.clear();
        try {
            for (int studentNo = 0; studentNo < numberOfStudents; ++studentNo) {
                studentCrashNumber = studentNo;
                this.getListOfSubmissions(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.studentList.getItemAt(studentNo));
                final int numberOfSubmissions = this.submissionList.size();
                List<String> studentEntry = new ArrayList<String>();
                for (int sNo = 0; sNo < numberOfSubmissions; ++sNo) {
                    studentEntry = this.getTableEntry(studentNo, Integer.parseInt(this.submissionList.get(sNo)));
                    studentCrashName = (String)studentEntry.get(3) + " " + (String)studentEntry.get(1) + " " + (String)studentEntry.get(2);
                    if (this.filter.equals("All") || this.displayFlag) {
                        if (!this.entryErrorFlag) {
                            for (int entryNo = 0; entryNo < studentEntry.size(); ++entryNo) {
                                this.listOfTmas.setValueAt(studentEntry.get(entryNo), adjustedStudentNo, entryNo);
                            }
                            if (this.listOfTmas.getValueAt(adjustedStudentNo, 5).equals("Unmarked")) {
                                final String latestSubmission = this.submissionList.get(numberOfSubmissions - 1);
                                if (latestSubmission.equals(this.submissionList.get(sNo))) {
                                    ++toBeMarked;
                                }
                                this.redRows.add(adjustedStudentNo);
                                this.blueRows.add(adjustedStudentNo);
                            }
                            this.listOfTmas.setValueAt(false, adjustedStudentNo, 11);
                        }
                        else {
                            this.listOfTmas.setValueAt("Error!", adjustedStudentNo, 0);
                            this.listOfTmas.setValueAt("Student Id:", adjustedStudentNo, 1);
                            this.listOfTmas.setValueAt(this.studentList.getItemAt(adjustedStudentNo), adjustedStudentNo, 2);
                            this.listOfTmas.setValueAt("check file:", adjustedStudentNo, 3);
                            this.listOfTmas.setValueAt("", adjustedStudentNo, 4);
                        }
                        ++adjustedStudentNo;
                        this.entryErrorFlag = false;
                    }
                }
            }
            this.totalTmas.setText(String.valueOf(this.studentList.getItemCount()));
            this.toBeMarkedTmas.setText(String.valueOf(toBeMarked));
            boolean inBold = false;
            Color aColor = new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2]));
            Color bColor = new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2]));
            if (this.highlightUnmarked.isSelected()) {
                aColor = Color.PINK;
                bColor = Color.YELLOW;
                inBold = true;
            }
            this.colorSetter(this.listOfTmas, this.redRows, this.blueRows, aColor, bColor, inBold);
        }
        catch (final NumberFormatException anException) {
            System.out.println("Error1:" + anException);
            JOptionPane.showMessageDialog((Component)null, "Non numerical folder name found in submission number folder: \n" + (studentCrashNumber + 1) + " " + studentCrashName + " " + anException.getMessage());
        }
    }
    
    private List<String> getTableEntry(final int aStudent, final int submissionNo) {
        this.thisLine.clear();
        String fhiName = this.courseList.getSelectedItem() + "-" + this.tmaList.getSelectedItem() + "-" + submissionNo + "-" + this.studentList.getItemAt(aStudent) + ".fhi";
        fhiName = this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.studentList.getItemAt(aStudent) + "/" + submissionNo + "/" + fhiName;
        File dataFile = new File(fhiName);
        dataFile = this.tagClean(dataFile);
        this.readErrorFlag = true;
        this.getShortDetails("student_details", this.studentDetailsShort, dataFile);
        this.getShortDetails("tutor_details", this.tutorDetailsShort, dataFile);
        this.getShortDetails("submission_details", this.submissionDetailsShort, dataFile);
        this.overall_grade_score.setText("");
        this.thisLine.add(this.studentDetailsShort.get("personal_id"));
        this.thisLine.add(this.studentDetailsShort.get("forenames"));
        this.thisLine.add(this.studentDetailsShort.get("surname"));
        this.thisLine.add(this.studentDetailsShort.get("ou_computer_user_name"));
        this.thisLine.add(this.submissionDetailsShort.get("overall_grade_score"));
        this.thisLine.add(this.submissionDetailsShort.get("submission_status"));
        this.thisLine.add(this.submissionDetailsShort.get("e_tma_submission_num"));
        this.thisLine.add(this.submissionDetailsShort.get("course_code"));
        this.thisLine.add(this.submissionDetailsShort.get("pres_code"));
        this.thisLine.add(this.submissionDetailsShort.get("assgnmt_suffix"));
        final String adjustedDate = this.fixDate(this.submissionDetailsShort.get("e_tma_submission_date"));
        this.thisLine.add(adjustedDate);
        final String status = this.submissionDetailsShort.get("submission_status");
        final String submissionNumber = this.submissionDetailsShort.get("e_tma_submission_num");
        final boolean showLatest = this.showLatestFlag.isSelected();
        if (status.equals(this.filter)) {
            this.displayFlag = true;
        }
        else {
            this.displayFlag = false;
        }
        this.ampersandCleanAlt(dataFile);
        dataFile = this.tagRestore(dataFile);
        return this.thisLine;
    }
    
    private void tmaListActionPerformed(final ActionEvent evt) {
        try {
            this.jScrollPane1.getVerticalScrollBar().setValue(0);
        }
        catch (final Exception anException) {
            System.out.println("Error1:" + anException);
        }
    }
    
    private void courseListActionPerformed(final ActionEvent evt) {
        try {
            this.courseMenuPreferences = (String)this.courseList.getSelectedItem();
            this.ourRoot.put("menuPreferences", this.courseMenuPreferences);
            this.setupMenus(this.etmasFolder.getText() + "/" + this.courseList.getSelectedItem(), this.tmaList);
            if (!this.startUp) {
                this.tmaList.setSelectedIndex(this.tmaList.getItemCount() - 1);
            }
            this.jScrollPane1.getVerticalScrollBar().setValue(0);
        }
        catch (final Exception anException) {
            System.out.println("Error3:" + anException);
        }
    }
    
    private void setupMenus(final String aPathName, final JComboBox aMenu) {
        while (aMenu.getItemCount() > 1) {
            aMenu.removeItemAt(1);
        }
        try {
            final File aFile = new File(aPathName);
            final String[] fileList = aFile.list();
            Arrays.sort(fileList);
            for (int i = 0; i < fileList.length; ++i) {
                final String tempPath = aPathName + "/" + fileList[i];
                final File tempFile = new File(tempPath);
                if (tempFile.isDirectory() && !tempFile.getName().equals("returns") && this.checkForDigit(tempFile.getName())) {
                    aMenu.addItem(fileList[i]);
                }
            }
            aMenu.updateUI();
        }
        catch (final Exception ex) {}
        if (aMenu.getItemCount() > 1) {
            aMenu.removeItemAt(0);
        }
        this.tmaList.updateUI();
        if (aMenu.equals(this.courseList)) {}
        aMenu.updateUI();
    }
    
    public void selectEtmasFolder() {
        String choiceTitle = "Please select your etmas folder - it must be called 'etmas':";
        int createInt = 0;
        final Object[] options = { "Yes", "No" };
        final JFrame frame = null;
        createInt = JOptionPane.showOptionDialog(frame, "Do you wish to create an etmas folder?", "", 1, 2, null, options, options[1]);
        if (createInt == 0) {
            choiceTitle = "Please choose the folder where you wish to create the etmas folder:";
        }
        final JFileChooser _fileChooser = new JFileChooser();
        _fileChooser.setFileSelectionMode(1);
        _fileChooser.setDialogTitle(choiceTitle);
        final FileSystemView fsv = _fileChooser.getFileSystemView();
        int path = _fileChooser.showOpenDialog(null);
        File aFile = _fileChooser.getSelectedFile();
        if (createInt == 0) {
            aFile = new File(aFile.getPath() + "/etmas/");
            if (!aFile.exists()) {
                aFile.mkdir();
            }
            else {
                JOptionPane.showMessageDialog(null, "There is already an etmas folder in this location!\nA new one will NOT be created, but \nthe path will be set to the existing folder.");
            }
        }
        for (String fName = aFile.getName(); !fName.equals("etmas") && !fName.equals(""); fName = aFile.getName()) {
            JOptionPane.showMessageDialog((Component)null, "Your folder must be called 'etmas' without the quotes.\nYou have selected a folder called " + fName);
            path = _fileChooser.showOpenDialog(null);
            aFile = _fileChooser.getSelectedFile();
        }
        this.etmasFolder.setText(aFile.getPath());
        this.ourRoot.put("etmasFolder", aFile.getPath());
        try {
            this.setupMenus(this.etmasFolder.getText(), this.courseList);
        }
        catch (final Exception anException) {
            System.out.println("Error:" + anException);
        }
    }
    
    private void setEtmasFolderActionPerformed(final ActionEvent evt) {
        this.selectEtmasFolder();
    }
    
    private void closePreferencesActionPerformed(final ActionEvent evt) {
        this.preferences.setVisible(false);
        this.ourRoot.put("etmasFolder", this.etmasFolder.getText());
    }
    
    private void openPreferencesActionPerformed(final ActionEvent evt) {
        this.openPrefsWindow();
    }
    
    private void openPrefsWindow() {
        this.preferences.setVisible(true);
        this.preferences.setSize(630, 600);
        this.etmasFolder.setText(this.ourRoot.get("etmasFolder", ""));
        this.downloadsFolder.setText(this.ourRoot.get("downloadsFolder", this.desktopPath));
    }
    
    private void savePt3ActionPerformed(final ActionEvent evt) {
        final Timer timer1 = new Timer();
        final ScheduleRunner task1 = new ScheduleRunner();
        timer1.schedule(task1, 1000L);
        this.saveDetails();
        JOptionPane.showMessageDialog(null, "PT3 saved!");
    }
    
    private void saveDetails() {
        this.checkStatus();
        this.calculateTotals();
        this.outString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><student_submission>";
        String tempString = this.tutor_comments_input.getText().replaceAll("<", this.string02);
        tempString = tempString.replaceAll(">", this.string03);
        this.tutor_comments_input.setText(tempString);
        this.putDetails(this.studentDetails, this.xmlTagsStringsReduced, "student_details");
        this.putDetails(this.tutorDetails, this.xmlTagsStringsReducedStaff, "tutor_details");
        final String cleanString = "";
        this.putDetails(this.submissionDetails, this.xmlTagsStringsReducedSubs, "submission_details");
        this.putMarks();
        this.outString += "</student_submission>";
        if (this.osName.equals("Mac OS X")) {
            this.outString = this.fixAccents(this.outString);
        }
        this.putFile(this.fhiFileName.getText(), this.outString);
        this.savedFlag = true;
        final File tempFile = new File(this.fhiFileName.getText());
        this.ampersandClean(tempFile);
        tempString = this.tutor_comments_input.getText().replaceAll(this.string02, "<");
        tempString = tempString.replaceAll(this.string03, ">");
        this.tutor_comments_input.setText(tempString);
    }
    
    private String addStartTag(final String aString) {
        return "<" + aString;
    }
    
    private String addEndTag(final String aString) {
        return "</" + aString;
    }
    
    private void tmaScoresInputMethodTextChanged(final InputMethodEvent evt) {
        this.checkRange();
        this.calculateTotals();
        this.savedFlag = false;
    }
    
    public boolean totalEntry() {
        boolean editOK = false;
        if (this.directEntryFlag.isSelected()) {
            final int colNo = this.tmaScores.getSelectedColumn();
            final int rowNo = this.tmaScores.getSelectedRow();
            if (colNo == 4 && this.partStarts.contains(rowNo)) {
                boolean emptyCheck = true;
                for (int i = rowNo; i < this.partStarts.get(this.partStarts.indexOf(rowNo) + 1); ++i) {
                    if (!this.tmaScores.getValueAt(i, 2).equals("")) {
                        emptyCheck = false;
                    }
                }
                if (emptyCheck) {
                    editOK = true;
                    final String questionNumber = (String)this.tmaScores.getValueAt(rowNo, 0);
                    final String questionMax = (String)this.tmaScores.getValueAt(rowNo, 5);
                    final String thisTotal = JOptionPane.showInputDialog("Please enter the total score for Question" + questionNumber);
                    if (thisTotal.equals("")) {
                        this.tmaScores.setValueAt("", rowNo, 4);
                    }
                    else if (Integer.parseInt(thisTotal) > Integer.parseInt(questionMax) || Integer.parseInt(thisTotal) < 0) {
                        JOptionPane.showMessageDialog(null, "Score out of range");
                        this.tmaScores.setValueAt("", rowNo, 4);
                    }
                    else {
                        this.tmaScores.setValueAt(thisTotal, rowNo, 4);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "If you want to enter the total only,\nplease remove all the part scores for this question.");
                }
            }
        }
        return editOK;
    }
    
    public void displayCurrentEntry() {
        final int xCoord1 = this.tmaScores.getSelectedRow();
        final int yCoord1 = this.tmaScores.getSelectedColumn();
        this.additionField.setText("Current entry: " + (String)this.tmaScores.getValueAt(xCoord1, yCoord1));
    }
    
    private void tmaScoresKeyReleased(final KeyEvent evt) {
        this.tmaScores.setSurrendersFocusOnKeystroke(false);
        if (this.autoFillFlag.isSelected() && this.permitted_question_count.getText().equals(this.total_question_count.getText())) {
            this.autoFillScores();
        }
        int thisQuestion = 0;
        if (this.tmaScores.getValueAt(this.xCoord, this.yCoord).equals("") & this.yCoord == 2) {
            for (int numbOfQuestions = this.partStarts.size(), i = 0; i < numbOfQuestions; ++i) {
                if (this.partStarts.get(i) <= this.xCoord) {
                    thisQuestion = this.partStarts.get(i);
                }
            }
            this.tmaScores.setValueAt("", thisQuestion, this.yCoord + 2);
        }
        this.checkRange();
        this.checkStatus();
        this.calculateTotals();
        if (this.tooHighFlag) {
            this.tmaScores.editCellAt(this.xCoord, this.yCoord);
            this.tooHighFlag = false;
        }
        this.savedFlag = false;
    }
    
    private void tmaScoresKeyPressed(final KeyEvent evt) {
        this.tmaScores.setSurrendersFocusOnKeystroke(false);
        this.tooHighFlag = false;
        this.xCoord = this.tmaScores.getSelectedRow();
        this.yCoord = this.tmaScores.getSelectedColumn();
        this.checkEntries();
        this.checkRange();
        this.calculateTotals();
        this.savedFlag = false;
    }
    
    private void checkTotalsActionPerformed(final ActionEvent evt) {
        this.calculateTotals();
    }
    
    private boolean statusChangeAgree() {
        int createInt = 0;
        if (this.submission_status.getText().equals("Zipped")) {
            final Object[] options = { "Yes", "No" };
            final JFrame frame = null;
            createInt = JOptionPane.showOptionDialog(frame, "This will reset the 'Zipped' status to 'Marked'!\nDo you wish to do this?", "", 1, 2, null, options, options[1]);
        }
        return createInt == 0;
    }
    
    private String checkStatus() {
        if (this.max_assgnmt_score.getText().equals("U")) {
            this.tmaScores.setValueAt("", 0, 4);
            this.tmaScores.setValueAt("0", 0, 3);
            this.tmaScores.setValueAt("", 0, 2);
            this.marked_date.setText(this.getDateAndTime());
        }
        else {
            this.markedStatus = "Unmarked";
            final int maxNoOfQuestions = Integer.valueOf(this.permitted_question_count.getText());
            final int actualNoOfQuestions = Integer.valueOf(this.total_question_count.getText());
            this.questionStatusList.clear();
            int markedCount = 0;
            int partmarkedCount = 0;
            int unmarkedCount = 0;
            for (int questionNumber = 0; questionNumber < this.numberOfQuestions; ++questionNumber) {
                final String tempStatus = this.checkQuestionStatus(questionNumber);
                this.questionStatusList.add(tempStatus);
            }
            for (int questionNumber = 0; questionNumber < this.numberOfQuestions; ++questionNumber) {
                if (this.questionStatusList.get(questionNumber).equals("Marked")) {
                    ++markedCount;
                }
                if (this.questionStatusList.get(questionNumber).equals("Unmarked")) {
                    ++unmarkedCount;
                }
                if (this.questionStatusList.get(questionNumber).equals("Partmarked")) {
                    ++partmarkedCount;
                }
            }
            if (markedCount == maxNoOfQuestions && partmarkedCount == 0) {
                this.markedStatus = "Marked";
            }
            if (markedCount + partmarkedCount > maxNoOfQuestions) {
                this.tooMany(markedCount + partmarkedCount, maxNoOfQuestions);
            }
            if (!this.submission_status.getText().equals("Zipped")) {
                this.submission_status.setText(this.markedStatus);
            }
            if (!this.submission_status.getText().equals("Zipped")) {
                if (this.markedStatus.equals("Marked")) {
                    this.marked_date.setText(this.getDateAndTime());
                }
                else {
                    this.marked_date.setText("");
                }
            }
        }
        return this.markedStatus;
    }
    
    private void tooMany(final int actual, final int max) {
        JOptionPane.showMessageDialog((Component)null, "You have marked too many questions! Required: " + max + ";   Marked or Partmarked: " + actual, "", 0);
        final int selRow = this.tmaScores.getSelectedRow();
        this.tmaScores.setValueAt("", selRow, 2);
        if (selRow == this.partStarts.get(this.partStarts.size() - 1)) {
            this.tmaScores.setValueAt("", selRow - 1, 2);
        }
        for (int i = 0; i < this.questionStatusList.size(); ++i) {
            if (this.questionStatusList.get(i).equals("Partmarked")) {
                for (int j = this.partStarts.get(i); j < this.partStarts.get(i + 1); ++j) {
                    this.tmaScores.setValueAt("", j, 2);
                }
                this.tmaScores.setValueAt("", this.partStarts.get(i), 4);
            }
        }
    }
    
    private String checkQuestionStatus(final int aQuestionNumber) {
        String questionStatus = "Marked";
        Boolean partFlag = false;
        for (int questionPartNumber = this.partStarts.get(aQuestionNumber); questionPartNumber < this.partStarts.get(aQuestionNumber + 1); ++questionPartNumber) {
            String currentQuestionPart = "";
            try {
                currentQuestionPart = (String)this.tmaScores.getValueAt(questionPartNumber, 2);
                if (currentQuestionPart.length() == 0) {
                    questionStatus = "Unmarked";
                }
                else {
                    partFlag = true;
                }
            }
            catch (final Exception ex) {}
        }
        if (questionStatus.equals("Unmarked") && partFlag) {
            questionStatus = "Partmarked";
        }
        final int rowNo = this.partStarts.get(aQuestionNumber);
        try {
            if (questionStatus.equals("Unmarked") && this.directEntryFlag.isSelected() && !this.tmaScores.getValueAt(rowNo, 4).equals("")) {
                questionStatus = "Marked";
            }
        }
        catch (final Exception anException) {
            System.out.println(anException);
        }
        return questionStatus;
    }
    
    public boolean testNumeric(final String aString) {
        boolean isNumeric = true;
        double aNumber = 0.0;
        if (!aString.contains("+")) {
            try {
                aNumber = Double.valueOf(aString);
            }
            catch (final Exception anException) {
                isNumeric = false;
            }
        }
        return isNumeric;
    }
    
    private void checkEntries() {
        for (int endLine = this.partStarts.get(this.getQuestionNumbers().size()), i = 0; i < endLine; ++i) {
            try {
                String studentScore = (String)this.tmaScores.getValueAt(i, 2);
                final String maxScore = (String)this.tmaScores.getValueAt(i, 3);
                if (!this.testNumeric(studentScore)) {
                    studentScore = "";
                    this.tmaScores.setValueAt(studentScore, i, 2);
                }
                final Double score = Double.parseDouble(studentScore);
                final Double max = Double.parseDouble(maxScore);
                if (score > max || score < 0.0) {
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "Mark out of range");
                    this.tmaScores.setValueAt("", i, 2);
                    this.tmaScores.changeSelection(i, 2, false, false);
                    this.tooHighFlag = true;
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    private boolean checkRange() {
        boolean inRangeFlag = true;
        final int colNo = this.tmaScores.getSelectedColumn();
        final int rowNo = this.tmaScores.getSelectedRow();
        final int noOfLines = this.partStarts.get(this.partStarts.size() - 1);
        final String currentValue = (String)this.tmaScores.getValueAt(rowNo, colNo);
        if (rowNo > noOfLines - 1) {
            for (int i = noOfLines; i < this.tmaScores.getRowCount(); ++i) {
                this.tmaScores.setValueAt(null, i, 2);
            }
            inRangeFlag = false;
        }
        return inRangeFlag;
    }
    
    public void autoFillScores() {
        final int rowNo = this.tmaScores.getSelectedRow();
        for (int j = 0; j < this.partStarts.size() - 1; ++j) {
            for (int i = this.partStarts.get(j); i < this.partStarts.get(j + 1); ++i) {
                final String tempScore = (String)this.tmaScores.getValueAt(i, 2);
                if (tempScore.equals("") && rowNo < this.partStarts.get(j + 1) && rowNo >= this.partStarts.get(j)) {
                    this.tmaScores.setValueAt(this.tmaScores.getValueAt(i, 3), i, 2);
                }
            }
        }
    }
    
    private void calculateTotals() {
        if (this.getQuestionNumbers().size() == 0) {
            return;
        }
        if (this.max_assgnmt_score.getText().equals("U")) {
            this.tmaScores.setValueAt("", 0, 4);
            this.tmaScores.setValueAt("", 0, 2);
            this.tmaScores.setValueAt("0", 0, 3);
            if (!this.submission_status.getText().equals("Zipped")) {
                this.submission_status.setText("Marked");
            }
            this.overall_grade_score.setText("U");
            this.checkStatus();
        }
        else {
            this.checkEntries();
            double grandTotal = 0.0;
            int maxTotal = 0;
            int thisMax = 0;
            for (int questionCount = 0; questionCount < this.getQuestionNumbers().size(); ++questionCount) {
                final int startLine = this.partStarts.get(questionCount);
                if (!this.directEntryFlag.isSelected()) {
                    this.tmaScores.setValueAt("", startLine, 4);
                }
                final int endLine = this.partStarts.get(questionCount + 1);
                double thisTotal = 0.0;
                maxTotal = 0;
                int thisTotalRounded = 0;
                boolean partBlank = true;
                for (int partCount = startLine; partCount < endLine; ++partCount) {
                    final String tempScore = (String)this.tmaScores.getValueAt(partCount, 2);
                    if (tempScore.indexOf("+") > 0) {
                        this.tmaScores.setValueAt(this.addEntries(tempScore), partCount, 2);
                    }
                    if (!tempScore.equals("")) {
                        partBlank = false;
                    }
                    try {
                        thisMax = Integer.parseInt((String)this.tmaScores.getValueAt(partCount, 3));
                        maxTotal += thisMax;
                        final double thisScore = Double.parseDouble((String)this.tmaScores.getValueAt(partCount, 2));
                        thisTotal += thisScore;
                        thisTotalRounded = (int)(thisTotal + 0.51);
                        if (!partBlank || !this.directEntryFlag.isSelected()) {
                            this.tmaScores.setValueAt(String.valueOf(thisTotalRounded), startLine, 4);
                        }
                    }
                    catch (final Exception ex) {}
                }
                try {
                    grandTotal += Double.parseDouble((String)this.tmaScores.getValueAt(startLine, 4));
                }
                catch (final Exception ex2) {}
            }
            final int grandTotalInt = (int)grandTotal;
            String grandTotalString = String.valueOf(grandTotalInt);
            for (int sLength = grandTotalString.length(); sLength < 3; sLength = grandTotalString.length()) {
                grandTotalString = "0" + grandTotalString;
            }
            if (this.late_submission_status.getText().equals("Y")) {
                grandTotalString = "L";
            }
            this.overall_grade_score.setText(grandTotalString);
            this.updatePreviousScores();
        }
        this.checkEntries();
        this.checkStatus();
    }
    
    public void updatePreviousScores() {
        final String currentTma = (String)this.tmaList.getSelectedItem();
        for (int nCols = this.prevMarks.getColumnCount(), i = 0; i < nCols; ++i) {
            if (this.prevMarks.getValueAt(0, i).equals(currentTma)) {
                this.prevMarks.setValueAt(this.overall_grade_score.getText(), 1, i);
            }
        }
        this.avePreviousScores((String)this.courseList.getSelectedItem());
    }
    
    public File ampersandClean(final File aFile) {
        BufferedReader buffer = null;
        String fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            for (String currentLine = buffer.readLine(); currentLine != null; currentLine = buffer.readLine()) {
                fileString += currentLine;
            }
        }
        catch (final Exception ex) {}
        finally {
            try {
                buffer.close();
            }
            catch (final Exception ex2) {}
        }
        final char char18 = '\u0012';
        final String string18 = "" + char18;
        if (fileString.contains("&") || fileString.contains(string18) || fileString.contains(this.string02) || fileString.contains(this.string03)) {
            fileString = fileString.replaceAll(string18, "&");
            fileString = fileString.replaceAll("&amp;", "xyzpqt");
            fileString = fileString.replaceAll("&", "&amp;");
            fileString = fileString.replaceAll("xyzpqt", "&amp;");
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write(fileString);
            }
            catch (final Exception ex3) {}
            finally {
                try {
                    bufferOut.close();
                }
                catch (final Exception ex4) {}
            }
        }
        return aFile;
    }
    
    public File tagClean(final File aFile) {
        BufferedReader buffer = null;
        String fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            for (String currentLine = buffer.readLine(); currentLine != null; currentLine = buffer.readLine()) {
                fileString += currentLine;
            }
        }
        catch (final Exception ex) {}
        finally {
            try {
                buffer.close();
            }
            catch (final Exception ex2) {}
        }
        final String fileStringOld = fileString;
        char thisChar = '\0';
        String thisString = "";
        for (int charNo = 14; charNo < 32; ++charNo) {
            thisChar = (char)charNo;
            thisString = "" + thisChar;
            fileString = fileString.replaceAll(thisString, "");
        }
        final char char12 = '\f';
        final String string12 = "" + char12;
        final char char13 = '\0';
        final String string13 = "" + char13;
        fileString = fileString.replaceAll(string12, "");
        fileString = fileString.replaceAll(string13, "");
        if (fileString.contains(this.string02) || fileString.contains(this.string03) || !fileString.equals(fileStringOld)) {
            fileString = fileString.replaceAll(this.string02, "xtagstart");
            fileString = fileString.replaceAll(this.string03, "xtagend");
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write(fileString);
            }
            catch (final Exception ex3) {}
            finally {
                try {
                    bufferOut.close();
                }
                catch (final Exception ex4) {}
            }
        }
        return aFile;
    }
    
    public File tagRestore(final File aFile) {
        BufferedReader buffer = null;
        String fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            for (String currentLine = buffer.readLine(); currentLine != null; currentLine = buffer.readLine()) {
                fileString += currentLine;
            }
        }
        catch (final Exception ex) {}
        finally {
            try {
                buffer.close();
            }
            catch (final Exception ex2) {}
        }
        final String fileStringOld = fileString;
        if (fileString.contains("xtagstart") || fileString.contains("xtagend") || !fileString.equals(fileStringOld)) {
            fileString = fileString.replaceAll("xtagstart", this.string02);
            fileString = fileString.replaceAll("xtagend", this.string03);
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write(fileString);
            }
            catch (final Exception ex3) {}
            finally {
                try {
                    bufferOut.close();
                }
                catch (final Exception ex4) {}
            }
            String tempString = this.tutor_comments_input.getText();
            tempString = tempString.replaceAll("xtagstart", "<");
            tempString = tempString.replaceAll("xtagend", ">");
            this.tutor_comments_input.setText(tempString);
        }
        return aFile;
    }
    
    public File ampersandCleanAlt(final File aFile) {
        BufferedReader buffer = null;
        String fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            for (String currentLine = buffer.readLine(); currentLine != null; currentLine = buffer.readLine()) {
                fileString += currentLine;
            }
        }
        catch (final Exception ex) {}
        finally {
            try {
                buffer.close();
            }
            catch (final Exception ex2) {}
        }
        if (fileString.contains("&amp;")) {
            fileString = fileString.replaceAll("&amp;", "&");
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write(fileString);
            }
            catch (final Exception ex3) {}
            finally {
                try {
                    bufferOut.close();
                }
                catch (final Exception ex4) {}
            }
        }
        return aFile;
    }
    
    private void getShortDetails(final String aString, final Map aMap, File aFile) {
        try {
            this.entryErrorFlag = false;
            this.ampersandClean(aFile);
            aFile = this.tagClean(aFile);
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = null;
            try {
                doc = docBuilder.parse(aFile);
            }
            catch (final Exception anException) {
                if (this.readErrorFlag) {
                    this.entryErrorFlag = true;
                }
            }
            this.readErrorFlag = false;
            doc.getDocumentElement().normalize();
            final NodeList listOfPersons = doc.getElementsByTagName(aString);
            final Node firstPersonNode = listOfPersons.item(0);
            final NodeList listOfStudentDetails = listOfPersons.item(0).getChildNodes();
            final int numberOfStudentItems = listOfStudentDetails.getLength();
            if (firstPersonNode.getNodeType() == 1) {
                for (int i = 0; i < numberOfStudentItems; ++i) {
                    try {
                        final Element studentElement = (Element)listOfStudentDetails.item(i);
                        final String thisDetail = studentElement.getNodeName();
                        try {
                            final NodeList detailList = listOfStudentDetails.item(i).getChildNodes();
                            aMap.put(thisDetail, detailList.item(0).getNodeValue());
                        }
                        catch (final Exception e) {
                            aMap.put(thisDetail, "");
                        }
                    }
                    catch (final Exception ex) {}
                }
            }
            aFile = this.tagClean(aFile);
        }
        catch (final Exception ex2) {}
    }
    
    public void getMarks2(File aFile) {
        this.ampersandClean(aFile);
        aFile = this.tagClean(aFile);
        final Map<Integer, Integer> swapMap = new HashMap<Integer, Integer>();
        swapMap.put(0, 3);
        swapMap.put(1, 2);
        swapMap.put(2, 1);
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final org.w3c.dom.Document doc = docBuilder.parse(aFile);
            this.getQuestionNumbers().clear();
            this.partStarts.clear();
            this.numberOfParts.clear();
            doc.getDocumentElement().normalize();
            final NodeList listOfQuestions = doc.getElementsByTagName("question_details");
            final NodeList listOfQuestions2 = listOfQuestions.item(0).getChildNodes();
            final int totalQuestions1 = listOfQuestions2.getLength();
            this.numberOfQuestions = totalQuestions1;
            final List<String> maxScores = new ArrayList<String>();
            final List<String> qpCount = new ArrayList<String>();
            String questionNumber = "";
            String maxScore = "";
            String qpc = "";
            Element questionElement = null;
            for (int questionNo = 0; questionNo < totalQuestions1; ++questionNo) {
                questionElement = (Element)listOfQuestions2.item(questionNo);
                questionNumber = questionElement.getAttribute("question_number");
                maxScore = questionElement.getElementsByTagName("maximum_question_score").item(0).getTextContent();
                maxScores.add(maxScore);
                qpc = questionElement.getElementsByTagName("question_parts_count").item(0).getTextContent();
                qpCount.add(qpc);
                System.out.println(qpCount);
                this.questionNumbers.add(questionNumber);
            }
            int lineNo = 0;
            final int lineCount = 1;
            for (int i = 0; i < totalQuestions1; ++i) {
                this.tmaScores.setValueAt(this.getQuestionNumbers().get(i), lineNo, 0);
                this.tmaScores.setValueAt(maxScores.get(i), lineNo, 5);
                final NodeList listOfQuestions3 = listOfQuestions2.item(i).getChildNodes();
                this.partStarts.add(lineNo);
                final int totalQuestions2 = listOfQuestions3.getLength();
                final List<String> questionScores = new ArrayList<String>();
                questionScores.clear();
                for (int s = 0; s < listOfQuestions3.getLength(); ++s) {
                    final Node firstQuestionNode = listOfQuestions3.item(s);
                    if (firstQuestionNode.getNodeType() == 1) {
                        try {
                            final Element firstQuestionElement = (Element)firstQuestionNode;
                            final NodeList partMarksList = firstQuestionElement.getChildNodes();
                            final int partMarksLength = partMarksList.getLength();
                            String thisScore = "";
                            for (int m = 0; m < partMarksLength; ++m) {
                                thisScore = "";
                                try {
                                    if (listOfQuestions3.getLength() != 3) {
                                        thisScore = partMarksList.item(m).getChildNodes().item(0).getTextContent();
                                        this.tmaScores.setValueAt(thisScore, lineNo, swapMap.get(m));
                                    }
                                    else {
                                        thisScore = partMarksList.item(m).getTextContent();
                                        if (swapMap.get(s) != 1) {
                                            this.tmaScores.setValueAt(thisScore, lineNo, swapMap.get(s));
                                        }
                                        if (s == 2) {
                                            ++lineNo;
                                        }
                                    }
                                }
                                catch (final Exception ex) {}
                            }
                        }
                        catch (final Exception anException) {
                            System.out.println("Error in getMarks " + anException);
                        }
                    }
                    if (s > 2) {
                        ++lineNo;
                    }
                }
            }
            this.partStarts.add(lineNo);
        }
        catch (final Exception ex2) {}
        try {
            this.calculateTotals();
        }
        catch (final Exception ex3) {}
    }
    
    public void getMarks3(File aFile) {
        aFile = this.tagClean(aFile);
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final org.w3c.dom.Document doc = docBuilder.parse(aFile);
            this.getQuestionNumbers().clear();
            this.partStarts.clear();
            this.numberOfParts.clear();
            doc.getDocumentElement().normalize();
            final NodeList questionBlock = doc.getElementsByTagName("question_details");
            final NodeList listOfQuestions = questionBlock.item(0).getChildNodes();
            this.numberOfQuestions = listOfQuestions.getLength();
            final List<String> maxScores = new ArrayList<String>();
            final List<String> questionScores = new ArrayList<String>();
            final List<String> qpCount = new ArrayList<String>();
            final List<String> studQnScores = new ArrayList<String>();
            String questionNumber = "";
            String maxScore = "";
            String qpc = "";
            String studQnScore = "";
            Element questionElement = null;
            String questionScore = "";
            for (int questionNo = 0; questionNo < this.numberOfQuestions; ++questionNo) {
                try {
                    questionElement = (Element)listOfQuestions.item(questionNo);
                    questionNumber = questionElement.getAttribute("question_number");
                    maxScore = questionElement.getElementsByTagName("maximum_question_score").item(0).getTextContent();
                    maxScore = maxScore.replaceAll(" ", "");
                    maxScores.add(maxScore);
                    questionScore = questionElement.getElementsByTagName("student_question_score").item(0).getTextContent();
                    questionScores.add(questionScore);
                    qpc = questionElement.getElementsByTagName("question_parts_count").item(0).getTextContent();
                    qpCount.add(qpc);
                    studQnScore = questionElement.getElementsByTagName("student_question_score").item(0).getTextContent();
                    studQnScores.add(studQnScore);
                    this.questionNumbers.add(questionNumber);
                }
                catch (final Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
            int lineNo = 0;
            for (int i = 0; i < this.numberOfQuestions; ++i) {
                this.tmaScores.setValueAt(maxScores.get(i), lineNo, 5);
                this.tmaScores.setValueAt(this.getQuestionNumbers().get(i), lineNo, 0);
                this.tmaScores.setValueAt(questionScores.get(i), lineNo, 4);
                if (qpCount.get(i).equals("0")) {
                    this.partStarts.add(lineNo);
                    this.tmaScores.setValueAt(this.getQuestionNumbers().get(i), lineNo, 0);
                    this.tmaScores.setValueAt(maxScores.get(i), lineNo, 3);
                    this.tmaScores.setValueAt(studQnScores.get(i), lineNo, 2);
                    this.tmaScores.setValueAt(questionScores.get(i), lineNo, 4);
                    ++lineNo;
                }
                else {
                    this.partStarts.add(lineNo);
                    final NodeList listOfQuestionParts = listOfQuestions.item(i).getChildNodes();
                    final int totalQuestionParts = listOfQuestionParts.getLength();
                    String partScore = "";
                    String partNumber = "";
                    String partMax = "";
                    for (int partNo = 3; partNo < totalQuestionParts; ++partNo) {
                        final Element partElement = (Element)listOfQuestionParts.item(partNo);
                        final String questionPartId = partElement.getAttribute("part_id");
                        partScore = partElement.getElementsByTagName("student_question_part_score").item(0).getTextContent();
                        partMax = partElement.getElementsByTagName("maximum_question_part_score").item(0).getTextContent();
                        partMax = partMax.replaceAll(" ", "");
                        partNumber = partElement.getElementsByTagName("questn_part_desc").item(0).getTextContent();
                        this.tmaScores.setValueAt(partScore, lineNo, 2);
                        this.tmaScores.setValueAt(partNumber, lineNo, 1);
                        this.tmaScores.setValueAt(partMax, lineNo, 3);
                        ++lineNo;
                    }
                }
            }
            this.partStarts.add(lineNo);
        }
        catch (final Exception anException) {
            JOptionPane.showMessageDialog(null, anException);
        }
    }
    
    public void zipDir(final String dir2zip, final ZipOutputStream zos, final int nesting) {
        try {
            final File zipDir = new File(dir2zip);
            final String[] dirList = zipDir.list();
            final byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            for (int i = 0; i < dirList.length; ++i) {
                final File f = new File(zipDir, dirList[i]);
                if (f.isDirectory()) {
                    final String filePath = f.getPath();
                    this.zipDir(filePath, zos, nesting);
                }
                else {
                    final FileInputStream fis = new FileInputStream(f);
                    final String anEntryString = f.getPath();
                    final String anEntryStringName = f.getName();
                    final int fLength = anEntryStringName.length();
                    if (fLength > this.maxAcceptFilename) {
                        this.maxFileNameLength = fLength;
                        this.longFilenames.add(anEntryString);
                    }
                    final String tempString = this.fixString(anEntryString, nesting);
                    final ZipEntry aNewEntry = new ZipEntry(tempString);
                    final String firstChar = tempString.substring(0, 1);
                    if (!firstChar.equals(".") && !anEntryString.contains("Icon\r")) {
                        zos.putNextEntry(aNewEntry);
                        while ((bytesIn = fis.read(readBuffer)) != -1) {
                            zos.write(readBuffer, 0, bytesIn);
                        }
                    }
                    fis.close();
                }
            }
        }
        catch (final Exception e) {
            System.out.println("Exception " + e);
        }
    }
    
    private String fixString(final String aString, final int nesting) {
        String newString = "";
        String tempString = "";
        final String etmasRoot = this.etmasFolder.getText();
        final int etmasRootLength = etmasRoot.length();
        newString = aString.substring(aString.indexOf(etmasRoot) + etmasRootLength + 1);
        for (int k = 0; k < nesting; ++k) {
            try {
                if (!this.osName.contains("Windows")) {
                    tempString = newString.substring(0, newString.indexOf("/") + 1);
                    newString = newString.replace(tempString, "");
                }
                else {
                    tempString = newString.substring(0, newString.indexOf("\\") + 1);
                    newString = newString.replace(tempString, "");
                }
            }
            catch (final Exception anException) {
                System.out.println("Error " + anException);
            }
        }
        return newString;
    }
    
    public boolean checkScriptClosureWindows(final String testfilename) {
        boolean isOpen = false;
        boolean continueFlag = false;
        if (this.checkClosureFlagPreferences) {
            try {
                final StringBuffer sb = new StringBuffer();
                final String[] cmd = { "tasklist", "/fi", "\"WINDOWTITLE eq " + testfilename + "*\"" };
                final Process p = Runtime.getRuntime().exec(cmd);
                final BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
                final BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line;
                while ((line = bri.readLine()) != null) {
                    sb.append(line);
                }
                bri.close();
                while ((line = bre.readLine()) != null) {
                    sb.append(line);
                }
                bre.close();
                p.waitFor();
                System.out.println("Done.");
                System.out.println(sb.toString());
                if (sb.toString().contains("PID")) {
                    System.out.println(testfilename + " is open");
                    isOpen = true;
                }
                else {
                    System.out.println(testfilename + " is NOT open");
                    isOpen = false;
                }
            }
            catch (final Exception err) {
                err.printStackTrace();
            }
        }
        continueFlag = this.reportScriptClosure(isOpen);
        return continueFlag;
    }
    
    public boolean checkScriptClosure() {
        boolean continueFlag = true;
        boolean isOpen = false;
        int continueInt = 0;
        String openWarning = "Before you zip, DO check that you have saved the current PT3\nand ALL open students' scripts.\nTake particular care that you have saved the final version\nof the current script!";
        String cmd = "";
        if (!this.savedFlag) {
            final Object[] options = { "Continue", "Cancel" };
            final JFrame frame = null;
            continueInt = JOptionPane.showOptionDialog(frame, "You are advised to save the current PT3 before continuing", "Careful...!", 1, 2, null, options, options[1]);
        }
        if (continueInt == 0) {
            if (this.osName.equals("Mac OS X")) {
                cmd = "lsof -n ";
                String listOfApps = "";
                if (this.osName.equals("Mac OS X") && this.checkClosureFlagPreferences) {
                    try {
                        this.messageWindow.setSize(400, 10);
                        this.messageWindow.setVisible(true);
                        this.messageWindow.setTitle("Please wait.... checking files....");
                        this.messageWindow.setLocation(300, 300);
                        final Runtime thisRun = Runtime.getRuntime();
                        final Process proc = thisRun.exec(cmd);
                        final InputStream is = proc.getInputStream();
                        final InputStreamReader isr = new InputStreamReader(is);
                        final BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        final StringBuilder sb = new StringBuilder(10000);
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        listOfApps = sb.toString();
                    }
                    catch (final Exception e) {
                        System.out.println(e);
                    }
                    this.messageWindow.setVisible(false);
                    final boolean tempFlag = listOfApps.contains(this.currentStudentScript) && !this.currentStudentScript.equals("");
                    if (listOfApps.contains(this.markedString) || tempFlag) {
                        isOpen = true;
                        String insert = "one";
                        if (tempFlag) {
                            insert = "the current";
                        }
                        openWarning = "You still appear to have at least " + insert + " student's Marked script open!\nIt's safer to close all the scripts before you zip.\n";
                    }
                }
            }
            final Object[] options = { "Continue", "Cancel" };
            final JFrame frame = null;
            final String alertText = openWarning;
            final int n = JOptionPane.showOptionDialog(frame, alertText, "Before you zip...", 1, 3, null, options, options[0]);
            if (n == 1) {
                continueFlag = false;
            }
        }
        else {
            continueFlag = false;
        }
        return continueFlag;
    }
    
    public boolean checkScriptClosureLinux(final String testfile) {
        boolean isOpen = false;
        boolean continueFlag = false;
        if (this.checkClosureFlagPreferences) {
            try {
                final StringBuffer sb = new StringBuffer();
                final String command = "lsof";
                final String[] cmd = { command, testfile };
                final Process p = Runtime.getRuntime().exec(cmd);
                final BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
                final BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line;
                while ((line = bri.readLine()) != null) {
                    sb.append(line);
                }
                bri.close();
                while ((line = bre.readLine()) != null) {
                    sb.append(line);
                }
                bre.close();
                p.waitFor();
                if (sb.toString().contains("PID")) {
                    System.out.println(testfile + " is open");
                    isOpen = true;
                }
                else {
                    System.out.println(testfile + " is not open");
                    isOpen = false;
                }
            }
            catch (final Exception err) {
                err.printStackTrace();
            }
        }
        continueFlag = this.reportScriptClosure(isOpen);
        return continueFlag;
    }
    
    public boolean reportScriptClosure(final boolean isOpen) {
        int continueInt = 0;
        boolean continueFlag = true;
        String openWarning = "";
        final String cmd = "";
        if (!this.savedFlag) {
            final Object[] options = { "Continue", "Cancel" };
            final JFrame frame = null;
            continueInt = JOptionPane.showOptionDialog(frame, "You are advised to save the current PT3 before continuing", "Careful...!", 1, 2, null, options, options[1]);
        }
        if (isOpen) {
            openWarning = "You still appear to have at least one student's Marked script open!\nIt's safer to close all the scripts before you zip.\n";
            continueFlag = false;
        }
        else {
            openWarning = "Before you zip, DO check that you have saved the current PT3\nand ALL open students' scripts.\nTake particular care that you have saved the final version\nof the current script!";
        }
        final Object[] options = { "Continue", "Cancel" };
        final JFrame frame = null;
        final String alertText = openWarning;
        final int n = JOptionPane.showOptionDialog(frame, alertText, "Before you zip...", 1, 3, null, options, options[0]);
        continueFlag = (n != 1);
        return continueFlag;
    }
    
    public void zipper() {
        this.maxFileNameLength = 0;
        this.longFilenames.clear();
        if (this.submission_status.getText().equals("Unmarked")) {
            JOptionPane.showMessageDialog(null, "You haven't finished marking this yet!", "", 2);
        }
        else {
            int n = 0;
            if (this.tutor_comments_input.getText().equals("")) {
                final Object[] options = { "Yes", "No" };
                final JFrame frame = null;
                n = JOptionPane.showOptionDialog(frame, "You don't seem to have commented on this assignment. \nDo you still wish to proceed and zip?", "", 1, 3, null, options, options[1]);
            }
            if (n == 0) {
                boolean continueFlag = false;
                if (this.osName.equals("Mac OS X")) {
                    continueFlag = this.checkScriptClosure();
                }
                if (this.osName.contains("Windows")) {
                    final File f = new File(this.currentStudentScript);
                    continueFlag = this.checkScriptClosureWindows(f.getName());
                }
                if (this.osName.contains("Linux")) {
                    continueFlag = this.checkScriptClosureLinux(this.currentStudentScript);
                }
                if (continueFlag) {
                    this.messageWindow.setSize(400, 10);
                    this.messageWindow.setVisible(true);
                    this.messageWindow.setTitle("Please wait.... zipping files....");
                    this.messageWindow.setLocation(300, 300);
                    try {
                        final String returnsPath = this.etmasFolder.getText() + this.returnsName;
                        final File returnsFolder = new File(returnsPath);
                        returnsFolder.mkdir();
                    }
                    catch (final Exception ex) {}
                    final File etmasBase = new File(this.etmasFolder.getText());
                    final File tempBase = new File(etmasBase + this.tempName);
                    final String pathName = this.fhiFileName.getText();
                    final File bFile = new File(pathName);
                    final String bFileName = bFile.getParent();
                    final File aFile = bFile.getParentFile();
                    try {
                        tempBase.mkdir();
                    }
                    catch (final Exception ex2) {}
                    this.zip_file.setText(this.getZipfileNameNonBatch2(this.e_tma_submission_num.getText()));
                    this.zip_date.setText(this.getDateAndTime());
                    this.submission_status.setText("Zipped");
                    final String name1 = this.getZipfileNameNonBatch1(this.e_tma_submission_num.getText());
                    final String name2 = this.getZipfileNameNonBatch2(this.e_tma_submission_num.getText());
                    this.saveDetails();
                    final boolean success1 = this.zipDirectory(aFile, this.etmasFolder.getText() + this.tempName + name1, 4);
                    final File thisFile = new File(this.etmasFolder.getText() + this.tempName);
                    final String fileToBeReturned = this.etmasFolder.getText() + this.returnsName + name2;
                    this.saveDetails();
                    final boolean success2 = this.zipDirectory(thisFile, fileToBeReturned, 3);
                    if (success1 && success2) {
                        final Timer timer = new Timer();
                        final ScheduleRunner task = new ScheduleRunner();
                        this.messageWindow.setVisible(false);
                        this.submission_status.setText("Zipped");
                        copyString(fileToBeReturned + ".zip");
                        copyString(name2 + ".zip");
                        if (this.maxFileNameLength > this.maxAcceptFilename) {
                            String longFileNameString = "";
                            for (String aFilename : this.longFilenames) {
                                longFileNameString += aFilename;
                            }
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog((Component)null, "At least one of your filenames looks too long!\n (" + this.maxFileNameLength + " characters).\n" + longFileNameString, "", 2);
                        }
                        this.returnTmas("Files have been zipped!");
                    }
                    else {
                        this.zip_file.setText("");
                        this.zip_date.setText("");
                        this.submission_status.setText("");
                        this.messageWindow.setVisible(false);
                        JOptionPane.showMessageDialog(null, "Error - files have NOT been zipped!", "", 0);
                    }
                    deleteDir(tempBase);
                }
            }
        }
    }
    
    public void returnTmas(final String errorMessage) {
        String returnAddress = "";
        final Object[] options = { "Return zipped eTMAs now", "Don't return eTMAs at present" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog((Component)frame, errorMessage + "\nThe path to the zipped file has been copied to the clipboard", "", 1, 3, (Icon)null, options, options[0]);
        if (n == 0) {
            returnAddress = this.ouEtmaAddress.getText() + "etmaT_tmas_return.asp?";
            final String cb = this.getClipBoard();
            if (cb.contains("XXX101")) {
                returnAddress = "http://etma-training.open.ac.uk/etma/tutor/etmat_training_signon.asp";
            }
            final String myURI = returnAddress;
            try {
                Desktop.getDesktop().browse(new URI(myURI));
            }
            catch (final Exception e) {
                JOptionPane.showMessageDialog((Component)null, "Unable to connect to " + myURI, "Sorry!", 2);
            }
        }
    }
    
    public static void copyString(final String data) {
        copyTransferableObject(new StringSelection(data));
    }
    
    public static void copyTransferableObject(final Transferable contents) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
    }
    
    private void zipFilesActionPerformed(final ActionEvent evt) {
        this.zipper();
    }
    
    private String getZipfileName1(final String subNo) {
        final int subNoInt = Integer.parseInt(subNo);
        final List<String> tmaDetails = this.getTableEntry(this.studentIndex, subNoInt);
        String firstString = "";
        firstString = (String)tmaDetails.get(0) + "-" + (String)tmaDetails.get(7) + "-" + (String)tmaDetails.get(8) + "-" + (String)tmaDetails.get(9) + "-" + (String)tmaDetails.get(3) + "-" + (String)tmaDetails.get(6);
        return firstString;
    }
    
    private String getZipfileName2(final String subNo) {
        final int subNoInt = Integer.parseInt(subNo);
        final List<String> tmaDetails = this.getTableEntry(this.studentList.getSelectedIndex(), subNoInt);
        String secondString = "";
        secondString = (String)tmaDetails.get(7) + "-" + (String)tmaDetails.get(8) + "-" + this.getDateAndTime() + "_M";
        return secondString;
    }
    
    private String getZipfileNameNonBatch1(final String subNo) {
        final int subNoInt = Integer.parseInt(subNo);
        final List<String> tmaDetails = this.getTableEntry(this.studentIndex, subNoInt);
        String firstString = "";
        firstString = this.personal_id.getText() + "-" + this.course_code.getText() + "-" + this.pres_code.getText() + "-" + this.assgnmt_suffix.getText() + "-" + this.ou_computer_user_name.getText() + "-" + this.e_tma_submission_num.getText();
        return firstString;
    }
    
    private String getZipfileNameNonBatch2(final String subNo) {
        final int subNoInt = Integer.parseInt(subNo);
        final List<String> tmaDetails = this.getTableEntry(this.studentList.getSelectedIndex(), subNoInt);
        String secondString = "";
        secondString = this.course_code.getText() + "-" + this.pres_code.getText() + "-" + this.getDateAndTime() + "_M";
        return secondString;
    }
    
    public String addZeros(final int anInt, final int numberOfDigits) {
        String newString;
        final String oldString = newString = String.valueOf(anInt);
        if (oldString.length() < numberOfDigits) {
            for (int i = 0; i < numberOfDigits - oldString.length(); ++i) {
                newString = "0" + newString;
            }
        }
        return newString;
    }
    
    private String getDateAndTime() {
        String theDateAndTime = "";
        final Date date = new Date();
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        final String stime = theDateAndTime = calendar.get(1) + "_" + this.addZeros(calendar.get(2) + 1, 2) + "_" + this.addZeros(calendar.get(5), 2) + "-" + this.addZeros(calendar.get(11), 2) + "_" + this.addZeros(calendar.get(12), 2) + "_" + this.addZeros(calendar.get(13), 2);
        return theDateAndTime;
    }
    
    private boolean zipDirectory(final File aFile, final String pathName, final int nesting) {
        boolean successFlag = true;
        try {
            final File bFile = new File(pathName);
            ZipOutputStream zos = null;
            final String currentOutputPath = bFile.getPath() + ".zip";
            if (!currentOutputPath.contains(".fhi")) {}
            zos = new ZipOutputStream(new FileOutputStream(currentOutputPath));
            zos.setMethod(8);
            zos.setLevel(-1);
            this.zipDir(aFile.getPath(), zos, nesting);
            final String fName = aFile.getPath();
            zos.close();
            return successFlag;
        }
        catch (final Exception e) {
            successFlag = false;
            return successFlag;
        }
    }
    
    private void selectAllActionPerformed(final ActionEvent evt) {
        final Object[] options = { "Select All", "Select None", "Cancel" };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, "Quick Select:", "", 1, 3, null, options, options[0]);
        final int nRow = this.listOfTmas.getRowCount();
        boolean selectAllFlag = true;
        if (n == 1) {
            selectAllFlag = false;
        }
        if (n != 2) {
            for (int i = 0; i < nRow; ++i) {
                if (!this.listOfTmas.getValueAt(i, 0).equals("")) {
                    this.listOfTmas.setValueAt(selectAllFlag, i, 11);
                }
            }
        }
    }
    
    private void startupScreenFlagActionPerformed(final ActionEvent evt) {
        this.startupScreenFlagPreferences = this.startupScreenFlag.isSelected();
        this.ourRoot.putBoolean("startupScreenPreferences", this.startupScreenFlag.isSelected());
    }
    
    private void suggestFlagActionPerformed(final ActionEvent evt) {
        this.ourRoot.putBoolean("suggestFlagPreferences", this.suggestFlag.isSelected());
    }
    
    private void printTmaListActionPerformed(final ActionEvent evt) {
        try {
            this.listOfTmas.print();
        }
        catch (final Exception ex) {}
    }
    
    private void printGradesActionPerformed(final ActionEvent evt) {
        try {
            this.gradesSummaryTable.print();
        }
        catch (final Exception ex) {}
    }
    
    private void setAudioAppActionPerformed(final ActionEvent evt) {
        this.ourRoot.put("currentAudioPreferences", this.currentAudioPreferences);
        final JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentAudioPath = JOptionPane.showInputDialog(null, "Please enter the short(launch) name of the application you wish the filehandler to use to open mp3s");
            }
            else {
                final Object[] options = { "Default", "Select other" };
                final JFrame frame = null;
                final int n = JOptionPane.showOptionDialog(frame, "Do you want to use the system default mp3 application?", "Default Audio", 1, 3, null, options, options[0]);
                if (n == 0) {
                    this.currentAudioPath = "System Default";
                    this.currentAudioPreferences = "System Default";
                }
                else {
                    _fileChooser.setDialogTitle("Please select application to open students' mp3s: ");
                    final int path = _fileChooser.showOpenDialog(null);
                    final File aFile = _fileChooser.getSelectedFile();
                    this.currentAudioPath = aFile.getPath();
                }
            }
            JOptionPane.showMessageDialog((Component)null, "Mp3 app path set to " + this.currentAudioPath);
            this.audioPath.setText(this.currentAudioPath);
            this.ourRoot.put("currentAudioPreferences", this.currentAudioPath);
        }
        catch (final Exception ex) {}
    }
    
    private void createFeedbackActionPerformed(final ActionEvent evt) {
        this.createFeedback();
    }
    
    private void autoMp3ActionPerformed(final ActionEvent evt) {
    }
    
    private void exportGradesActionPerformed(final ActionEvent evt) {
        this.exportMarksGrid(this.gradesSummaryTable, "gradeslist", 1);
    }
    
    private void highlightUnmarkedActionPerformed(final ActionEvent evt) {
        this.highlightedFlag = this.highlightUnmarked.isSelected();
        this.ourRoot.putBoolean("highlightedFlag", this.highlightedFlag);
        if (this.highlightUnmarked.isSelected()) {
            this.getNewTmaRedRows(this.listOfTmas.getRowCount());
        }
        this.openList();
    }
    
    private void distributeDocumentActionPerformed(final ActionEvent evt) {
        this.distributeFile();
    }
    
    public void distributeFile() {
        if (this.course_code.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "You must load a PT3 first!");
        }
        else {
            final JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
            try {
                _fileChooser.setDialogTitle("Please select document to be distributed to all students' folders:");
                final int path = _fileChooser.showOpenDialog(null);
                int n2 = 0;
                this.distFile = _fileChooser.getSelectedFile();
                final String distFileName = this.distFile.getName();
                final String distFilePath = this.distFile.getPath();
                if (distFileName.contains(".fhi") || distFileName.contains(".app") || distFileName.contains(".exe")) {
                    JOptionPane.showMessageDialog(null, "This is either an etma system file or an application - you can't distribute it!");
                }
                else {
                    final double fileSize = (double)this.distFile.length();
                    if (fileSize > 1000000.0) {
                        final Object[] options2 = { "Continue", "Stop!" };
                        final JFrame frame2 = null;
                        n2 = JOptionPane.showOptionDialog((Component)frame2, "This file is " + this.distFile.length() + "bytes long!\nAre you sure you want to distribute it to all students?", "", 1, 3, (Icon)null, options2, options2[1]);
                    }
                    if (n2 != 1) {
                        final Object[] options3 = { "OK - Distribute", "Stop!" };
                        final JFrame frame3 = null;
                        final int n3 = JOptionPane.showOptionDialog((Component)frame3, "Are you sure you want to distribute the file\n" + distFilePath + "\nto the folders of all " + this.course_code.getText() + " students who have submitted TMA" + this.assgnmt_suffix.getText() + "\nThis is not easily undoable!", "", 1, 3, (Icon)null, options3, options3[1]);
                        if (n3 != 1) {
                            final String pathToCurrentTmaFolder = this.etmasFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText();
                            final File thisTmaFolder = new File(pathToCurrentTmaFolder);
                            boolean existsFlag = false;
                            final File[] tmaStudentList = thisTmaFolder.listFiles();
                            for (int i = 0; i < tmaStudentList.length; ++i) {
                                if (tmaStudentList[i].isDirectory()) {
                                    final String studName = "";
                                    final File[] subNos = tmaStudentList[i].listFiles();
                                    for (int j = 0; j < subNos.length; ++j) {
                                        final String submNo = subNos[j].getName();
                                        if (subNos[j].isDirectory()) {
                                            final String insertFilePath = tmaStudentList[i] + "/" + submNo + "/" + distFileName;
                                            final File bFile = new File(insertFilePath);
                                            if (bFile.exists()) {
                                                final Object[] options4 = { "Replace", "Don't replace" };
                                                final JFrame frame4 = null;
                                                final int n4 = JOptionPane.showOptionDialog((Component)frame3, "The file " + distFilePath + "\nalready exists in " + tmaStudentList[i].getName() + studName + "\nDo you want to replace it?", "", 1, 3, (Icon)null, options4, options4[1]);
                                                if (n4 == 1) {
                                                    existsFlag = true;
                                                }
                                                else {
                                                    this.makeCopy(this.distFile, bFile);
                                                }
                                            }
                                            else {
                                                this.makeCopy(this.distFile, bFile);
                                            }
                                        }
                                    }
                                }
                            }
                            if (!existsFlag) {
                                JOptionPane.showMessageDialog((Component)null, "The file " + distFilePath + " has been distributed to all students.");
                            }
                            else {
                                JOptionPane.showMessageDialog((Component)null, "The file " + distFilePath + " was distributed to students,\nexcept those where you decided not to replace the file.");
                            }
                        }
                    }
                }
            }
            catch (final Exception anException) {
                System.out.println(anException);
            }
        }
    }
    
    public void undoDistribution() {
        try {
            final int n2 = 0;
            final String distFileName = this.distFile.getName();
            final String distFilePath = this.distFile.getPath();
            if (distFileName.contains(".fhi") || distFileName.contains(".app") || distFileName.contains(".exe")) {
                JOptionPane.showMessageDialog(null, "This is either an etma system file or an application - you can't delete it!");
            }
            else {
                final Object[] options = { "OK - Delete", "Stop!" };
                final JFrame frame = null;
                final int n3 = JOptionPane.showOptionDialog((Component)frame, "Are you sure you want to delete the file\n" + distFilePath + "\nin the folders of all " + this.course_code.getText() + " students who have submitted TMA" + this.assgnmt_suffix.getText() + "\nThis is not undoable!", "", 1, 3, (Icon)null, options, options[1]);
                if (n3 != 1) {
                    final String pathToCurrentTmaFolder = this.etmasFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText();
                    final File thisTmaFolder = new File(pathToCurrentTmaFolder);
                    boolean preserveFlag = false;
                    final File[] tmaStudentList = thisTmaFolder.listFiles();
                    for (int i = 0; i < tmaStudentList.length; ++i) {
                        if (tmaStudentList[i].isDirectory()) {
                            final String studName = "";
                            final File[] subNos = tmaStudentList[i].listFiles();
                            for (int j = 0; j < subNos.length; ++j) {
                                final String submNo = subNos[j].getName();
                                if (subNos[j].isDirectory()) {
                                    final String insertFilePath = tmaStudentList[i] + "/" + submNo + "/" + distFileName;
                                    final File bFile = new File(insertFilePath);
                                    if (bFile.exists()) {
                                        final Object[] options2 = { "Delete", "Don't delete" };
                                        final JFrame frame2 = null;
                                        final int n4 = JOptionPane.showOptionDialog((Component)frame, "The file " + distFilePath + "\nexists in " + tmaStudentList[i].getName() + studName + "\nDo you want to delete it?", "", 1, 3, (Icon)null, options2, options2[1]);
                                        if (n4 == 1) {
                                            preserveFlag = true;
                                        }
                                        else {
                                            bFile.delete();
                                        }
                                    }
                                    else {
                                        bFile.delete();
                                    }
                                }
                            }
                        }
                    }
                    if (!preserveFlag) {
                        JOptionPane.showMessageDialog((Component)null, "The file " + distFilePath + " has been deleted in all students' folders.");
                    }
                    else {
                        JOptionPane.showMessageDialog((Component)null, "The file " + distFilePath + " was deleted in students' folders,\nexcept those where you decided not to delete the file.");
                    }
                }
            }
        }
        catch (final Exception anException) {
            System.out.println(anException);
            JOptionPane.showMessageDialog(null, "You can only undo a distribution immediately after you've carried it out.\nYou will have to distribute the file again, then immediately undo it.");
        }
    }
    
    private void setCommentBankEditorActionPerformed(final ActionEvent evt) {
        this.ourRoot.put("currentEdPreferences", this.currentEdPreferences);
        final JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentEdPath = JOptionPane.showInputDialog(null, "Please enter the short(launch) name of your Text Editor for the comment bank (eg'gedit')");
            }
            else {
                final Object[] options = { "Default", "Select other" };
                final JFrame frame = null;
                final int n = JOptionPane.showOptionDialog(frame, "Do you want to use the system default text editor?", "Default", 1, 3, null, options, options[0]);
                if (n == 0) {
                    this.currentEdPath = "System Default";
                    this.currentEdPreferences = "System Default";
                }
                else {
                    _fileChooser.setDialogTitle("Please select application to open comment bank: ");
                    final int path = _fileChooser.showOpenDialog(null);
                    final File aFile = _fileChooser.getSelectedFile();
                    this.currentEdPath = aFile.getPath();
                }
            }
            JOptionPane.showMessageDialog((Component)null, "Comment bank editor path set to " + this.currentEdPath);
            this.commentBankEd.setText(this.currentEdPath);
            this.ourRoot.put("currentEdPreferences", this.currentEdPath);
        }
        catch (final Exception ex) {}
    }
    
    public int askFrom2Options(final String option0, final String option1, final String message, final int defaultOption, final String title) {
        final Object[] options = { option0, option1 };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, message, title, 1, 3, null, options, options[defaultOption]);
        return n;
    }
    
    public int askFrom3Options(final String option0, final String option1, final String option2, final String message, final int defaultOption, final String title) {
        final Object[] options = { option0, option1, option2 };
        final JFrame frame = null;
        final int n = JOptionPane.showOptionDialog(frame, message, title, 1, 3, null, options, options[defaultOption]);
        return n;
    }
    
    private void tutor_comments_inputKeyPressed(final KeyEvent evt) {
    }
    
    private void sizeWarnFlagActionPerformed(final ActionEvent evt) {
        this.sizeWarnFlagPreferences = this.sizeWarnFlag.isSelected();
        this.ourRoot.putBoolean("sizeWarnFlagPreferences", this.sizeWarnFlagPreferences);
    }
    
    private void autoImportFlag1ActionPerformed(final ActionEvent evt) {
        final String df = this.downloadsFolder.getText();
        if (df.equals("")) {
            JOptionPane.showMessageDialog(null, "Auto import will only work if you set the location of\nyour browser's download folder in the Filehandler preferences\n(second button from the top).");
            this.autoImportFlag1.setSelected(false);
        }
        else {
            this.autoImportFlag = this.autoImportFlag1.isSelected();
            this.ourRoot.putBoolean("autoImportFlag", this.autoImportFlag1.isSelected());
            if (this.autoImportFlag1.isSelected()) {
                this.startImportTimer();
            }
            else {
                try {
                    this.timer2.cancel();
                }
                catch (final Exception ex) {}
            }
        }
    }
    
    private void globalFontsActionPerformed(final ActionEvent evt) {
        this.globalFontsPreferences = this.globalFonts.isSelected();
        this.ourRoot.putBoolean("globalFonts", this.globalFonts.isSelected());
        if (this.globalFontsPreferences) {
            this.setFonts(this.fontPreferences);
        }
        else {
            this.setFonts(10);
        }
    }
    
    private void undoDistributionActionPerformed(final ActionEvent evt) {
        this.undoDistribution();
    }
    
    private void checkReturnsFlagActionPerformed(final ActionEvent evt) {
        this.checkReturnsFlagPreferences = this.checkReturnsFlag.isSelected();
        this.ourRoot.putBoolean("checkReturnsFlagPreferences", this.checkReturnsFlagPreferences);
    }
    
    private void ignoreCurrentTmaActionPerformed(final ActionEvent evt) {
        this.openGradesListAlt();
    }
    
    private void comparePartScoresActionPerformed(final ActionEvent evt) {
        this.openPartScores();
    }
    
    private void cutTextActionPerformed(final ActionEvent evt) {
        String selectedText = "";
        final Component tempComp = this.getFocusOwner();
        try {
            final JTextField tempComp2 = (JTextField)tempComp;
            selectedText = tempComp2.getSelectedText();
        }
        catch (final Exception anException) {
            selectedText = this.tutor_comments_input.getSelectedText();
        }
        this.tutor_comments_input.replaceSelection("");
        final StringSelection textToCopy = new StringSelection(selectedText);
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }
    
    private void tutor_comments_inputMouseReleased(final MouseEvent evt) {
        if (this.statusChangeAgree()) {
            this.tmaScores.clearSelection();
            this.submission_status.setText("Unmarked");
            this.calculateTotals();
        }
        if (evt.isPopupTrigger()) {
            this.showPopupMenu(evt);
        }
    }
    
    private void gradesSummaryTable1MouseReleased(final MouseEvent evt) {
    }
    
    private void exportGrades1ActionPerformed(final ActionEvent evt) {
        this.exportMarksGrid(this.gradesSummaryTable1, "PartSCoreComparison", 1);
    }
    
    private void partScoresButtonActionPerformed(final ActionEvent evt) {
        this.openPartScores();
    }
    
    private void WordCountActionPerformed(final ActionEvent evt) {
        final String str = this.tutor_comments_input.getText();
        final String[] result = str.split("\\s+");
        final int wCount = result.length;
        System.out.println(wCount);
        JOptionPane.showMessageDialog((Component)null, "Approximate word count in PT3 = " + wCount + " words.");
    }
    
    private void late_submission_statusActionPerformed(final ActionEvent evt) {
    }
    
    private void showPopupMenu(final MouseEvent e) {
        this.jPopupMenu1.show(this.tutor_comments_input, e.getX(), e.getY());
    }
    
    private void jMenuItemUndoActionPerformed(final ActionEvent evt) {
        this.UndoActionPerformed(evt);
    }
    
    private void jMenuItemRedoActionPerformed(final ActionEvent evt) {
        this.RedoActionPerformed(evt);
    }
    
    private void jMenuItemCopyActionPerformed(final ActionEvent evt) {
        this.copyTextActionPerformed(evt);
    }
    
    private void jMenuItemPasteActionPerformed(final ActionEvent evt) {
        this.pasteTextActionPerformed(evt);
    }
    
    private void jMenuItemSelectAllActionPerformed(final ActionEvent evt) {
        this.selectAllTextActionPerformed(evt);
    }
    
    private void jMenuItemCutActionPerformed(final ActionEvent evt) {
        this.cutTextActionPerformed(evt);
    }
    
    private void tutor_comments_inputMousePressed(final MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            this.showPopupMenu(evt);
        }
    }
    
    private void jMenuItemVersionActionPerformed(final ActionEvent evt) {
        JOptionPane.showMessageDialog((Component)null, "This is version " + this.thisVersion, "About " + this.getTitle(), 1);
    }
    
    private void checkUpdatesActionPerformed(final ActionEvent evt) {
        this.loadVersion("http://s376541606.websitehome.co.uk/etmahandlerpage.html");
    }
    
    private void customWindowClosing(final WindowEvent evt) {
        this.custom.setVisible(false);
        this.buttonHider();
    }
    
    private void colorRemoveActionPerformed(final ActionEvent evt) {
    }
    
    private void closeCustomizeActionPerformed(final ActionEvent evt) {
        this.custom.setVisible(false);
        this.buttonHider();
    }
    
    private void hideTestJsActionPerformed(final ActionEvent evt) {
    }
    
    private void hideOpenPreferencesActionPerformed(final ActionEvent evt) {
    }
    
    public void createLog() {
        try {
            final FileHandler handler = new FileHandler("my.log");
            final Logger logger = Logger.getLogger("com.mycompany");
            logger.addHandler(handler);
        }
        catch (final IOException e) {
            System.out.println(e);
        }
    }
    
    public void createFeedback() {
        final File fhiFile = new File(this.fhiFileName.getText());
        final String tmaFolder = fhiFile.getParent();
        final String fbString = tmaFolder + "/tutorcomments.rtf";
        String rtfString = "testing" + this.lf;
        rtfString = "{\\rtf1\\ansi\\ansicpg1252\\cocoartf949\\cocoasubrtf270" + this.lf + "{\\fonttbl}" + this.lf + "{\\colortbl;\\red255\\green255\\blue255;}" + this.lf + "\\paperw11900\\paperh16840\\margl1440\\margr1440\\vieww12000\\viewh14000\\viewkind0" + this.lf;
        final File testFile = new File(fbString);
        if (testFile.exists()) {
            final JFrame frame = null;
            int n = 0;
            final Object[] options = { "Create new", "Open current", "Cancel" };
            n = JOptionPane.showOptionDialog(frame, "File exists - create new one and LOSE current comments?", "", 1, 3, null, options, options[1]);
            if (n == 0) {
                this.putFile1(fbString, rtfString);
                this.tmaOpener(fbString);
            }
            if (n == 1) {
                this.tmaOpener(fbString);
            }
        }
        else {
            this.putFile1(fbString, rtfString);
            this.tmaOpener(fbString);
        }
    }
    
    public static void main(final String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EtmaHandlerJ().setVisible(true);
            }
        });
    }
    
    public void undoCode() {
        this.tutor_comments_input.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(final UndoableEditEvent evt) {
                EtmaHandlerJ.this.undo.addEdit(evt.getEdit());
            }
        });
        this.tutor_comments_input.getActionMap().put("Undo", new AbstractAction("Undo") {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                try {
                    if (EtmaHandlerJ.this.undo.canUndo()) {
                        EtmaHandlerJ.this.undo.undo();
                    }
                }
                catch (final CannotUndoException ex) {}
            }
        });
        this.tutor_comments_input.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        this.tutor_comments_input.getActionMap().put("Redo", new AbstractAction("Redo") {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                try {
                    if (EtmaHandlerJ.this.undo.canRedo()) {
                        EtmaHandlerJ.this.undo.redo();
                    }
                }
                catch (final CannotRedoException ex) {}
            }
        });
        this.tutor_comments_input.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }
    
    static {
        EtmaHandlerJ.alertFlag = false;
        EtmaHandlerJ.wordToAdd = "";
    }
    
    class ScheduleRunner extends TimerTask
    {
        @Override
        public void run() {
            try {
                final Robot robot = new Robot();
                robot.keyPress(10);
                robot.keyRelease(10);
            }
            catch (final Exception ex) {}
        }
    }
    
    class ScheduleRunner2 extends TimerTask
    {
        @Override
        public void run() {
            try {
                final String dfPath = EtmaHandlerJ.this.downloadsFolder.getText();
                final File dfFile = new File(dfPath);
                final File[] thisFile = dfFile.listFiles();
                EtmaHandlerJ.this.pathToDownloadedFile = "";
                boolean foundIt = false;
                EtmaHandlerJ.this.foundItZip = false;
                for (int count1 = 0; count1 < thisFile.length; ++count1) {
                    String lastFour = "*&%£";
                    final int nameLength = thisFile[count1].getName().length();
                    if (nameLength > 4) {
                        lastFour = thisFile[count1].getName().substring(nameLength - 4, nameLength);
                    }
                    if (EtmaHandlerJ.this.acceptableEnds.contains(lastFour)) {
                        boolean folderLooksOk = true;
                        final File[] fileContentsList = thisFile[count1].listFiles();
                        for (int i = 0; i < fileContentsList.length; ++i) {
                            final String fName = fileContentsList[i].getName();
                            if (fName.length() != 2 && !fName.equals(".DS_Store")) {
                                folderLooksOk = false;
                            }
                        }
                        if (folderLooksOk) {
                            EtmaHandlerJ.this.pathToDownloadedFile = thisFile[count1].getPath();
                            foundIt = true;
                        }
                    }
                }
                if (foundIt) {
                    EtmaHandlerJ.this.courseDirectoryList.clear();
                    EtmaHandlerJ.this.courseDirectoryList.add(EtmaHandlerJ.this.pathToDownloadedFile);
                    EtmaHandlerJ.this.collectTmas();
                    EtmaHandlerJ.this.courseList.setSelectedItem(EtmaHandlerJ.this.courseName);
                    EtmaHandlerJ.this.foundItZip = false;
                }
                else if (!EtmaHandlerJ.this.foundItZip) {
                    for (int count2 = 0; count2 < thisFile.length; ++count2) {
                        if (thisFile[count2].getName().contains(EtmaHandlerJ.this.zipFileName)) {
                            EtmaHandlerJ.this.fileToUnzip = thisFile[count2].getPath();
                            if (EtmaHandlerJ.this.fileToUnzip.contains(".zip") && !EtmaHandlerJ.this.fileToUnzip.contains("Imported")) {
                                System.out.println(EtmaHandlerJ.this.fileToUnzip);
                                EtmaHandlerJ.this.foundItZip = true;
                                final boolean zipFlag = EtmaHandlerJ.this.unZipAlt();
                                EtmaHandlerJ.this.foundItZip = false;
                            }
                        }
                    }
                }
            }
            catch (final Exception anException) {
                System.out.println(anException);
            }
        }
    }
    
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
    {
        public MyHighlightPainter(final Color color) {
            super(color);
        }
    }
    
    public class ColumnSorter implements Comparator
    {
        int colIndex;
        boolean ascending;
        
        ColumnSorter(final int colIndex, final boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }
        
        @Override
        public int compare(final Object a, final Object b) {
            final Vector v1 = (Vector)a;
            final Vector v2 = (Vector)b;
            Object o1 = v1.get(this.colIndex);
            Object o2 = v2.get(this.colIndex);
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            if (o1 instanceof Comparable) {
                if (this.ascending) {
                    return ((Comparable)o1).compareTo(o2);
                }
                return ((Comparable)o2).compareTo(o1);
            }
            else {
                if (this.ascending) {
                    return o1.toString().compareTo(o2.toString());
                }
                return o2.toString().compareTo(o1.toString());
            }
        }
    }
    
    public class GACellEditor extends DefaultCellEditor
    {
        private Component editorComponent1;
        private JTextField tf;
        
        public GACellEditor(final JTextField t) {
            super(t);
            this.tf = null;
            this.editorComponent1 = t;
            (this.tf = (JTextField)this.editorComponent1).setFont(new Font("Lucida Grande", 0, 9));
            this.tf.setCaretPosition(this.tf.getText().length());
            this.tf.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(final KeyEvent e) {
                }
                
                @Override
                public void keyReleased(final KeyEvent e) {
                    final String currentText = GACellEditor.this.tf.getText();
                    final String currentPart = (String)EtmaHandlerJ.this.tmaScores.getValueAt(EtmaHandlerJ.this.xCoord, 1);
                    if (currentText.contains("+")) {
                        EtmaHandlerJ.this.additionField.setText("Current entry for part " + currentPart + ": " + currentText);
                    }
                    else {
                        EtmaHandlerJ.this.additionField.setText("");
                    }
                    GACellEditor.this.tf.setCaretPosition(GACellEditor.this.tf.getText().length());
                }
                
                @Override
                public void keyTyped(final KeyEvent e) {
                    System.out.println("3test3");
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
            ((JTextField)this.editorComponent1).setText(value.toString());
            table.repaint();
            this.tf.selectAll();
            return this.editorComponent1;
        }
    }
    
    public class ZipFilter extends FileFilter
    {
        @Override
        public boolean accept(final File f) {
            String extension = "";
            if (f.isDirectory()) {
                return true;
            }
            extension = f.getName();
            try {
                extension = extension.substring(extension.lastIndexOf("."));
            }
            catch (final Exception ex) {}
            return extension != null && extension.equals(".zip");
        }
        
        @Override
        public String getDescription() {
            return "Only Zipped Files";
        }
    }
    
    public class DocumentFilter extends FileFilter
    {
        @Override
        public boolean accept(final File f) {
            String extension = "";
            if (f.isDirectory()) {
                return true;
            }
            extension = f.getName();
            try {
                extension = extension.substring(extension.lastIndexOf("."));
            }
            catch (final Exception ex) {}
            boolean acceptFlag = false;
            if (extension != null) {
                for (final String accExt : EtmaHandlerJ.this.acceptableFilesSet) {
                    if (extension.equals(accExt)) {
                        acceptFlag = true;
                    }
                }
            }
            return acceptFlag;
        }
        
        @Override
        public String getDescription() {
            return "Only recommended formats";
        }
    }
    
    public class etmaPrinter implements Printable, ActionListener
    {
        public JFrame printFrame;
        public double aScaling;
        
        public etmaPrinter(final JFrame aFrame, final double scale) {
            this.printFrame = null;
            this.aScaling = 1.0;
            this.printFrame = aFrame;
            this.aScaling = scale;
        }
        
        @Override
        public int print(final Graphics g, final PageFormat pf, final int page) throws PrinterException {
            if (page > 0) {
                return 1;
            }
            final Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            g2d.scale(this.aScaling, this.aScaling);
            this.printFrame.printAll(g);
            return 0;
        }
        
        @Override
        public void actionPerformed(final ActionEvent e) {
            final PrinterJob job = PrinterJob.getPrinterJob();
            final PageFormat pf = job.defaultPage();
            pf.setOrientation(2);
            job.setPrintable(this, pf);
            final boolean ok = job.printDialog();
            if (ok) {
                try {
                    job.print();
                }
                catch (final PrinterException ex) {}
            }
        }
    }
}
