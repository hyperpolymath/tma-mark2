/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.swabunga.spell.engine.SpellDictionary
 *  com.swabunga.spell.engine.SpellDictionaryHashMap
 *  com.swabunga.spell.event.SpellCheckListener
 *  com.swabunga.spell.event.SpellChecker
 *  com.swabunga.spell.event.StringWordTokenizer
 *  com.swabunga.spell.event.WordTokenizer
 *  javax.mail.MessagingException
 *  net.roydesign.io.DocumentFile
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 */
package etmaMonitor;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.event.WordTokenizer;
import etmaMonitor.Suggest;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Robot;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.invoke.CallSite;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.roydesign.io.DocumentFile;
import org.jdesktop.layout.GroupLayout;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EtmaMonitorJ
extends JFrame {
    public String thisVersion = "2.1.1NU";
    public String latestVersion = "0.0";
    public String[] defaultRubric = new String[]{"1) Acknowledges good work", "2) Provides encouragement and support", "3) Suggests ways of improving future work", "4) Corrects and explains errors and omissions", "5) Makes clear and constructive comments", "6) Directs student to course materials and/or other relevant materials", "7) Clearly explains where marks were gained or lost", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    public String[] xmlTagsStringsTutor = new String[]{"tutor_forenames", "tutor_surname", "staff_id", "tutor_status", "locn_code", "new_tutor", "number_scripts"};
    public String[] xmlTagsStringsMonitor = new String[]{"monitoring_level_code", "previous_monitor_forms", "monitor_collect_date", "monitor_forenames", "monitor_surname", "sample_available_date", "monitor_staff_id", "monitor_comments_file", "monitor_comment_date", "monitor_return_date", "sent_to_tutor_date", "staff_tutor_comments", "staff_tutor_return_date"};
    public String[] xmlTagsStringsReducedQuestions = new String[]{"questn_part_desc"};
    public String[] xmlTagsStringsTmas = new String[]{"course_code", "assgnmt_cut_off_date", "monitoring_type", "assgnmt_suffix", "pres_code"};
    public String[] xmlTagsStringsZip = new String[]{"zip_date", "zip_filepath", "zip_filename"};
    public String[] xmlTagsStringsSystem = new String[]{"download_exe_name", "version_num", "basic_monitor_form_name", "basic_monitor_form_path"};
    public int lString = this.xmlTagsStringsTutor.length;
    public int lStringStaff = this.xmlTagsStringsMonitor.length;
    public int lStringQuestions = this.xmlTagsStringsReducedQuestions.length;
    public int lStringZip = this.xmlTagsStringsZip.length;
    public Map<String, String> tutorDetails = new HashMap<String, String>();
    public Map<String, String> zipDetails = new HashMap<String, String>();
    public Map<String, String> monitorDetails = new HashMap<String, String>();
    public Map<String, String> systemDetails = new HashMap<String, String>();
    public Map<String, String> tmaDetails = new HashMap<String, String>();
    public Map<String, String> tutorDetailsShort = new HashMap<String, String>();
    public Map<String, String> monitorDetailsShort = new HashMap<String, String>();
    public Map<String, String> studentDetailsShort = new HashMap<String, String>();
    public Map<String, String> tmaDetailsShort = new HashMap<String, String>();
    public Map<String, JTextField> fieldNames1 = new HashMap<String, JTextField>();
    public Map<String, JTextField> fieldNames2 = new HashMap<String, JTextField>();
    private List<String> questionNumbers = new ArrayList<String>();
    public List<Integer> partStarts = new ArrayList<Integer>();
    public String outString = "";
    public String markString = "";
    public int numberOFStudents = 0;
    public List<Integer> numberOfParts = new ArrayList<Integer>();
    public Preferences ourRoot = Preferences.userNodeForPackage(this.getClass());
    public List thisLine = new ArrayList();
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
    public boolean createMarkedPreferences;
    public double[] mainLocationPreferences = new double[2];
    public boolean sizePreferences = true;
    public String commentBankFilePreferences;
    public String currentWpPreferences = "";
    public String currentEdPreferences = "";
    public String currentAudioPreferences = "";
    public String currentBrowserPreferences = "";
    public static String dictionaryPathPreferences;
    public String eTmaAddressPreferences;
    public String trainingAddressPreferences;
    public String csvFilePreference = "";
    public boolean startedFlag = false;
    public boolean highlightedFlag = true;
    public int studentIndex;
    public String markedStatus;
    public List<String> questionStatusList = new ArrayList<String>();
    public List<String> studentAttributelist = new ArrayList<String>();
    public List<String> studentMarksList = new ArrayList<String>();
    public String newDownloadFolderPath = "";
    List<String> tmaNewFiles = new ArrayList<String>();
    List<String> tmaTempFiles = new ArrayList<String>();
    List<String> tmaTransFiles = new ArrayList<String>();
    List<String> tmaWeightings = new ArrayList<String>();
    List<String> tmaMaxScores = new ArrayList<String>();
    public String[] tmaNames = new String[]{"Course", "TMA00", "TMA01", "TMA02", "TMA03", "TMA04", "TMA05", "TMA06", "TMA07", "TMA08", "TMA09", "TMA10", "TMA11", "TMA12", "Pass Mark"};
    public String[] markingCategories = new String[]{"Too high", "Appropriate", "Too low", ""};
    public String[] commentingCategories = new String[]{"Meets or exceeds requirements", "Requires further exploration", ""};
    public String[] yesNoCategories = new String[]{"No", "Yes"};
    public String[] monitoringRatingCategories = new String[]{"Meets or exceeds requirements", "Requires further exploration", ""};
    public Map<String, Integer> weightingsMap = new HashMap<String, Integer>();
    public Map<String, Integer> maxScoresMap = new HashMap<String, Integer>();
    public List<Integer> weightingsList = new ArrayList<Integer>();
    public List<Integer> maxScoresList = new ArrayList<Integer>();
    public boolean sortOrder = true;
    public int[] tmaListSizes = new int[]{70, 60, 90, 65, 60, 30, 60, 60, 90, 10, 10, 20};
    public int[] monListSizes = new int[]{500, 300};
    public int[] studentListSizes = new int[]{30, 40, 40, 50, 180, 5, 40, 40, 80, 80};
    public int[] scriptListSizes = new int[]{250, 120};
    public boolean savedFlag = true;
    public int[] partScoresListSizes = new int[]{50, 70, 70, 50, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 25};
    public int toBeMarked = 0;
    public String filter = "All";
    public boolean displayFlag = true;
    public static boolean alertFlag;
    public List<Integer> redRows = new ArrayList<Integer>();
    public List<Integer> blueRows = new ArrayList<Integer>();
    public int passMark = 0;
    public int fSize = 0;
    public boolean size = true;
    public File[] pt3Files = null;
    public String[] emailRecipients = new String[3];
    protected UndoManager undo = new UndoManager();
    public Transferable clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    public int xCoord = 0;
    public int yCoord = 0;
    public boolean tooHighFlag = false;
    public static String wordToAdd;
    public static int actionFlag;
    public String overallGrade = "";
    public String currentWpPath = "System Default";
    public String currentEdPath = "System Default";
    public String currentAudioPath = "System Default";
    public String osName = "";
    public Map<String, String> wpMap = new HashMap<String, String>();
    public Map<String, String> wpMap1 = new HashMap<String, String>();
    public File attachmentFile = null;
    public boolean attFlag = false;
    public int[] smallWindowSize = new int[]{870, 520};
    public int[] largeWindowSize = new int[]{870, 740};
    public final String[] MONTHLIST = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public String currentStudentScript = "";
    public double tmaListLocX = 20.0;
    public double tmaListLocY = 20.0;
    public double tmaListWidth = 20.0;
    public double tmaListHeight = 20.0;
    public int cursorPos;
    public boolean startUp = true;
    public List<String> regionList = new ArrayList<String>();
    public boolean readErrorFlag = false;
    public boolean entryErrorFlag = false;
    public String courseName = "";
    public String colorPreferences = "219;219;219:255;255;255:184;249;196";
    public final String colorDefaultString = "219;219;219:255;255;255:184;249;196";
    public boolean[] colorDefaultsFlag = new boolean[]{true, true, true};
    public int currentColorIndex = 0;
    public final String[] LINUXFILEMANAGER = new String[]{"nautilus", "konqueror"};
    public String linuxWP = "ooffice";
    public String linuxAudio = "ooffice";
    public char char02 = (char)2;
    public String string02 = "" + this.char02;
    public char char03 = (char)3;
    public String string03 = "" + this.char03;
    public char char28 = (char)28;
    public String string28 = "" + this.char28;
    public char char29 = (char)29;
    public String string29 = "" + this.char29;
    public char wineacute = (char)233;
    public String wineacuteString = "" + this.wineacute;
    public char rtn = (char)13;
    public char lf = (char)10;
    public char leftq = (char)210;
    public String leftqString = "" + this.leftq;
    public char rightq = (char)211;
    public String rightqString = "" + this.rightq;
    public String lfString = "" + this.lf;
    public String rtnString = "" + this.rtn;
    public String courseDirectory = "";
    public Set<String> courseDirectoryList = new HashSet<String>();
    public Boolean autoImportFlag = false;
    public String unzippedFilePath = "";
    public Boolean tmaListError = false;
    public JFrame messageWindow2 = null;
    public JFrame messageWindow3 = new JFrame();
    public SpellDictionary dictionary = null;
    public SpellChecker spellChecker = null;
    public String[] acceptableFiles = new String[]{".doc", ".rtf", ".pdf", "docx", ".mp3", ".MP3"};
    public Set<String> acceptableFilesSet = new HashSet<String>();
    public String feedbackString = "";
    public String markedString = "-MARKED";
    public String markedString1 = "";
    public Set<String> allStudents = new TreeSet<String>();
    public Set<String> allTmas = new TreeSet<String>();
    public String returnsName = "/returns/";
    public String tempName = "/temp/";
    public String[] gridColor = null;
    public JMenu additionField = new JMenu("");
    public Timer timer2;
    public Set<String> acceptableEnds = new HashSet<String>();
    public String[] yearEnds = new String[]{"08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25"};
    public String[] monthTags = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
    public String pathToDownloadedFile = "";
    public boolean autoImportPreferences = false;
    public boolean globalFontsPreferences = false;
    public String zipFileName = "";
    public boolean foundItZip = false;
    public String fileToUnzip = "";
    public String desktopPath = "";
    public File distFile = null;
    public boolean checkReturnsFlagPreferences = true;
    public String parentName1 = "";
    public boolean sortPreference;
    public int sortRow;
    public String dictionaryLocation;
    public String studentFolderPathName;
    public String tutorMonitorFilePathname;
    public String folderForZippingPathname = "";
    public int currentStudentRow;
    public List<List<String>> studentFileFullList = new ArrayList<List<String>>();
    public List<List<String>> studentAnnotations = new ArrayList<List<String>>();
    public String currentStudentPID;
    public String version1 = this.getClass().getPackage().getImplementationVersion();
    public List<Map<String, String>> studentAnnotationMapList = new ArrayList<Map<String, String>>();
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.yellow);
    Highlighter.HighlightPainter blankHighlightPainter = new MyHighlightPainter(Color.white);
    private JMenu Edit;
    private JMenu File;
    private JMenuItem Redo;
    private JMenuItem Undo;
    private JButton addAttachmentButton;
    private JTextField addRecip;
    private JTextField assgnmt_cut_off_date;
    private JTextField assgnmt_suffix;
    public JTextField audioPath;
    private JCheckBox authenticationFlag;
    private JCheckBox autoFillFlag;
    private JCheckBox autoImportFlag1;
    private JCheckBox autoMp3;
    private JButton backUp;
    private JMenuItem backUpMenu;
    private JTextField basic_monitor_form_name;
    private JTextField basic_monitor_form_path;
    private JButton batchZipNew;
    private ButtonGroup buttonGroup1;
    private JCheckBox checkClosureFlag;
    private JCheckBox checkReturnsFlag;
    private JMenuItem checkSpelling;
    private JButton checkSpellingButton;
    private JMenuItem checkupdates;
    private JMenuItem chooseColor;
    private JButton closeCustomize;
    private JButton closePreferences;
    private JButton collectTmas;
    private JMenuItem collectTmasMenu;
    private JFrame colorFrame1;
    private JCheckBox colorRemove;
    private JComboBox colorWindowSelector;
    private JMenuItem copyText;
    private JComboBox courseList;
    private JTextField course_code;
    private JMenuItem createFeedback;
    private JCheckBox createMarked;
    private JFrame custom;
    private JMenuItem customize;
    private JMenuItem cutText;
    private JCheckBox defaultFlag;
    private JButton deleteAttachment;
    public JTextField dictionaryPath;
    private JCheckBox doubleClickFlag;
    private JTextField download_exe_name;
    private JTextField downloadsFolder;
    private JTextField e_tma_submission_date;
    private JMenuItem etmaHandlerHelp;
    private JTextField etmaMonitoringFolder;
    private JMenuItem exitMenuItem;
    private JTextField fhiFileName;
    private JComboBox fontSize;
    private JCheckBox globalFonts;
    private JMenu help;
    private JFrame helpFrame;
    private JScrollPane helpScroll;
    private JCheckBox hideBackupEtmas;
    private JCheckBox hideBankComment;
    private JCheckBox hideECollectTmas;
    private JCheckBox hideEtmaSite;
    private JCheckBox hideListTmas;
    private JCheckBox hideOpenPreferences;
    private JCheckBox hideOpenReturnsFolder;
    private JCheckBox hideOpenTmaFolder;
    private JCheckBox hideSavePt3;
    private JCheckBox hideSendEmail;
    private JCheckBox hideTestJs;
    private JCheckBox hideTrainingSite;
    private JCheckBox hideZipFiles;
    private JCheckBox highlightUnmarked;
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
    private JLabel jLabel38;
    private JLabel jLabel39;
    private JLabel jLabel4;
    private JLabel jLabel40;
    private JLabel jLabel41;
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
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPopupMenu jPopupMenu1;
    private JRadioButton jRadioButton1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane3;
    private JScrollPane jScrollPane4;
    private JScrollPane jScrollPane5;
    private JScrollPane jScrollPane6;
    private JScrollPane jScrollPane7;
    private JTextArea jTextArea1;
    private JTextArea jTextArea2;
    private JTextArea jTextArea3;
    private JTextField jTextField1;
    private JTextPane jTextPane1;
    private JTextField late_submission_status;
    private JCheckBox launchTmaList;
    private JTable listOfTutors;
    private JButton listStudents;
    private JMenuItem listTmasMenuItem;
    private JButton loadXMLAlt;
    private JTextField locn_code;
    private JFrame mailClient;
    private JPasswordField mailPassword;
    private JButton mailPreferences;
    private JFrame mailPreferencesFrame;
    private JTextField mailUserName;
    private JTable maxScoreTable1;
    private JTable maxScoreTable2;
    private JTable maxScoreTable3;
    private JTable maxScoreTable4;
    private JScrollPane maxScores1;
    private JScrollPane maxScores2;
    private JScrollPane maxScores3;
    private JScrollPane maxScores4;
    private JTextField messageAddresses;
    private JScrollPane messageBody;
    private JTextField messageSubject;
    private JTextArea messageText;
    private JFrame messageWindow;
    private JTextField monitor_collect_date;
    private JTextField monitor_comment_date;
    private JTextArea monitor_comments;
    private JTextField monitor_comments_file;
    private JTextField monitor_forenames;
    private JTextField monitor_return_date;
    private JTextField monitor_staff_id;
    private JTextField monitor_surname;
    private JTable monitoringRatings;
    private JButton monitoringSite;
    private JMenuItem monitoringSiteMenu;
    private JTextField monitoring_level_code;
    private JTextField monitoring_type;
    private JButton moreDetails;
    private JTextField new_tutor;
    private JTextField number_scripts;
    private JButton openPreferences;
    private JButton openReturnsFolder;
    private JMenuItem openTmaFolderMenu;
    private JMenuItem openTmaMenu;
    private JButton openTutorFolder;
    private JButton openTutorList;
    private JTextField ouEtmaAddress;
    private JTextField ouTrainingAddress;
    private JMenuItem pasteText;
    private JTextField personal_id;
    private JFrame preferences;
    private JMenuItem preferencesMenu;
    private JTextField pres_code;
    private JComboBox previousReports;
    private JTextField previous_monitor_forms;
    private JMenuItem printDoc;
    private JButton printGrades;
    private JButton printTmaList;
    private JTextField sample_available_date;
    private JButton saveAddress;
    private JButton saveMailPreferences;
    private JButton savePt3;
    private JMenuItem savePt3MenuItem;
    private JButton saveWeightings;
    private JFrame scriptTable;
    private JTable scriptsSummaryTable;
    private JButton selectAllFilesToZip;
    private JMenuItem selectAllText;
    private JButton selectDictionary;
    private JButton selectDownloadsFolder;
    private JButton sendButton;
    private JButton sendEmail;
    private JTextField sent_to_tutor_date;
    private JButton setAudioApp;
    private JButton setEtmasFolder;
    private JCheckBox showLatestFlag;
    private JMenu sites;
    private JCheckBox sizeWarnFlag;
    private JTextField smtpHost;
    private JCheckBox spellCheckFlag;
    private JFrame spellChooser;
    private JTextField staff_id;
    private JTextField staff_tutor_comments;
    private JTextField staff_tutor_return_date;
    private JCheckBox startupScreenFlag;
    private JFrame studentSummary;
    private JTable studentsListTable;
    private JComboBox subNo;
    private JFrame submittedMonitoring;
    private JCheckBox suggestFlag;
    private JComboBox tmaList;
    private JScrollPane tmaMarks;
    private JTable tmaNumbers;
    private JComboBox tmaSelectMenu;
    private JTextField toBeMarkedTmas;
    private JCheckBox toolTipFlag;
    private JTextField totalTmas;
    private JButton trainingSite;
    private JMenuItem trainingSiteMenu;
    private JComboBox tutorList;
    private JTextField tutor_comments;
    private JScrollPane tutor_comments_area;
    private JTextField tutor_forenames;
    private JTextField tutor_status;
    private JTextField tutor_surname;
    private JMenuItem versionNumber;
    private JTextField version_num;
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
    private JTextField zip_filename;
    private JTextField zip_filepath;

    public List<String> getQuestionNumbers() {
        return this.questionNumbers;
    }

    public void setQuestionNumbers(List<String> questionNumbers) {
    }

    public void findAcceptableEnds() {
        String yearCode;
        boolean foundIt = false;
        this.zipFileName = this.makeZipFileName();
        String thisYear = yearCode = this.getDateAndTime().substring(2, 4);
        String lastYear = this.adjustYear(this.getDateAndTime().substring(0, 4), -1);
        String nextYear = this.adjustYear(this.getDateAndTime().substring(0, 4), 1);
        String[] yearEndsAlt = new String[]{"", "", ""};
        yearEndsAlt[0] = lastYear;
        yearEndsAlt[1] = thisYear;
        yearEndsAlt[2] = nextYear;
        this.acceptableEnds = new HashSet<String>();
        this.acceptableEnds.add(this.zipFileName);
        this.acceptableEnds.add("1-99");
        for (int count = 0; count < yearEndsAlt.length; ++count) {
            for (int count2 = 0; count2 < this.monthTags.length; ++count2) {
                this.acceptableEnds.add("-" + yearEndsAlt[count] + this.monthTags[count2]);
            }
        }
    }

    public String makeZipFileName() {
        Object nowTime = this.getDateAndTime();
        nowTime = ((String)nowTime).substring(0, 10);
        nowTime = ((String)nowTime).replace("_", "-") + "_";
        return nowTime;
    }

    public String adjustYear(String aYear, int aNumber) {
        String previousYear = "";
        int thisYearNumber = Integer.parseInt(aYear);
        int lastYearNumber = thisYearNumber + aNumber;
        String lastYear = String.valueOf(lastYearNumber);
        return lastYear.substring(2, 4);
    }

    public void splashScreen() {
        this.messageWindow2 = new JFrame("Welcome!");
        this.messageWindow2.setAlwaysOnTop(true);
        this.messageWindow2.setSize(430, 230);
        JTextField lbl = new JTextField();
        JTextField lbl1 = new JTextField();
        JTextField lbl2 = new JTextField();
        JLabel lbl3 = new JLabel();
        Color messageColor = new Color(184, 249, 196);
        lbl.setFont(new Font("Times", 0, 28));
        lbl.setText("Monitor Handler for Mackintosh");
        lbl.setSize(430, 30);
        lbl.setLocation(0, 20);
        lbl.setVisible(true);
        lbl.setHorizontalAlignment(0);
        lbl.setBackground(messageColor);
        this.messageWindow2.add((Component)lbl, 0);
        lbl2.setFont(new Font("Times", 0, 24));
        lbl2.setText("c MJ Hay 2017");
        lbl2.setSize(430, 30);
        lbl2.setLocation(0, 60);
        lbl2.setVisible(true);
        lbl2.setHorizontalAlignment(0);
        lbl2.setBackground(messageColor);
        this.messageWindow2.add((Component)lbl2, 1);
        lbl1.setFont(new Font("Times", 1, 18));
        lbl1.setText("Please press the 'return key'to continue");
        lbl1.setSize(430, 30);
        lbl1.setLocation(0, 90);
        lbl1.setVisible(true);
        lbl1.setHorizontalAlignment(0);
        this.messageWindow2.add((Component)lbl1, 2);
        lbl3.setFont(new Font("Times", 2, 18));
        lbl3.setBackground(messageColor);
        lbl3.setText("You can disable this start-up screen in the preferences!");
        lbl3.setSize(430, 30);
        lbl3.setVisible(true);
        lbl3.setHorizontalAlignment(0);
        lbl3.setAlignmentX(200.0f);
        lbl3.setVerticalAlignment(3);
        this.messageWindow2.add((Component)lbl3, 3);
        this.messageWindow2.setLocationRelativeTo(null);
        this.messageWindow2.setVisible(true);
        JOptionPane.showMessageDialog(null, "");
        this.messageWindow2.setVisible(false);
    }

    public EtmaMonitorJ() {
        this.initComponents();
        this.additionField = new JMenu("");
        this.osName = System.getProperty("os.name");
        if (this.osName.equals("Mac OS X")) {
            this.getRootPane().putClientProperty("apple.awt.fullscreenable", true);
        }
        this.jMenuBar1.add(this.additionField);
        this.versionNumber.setText("Version info: " + this.thisVersion);
        this.makeMap();
        try {
            this.desktopPath = System.getProperty("user.home") + "/Desktop";
            this.desktopPath = this.desktopPath.replace("\\", "/");
        }
        catch (Exception e) {
            System.out.println("Exception caught =" + e.getMessage());
        }
        this.findAcceptableEnds();
        int keyModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        TableColumn zipColumn = this.listOfTutors.getColumnModel().getColumn(11);
        JCheckBox checkBox = new JCheckBox();
        zipColumn.setCellEditor(new DefaultCellEditor(checkBox));
        checkBox.updateUI();
        this.tutor_comments.setVisible(false);
        this.moreDetails.setVisible(true);
        this.osName = System.getProperty("os.name");
        if (!this.osName.equals("Mac OS X")) {
            this.smallWindowSize[0] = 810;
            this.smallWindowSize[1] = 540;
            this.largeWindowSize[0] = this.smallWindowSize[0];
        } else {
            this.exitMenuItem.setVisible(false);
            this.preferencesMenu.setVisible(false);
            this.largeWindowSize[1] = 770;
        }
        if (this.osName.contains("Windows")) {
            this.returnsName = "\\returns\\";
            this.tempName = "\\temp\\";
        }
        this.setPreferences();
        try {
            File aFile = new File(this.dictionaryPath.getText());
            this.dictionary = new SpellDictionaryHashMap(aFile);
        }
        catch (Exception aFile) {
            // empty catch block
        }
        if (!this.startupScreenFlagPreferences) {
            this.splashScreen();
        }
        for (int i = 0; i < this.tmaNames.length; ++i) {
            this.tmaNumbers.setValueAt(this.tmaNames[i], i, 0);
        }
        this.restoreHidePreferences();
        this.batchZipNew.setVisible(true);
        if (this.etmaMonitoringFolder.getText().equals("")) {
            Component frame = null;
            Object[] options = new Object[]{"Yes", "Quit program"};
            int n = JOptionPane.showOptionDialog(frame, "Before you can continue, you must select your 'etmamonitoring' folder.\nIt MUST be called 'etmamonitoring' exactly without the quotes.\nWould you like to do this now? ", "", 1, 3, null, options, options[0]);
            if (n == 1) {
                System.exit(0);
            }
            if (n == 0) {
                this.selectEtmamonitoringFolder();
            }
            JOptionPane.showMessageDialog(null, "The program will now quit. Please restart it!");
            System.exit(0);
        } else {
            File testFile = new File(this.etmaMonitoringFolder.getText());
            if (!testFile.exists()) {
                JOptionPane.showMessageDialog(null, "Your etmamonitoring folder cannot be found!\nHave you moved or deleted it?\nIt should be at " + this.etmaMonitoringFolder.getText() + "\n\nPlease re-locate it in the preferences before going any further, then quit and relaunch the Filehandler.");
                this.openPrefsWindow();
            }
        }
        this.dictionaryLocation = this.etmaMonitoringFolder.getText() + "/etmaDictionary.txt";
        this.buttonHider();
        ToolTipManager.sharedInstance().setEnabled(this.toolTipFlag.isSelected());
        String tempCourseMenuPreference = this.courseMenuPreferences;
        String tempTmaMenuPreference = this.tmaMenuPreferences;
        this.monitoringRatings.getTableHeader().setFont(new Font("SansSerif", 1, 10));
        this.studentsListTable.getTableHeader().setFont(new Font("SansSerif", 1, 10));
        this.scriptsSummaryTable.getTableHeader().setFont(new Font("SansSerif", 1, 10));
        try {
            this.setupMenus(this.etmaMonitoringFolder.getText(), this.courseList);
        }
        catch (Exception n) {
            // empty catch block
        }
        this.startedFlag = true;
        this.courseList.setSelectedItem(tempCourseMenuPreference);
        this.tmaList.setSelectedItem(tempTmaMenuPreference);
        this.subNo.setVisible(false);
        this.tutorList.setVisible(false);
        this.loadXMLAlt.setVisible(false);
        this.fhiFileName.setVisible(false);
        this.savePt3.setEnabled(false);
        this.zipFiles.setEnabled(false);
        this.personal_id.setVisible(false);
        this.staff_tutor_comments.setVisible(false);
        this.download_exe_name.setVisible(false);
        this.zip_file.setVisible(false);
        this.sent_to_tutor_date.setVisible(false);
        this.basic_monitor_form_name.setVisible(false);
        this.basic_monitor_form_path.setVisible(false);
        this.monitor_comments_file.setVisible(false);
        this.openTutorFolder.setEnabled(false);
        this.listStudents.setEnabled(false);
        this.folderForZippingPathname = this.etmaMonitoringFolder.getText() + "/returns/folderForZipping";
        this.printDoc.addActionListener(new etmaPrinter(this, 0.8));
        this.listOfTutors.setSelectionMode(2);
        this.setSizesOfMonList();
        this.loadRubric(this.etmaMonitoringFolder.getText() + "/monrubricreplacement.txt");
        this.monitor_comments.setFont(new Font("Lucida Grande", 0, this.fontPreferences));
        SecurityManager appsm = System.getSecurityManager();
        this.savePt3MenuItem.setAccelerator(KeyStroke.getKeyStroke("control S"));
        this.savePt3MenuItem.setAccelerator(KeyStroke.getKeyStroke(83, keyModifier));
        this.listTmasMenuItem.setAccelerator(KeyStroke.getKeyStroke(76, keyModifier));
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
        catch (Exception exception) {
            // empty catch block
        }
        this.mainLocationPreferences[0] = this.ourRoot.getDouble("menuPreferences1", 30.0);
        this.mainLocationPreferences[1] = this.ourRoot.getDouble("menuPreferences2", 30.0);
        this.setLocation((int)this.mainLocationPreferences[0], (int)this.mainLocationPreferences[1]);
        this.setLocation((int)this.mainLocationPreferences[0], (int)this.mainLocationPreferences[1]);
        this.size = this.ourRoot.getBoolean("size", true);
        if (this.size) {
            this.setSize((int)this.ourRoot.getDouble("menuPreferences3", 900.0), (int)this.ourRoot.getDouble("smallWindowHeight", 480.0));
        } else {
            this.setSize((int)this.ourRoot.getDouble("menuPreferences3", 900.0), (int)this.ourRoot.getDouble("largeWindowHeight", 760.0));
        }
        this.undoCode();
        this.setupWeightings();
        Transferable clipBoard1 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            String string = (String)clipBoard1.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.JavaAwtDesktop();
        this.setProgIcon();
        JTableHeader header = this.listOfTutors.getTableHeader();
        header.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                TableColumnModel tcm = EtmaMonitorJ.this.listOfTutors.getColumnModel();
                int vc = tcm.getColumnIndexAtX(e.getX());
                if (vc != 11) {
                    DefaultTableModel model = (DefaultTableModel)EtmaMonitorJ.this.listOfTutors.getModel();
                    EtmaMonitorJ.this.sortAllRowsBy(model, vc, EtmaMonitorJ.this.sortOrder);
                    EtmaMonitorJ.this.getNewTmaRedRows(EtmaMonitorJ.this.listOfTutors.getRowCount());
                    try {
                        EtmaMonitorJ.this.setSizesOfTmaList();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    EtmaMonitorJ.this.sortPreference = EtmaMonitorJ.this.sortOrder;
                    EtmaMonitorJ.this.sortRow = vc;
                    EtmaMonitorJ.this.ourRoot.putBoolean("sortPreference", EtmaMonitorJ.this.sortPreference);
                    EtmaMonitorJ.this.ourRoot.putInt("sortRow", EtmaMonitorJ.this.sortRow);
                    EtmaMonitorJ.this.sortOrder = !EtmaMonitorJ.this.sortOrder;
                } else {
                    Object[] options = new Object[]{"Select All", "Select None", "Cancel"};
                    Component frame = null;
                    int n = JOptionPane.showOptionDialog(frame, "Quick Select:", "", 1, 3, null, options, options[0]);
                    int nRow = EtmaMonitorJ.this.listOfTutors.getRowCount();
                    boolean selectAllFlag = true;
                    if (n == 1) {
                        selectAllFlag = false;
                    }
                    if (n != 2) {
                        for (int i = 0; i < nRow; ++i) {
                            if (EtmaMonitorJ.this.listOfTutors.getValueAt(i, 0).equals("")) continue;
                            EtmaMonitorJ.this.listOfTutors.setValueAt(false, i, 11);
                        }
                    }
                }
            }
        });
        if (this.launchTmaList.isSelected()) {
            this.openList();
        }
        this.submittedMonitoring.setLocation((int)this.tmaListLocX, (int)this.tmaListLocY);
        this.startUp = false;
        if (this.checkReturnsFlag.isSelected()) {
            try {
                this.returnsFolderCheckEmpty();
            }
            catch (Exception exception) {
                // empty catch block
            }
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
        int studentWindowX = this.ourRoot.getInt("studentWindowX", (int)this.studentSummary.getLocation().getX());
        int studentWindowY = this.ourRoot.getInt("studentWindowY", (int)this.studentSummary.getLocation().getY());
        if (studentWindowX == 0) {
            studentWindowX = 200;
            studentWindowY = 650;
        }
        this.studentSummary.setLocation(studentWindowX, studentWindowY);
        int scriptTableX = this.ourRoot.getInt("scriptTableX", (int)this.scriptTable.getLocation().getX());
        int scriptTableY = this.ourRoot.getInt("scriptTableY", (int)this.scriptTable.getLocation().getY());
        this.scriptTable.setLocation(scriptTableX, scriptTableY);
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
        this.showLatestFlag.setSelected(this.ourRoot.getBoolean("showLatestFlag", false));
        this.checkClosureFlag.setSelected(this.checkClosureFlagPreferences);
        this.spellCheckFlag.setSelected(this.spellCheckFlagPreferences);
        this.suggestFlag.setSelected(this.suggestFlagPreferences);
        this.toolTipFlag.setSelected(this.ourRoot.getBoolean("toolTipFlagPreferences", true));
        this.startupScreenFlag.setSelected(this.ourRoot.getBoolean("startupScreenPreferences", true));
        this.startupScreenFlagPreferences = this.osName.equals("Mac OS X") ? this.startupScreenFlag.isSelected() : true;
        this.autoImportPreferences = this.ourRoot.getBoolean("autoImportFlag", false);
        this.autoImportFlag1.setSelected(this.autoImportPreferences);
        this.wpPath.setText(this.ourRoot.get("currentWpPreferences", "System Default"));
        this.audioPath.setText(this.ourRoot.get("currentAudioPreferences", "System Default"));
        this.currentBrowserPreferences = this.ourRoot.get("currentBrowserPreferences", "");
        this.passwordPreferences = this.ourRoot.get("passwordPreferences", "");
        this.smtpServerPreferences = this.ourRoot.get("smtpServerPreferences", "");
        dictionaryPathPreferences = this.ourRoot.get("dictionaryPathPreferences", this.dictionaryLocation);
        this.dictionaryPath.setText(dictionaryPathPreferences);
        this.yourEmailAddressPreferences = this.ourRoot.get("yourEmailAddressPreferences", "");
        this.authenticationFlagPreferences = this.ourRoot.getBoolean("authenticationFlagPreferences", true);
        this.launchTmaListFlagPreferences = this.ourRoot.getBoolean("launchTmaListFlagPreferences", true);
        this.launchTmaList.setSelected(this.launchTmaListFlagPreferences);
        this.autoFillFlagPreferences = this.ourRoot.getBoolean("autoFillFlagPreferences", false);
        this.autoFillFlag.setSelected(this.ourRoot.getBoolean("autoFillFlagPreferences", false));
        this.checkReturnsFlagPreferences = this.ourRoot.getBoolean("checkReturnsFlagPreferences", true);
        this.checkReturnsFlag.setSelected(this.ourRoot.getBoolean("checkReturnsFlagPreferences", true));
        this.globalFonts.setSelected(this.ourRoot.getBoolean("globalFonts", false));
        this.openTutorFolder.setEnabled(false);
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
        this.etmaMonitoringFolder.setText(this.ourRoot.get("etmamonitoringFolder", ""));
        this.downloadsFolder.setText(this.ourRoot.get("downloadsFolder", this.desktopPath));
        this.createMarked.setSelected(this.ourRoot.getBoolean("createMarked", true));
        this.eTmaAddressPreferences = this.ourRoot.get("etmaAddress", "https://css3.open.ac.uk/etma/Monitor/");
        this.ouEtmaAddress.setText(this.ourRoot.get("etmaAddress", "https://css3.open.ac.uk/etma/Monitor/"));
        this.ouTrainingAddress.setText(this.ourRoot.get("trainingAddress", "https://etma-training.open.ac.uk/eTMA/monitor/etmaM_collect.asp"));
        this.trainingAddressPreferences = this.ourRoot.get("trainingAddress", "https://etma-training.open.ac.uk/eTMA/monitor/etmaM_collect.asp");
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
        this.colorPreferences = this.ourRoot.get("colorPreferences", "255;255;255:255;255;255:184;249;196");
        this.setColors();
        this.autoMp3Preferences = this.ourRoot.getBoolean("autoMp3", false);
        this.autoMp3.setSelected(this.autoMp3Preferences);
        this.colorRemove.setSelected(this.ourRoot.getBoolean("colorRemovePreferences", true));
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
        } else {
            this.setSize(this.largeWindowSize[0], this.largeWindowSize[1]);
            this.moreDetails.setText("Fewer details");
            this.moreDetails.setToolTipText("Shows the smaller Handler window");
        }
        this.sendEmail.setVisible(false);
    }

    public boolean checkForDigit(String aString) {
        boolean thereIsADigit = false;
        int strLen = aString.length();
        for (int i = 0; i < strLen; ++i) {
            Character thisChar = Character.valueOf(aString.charAt(i));
            if (!Character.isDigit(thisChar.charValue())) continue;
            thereIsADigit = true;
        }
        return thereIsADigit;
    }

    public String getClipBoard() {
        String result = "";
        Transferable clipBoard1 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            result = (String)clipBoard1.getTransferData(DataFlavor.stringFlavor);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return result;
    }

    public void highlight(JTextArea textComp, String pattern) {
        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                hilite.addHighlight(pos, pos + pattern.length() - 1, this.myHighlightPainter);
                pos += pattern.length();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void removeSingleHighlight(JTextArea textComp, String pattern) {
        try {
            Highlighter hilite = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                hilite.addHighlight(pos, pos + pattern.length(), this.blankHighlightPainter);
                pos += pattern.length();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void removeHighlights(JTextArea textComp) {
        Highlighter hilite = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        for (int i = 0; i < hilites.length; ++i) {
            if (!(hilites[i].getPainter() instanceof MyHighlightPainter)) continue;
            hilite.removeHighlight(hilites[i]);
        }
    }

    public void spellReplace() {
        String newWord = "";
        List<Object> suggestions = new ArrayList();
        String wrongWord = "";
        wrongWord = Suggest.SuggestionListener.wrongWord;
        Suggest.SuggestionListener.wrongWord = "";
        if (!wrongWord.equals("")) {
            Toolkit.getDefaultToolkit().beep();
            String newText = "";
            int thisOption = 0;
            boolean suggFlag = false;
            ButtonGroup bgroup = new ButtonGroup();
            JPanel radioPanel = new JPanel();
            suggestions = Suggest.SuggestionListener.spellOutputList;
            try {
                int wwBegin = this.monitor_comments.getText().indexOf(wrongWord);
                int wwEnd = wwBegin + wrongWord.length();
                this.monitor_comments.select(wwBegin, wwEnd);
            }
            catch (Exception anException) {
                System.out.println("Error " + anException);
            }
            if (suggestions.size() > 0) {
                suggFlag = true;
            }
            if (suggFlag) {
                JRadioButton[] thisButton = new JRadioButton[suggestions.size()];
                radioPanel.setLayout(new GridLayout(3, 1));
                for (int i = 0; i < suggestions.size(); ++i) {
                    thisButton[i] = new JRadioButton((String)suggestions.get(i), false);
                    bgroup.add(thisButton[i]);
                    radioPanel.add(thisButton[i]);
                }
                radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Please select replacement spelling:"));
                Component frame = null;
                Object[] options = new Object[]{"Ignore", "Add to dictionary", "Replace", "Type in replacement"};
                if (thisButton.length >= 1) {
                    thisButton[0].setSelected(true);
                }
                thisOption = JOptionPane.showOptionDialog(frame, radioPanel, "Spelling Report: " + wrongWord, 1, 3, null, options, options[0]);
                for (int i = 0; i < thisButton.length; ++i) {
                    if (!thisButton[i].isSelected()) continue;
                    newWord = (String)suggestions.get(i);
                }
                if (thisOption == 2) {
                    String aPeriod = ".";
                    for (int i = 0; i < thisButton.length; ++i) {
                        if (!thisButton[i].isSelected()) continue;
                        newWord = (String)suggestions.get(i);
                        if (this.checkCase(wrongWord)) {
                            newWord = this.changeCase(newWord);
                        }
                        newText = this.monitor_comments.getText().replaceAll(" " + wrongWord, " " + newWord);
                        newText = this.fixPunctuation(newText, wrongWord, newWord);
                    }
                    if (!newWord.equals("")) {
                        this.monitor_comments.setText(newText);
                    } else {
                        JOptionPane.showMessageDialog(null, "You haven't selected a replacement!");
                    }
                }
                if (thisOption == 1) {
                    Suggest.SuggestionListener.addToDictionary(wrongWord);
                }
                if (thisOption == 3) {
                    newWord = JOptionPane.showInputDialog("Please type in the amended word");
                    if (!newWord.equals("")) {
                        newText = this.monitor_comments.getText().replaceAll(" " + wrongWord, " " + newWord);
                        newText = this.fixPunctuation(newText, wrongWord, newWord);
                        this.monitor_comments.setText(newText);
                    } else {
                        JOptionPane.showMessageDialog(null, "You haven't typed anything!", "", 2);
                    }
                }
            } else if (Suggest.SuggestionListener.checkFlag) {
                Suggest.SuggestionListener.checkFlag = false;
                Component frame = null;
                radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "No suggestions"));
                Object[] options = new Object[]{"Ignore", "Add to dictionary", "Change"};
                thisOption = JOptionPane.showOptionDialog(frame, radioPanel, "Spelling Report: " + wrongWord, 1, 3, null, options, options[0]);
                if (thisOption == 1) {
                    Suggest.SuggestionListener.addToDictionary(wrongWord);
                }
                if (thisOption == 2) {
                    newWord = JOptionPane.showInputDialog("Please type in the amended word");
                    if (!newWord.equals("")) {
                        newText = this.monitor_comments.getText().replaceAll(" " + wrongWord, " " + newWord);
                        newText = this.fixPunctuation(newText, wrongWord, newWord);
                        this.monitor_comments.setText(newText);
                    } else {
                        JOptionPane.showMessageDialog(null, "You haven't typed anything!", "", 2);
                    }
                }
            }
            this.monitor_comments.setCaretPosition(Math.max(0, this.cursorPos + newWord.length() - wrongWord.length()));
            Suggest.SuggestionListener.spellOutputList.clear();
            Suggest.SuggestionListener.wrongWord = "";
        }
    }

    public boolean checkCase(String aWord) {
        boolean isCapital = false;
        int ascii = aWord.codePointAt(0);
        if (ascii > 64 && ascii < 97) {
            isCapital = true;
        }
        return isCapital;
    }

    public String changeCase(String aWord) {
        int ascii;
        Object upperCase = "";
        int newAscii = ascii = aWord.codePointAt(0);
        if (ascii > 96) {
            newAscii = ascii - 32;
        }
        char firstCharacter = (char)newAscii;
        upperCase = firstCharacter + aWord.substring(1);
        return upperCase;
    }

    public String fixPunctuation(String aComment, String aWord, String bWord) {
        int wordLength;
        int returnChar = 13;
        char linefeedChar = '\n';
        aComment = aComment.replace(" " + aWord + ",", " " + bWord + ",");
        aComment = aComment.replace(" " + aWord + ";", " " + bWord + ";");
        aComment = aComment.replace(" " + aWord + ":", " " + bWord + ":");
        aComment = aComment.replace(" " + aWord + "?", " " + bWord + "?");
        aComment = aComment.replace(" " + aWord + ".", " " + bWord + ".");
        aComment = aComment.replace(" " + aWord + "!", " " + bWord + "!");
        aComment = aComment.replace(" " + aWord + linefeedChar, " " + bWord + linefeedChar);
        String existingFirstWord = (aComment = aComment.replace(linefeedChar + aWord + " ", linefeedChar + bWord + " ")).substring(0, wordLength = aWord.length());
        if (existingFirstWord.equals(aWord)) {
            aComment = aComment.replaceFirst(aWord, bWord);
        }
        return aComment;
    }

    public void spellHighlight(String wrongWord) {
        ArrayList suggestions = new ArrayList();
        this.highlight(this.monitor_comments, " " + wrongWord + " ");
    }

    public void wordReplace(String aWord, String bWord, JPanel aPanel, JRadioButton[] aButton, List<String> aList) {
        while (aWord.equals("")) {
            Component frame = null;
            Object[] options = new Object[]{"Replace", "Add to dictionary", "Cancel"};
            int n = JOptionPane.showOptionDialog(frame, aPanel, "Spelling Report: " + bWord, 1, 3, null, options, options[0]);
            if (n == 0) {
                for (int i = 0; i < aButton.length; ++i) {
                    if (!aButton[i].isSelected()) continue;
                    aWord = aList.get(i);
                    String newText = this.monitor_comments.getText().replaceAll(bWord, aWord);
                    this.monitor_comments.setText(newText);
                }
            }
            if (n == 1) {
                Suggest.SuggestionListener.addToDictionary(bWord);
                aWord = "*";
            }
            if (n != 2) continue;
            aWord = "*";
        }
    }

    public void spellCheckComments() {
        File aFile = new File(this.dictionaryPath.getText());
        String line = "";
        String textToCheck = this.monitor_comments.getText();
        textToCheck = textToCheck.replace(",", "");
        String[] linesToCheck = textToCheck.split(" ");
        try {
            this.dictionary = new SpellDictionaryHashMap(aFile);
            this.spellChecker = new SpellChecker(this.dictionary);
            this.spellChecker.addSpellCheckListener((SpellCheckListener)new Suggest.SuggestionListener());
            for (int i = 0; i < linesToCheck.length; ++i) {
                line = linesToCheck[i];
                this.spellChecker.checkSpelling((WordTokenizer)new StringWordTokenizer(line));
                this.spellReplace();
            }
            JOptionPane.showMessageDialog(null, "Spellcheck complete!");
        }
        catch (Exception anException) {
            System.out.println("Error107 " + anException);
            JOptionPane.showMessageDialog(null, "Please choose a dictionary, using the Preferences menu!");
        }
        this.highlightAllErrors();
    }

    public void JavaAwtDesktop() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.setAboutHandler(e -> JOptionPane.showMessageDialog(null, "This is version " + this.thisVersion, "About " + this.getTitle(), 1));
            desktop.setPreferencesHandler(e -> this.openPrefsWindow());
            desktop.setQuitHandler((e, response) -> {
                this.exitRoutine();
                response.cancelQuit();
            });
            Taskbar taskbar = Taskbar.getTaskbar();
            BufferedImage image = ImageIO.read(this.getClass().getResource("/res/etmamonitor.jpeg"));
            taskbar.setIconImage(image);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setProgIcon() {
        try {
            this.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/res/etmamonitor.jpeg")));
            this.preferences.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmamonitor.jpeg")).getImage());
            this.submittedMonitoring.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmamonitor.jpeg")).getImage());
            this.studentSummary.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmamonitor.jpeg")).getImage());
            this.colorFrame1.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmamonitor.jpeg")).getImage());
            this.custom.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmamonitor.jpeg")).getImage());
            this.helpFrame.setIconImage(new ImageIcon(this.getClass().getResource("/res/etmamonitor.jpeg")).getImage());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void saveLocation() {
        this.ourRoot.putDouble("menuPreferences1", this.getLocation().getX());
        this.ourRoot.putDouble("menuPreferences2", this.getLocation().getY());
        this.ourRoot.putDouble("tmaListLocX", this.submittedMonitoring.getLocation().getX());
        this.ourRoot.putDouble("tmaListLocY", this.submittedMonitoring.getLocation().getY());
        this.tmaListLocX = this.submittedMonitoring.getLocation().getX();
        this.tmaListLocY = this.submittedMonitoring.getLocation().getY();
        this.ourRoot.putDouble("menuPreferences3", this.getSize().getWidth());
        this.ourRoot.putDouble("menuPreferences4", this.getSize().getHeight());
        if (this.size) {
            this.smallWindowSize[1] = (int)this.getSize().getHeight();
            this.ourRoot.putDouble("smallWindowHeight", this.smallWindowSize[1]);
        } else {
            this.largeWindowSize[1] = (int)this.getSize().getHeight();
            this.ourRoot.putDouble("largeWindowHeight", this.largeWindowSize[1]);
        }
        this.ourRoot.putInt("studentWindowX", (int)this.studentSummary.getLocation().getX());
        this.ourRoot.putInt("studentWindowY", (int)this.studentSummary.getLocation().getY());
        this.ourRoot.putInt("scriptTableX", (int)this.scriptTable.getLocation().getX());
        this.ourRoot.putInt("scriptTableY", (int)this.scriptTable.getLocation().getY());
    }

    /*
     * Opcode count of 13946 triggered aggressive code reduction.  Override with --aggressivesizethreshold.
     */
    private void initComponents() {
        this.buttonGroup1 = new ButtonGroup();
        this.preferences = new JFrame();
        this.closePreferences = new JButton();
        this.setEtmasFolder = new JButton();
        this.etmaMonitoringFolder = new JTextField();
        this.selectDownloadsFolder = new JButton();
        this.downloadsFolder = new JTextField();
        this.createMarked = new JCheckBox();
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
        this.checkClosureFlag = new JCheckBox();
        this.launchTmaList = new JCheckBox();
        this.doubleClickFlag = new JCheckBox();
        this.showLatestFlag = new JCheckBox();
        this.startupScreenFlag = new JCheckBox();
        this.suggestFlag = new JCheckBox();
        this.setAudioApp = new JButton();
        this.audioPath = new JTextField();
        this.autoMp3 = new JCheckBox();
        this.sizeWarnFlag = new JCheckBox();
        this.autoImportFlag1 = new JCheckBox();
        this.globalFonts = new JCheckBox();
        this.checkReturnsFlag = new JCheckBox();
        this.jLabel37 = new JLabel();
        this.jLabel41 = new JLabel();
        this.ouTrainingAddress = new JTextField();
        this.submittedMonitoring = new JFrame();
        this.jScrollPane1 = new JScrollPane();
        this.listOfTutors = new JTable();
        this.totalTmas = new JTextField();
        this.toBeMarkedTmas = new JTextField();
        this.jLabel20 = new JLabel();
        this.jLabel21 = new JLabel();
        this.tmaSelectMenu = new JComboBox();
        this.printTmaList = new JButton();
        this.jLabel34 = new JLabel();
        this.highlightUnmarked = new JCheckBox();
        this.batchZipNew = new JButton();
        this.selectAllFilesToZip = new JButton();
        this.studentSummary = new JFrame();
        this.jScrollPane3 = new JScrollPane();
        this.studentsListTable = new JTable();
        this.printGrades = new JButton();
        this.jLabel33 = new JLabel();
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
        this.hideBankComment = new JCheckBox();
        this.hideZipFiles = new JCheckBox();
        this.hideBackupEtmas = new JCheckBox();
        this.hideSendEmail = new JCheckBox();
        this.hideOpenPreferences = new JCheckBox();
        this.hideTestJs = new JCheckBox();
        this.closeCustomize = new JButton();
        this.colorRemove = new JCheckBox();
        this.hideOpenReturnsFolder = new JCheckBox();
        this.jLabel36 = new JLabel();
        this.colorFrame1 = new JFrame();
        this.jColorChooser1 = new JColorChooser();
        this.colorWindowSelector = new JComboBox();
        this.defaultFlag = new JCheckBox();
        this.scriptTable = new JFrame();
        this.jScrollPane5 = new JScrollPane();
        this.scriptsSummaryTable = new JTable();
        this.jTextField1 = new JTextField();
        this.jPopupMenu1 = new JPopupMenu();
        this.jMenuItemUndo = new JMenuItem();
        this.jMenuItemRedo = new JMenuItem();
        this.jMenuItemCopy = new JMenuItem();
        this.jMenuItemPaste = new JMenuItem();
        this.jMenuItemSelectAll = new JMenuItem();
        this.jMenuItemCut = new JMenuItem();
        this.tutor_comments_area = new JScrollPane();
        this.monitor_comments = new JTextArea();
        this.fhiFileName = new JTextField();
        this.zipFiles = new JButton();
        this.tmaMarks = new JScrollPane();
        this.monitoringRatings = new JTable();
        this.staff_id = new JTextField();
        this.personal_id = new JTextField();
        this.monitor_comment_date = new JTextField();
        this.staff_tutor_comments = new JTextField();
        this.tutor_forenames = new JTextField();
        this.tutor_surname = new JTextField();
        this.monitor_comments_file = new JTextField();
        this.download_exe_name = new JTextField();
        this.staff_tutor_return_date = new JTextField();
        this.basic_monitor_form_name = new JTextField();
        this.basic_monitor_form_path = new JTextField();
        this.monitoring_level_code = new JTextField();
        this.monitor_return_date = new JTextField();
        this.assgnmt_cut_off_date = new JTextField();
        this.loadXMLAlt = new JButton();
        this.sent_to_tutor_date = new JTextField();
        this.monitor_staff_id = new JTextField();
        this.monitor_forenames = new JTextField();
        this.number_scripts = new JTextField();
        this.monitor_surname = new JTextField();
        this.locn_code = new JTextField();
        this.course_code = new JTextField();
        this.version_num = new JTextField();
        this.pres_code = new JTextField();
        this.assgnmt_suffix = new JTextField();
        this.new_tutor = new JTextField();
        this.zip_file = new JTextField();
        this.zip_filepath = new JTextField();
        this.zip_filename = new JTextField();
        this.zip_date = new JTextField();
        this.late_submission_status = new JTextField();
        this.tutor_status = new JTextField();
        this.monitor_collect_date = new JTextField();
        this.sample_available_date = new JTextField();
        this.e_tma_submission_date = new JTextField();
        this.monitoring_type = new JTextField();
        this.previous_monitor_forms = new JTextField();
        this.tutor_comments = new JTextField();
        this.savePt3 = new JButton();
        this.openPreferences = new JButton();
        this.tmaList = new JComboBox();
        this.courseList = new JComboBox();
        this.tutorList = new JComboBox();
        this.openTutorList = new JButton();
        this.subNo = new JComboBox();
        this.collectTmas = new JButton();
        this.openTutorFolder = new JButton();
        this.monitoringSite = new JButton();
        this.trainingSite = new JButton();
        this.listStudents = new JButton();
        this.openReturnsFolder = new JButton();
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
        this.fontSize = new JComboBox();
        this.moreDetails = new JButton();
        this.jLabel22 = new JLabel();
        this.previousReports = new JComboBox();
        this.sendEmail = new JButton();
        this.checkSpellingButton = new JButton();
        this.backUp = new JButton();
        this.jLabel30 = new JLabel();
        this.jLabel31 = new JLabel();
        this.jLabel32 = new JLabel();
        this.jPanel2 = new JPanel();
        this.jLabel38 = new JLabel();
        this.jLabel39 = new JLabel();
        this.jLabel1 = new JLabel();
        this.jLabel40 = new JLabel();
        this.jMenuBar1 = new JMenuBar();
        this.File = new JMenu();
        this.preferencesMenu = new JMenuItem();
        this.savePt3MenuItem = new JMenuItem();
        this.collectTmasMenu = new JMenuItem();
        this.listTmasMenuItem = new JMenuItem();
        this.openTmaMenu = new JMenuItem();
        this.openTmaFolderMenu = new JMenuItem();
        JMenuItem openReturnsFolderMenu = new JMenuItem();
        this.printDoc = new JMenuItem();
        this.zipFilesMenu = new JMenuItem();
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
        this.checkSpelling = new JMenuItem();
        this.backUpMenu = new JMenuItem();
        this.chooseColor = new JMenuItem();
        this.customize = new JMenuItem();
        this.checkupdates = new JMenuItem();
        this.sites = new JMenu();
        this.monitoringSiteMenu = new JMenuItem();
        this.trainingSiteMenu = new JMenuItem();
        this.help = new JMenu();
        this.etmaHandlerHelp = new JMenuItem();
        this.versionNumber = new JMenuItem();
        this.preferences.setTitle("Preferences");
        this.preferences.setName("Preferences");
        this.closePreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.closePreferences.setText("Close preferences");
        this.closePreferences.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.closePreferencesActionPerformed(evt);
            }
        });
        this.setEtmasFolder.setBackground(new Color(255, 0, 0));
        this.setEtmasFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.setEtmasFolder.setText("Select  etmamonitoring folder");
        this.setEtmasFolder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.setEtmasFolderActionPerformed(evt);
            }
        });
        this.etmaMonitoringFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.etmaMonitoringFolder.setText("etmamonitoring Folder");
        this.selectDownloadsFolder.setBackground(new Color(0, 153, 0));
        this.selectDownloadsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.selectDownloadsFolder.setText("Select Downloads Folder");
        this.selectDownloadsFolder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.selectDownloadsFolderActionPerformed(evt);
            }
        });
        this.downloadsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.downloadsFolder.setText("downloads Folder");
        this.createMarked.setBackground(new Color(0, 153, 0));
        this.createMarked.setFont(new Font("Lucida Grande", 0, 10));
        this.createMarked.setSelected(true);
        this.createMarked.setText("Not used");
        this.createMarked.setToolTipText("");
        this.createMarked.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.createMarkedMouseReleased(evt);
            }
        });
        this.ouEtmaAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.ouEtmaAddress.setText("jTextField1");
        this.jLabel5.setFont(new Font("Lucida Grande", 0, 8));
        this.jLabel5.setForeground(new Color(255, 0, 0));
        this.jLabel5.setText("Monitoring site:default:http://css3.open.ac.uk/etma/monitor/");
        this.saveAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.saveAddress.setText("Save");
        this.saveAddress.setToolTipText("You would only need to use this button if the OU changes the address of the etma site on Tutor Home.");
        this.saveAddress.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.saveAddressActionPerformed(evt);
            }
        });
        this.autoFillFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.autoFillFlag.setText("Autofill marks");
        this.autoFillFlag.setToolTipText("");
        this.autoFillFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.autoFillFlagActionPerformed(evt);
            }
        });
        this.toolTipFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.toolTipFlag.setText("Turn on Tooltips");
        this.toolTipFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.toolTipFlagActionPerformed(evt);
            }
        });
        this.spellCheckFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.spellCheckFlag.setSelected(true);
        this.spellCheckFlag.setText("Activate live spellcheck");
        this.spellCheckFlag.setToolTipText("<html>If this is ticked, the comments box will be checked for spelling errors as you type. <br>You must select a valid dictionary using 'Select spellcheck dictionary' above.</html>");
        this.spellCheckFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.spellCheckFlagActionPerformed(evt);
            }
        });
        this.selectDictionary.setBackground(new Color(102, 102, 255));
        this.selectDictionary.setFont(new Font("Lucida Grande", 0, 10));
        this.selectDictionary.setText("Select spellcheck dictionary");
        this.selectDictionary.setToolTipText("<html>You must select a valid dictionary<br> (enclosed with the package) <br>if you want to use the spellchecker.</html>");
        this.selectDictionary.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.selectDictionaryActionPerformed(evt);
            }
        });
        this.dictionaryPath.setEditable(false);
        this.dictionaryPath.setFont(new Font("Lucida Grande", 0, 10));
        this.dictionaryPath.setText("dictionaryPath");
        this.wpSelect.setBackground(new Color(0, 153, 0));
        this.wpSelect.setFont(new Font("Lucida Grande", 0, 10));
        this.wpSelect.setText("Select Word Processor");
        this.wpSelect.setToolTipText("<html>Select the word processor you want to use<br> to read and annotate students' marked scripts,<br> or select 'System Default'</html>");
        this.wpSelect.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.wpSelectActionPerformed(evt);
            }
        });
        this.wpPath.setEditable(false);
        this.wpPath.setFont(new Font("Lucida Grande", 0, 10));
        this.wpPath.setText("wpPath");
        this.checkClosureFlag.setBackground(new Color(255, 102, 0));
        this.checkClosureFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.checkClosureFlag.setText("Check for open scripts before zipping ");
        this.checkClosureFlag.setToolTipText("<html>If this is ticked, a check will be carried out before zipping files<br> to see if there are any open scripts which might not <br>have been saved. It takes a few moments.</html>");
        this.checkClosureFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.checkClosureFlagActionPerformed(evt);
            }
        });
        this.launchTmaList.setBackground(new Color(0, 153, 0));
        this.launchTmaList.setFont(new Font("Lucida Grande", 0, 10));
        this.launchTmaList.setSelected(true);
        this.launchTmaList.setText("Open Tutor List on launch");
        this.launchTmaList.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.launchTmaListActionPerformed(evt);
            }
        });
        this.doubleClickFlag.setBackground(new Color(0, 153, 0));
        this.doubleClickFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.doubleClickFlag.setSelected(true);
        this.doubleClickFlag.setText("Double-click to open Tutor's File");
        this.doubleClickFlag.setToolTipText("<html>If you prefer to <b>double-click</b> the list of tutors<br> to open their momitoring, tick this box. <br>The default is single-click.</html>");
        this.doubleClickFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.doubleClickFlagActionPerformed(evt);
            }
        });
        this.showLatestFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.showLatestFlag.setText("Show only latest submissions");
        this.showLatestFlag.setToolTipText("If ticked, only the latest submission from a student for the selected TMA will be shown.");
        this.showLatestFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.showLatestFlagActionPerformed(evt);
            }
        });
        this.startupScreenFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.startupScreenFlag.setText("Disable startup screen");
        this.startupScreenFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.startupScreenFlagActionPerformed(evt);
            }
        });
        this.suggestFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.suggestFlag.setSelected(true);
        this.suggestFlag.setText("Suggest corrections");
        this.suggestFlag.setToolTipText("<html>Tick this box if you want the live spellcheck to<br> suggest corrections for misspellings as you type.</html>");
        this.suggestFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.suggestFlagActionPerformed(evt);
            }
        });
        this.setAudioApp.setBackground(new Color(255, 255, 255));
        this.setAudioApp.setFont(new Font("Lucida Grande", 0, 10));
        this.setAudioApp.setText("Set Audio App");
        this.setAudioApp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.setAudioAppActionPerformed(evt);
            }
        });
        this.audioPath.setEditable(false);
        this.audioPath.setFont(new Font("Lucida Grande", 0, 10));
        this.audioPath.setText("wpPath");
        this.autoMp3.setFont(new Font("Lucida Grande", 0, 10));
        this.autoMp3.setSelected(true);
        this.autoMp3.setText("Auto open MP3");
        this.autoMp3.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.autoMp3ActionPerformed(evt);
            }
        });
        this.sizeWarnFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.sizeWarnFlag.setSelected(true);
        this.sizeWarnFlag.setText("Warn if  folder is large");
        this.sizeWarnFlag.setToolTipText("<html>Tick this box if you want the program to warn you<br> if the tutor's folder is over about 10MB</html>");
        this.sizeWarnFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.sizeWarnFlagActionPerformed(evt);
            }
        });
        this.autoImportFlag1.setFont(new Font("Lucida Grande", 0, 10));
        this.autoImportFlag1.setText("AutoImport");
        this.autoImportFlag1.setToolTipText("<html>If ticked, downloaded eTMA folder will be imported without further intervention.<br> Browser must be set to autounzip, and the downloads folder\n must<br> be set in the filehandler preferences (second top button)<br> to your browser's download location.</html>");
        this.autoImportFlag1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.autoImportFlag1ActionPerformed(evt);
            }
        });
        this.globalFonts.setFont(new Font("Lucida Grande", 0, 10));
        this.globalFonts.setText("Global fontsize");
        this.globalFonts.setToolTipText("<html>If ticked, changes to the font size will affect both<br> the comments box and a number of other components.</html>");
        this.globalFonts.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.globalFontsActionPerformed(evt);
            }
        });
        this.checkReturnsFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.checkReturnsFlag.setText("Check returns folder for old zipped files on launch");
        this.checkReturnsFlag.setToolTipText("<html>If ticked, the returns folder will be checked for zipped files older than one day,<br> and you will be given the option to delete them.</html>");
        this.checkReturnsFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.checkReturnsFlagActionPerformed(evt);
            }
        });
        this.jLabel41.setFont(new Font("Lucida Grande", 0, 8));
        this.jLabel41.setForeground(new Color(255, 0, 0));
        this.jLabel41.setText("https://etma-training.open.ac.uk/eTMA/monitor/etmaM_collect.asp");
        this.ouTrainingAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.ouTrainingAddress.setText("jTextField1");
        GroupLayout preferencesLayout = new GroupLayout(this.preferences.getContentPane());
        this.preferences.getContentPane().setLayout((LayoutManager)preferencesLayout);
        preferencesLayout.setHorizontalGroup((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add(2, (GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.setAudioApp, -1, -1, Short.MAX_VALUE).add(18, 18, 18).add((Component)this.audioPath, -2, 162, -2).addPreferredGap(1).add((Component)this.autoMp3).add(82, 82, 82)).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(6, 6, 6).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((GroupLayout.Group)preferencesLayout.createParallelGroup(2, false).add(1, (Component)this.selectDictionary, -1, -1, Short.MAX_VALUE).add(1, (Component)this.selectDownloadsFolder, -1, -1, Short.MAX_VALUE).add(1, (Component)this.setEtmasFolder, -1, -1, Short.MAX_VALUE).add(1, (Component)this.wpSelect, -2, 189, -2)).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(15, 15, 15).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1, false).add((Component)this.etmaMonitoringFolder).add((Component)this.downloadsFolder, -2, 304, -2))).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().addPreferredGap(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((Component)this.wpPath, -2, 304, -2).add((Component)this.dictionaryPath, -2, 304, -2))))).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.checkReturnsFlag).add(29, 29, 29).add((Component)this.startupScreenFlag)).add((Component)this.ouEtmaAddress, -2, 216, -2).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(2).add(1, (Component)this.autoFillFlag).add(1, (Component)this.spellCheckFlag).add((Component)this.suggestFlag)).add((Component)this.toolTipFlag).add((Component)this.showLatestFlag)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.globalFonts).add(64, 64, 64).add((Component)this.closePreferences)).add((Component)this.checkClosureFlag).add((Component)this.sizeWarnFlag).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((Component)this.launchTmaList).add((Component)this.autoImportFlag1)).add(33, 33, 33).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((Component)this.createMarked).add((Component)this.doubleClickFlag))))).add((GroupLayout.Group)preferencesLayout.createParallelGroup(2).add((Component)this.saveAddress).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add((Component)this.jLabel5).add(100, 100, 100).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((Component)this.ouTrainingAddress, -2, 216, -2).add((Component)this.jLabel41))))))).add(32, 32, 32).add((Component)this.jLabel37).addContainerGap(793, Short.MAX_VALUE)));
        preferencesLayout.setVerticalGroup((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createSequentialGroup().add(377, 377, 377).add((Component)this.jLabel37).addContainerGap(-1, Short.MAX_VALUE)).add(2, (GroupLayout.Group)preferencesLayout.createSequentialGroup().addContainerGap(-1, Short.MAX_VALUE).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.etmaMonitoringFolder, -2, -1, -2).add((Component)this.setEtmasFolder)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.downloadsFolder, -2, -1, -2).add((Component)this.selectDownloadsFolder)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.selectDictionary).add((Component)this.dictionaryPath, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.wpSelect).add((Component)this.wpPath, -2, -1, -2)).addPreferredGap(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.audioPath, -2, -1, -2).add((Component)this.autoMp3)).add((Component)this.setAudioApp)).add(4, 4, 4).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.jLabel5).add((Component)this.jLabel41)).add(10, 10, 10).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.ouEtmaAddress, -2, -1, -2).add((Component)this.ouTrainingAddress, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.checkReturnsFlag).add((Component)this.startupScreenFlag).add((Component)this.saveAddress)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.checkClosureFlag).add((Component)this.toolTipFlag)).add(8, 8, 8).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.autoFillFlag).add((Component)this.doubleClickFlag).add((Component)this.launchTmaList)).add(8, 8, 8).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.showLatestFlag).add((Component)this.autoImportFlag1).add((Component)this.createMarked)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.spellCheckFlag).add((Component)this.sizeWarnFlag)).addPreferredGap(0).add((GroupLayout.Group)preferencesLayout.createParallelGroup(1).add((GroupLayout.Group)preferencesLayout.createParallelGroup(3).add((Component)this.closePreferences).add((Component)this.globalFonts)).add((Component)this.suggestFlag)).addContainerGap()));
        this.submittedMonitoring.setTitle("Current Monitoring");
        this.submittedMonitoring.setFocusTraversalPolicyProvider(true);
        this.submittedMonitoring.setSize(new Dimension(800, 200));
        this.submittedMonitoring.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent evt) {
                EtmaMonitorJ.this.submittedMonitoringWindowClosing(evt);
            }
        });
        this.jScrollPane1.setHorizontalScrollBarPolicy(32);
        this.jScrollPane1.setVerticalScrollBarPolicy(22);
        this.jScrollPane1.setAlignmentX(10.0f);
        this.jScrollPane1.setAlignmentY(10.0f);
        this.jScrollPane1.setFont(new Font("Lucida Grande", 0, 10));
        this.jScrollPane1.setMaximumSize(new Dimension(1200, 300));
        this.jScrollPane1.setMinimumSize(new Dimension(1200, 300));
        this.jScrollPane1.setPreferredSize(new Dimension(1200, 600));
        this.jScrollPane1.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.jScrollPane1MouseReleased(evt);
            }
        });
        this.listOfTutors.setBackground(new Color(184, 249, 196));
        this.listOfTutors.setFont(new Font("Lucida Grande", 0, 11));
        this.listOfTutors.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null, null, null}}, new String[]{"PI", "Forenames", "Surname", "Region", "Module", "Pres", "TMA No", "Status", "Collected:", "", "", "Zip:"}){
            Class[] types;
            boolean[] canEdit;
            {
                this.types = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class};
                this.canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, true};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.listOfTutors.setAutoResizeMode(0);
        this.listOfTutors.setAutoscrolls(false);
        this.listOfTutors.setFocusTraversalPolicyProvider(true);
        this.listOfTutors.setMaximumSize(new Dimension(1050, 2000));
        this.listOfTutors.setMinimumSize(new Dimension(700, 2000));
        this.listOfTutors.setOpaque(false);
        this.listOfTutors.setPreferredSize(new Dimension(750, 2000));
        this.listOfTutors.setSelectionForeground(new Color(0, 0, 0));
        this.listOfTutors.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.listOfTutorsMouseReleased(evt);
            }
        });
        this.jScrollPane1.setViewportView(this.listOfTutors);
        this.totalTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.totalTmas.setText("jTextField1");
        this.toBeMarkedTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.toBeMarkedTmas.setText("jTextField1");
        this.jLabel20.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel20.setText("Number of Submissions");
        this.jLabel21.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel21.setText("To be monitored");
        this.tmaSelectMenu.setFont(new Font("Lucida Grande", 0, 10));
        this.tmaSelectMenu.setModel(new DefaultComboBoxModel<String>(new String[]{"All", "Unmonitored", "Monitored", "Zipped"}));
        this.tmaSelectMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.tmaSelectMenuActionPerformed(evt);
            }
        });
        this.printTmaList.setFont(new Font("Lucida Grande", 0, 10));
        this.printTmaList.setText("Print list");
        this.printTmaList.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.printTmaListActionPerformed(evt);
            }
        });
        this.jLabel34.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel34.setForeground(new Color(255, 51, 51));
        this.jLabel34.setText("Click on any row to open that Tutor's work.");
        this.highlightUnmarked.setFont(new Font("Lucida Grande", 0, 12));
        this.highlightUnmarked.setText("Highlight unmonitored work");
        this.highlightUnmarked.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.highlightUnmarkedActionPerformed(evt);
            }
        });
        this.batchZipNew.setFont(new Font("Lucida Grande", 1, 13));
        this.batchZipNew.setForeground(new Color(51, 51, 255));
        this.batchZipNew.setText("Batch Zip");
        this.batchZipNew.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.batchZipNewActionPerformed(evt);
            }
        });
        this.selectAllFilesToZip.setFont(new Font("Lucida Grande", 0, 12));
        this.selectAllFilesToZip.setText("SelectAll/None to Zip");
        this.selectAllFilesToZip.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.selectAllFilesToZipActionPerformed(evt);
            }
        });
        GroupLayout submittedMonitoringLayout = new GroupLayout(this.submittedMonitoring.getContentPane());
        this.submittedMonitoring.getContentPane().setLayout((LayoutManager)submittedMonitoringLayout);
        submittedMonitoringLayout.setHorizontalGroup((GroupLayout.Group)submittedMonitoringLayout.createParallelGroup(1).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().add((GroupLayout.Group)submittedMonitoringLayout.createParallelGroup(1).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)submittedMonitoringLayout.createParallelGroup(1).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().add(17, 17, 17).add((Component)this.jLabel34).add(30, 30, 30).add((Component)this.selectAllFilesToZip).add(18, 18, 18).add((Component)this.batchZipNew)).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().add((Component)this.highlightUnmarked).add(35, 35, 35).add((Component)this.jLabel20).addPreferredGap(0).add((Component)this.totalTmas, -2, 30, -2).add(34, 34, 34).add((Component)this.jLabel21).addPreferredGap(0).add((Component)this.toBeMarkedTmas, -2, 24, -2).add(120, 120, 120).add((Component)this.printTmaList)))).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().add(371, 371, 371).add((Component)this.tmaSelectMenu, -2, 128, -2)).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().addContainerGap().add((Component)this.jScrollPane1, -2, 778, -2))).addContainerGap(-1, Short.MAX_VALUE)));
        submittedMonitoringLayout.setVerticalGroup((GroupLayout.Group)submittedMonitoringLayout.createParallelGroup(1).add((GroupLayout.Group)submittedMonitoringLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)submittedMonitoringLayout.createParallelGroup(3).add((Component)this.highlightUnmarked).add((Component)this.jLabel20).add((Component)this.totalTmas, -2, -1, -2).add((Component)this.printTmaList).add((Component)this.jLabel21).add((Component)this.toBeMarkedTmas, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)submittedMonitoringLayout.createParallelGroup(3).add((Component)this.selectAllFilesToZip).add((Component)this.jLabel34, -1, -1, Short.MAX_VALUE).add((Component)this.batchZipNew)).add(1, 1, 1).add((Component)this.jScrollPane1, -2, 200, -2).addPreferredGap(0).add((Component)this.tmaSelectMenu, -2, 22, -2)));
        this.studentSummary.setResizable(false);
        this.studentSummary.setSize(new Dimension(800, 200));
        this.studentsListTable.setFont(new Font("Lucida Grande", 0, 10));
        this.studentsListTable.setModel(new DefaultTableModel(new Object[][]{{null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}, {null, null, null, null, null, null, null, null, null, null}}, new String[]{"PID", "Forenames", "SURNAME", "Marking", "PT3 ", " ", "Annotated?", "Complete?", "", ""}){
            Class[] types;
            boolean[] canEdit;
            {
                this.types = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class};
                this.canEdit = new boolean[]{false, false, false, false, false, false, false, false, true, true};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.studentsListTable.setGridColor(new Color(102, 153, 255));
        this.studentsListTable.setPreferredSize(new Dimension(800, 200));
        this.studentsListTable.setRowSelectionAllowed(false);
        this.studentsListTable.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.studentsListTableMouseReleased(evt);
            }
        });
        this.jScrollPane3.setViewportView(this.studentsListTable);
        this.printGrades.setFont(new Font("Lucida Grande", 0, 10));
        this.printGrades.setText("Print Student LIst");
        this.printGrades.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.printGradesActionPerformed(evt);
            }
        });
        this.jLabel33.setFont(new Font("Lucida Grande", 0, 12));
        this.jLabel33.setForeground(new Color(255, 0, 0));
        this.jLabel33.setText("Click on ths student's PID  or name to open the script. ");
        GroupLayout studentSummaryLayout = new GroupLayout(this.studentSummary.getContentPane());
        this.studentSummary.getContentPane().setLayout((LayoutManager)studentSummaryLayout);
        studentSummaryLayout.setHorizontalGroup((GroupLayout.Group)studentSummaryLayout.createParallelGroup(1).add((GroupLayout.Group)studentSummaryLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)studentSummaryLayout.createParallelGroup(1).add((GroupLayout.Group)studentSummaryLayout.createSequentialGroup().add((Component)this.jScrollPane3, -1, 794, Short.MAX_VALUE).add(14, 14, 14)).add((GroupLayout.Group)studentSummaryLayout.createSequentialGroup().add((Component)this.jLabel33).add(218, 218, 218).add((Component)this.printGrades).addContainerGap(-1, Short.MAX_VALUE)))));
        studentSummaryLayout.setVerticalGroup((GroupLayout.Group)studentSummaryLayout.createParallelGroup(1).add((GroupLayout.Group)studentSummaryLayout.createSequentialGroup().addContainerGap().add((Component)this.jScrollPane3, -1, 165, Short.MAX_VALUE).add(18, 18, 18).add((GroupLayout.Group)studentSummaryLayout.createParallelGroup(1).add((Component)this.printGrades).add((Component)this.jLabel33)).add(0, 19, Short.MAX_VALUE)));
        this.weightings.setDefaultCloseOperation(0);
        this.weightings.setTitle("TMA Weightings");
        this.weightings.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent evt) {
                EtmaMonitorJ.this.weightingsWindowClosing(evt);
            }
        });
        this.weightingsTable1.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable1.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Weightings for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.weightingsTable1.setGridColor(new Color(0, 51, 255));
        this.weightingsTable1.setSelectionBackground(new Color(255, 255, 255));
        this.weightingsTable1.setSelectionForeground(new Color(0, 0, 0));
        this.weightings1.setViewportView(this.weightingsTable1);
        this.weightingsTable2.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable2.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Weightings for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.weightingsTable2.setGridColor(new Color(0, 51, 255));
        this.weightingsTable2.setSelectionBackground(new Color(255, 255, 255));
        this.weightingsTable2.setSelectionForeground(new Color(0, 0, 0));
        this.weightings2.setViewportView(this.weightingsTable2);
        this.weightingsTable3.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable3.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Weightings for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
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
        this.tmaNumbers.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{""}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }
        });
        this.tmaNumbers.setGridColor(new Color(0, 51, 255));
        this.tmaNumbers.setSelectionBackground(new Color(255, 255, 255));
        this.tmaNumbers.setSelectionForeground(new Color(0, 0, 0));
        this.weightingsHeaders.setViewportView(this.tmaNumbers);
        this.saveWeightings.setFont(new Font("Lucida Grande", 0, 10));
        this.saveWeightings.setText("Save");
        this.saveWeightings.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.saveWeightingsActionPerformed(evt);
            }
        });
        this.weightingsTable4.setFont(new Font("Lucida Grande", 0, 10));
        this.weightingsTable4.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Weightings for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
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
        this.maxScoreTable1.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Max score for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
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
        this.maxScoreTable2.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Max Score for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
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
        this.maxScoreTable3.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Max Score for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
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
        this.maxScoreTable4.setModel(new DefaultTableModel(new Object[][]{{null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}, {null}}, new String[]{"Max Score for"}){
            Class[] types;
            {
                this.types = new Class[]{String.class};
            }

            public Class getColumnClass(int columnIndex) {
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
        GroupLayout weightingsLayout = new GroupLayout(this.weightings.getContentPane());
        this.weightings.getContentPane().setLayout((LayoutManager)weightingsLayout);
        weightingsLayout.setHorizontalGroup((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add(2, (GroupLayout.Group)weightingsLayout.createSequentialGroup().addContainerGap().add((Component)this.weightingsHeaders, -2, 58, -2).addPreferredGap(0).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add(2, (GroupLayout.Group)weightingsLayout.createSequentialGroup().add((Component)this.weightings1, -2, 101, -2).addPreferredGap(0)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(21, 21, 21).add((Component)this.saveWeightings).add(15, 15, 15))).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1, false).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.maxScores1, -2, 101, -2).addPreferredGap(0).add((Component)this.weightings2, -2, 101, -2).addPreferredGap(0).add((Component)this.maxScores2, -2, 101, -2)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(3, 3, 3).add((Component)this.jScrollPane7))).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(10, 10, 10).add((Component)this.weightings3, -2, 101, -2).addPreferredGap(0).add((Component)this.maxScores3, -2, 101, -2).addPreferredGap(0).add((Component)this.weightings4, -2, 101, -2).addPreferredGap(0).add((Component)this.maxScores4, -2, 101, -2)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(18, 18, 18).add((Component)this.jScrollPane6, -2, 323, -2))).add(648, 648, 648)));
        weightingsLayout.setVerticalGroup((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(55, 55, 55).add((GroupLayout.Group)weightingsLayout.createParallelGroup(2, false).add(1, (Component)this.weightings1, -1, 260, Short.MAX_VALUE).add((Component)this.weightingsHeaders, -2, 245, -2)).add(7, 7, 7)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().add(57, 57, 57).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((Component)this.maxScores1, -1, 265, Short.MAX_VALUE).add((Component)this.weightings2, -1, 265, Short.MAX_VALUE).add(2, (Component)this.maxScores2, -1, 265, Short.MAX_VALUE).add((Component)this.weightings3, -1, 265, Short.MAX_VALUE).add((Component)this.maxScores4, -1, 265, Short.MAX_VALUE).add((Component)this.weightings4, -1, 265, Short.MAX_VALUE).add((Component)this.maxScores3, -1, 265, Short.MAX_VALUE)))).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.saveWeightings)).add((GroupLayout.Group)weightingsLayout.createSequentialGroup().addPreferredGap(1).add((GroupLayout.Group)weightingsLayout.createParallelGroup(1).add((Component)this.jScrollPane6, -2, -1, -2).add((Component)this.jScrollPane7, -2, 106, -2)))).add(1072, 1072, 1072)));
        this.helpFrame.setTitle("Help");
        this.jTextPane1.setEditable(false);
        this.jTextPane1.setFont(new Font("Lucida Grande", 0, 14));
        this.jTextPane1.setText("Welcome to the Mac MonitorHandler (Java Version)!\nGETTING STARTED:\n\n(Please note that this program has at present been tested on MacOS10.13 (High Sierra), but should work on any Mac system that can run Java 7 or higher.\n\nIMPORTANT: Although the setup procedures are, I hope, reasonably straightforward, you are strongly advised to thoroughly test your setup before using it \"live\", with monitoring  from the monitoring training site (there is a link button to the Training site on the app). See also the disclaimer below. If you get error messages, or something just doesn't work, it will almost certainly be because you haven't set the preferences correctly!\n\nIt is also worthwhile (at least initially) after zipping the files for final return to unzip a copy first to check that it does contain what you expect! The zipped file should contain a file called monitor.fhi, and any scripts that you have annotated for the tutor\u2019s information.\n\n\n\tYou will need a folder called \"etmamonitoring\" without the quotes. It is strongly advised that you use the one included in the package - move it to your Desktop or wherever else is convenient. If you create your own etmamonitoring folder, you must move the two files monrubric.txt and etmaDictionary.txt from the etmamonitoring folder in the downloaded package into that folder.\n\nDouble-click the application. You will be asked to set the path to your etmamonitoring folder. Navigate to that folder, select the whole folder and choose. The app will quit -relaunch it.\n\nDownload monitoring from the OU site (you can use the button on the app to get there). Set zipped files on your browser to be automatically expanded, or unzip manually.Next, using \"Select Downloads Folder\", in the Preferences select your folder for files you will be downloading from the OU website  (this will generally be wherever your browser downloads usually go - IMPORTANT: do not set it to, or anywhere inside, your etmamonitoring folder. You can set other preferences now, or come back to them later.\n\nRelaunch, click the button \"Import Monitoring\" , and navigate to your downloaded TMA files folder (the complete folder, not one of the internal ones - it should begin with the course code). DON'T select any of the folders inside the complete folder - this can cause chaos!). New monitoring should now have been automagically transferred into the appropriate places in the \"etmamonitoring\" folder you created earlier.  Unless you know what you're doing, don't manually move files into your \"etmas\" folder  this should have happened automatically!\n\nSelect the module code, and then the TMA number from the pull down menu. Then click on the List Tutors button, and, if you've set up correctly, a summary window should appear with details of all tutors whose work has been selected for monitoring.  Click on the appropriate name in the Tutor List window and the details for that tutor will be displayed. The list of students whose scripts have been selected will be shown in a separate window.\n\nClicking on a student\u2019s name or PID will open a separate window showing their marked script(s) and PT3 form. If you click on the name of the script it should open in your chosen Word Processor, for you to assess, and if appropriate annotate. Click on the \u201cNo\u201d next to a script and it will become \u201cYes\u201d, in which case the annotated file will be returned to the Tutor.\n\n\nOnce you\u2019ve read the script(s), made a judgement and possibly annotated, grade the script and PT3 by clicking on the blank fields on the list of Students. The headings will cycle round the various categories each time you click. Once you\u2019ve finished, click \u201cYes\u201d under \u201cComplete?\u201d - you won\u2019t be able to zip the work to return unless there\u2019s a \u201cYes\u201d against each script.\n\nOn the main page, comment on the over-all marking in the comment box, and enter in a grade against each of the 15 or so criteria, according to the instructions of your module team.  You can move between cells of the scores grid using the up-and down-arrow keys.\n\nThe link to the OU monitoring site is set by default to  http://css3.open.ac.uk/etma/monitor/ . If necessary, this can be changed in the General preferences.?\nThere is no \u201cBatch Zip\u201d facility at present (it may come later!). Simply click the \u201cZip Files\u201d button. The zipped file is stored in the  \u201creturns\u201d folder. In your browser, go to the OU Site, choose the \u201cReturn\u201d tab. Click \u201cChoose file\u201d and navigate to the zipped file. The click the \u201cReturn\u201d button and hope - you will be asked to confirm your submission. Note that, unlike TMAs, you can resubmit if necessary.\n\n\nOther functions are, I hope, self-explanatory or obvious!  \n\nGood luck! Comments questions and suggestions most welcome!\n\nMike Hay (mike@hayfamily.co.uk) Tutor Number 00516109\n\n\nTroubleshooting:\nA.\tMake sure you have selected the entire etmamonitoring folder in the etmamonitoring preference tab, not any of the contained folders. The file path in that tab should end in \u201c/etmamonitoring\u201d.  Also make sure that you have not set the \"Downloaded TMAs\" to your \"etmamonitoring\" folder\n\nB.\tSimilarly, make sure when moving downloaded Monitoring to the etmamonitoring folder that you select the entire downloaded (fully unzipped) folder (the name should begin with the course code). If something seems to have gone wrong, try removing the appropriate files from the etmamonitoring folder, rename the downloaded file back to its original name and try again!\n\nC\t Double-check that your folder preferences are set correctly. I suggest  that on a Mac initially you use the hard disk for all your storage until you\u2019re clear what you\u2019re doing.\n\n\n\nIMPORTANT DISCLAIMER: this program has been produced by me for my own convenience, but is offered freely to OU tutors to mark eTMAs. It is offered \"as is\", and you use it entirely at your own risk. I take no responsibility whatsoever for any loss or damage however caused, or any consequential loss or damage to Open University systems, or to students' systems or progress with their studies. It is not supported or recommended in any way by the Open University. Your attention is drawn again to the importance of initial testing, either with students' dummy TMAs, or using TMAs from the eTMA training site.\n\nMJHay 29 July 2007\n\n\n\n\n\n\n\n\n");
        this.helpScroll.setViewportView(this.jTextPane1);
        GroupLayout helpFrameLayout = new GroupLayout(this.helpFrame.getContentPane());
        this.helpFrame.getContentPane().setLayout((LayoutManager)helpFrameLayout);
        helpFrameLayout.setHorizontalGroup((GroupLayout.Group)helpFrameLayout.createParallelGroup(1).add(2, (GroupLayout.Group)helpFrameLayout.createSequentialGroup().addContainerGap(21, Short.MAX_VALUE).add((Component)this.helpScroll, -2, 692, -2).add(16, 16, 16)));
        helpFrameLayout.setVerticalGroup((GroupLayout.Group)helpFrameLayout.createParallelGroup(1).add(2, (GroupLayout.Group)helpFrameLayout.createSequentialGroup().addContainerGap(-1, Short.MAX_VALUE).add((Component)this.helpScroll, -2, 630, -2).addContainerGap()));
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
        this.sendButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.sendButtonActionPerformed(evt);
            }
        });
        this.mailPreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.mailPreferences.setText("Mail preferences");
        this.mailPreferences.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.mailPreferencesActionPerformed(evt);
            }
        });
        this.addAttachmentButton.setFont(new Font("Lucida Grande", 0, 10));
        this.addAttachmentButton.setText("Add attachment");
        this.addAttachmentButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.addAttachmentButtonActionPerformed(evt);
            }
        });
        this.deleteAttachment.setFont(new Font("Lucida Grande", 0, 10));
        this.deleteAttachment.setText("Remove attachment");
        this.deleteAttachment.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.deleteAttachmentActionPerformed(evt);
            }
        });
        this.addRecip.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel29.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel29.setText("Additional Recipient:");
        this.jLabel3.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel3.setForeground(new Color(255, 0, 0));
        this.jLabel3.setText("(A copy will be sent to you automatically)");
        GroupLayout mailClientLayout = new GroupLayout(this.mailClient.getContentPane());
        this.mailClient.getContentPane().setLayout((LayoutManager)mailClientLayout);
        mailClientLayout.setHorizontalGroup((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)mailClientLayout.createParallelGroup(1, false).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().add((Component)this.jLabel23).addPreferredGap(0).add((Component)this.messageAddresses)).add((Component)this.messageBody).add(2, (GroupLayout.Group)mailClientLayout.createSequentialGroup().add((Component)this.jLabel24).add(27, 27, 27).add((Component)this.messageSubject)).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().add((Component)this.jLabel29).addPreferredGap(0).add((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((Component)this.jLabel3).add((Component)this.addRecip, -2, 347, -2)))).add(15, 15, 15).add((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((Component)this.addAttachmentButton).add((Component)this.sendButton).add((Component)this.deleteAttachment).add((Component)this.mailPreferences)).addContainerGap(462, Short.MAX_VALUE)));
        mailClientLayout.setVerticalGroup((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)mailClientLayout.createParallelGroup(1, false).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.sendButton).addPreferredGap(0).add((Component)this.addAttachmentButton)).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().add((GroupLayout.Group)mailClientLayout.createParallelGroup(3).add((Component)this.jLabel23).add((Component)this.messageAddresses, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)mailClientLayout.createParallelGroup(3).add((Component)this.jLabel29, -2, 13, -2).add((Component)this.addRecip, -2, -1, -2)).addPreferredGap(0, -1, Short.MAX_VALUE).add((Component)this.jLabel3))).add(7, 7, 7).add((GroupLayout.Group)mailClientLayout.createParallelGroup(3).add((Component)this.messageSubject, -2, -1, -2).add((Component)this.jLabel24, -2, 13, -2).add((Component)this.deleteAttachment)).add((GroupLayout.Group)mailClientLayout.createParallelGroup(1).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addPreferredGap(0, 8, Short.MAX_VALUE).add((Component)this.messageBody, -2, 360, -2).addContainerGap()).add((GroupLayout.Group)mailClientLayout.createSequentialGroup().addPreferredGap(0).add((Component)this.mailPreferences).addContainerGap()))));
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
        this.authenticationFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.authenticationFlagActionPerformed(evt);
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
        this.saveMailPreferences.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.saveMailPreferencesActionPerformed(evt);
            }
        });
        this.yourEmailAddress.setFont(new Font("Lucida Grande", 0, 10));
        this.yourEmailAddress.setText("jTextField2");
        this.jLabel28.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel28.setText("Your email address");
        GroupLayout mailPreferencesFrameLayout = new GroupLayout(this.mailPreferencesFrame.getContentPane());
        this.mailPreferencesFrame.getContentPane().setLayout((LayoutManager)mailPreferencesFrameLayout);
        mailPreferencesFrameLayout.setHorizontalGroup((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(1).add((GroupLayout.Group)mailPreferencesFrameLayout.createSequentialGroup().add(46, 46, 46).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(2).add((Component)this.saveMailPreferences).add((GroupLayout.Group)mailPreferencesFrameLayout.createSequentialGroup().add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(2).add((Component)this.jLabel25).add((Component)this.jLabel26).add((Component)this.jLabel27).add((Component)this.jLabel28)).addPreferredGap(0).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(1, false).add((Component)this.authenticationFlag).add((Component)this.mailUserName, -1, 237, Short.MAX_VALUE).add((Component)this.mailPassword).add((Component)this.smtpHost).add(2, (Component)this.yourEmailAddress)))).add(151, 151, 151)));
        mailPreferencesFrameLayout.setVerticalGroup((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(1).add((GroupLayout.Group)mailPreferencesFrameLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.jLabel25).add((Component)this.mailUserName, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.jLabel26).add((Component)this.mailPassword, -2, -1, -2)).add(21, 21, 21).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.jLabel27).add((Component)this.smtpHost, -2, -1, -2)).addPreferredGap(0).add((Component)this.authenticationFlag).add(13, 13, 13).add((GroupLayout.Group)mailPreferencesFrameLayout.createParallelGroup(3).add((Component)this.yourEmailAddress, -2, -1, -2).add((Component)this.jLabel28)).add(16, 16, 16).add((Component)this.saveMailPreferences).addContainerGap(110, Short.MAX_VALUE)));
        this.messageWindow.setTitle("Backing up..... please wait.....");
        this.messageWindow.setAlwaysOnTop(true);
        this.messageWindow.setEnabled(false);
        this.messageWindow.setFocusable(false);
        this.messageWindow.setFocusableWindowState(false);
        this.messageWindow.setResizable(false);
        this.messageText.setColumns(20);
        this.messageText.setRows(5);
        this.jScrollPane4.setViewportView(this.messageText);
        GroupLayout messageWindowLayout = new GroupLayout(this.messageWindow.getContentPane());
        this.messageWindow.getContentPane().setLayout((LayoutManager)messageWindowLayout);
        messageWindowLayout.setHorizontalGroup((GroupLayout.Group)messageWindowLayout.createParallelGroup(1).add((GroupLayout.Group)messageWindowLayout.createSequentialGroup().add(59, 59, 59).add((Component)this.jScrollPane4, -2, -1, -2).addContainerGap(97, Short.MAX_VALUE)));
        messageWindowLayout.setVerticalGroup((GroupLayout.Group)messageWindowLayout.createParallelGroup(1).add((GroupLayout.Group)messageWindowLayout.createSequentialGroup().add(50, 50, 50).add((Component)this.jScrollPane4, -2, -1, -2).addContainerGap(166, Short.MAX_VALUE)));
        this.jRadioButton1.setText("jRadioButton1");
        GroupLayout jPanel1Layout = new GroupLayout((Container)this.jPanel1);
        this.jPanel1.setLayout((LayoutManager)jPanel1Layout);
        jPanel1Layout.setHorizontalGroup((GroupLayout.Group)jPanel1Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel1Layout.createSequentialGroup().add(9, 9, 9).add((Component)this.jRadioButton1).addContainerGap(52, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup((GroupLayout.Group)jPanel1Layout.createParallelGroup(1).add((GroupLayout.Group)jPanel1Layout.createSequentialGroup().addContainerGap().add((Component)this.jRadioButton1).addContainerGap(202, Short.MAX_VALUE)));
        GroupLayout spellChooserLayout = new GroupLayout(this.spellChooser.getContentPane());
        this.spellChooser.getContentPane().setLayout((LayoutManager)spellChooserLayout);
        spellChooserLayout.setHorizontalGroup((GroupLayout.Group)spellChooserLayout.createParallelGroup(1).add((GroupLayout.Group)spellChooserLayout.createSequentialGroup().add(78, 78, 78).add((Component)this.jPanel1, -2, -1, -2).addContainerGap(54, Short.MAX_VALUE)));
        spellChooserLayout.setVerticalGroup((GroupLayout.Group)spellChooserLayout.createParallelGroup(1).add((GroupLayout.Group)spellChooserLayout.createSequentialGroup().add(31, 31, 31).add((Component)this.jPanel1, -2, -1, -2).addContainerGap(50, Short.MAX_VALUE)));
        this.custom.setDefaultCloseOperation(0);
        this.custom.setTitle("Customise");
        this.custom.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent evt) {
                EtmaMonitorJ.this.customWindowClosing(evt);
            }
        });
        this.jLabel35.setFont(new Font("Lucida Grande", 0, 12));
        this.jLabel35.setForeground(new Color(255, 51, 51));
        this.jLabel35.setHorizontalAlignment(0);
        this.jLabel35.setText("Tick the buttons you wish to hide");
        this.hideEtmaSite.setFont(new Font("Lucida Grande", 0, 10));
        this.hideEtmaSite.setText("Monitoring site");
        this.hideListTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.hideListTmas.setText("List Tutors");
        this.hideOpenTmaFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenTmaFolder.setText("Open Tutor'sFolder");
        this.hideOpenTmaFolder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.hideOpenTmaFolderActionPerformed(evt);
            }
        });
        this.hideTrainingSite.setFont(new Font("Lucida Grande", 0, 10));
        this.hideTrainingSite.setText("Training Site");
        this.hideSavePt3.setFont(new Font("Lucida Grande", 0, 10));
        this.hideSavePt3.setText("Save Monitoring");
        this.hideECollectTmas.setFont(new Font("Lucida Grande", 0, 10));
        this.hideECollectTmas.setText("Collect Monitoring");
        this.hideBankComment.setFont(new Font("Lucida Grande", 0, 10));
        this.hideBankComment.setText("Bank Comment");
        this.hideZipFiles.setFont(new Font("Lucida Grande", 0, 10));
        this.hideZipFiles.setText("Zip Files");
        this.hideBackupEtmas.setFont(new Font("Lucida Grande", 0, 10));
        this.hideBackupEtmas.setText("Backup work");
        this.hideSendEmail.setFont(new Font("Lucida Grande", 0, 10));
        this.hideSendEmail.setText("Send Email");
        this.hideOpenPreferences.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenPreferences.setText("Open Preferences");
        this.hideTestJs.setFont(new Font("Lucida Grande", 0, 10));
        this.hideTestJs.setSelected(true);
        this.hideTestJs.setText("Spelling");
        this.hideTestJs.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.hideTestJsActionPerformed(evt);
            }
        });
        this.closeCustomize.setFont(new Font("Lucida Grande", 0, 10));
        this.closeCustomize.setText("Close");
        this.closeCustomize.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.closeCustomizeActionPerformed(evt);
            }
        });
        this.colorRemove.setText("Remove colour background from all main window buttons");
        this.colorRemove.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.colorRemoveActionPerformed(evt);
            }
        });
        this.hideOpenReturnsFolder.setFont(new Font("Lucida Grande", 0, 10));
        this.hideOpenReturnsFolder.setText("Open Returns Folder");
        this.jLabel36.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel36.setForeground(new Color(255, 51, 51));
        this.jLabel36.setText("(To restore the colours, untick the box and relaunch the program)");
        GroupLayout customLayout = new GroupLayout(this.custom.getContentPane());
        this.custom.getContentPane().setLayout((LayoutManager)customLayout);
        customLayout.setHorizontalGroup((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().add((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().add(28, 28, 28).add((Component)this.colorRemove)).add((GroupLayout.Group)customLayout.createSequentialGroup().add(62, 62, 62).add((Component)this.jLabel36)).add((GroupLayout.Group)customLayout.createParallelGroup(2).add((GroupLayout.Group)customLayout.createSequentialGroup().add(8, 8, 8).add((Component)this.closeCustomize).addPreferredGap(0).add((Component)this.jLabel35, -2, 234, -2)).add(1, (GroupLayout.Group)customLayout.createSequentialGroup().add(39, 39, 39).add((GroupLayout.Group)customLayout.createParallelGroup(1).add((Component)this.hideEtmaSite, -2, 122, -2).add((Component)this.hideListTmas, -2, 122, -2).add((Component)this.hideOpenTmaFolder, -2, 122, -2).add((Component)this.hideTrainingSite, -2, 122, -2).add((Component)this.hideSavePt3, -2, 122, -2).add((Component)this.hideECollectTmas, -2, 122, -2)).add(40, 40, 40).add((GroupLayout.Group)customLayout.createParallelGroup(1).add((Component)this.hideOpenReturnsFolder, -2, 122, -2).add((Component)this.hideTestJs, -2, 122, -2).add((Component)this.hideOpenPreferences, -2, 122, -2).add((Component)this.hideZipFiles, -2, 122, -2).add((Component)this.hideBankComment, -2, 122, -2).add((Component)this.hideSendEmail, -2, 122, -2).add((Component)this.hideBackupEtmas, -2, 122, -2))))).addContainerGap(138, Short.MAX_VALUE)));
        customLayout.setVerticalGroup((GroupLayout.Group)customLayout.createParallelGroup(1).add((GroupLayout.Group)customLayout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.jLabel35).add((Component)this.closeCustomize)).add(16, 16, 16).add((Component)this.hideEtmaSite).add(13, 13, 13).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideListTmas).add((Component)this.hideZipFiles)).add(15, 15, 15).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideOpenTmaFolder).add((Component)this.hideBankComment)).add(15, 15, 15).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideTrainingSite).add((Component)this.hideBackupEtmas)).add(8, 8, 8).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideSavePt3).add((Component)this.hideSendEmail)).add(14, 14, 14).add((GroupLayout.Group)customLayout.createParallelGroup(3).add((Component)this.hideECollectTmas).add((Component)this.hideOpenPreferences)).add(15, 15, 15).add((Component)this.hideTestJs).add(21, 21, 21).add((Component)this.hideOpenReturnsFolder).addPreferredGap(1).add((Component)this.colorRemove).addPreferredGap(0).add((Component)this.jLabel36).addContainerGap(25, Short.MAX_VALUE)));
        this.colorFrame1.setTitle("Colours");
        this.colorFrame1.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent evt) {
                EtmaMonitorJ.this.colorFrame1WindowClosing(evt);
            }
        });
        this.colorWindowSelector.setFont(new Font("Lucida Grande", 0, 10));
        this.colorWindowSelector.setModel(new DefaultComboBoxModel<String>(new String[]{"Select a window:", "Main Window", "Comments Window", "Scores Grid"}));
        this.colorWindowSelector.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.colorWindowSelectorActionPerformed(evt);
            }
        });
        this.defaultFlag.setFont(new Font("Lucida Grande", 0, 10));
        this.defaultFlag.setText("Use default");
        this.defaultFlag.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.defaultFlagActionPerformed(evt);
            }
        });
        GroupLayout colorFrame1Layout = new GroupLayout(this.colorFrame1.getContentPane());
        this.colorFrame1.getContentPane().setLayout((LayoutManager)colorFrame1Layout);
        colorFrame1Layout.setHorizontalGroup((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((GroupLayout.Group)colorFrame1Layout.createSequentialGroup().addContainerGap().add((Component)this.jColorChooser1, -2, -1, -2).add(15, 15, 15).add((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((Component)this.colorWindowSelector, -2, 155, -2).add((Component)this.defaultFlag)).add(41, 41, 41)));
        colorFrame1Layout.setVerticalGroup((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((GroupLayout.Group)colorFrame1Layout.createSequentialGroup().add((GroupLayout.Group)colorFrame1Layout.createParallelGroup(1).add((Component)this.jColorChooser1, -2, -1, -2).add((GroupLayout.Group)colorFrame1Layout.createSequentialGroup().add(37, 37, 37).add((Component)this.colorWindowSelector, -2, -1, -2).add(21, 21, 21).add((Component)this.defaultFlag))).addContainerGap(-1, Short.MAX_VALUE)));
        this.scriptsSummaryTable.setFont(new Font("Lucida Grande", 0, 10));
        this.scriptsSummaryTable.setModel(new DefaultTableModel(new Object[][]{{null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}}, new String[]{"File name", "Annotated? (Click)"}){
            Class[] types;
            boolean[] canEdit;
            {
                this.types = new Class[]{String.class, String.class};
                this.canEdit = new boolean[]{false, false};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.scriptsSummaryTable.setGridColor(new Color(102, 153, 255));
        this.scriptsSummaryTable.setRowSelectionAllowed(false);
        this.scriptsSummaryTable.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent evt) {
                EtmaMonitorJ.this.scriptsSummaryTableMousePressed(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.scriptsSummaryTableMouseReleased(evt);
            }
        });
        this.jScrollPane5.setViewportView(this.scriptsSummaryTable);
        this.jTextField1.setEditable(false);
        this.jTextField1.setForeground(new Color(255, 51, 51));
        this.jTextField1.setText("Click on any file to open it.");
        this.jTextField1.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jTextField1ActionPerformed(evt);
            }
        });
        GroupLayout scriptTableLayout = new GroupLayout(this.scriptTable.getContentPane());
        this.scriptTable.getContentPane().setLayout((LayoutManager)scriptTableLayout);
        scriptTableLayout.setHorizontalGroup((GroupLayout.Group)scriptTableLayout.createParallelGroup(1).add((GroupLayout.Group)scriptTableLayout.createSequentialGroup().add(124, 124, 124).add((Component)this.jTextField1, -2, -1, -2).addContainerGap(502, Short.MAX_VALUE)).add((Component)this.jScrollPane5, -1, 808, Short.MAX_VALUE));
        scriptTableLayout.setVerticalGroup((GroupLayout.Group)scriptTableLayout.createParallelGroup(1).add(2, (GroupLayout.Group)scriptTableLayout.createSequentialGroup().add(24, 24, 24).add((Component)this.jScrollPane5, -2, 359, -2).addPreferredGap(0, 32, Short.MAX_VALUE).add((Component)this.jTextField1, -2, -1, -2).addContainerGap()));
        this.jMenuItemUndo.setText("Undo");
        this.jMenuItemUndo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jMenuItemUndoActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemUndo);
        this.jMenuItemRedo.setText("Redo");
        this.jMenuItemRedo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jMenuItemRedoActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemRedo);
        this.jMenuItemCopy.setText("Copy");
        this.jMenuItemCopy.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jMenuItemCopyActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemCopy);
        this.jMenuItemPaste.setText("Paste");
        this.jMenuItemPaste.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jMenuItemPasteActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemPaste);
        this.jMenuItemSelectAll.setText("Select All");
        this.jMenuItemSelectAll.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jMenuItemSelectAllActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemSelectAll);
        this.jMenuItemCut.setText("Cut");
        this.jMenuItemCut.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.jMenuItemCutActionPerformed(evt);
            }
        });
        this.jPopupMenu1.add(this.jMenuItemCut);
        this.setDefaultCloseOperation(0);
        this.setTitle("Monitor Handler");
        this.setCursor(new Cursor(0));
        this.setMinimumSize(new Dimension(900, 530));
        this.setSize(new Dimension(930, 540));
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent evt) {
                EtmaMonitorJ.this.formWindowClosing(evt);
            }

            @Override
            public void windowOpened(WindowEvent evt) {
                EtmaMonitorJ.this.formWindowOpened(evt);
            }
        });
        this.tutor_comments_area.setViewportBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.monitor_comments.setColumns(20);
        this.monitor_comments.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_comments.setLineWrap(true);
        this.monitor_comments.setRows(5);
        this.monitor_comments.setTabSize(4);
        this.monitor_comments.setWrapStyleWord(true);
        this.monitor_comments.setBorder(BorderFactory.createBevelBorder(0));
        this.monitor_comments.setMinimumSize(new Dimension(0, 0));
        this.monitor_comments.setName("fhiFileName");
        this.monitor_comments.addFocusListener(new FocusAdapter(){

            @Override
            public void focusGained(FocusEvent evt) {
                EtmaMonitorJ.this.monitor_commentsFocusGained(evt);
            }
        });
        this.monitor_comments.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent evt) {
                EtmaMonitorJ.this.monitor_commentsMousePressed(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.monitor_commentsMouseReleased(evt);
            }
        });
        this.monitor_comments.addKeyListener(new KeyAdapter(){

            @Override
            public void keyPressed(KeyEvent evt) {
                EtmaMonitorJ.this.monitor_commentsKeyPressed(evt);
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                EtmaMonitorJ.this.monitor_commentsKeyReleased(evt);
            }
        });
        this.tutor_comments_area.setViewportView(this.monitor_comments);
        this.fhiFileName.setFont(new Font("Lucida Grande", 0, 10));
        this.zipFiles.setBackground(new Color(255, 102, 0));
        this.zipFiles.setFont(new Font("Lucida Grande", 1, 10));
        this.zipFiles.setText("Zip Files");
        this.zipFiles.setToolTipText("Saves and zips the current  tutors's files. The zipped file will be found in the returns folder. It is then ready to return to MK using the browser.");
        this.zipFiles.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.zipFilesActionPerformed(evt);
            }
        });
        this.tmaMarks.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.tmaMarks.setViewportBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        this.monitoringRatings.setBackground(new Color(184, 249, 196));
        this.monitoringRatings.setModel(new DefaultTableModel(new Object[][]{{null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}, {null, null}}, new String[]{"Checklist - in the PT3 and/or script(s) selected for monitoring this tutor", "  "}){
            Class[] types;
            boolean[] canEdit;
            {
                this.types = new Class[]{String.class, String.class};
                this.canEdit = new boolean[]{false, false};
            }

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.monitoringRatings.setCellSelectionEnabled(true);
        this.monitoringRatings.setGridColor(new Color(153, 153, 153));
        this.monitoringRatings.setIntercellSpacing(new Dimension(2, 2));
        this.monitoringRatings.setName("TMA Scores");
        this.monitoringRatings.setSelectionBackground(new Color(0, 255, 0));
        this.monitoringRatings.setSelectionForeground(new Color(0, 0, 0));
        this.monitoringRatings.setSurrendersFocusOnKeystroke(true);
        this.monitoringRatings.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseExited(MouseEvent evt) {
                EtmaMonitorJ.this.monitoringRatingsMouseExited(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                EtmaMonitorJ.this.monitoringRatingsMouseReleased(evt);
            }
        });
        this.monitoringRatings.addInputMethodListener(new InputMethodListener(){

            @Override
            public void caretPositionChanged(InputMethodEvent evt) {
            }

            @Override
            public void inputMethodTextChanged(InputMethodEvent evt) {
                EtmaMonitorJ.this.monitoringRatingsInputMethodTextChanged(evt);
            }
        });
        this.monitoringRatings.addKeyListener(new KeyAdapter(){

            @Override
            public void keyPressed(KeyEvent evt) {
                EtmaMonitorJ.this.monitoringRatingsKeyPressed(evt);
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                EtmaMonitorJ.this.monitoringRatingsKeyReleased(evt);
            }
        });
        this.tmaMarks.setViewportView(this.monitoringRatings);
        this.staff_id.setEditable(false);
        this.staff_id.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_id.setToolTipText("The tutor's PID.");
        this.staff_id.setName("staff_id");
        this.personal_id.setEditable(false);
        this.personal_id.setFont(new Font("Lucida Grande", 0, 10));
        this.personal_id.setToolTipText("The student's PID");
        this.personal_id.setName("personal_id");
        this.monitor_comment_date.setEditable(false);
        this.monitor_comment_date.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_comment_date.setToolTipText("The student's Title");
        this.monitor_comment_date.setName("monitor_comment_date");
        this.staff_tutor_comments.setEditable(false);
        this.staff_tutor_comments.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_tutor_comments.setToolTipText("All the student's initials.");
        this.staff_tutor_comments.setName("staff_tutor_comments");
        this.tutor_forenames.setEditable(false);
        this.tutor_forenames.setFont(new Font("Lucida Grande", 1, 10));
        this.tutor_forenames.setForeground(new Color(255, 51, 51));
        this.tutor_forenames.setToolTipText("Tutor's forename");
        this.tutor_forenames.setName("tutor_forenames");
        this.tutor_surname.setEditable(false);
        this.tutor_surname.setFont(new Font("Lucida Grande", 1, 10));
        this.tutor_surname.setForeground(new Color(255, 51, 51));
        this.tutor_surname.setToolTipText("Tutor's surname");
        this.tutor_surname.setName("tutor_surname");
        this.monitor_comments_file.setEditable(false);
        this.monitor_comments_file.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_comments_file.setName("monitor_comments_file");
        this.download_exe_name.setEditable(false);
        this.download_exe_name.setFont(new Font("Lucida Grande", 0, 10));
        this.download_exe_name.setName("download_exe_name");
        this.staff_tutor_return_date.setEditable(false);
        this.staff_tutor_return_date.setFont(new Font("Lucida Grande", 0, 10));
        this.staff_tutor_return_date.setName("staff_tutor_return_date");
        this.basic_monitor_form_name.setEditable(false);
        this.basic_monitor_form_name.setFont(new Font("Lucida Grande", 0, 10));
        this.basic_monitor_form_name.setName("basic_monitor_form_name");
        this.basic_monitor_form_path.setEditable(false);
        this.basic_monitor_form_path.setFont(new Font("Lucida Grande", 0, 10));
        this.basic_monitor_form_path.setName("basic_monitor_form_path");
        this.monitoring_level_code.setEditable(false);
        this.monitoring_level_code.setFont(new Font("Lucida Grande", 0, 10));
        this.monitoring_level_code.setToolTipText("The student's postcode.");
        this.monitoring_level_code.setName("monitoring_level_code");
        this.monitor_return_date.setEditable(false);
        this.monitor_return_date.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_return_date.setName("monitor_return_date");
        this.assgnmt_cut_off_date.setEditable(false);
        this.assgnmt_cut_off_date.setFont(new Font("Lucida Grande", 0, 10));
        this.assgnmt_cut_off_date.setName("assgnmt_cut_off_date");
        this.loadXMLAlt.setFont(new Font("Lucida Grande", 0, 10));
        this.loadXMLAlt.setText("LoadPT3");
        this.loadXMLAlt.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.loadXMLAltActionPerformed(evt);
            }
        });
        this.sent_to_tutor_date.setEditable(false);
        this.sent_to_tutor_date.setFont(new Font("Lucida Grande", 0, 10));
        this.sent_to_tutor_date.setName("staff_id");
        this.monitor_staff_id.setEditable(false);
        this.monitor_staff_id.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_staff_id.setName("staff_id");
        this.monitor_forenames.setEditable(false);
        this.monitor_forenames.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_forenames.setName("staff_id");
        this.number_scripts.setEditable(false);
        this.number_scripts.setFont(new Font("Lucida Grande", 1, 10));
        this.number_scripts.setToolTipText("Total score for this TMA.");
        this.number_scripts.setName("number_scripts");
        this.monitor_surname.setEditable(false);
        this.monitor_surname.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_surname.setName("staff_id");
        this.locn_code.setEditable(false);
        this.locn_code.setFont(new Font("Lucida Grande", 0, 10));
        this.locn_code.setName("locn_code");
        this.course_code.setEditable(false);
        this.course_code.setFont(new Font("Lucida Grande", 0, 10));
        this.course_code.setName("staff_id");
        this.version_num.setEditable(false);
        this.version_num.setFont(new Font("Lucida Grande", 0, 10));
        this.version_num.setName("staff_id");
        this.pres_code.setEditable(false);
        this.pres_code.setFont(new Font("Lucida Grande", 0, 10));
        this.pres_code.setName("staff_id");
        this.assgnmt_suffix.setEditable(false);
        this.assgnmt_suffix.setFont(new Font("Lucida Grande", 0, 10));
        this.assgnmt_suffix.setName("staff_id");
        this.new_tutor.setEditable(false);
        this.new_tutor.setFont(new Font("Lucida Grande", 0, 10));
        this.new_tutor.setName("new_tutor");
        this.zip_file.setEditable(false);
        this.zip_file.setFont(new Font("Lucida Grande", 0, 10));
        this.zip_file.setName("address_line4");
        this.zip_filepath.setEditable(false);
        this.zip_filepath.setFont(new Font("Lucida Grande", 0, 10));
        this.zip_filepath.setName("address_line5");
        this.zip_filename.setEditable(false);
        this.zip_filename.setFont(new Font("Lucida Grande", 0, 10));
        this.zip_filename.setName("postcode");
        this.zip_date.setEditable(false);
        this.zip_date.setFont(new Font("Lucida Grande", 0, 10));
        this.zip_date.setName("staff_id");
        this.late_submission_status.setEditable(false);
        this.late_submission_status.setFont(new Font("Lucida Grande", 0, 10));
        this.late_submission_status.setName("address_line3");
        this.tutor_status.setEditable(false);
        this.tutor_status.setFont(new Font("Lucida Grande", 0, 10));
        this.tutor_status.setToolTipText("Shows whether the tutor's work is Unmoitored, Monitored or Zipped.");
        this.tutor_status.setName("tutor_status");
        this.monitor_collect_date.setEditable(false);
        this.monitor_collect_date.setFont(new Font("Lucida Grande", 0, 10));
        this.monitor_collect_date.setName("address_line5");
        this.sample_available_date.setEditable(false);
        this.sample_available_date.setFont(new Font("Lucida Grande", 0, 10));
        this.sample_available_date.setName("postcode");
        this.e_tma_submission_date.setEditable(false);
        this.e_tma_submission_date.setFont(new Font("Lucida Grande", 0, 10));
        this.e_tma_submission_date.setName("staff_id");
        this.monitoring_type.setEditable(false);
        this.monitoring_type.setFont(new Font("Lucida Grande", 0, 10));
        this.monitoring_type.setName("address_line5");
        this.previous_monitor_forms.setEditable(false);
        this.previous_monitor_forms.setFont(new Font("Lucida Grande", 0, 10));
        this.previous_monitor_forms.setName("address_line5");
        this.tutor_comments.setFont(new Font("Lucida Grande", 0, 10));
        this.tutor_comments.setName("address_line5");
        this.savePt3.setBackground(new Color(255, 102, 0));
        this.savePt3.setFont(new Font("Lucida Grande", 0, 12));
        this.savePt3.setText("Save Report");
        this.savePt3.setToolTipText("Save the current monitoring report (command-S is a shortcut)");
        this.savePt3.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.savePt3ActionPerformed(evt);
            }
        });
        this.openPreferences.setBackground(new Color(255, 0, 0));
        this.openPreferences.setFont(new Font("Lucida Grande", 1, 10));
        this.openPreferences.setText("Open preferences");
        this.openPreferences.setName("Open Preferences");
        this.openPreferences.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openPreferencesActionPerformed(evt);
            }
        });
        this.tmaList.setFont(new Font("Lucida Grande", 1, 10));
        this.tmaList.setModel(new DefaultComboBoxModel<String>(new String[]{"Select TMA No"}));
        this.tmaList.setToolTipText("Select the number of the TMA you wish to monitor");
        this.tmaList.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent evt) {
                EtmaMonitorJ.this.tmaListItemStateChanged(evt);
            }
        });
        this.tmaList.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.tmaListActionPerformed(evt);
            }
        });
        this.courseList.setFont(new Font("Lucida Grande", 1, 10));
        this.courseList.setModel(new DefaultComboBoxModel<String>(new String[]{"Select module"}));
        this.courseList.setToolTipText("Select the Course Code (eg MST124-17J)");
        this.courseList.addItemListener(new ItemListener(){

            @Override
            public void itemStateChanged(ItemEvent evt) {
                EtmaMonitorJ.this.courseListItemStateChanged(evt);
            }
        });
        this.courseList.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.courseListActionPerformed(evt);
            }
        });
        this.tutorList.setFont(new Font("Lucida Grande", 0, 10));
        this.tutorList.setModel(new DefaultComboBoxModel<String>(new String[]{"No courses"}));
        this.tutorList.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.tutorListActionPerformed(evt);
            }
        });
        this.openTutorList.setBackground(new Color(0, 153, 0));
        this.openTutorList.setFont(new Font("Lucida Grande", 1, 10));
        this.openTutorList.setText("List Tutors");
        this.openTutorList.setToolTipText("Lists the tutors whose work has been selected for monitoring. Click on any name to open the record..");
        this.openTutorList.setName("Open Preferences");
        this.openTutorList.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openTutorListActionPerformed(evt);
            }
        });
        this.subNo.setFont(new Font("Lucida Grande", 0, 10));
        this.subNo.setModel(new DefaultComboBoxModel<String>(new String[]{"No courses"}));
        this.subNo.setInheritsPopupMenu(true);
        this.subNo.setName("No Courses");
        this.collectTmas.setBackground(new Color(0, 153, 0));
        this.collectTmas.setFont(new Font("Lucida Grande", 1, 10));
        this.collectTmas.setForeground(new Color(255, 51, 102));
        this.collectTmas.setText("Import Monitoring");
        this.collectTmas.setToolTipText("<html>This button transfers files from your newly downloaded folder to the correct place in your 'etmamonitoring' folder.<br>\nSelect the course folder that you've just downloaded (probably on your desktop),<br>\nThe program will do the rest!");
        this.collectTmas.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.collectTmasActionPerformed(evt);
            }
        });
        this.openTutorFolder.setBackground(new Color(0, 153, 0));
        this.openTutorFolder.setFont(new Font("Lucida Grande", 0, 12));
        this.openTutorFolder.setText("Open Tutor's Folder");
        this.openTutorFolder.setToolTipText("Open's the folder containing the tutor's files for the currently selected tutor whose work is eing moderated.");
        this.openTutorFolder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openTutorFolderActionPerformed(evt);
            }
        });
        this.monitoringSite.setBackground(new Color(0, 153, 0));
        this.monitoringSite.setFont(new Font("Lucida Grande", 1, 10));
        this.monitoringSite.setText(" Monitoring site");
        this.monitoringSite.setToolTipText("<html>Go to the OU eTMA site to download new Monitoring.<br>\n If you want to re-download older Monitoring,<br>\nmake sure the left hand pop-up is set to 'collected' or 'returned\"");
        this.monitoringSite.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.monitoringSiteActionPerformed(evt);
            }
        });
        this.trainingSite.setBackground(new Color(153, 0, 153));
        this.trainingSite.setFont(new Font("Lucida Grande", 1, 10));
        this.trainingSite.setText("Training Site");
        this.trainingSite.setToolTipText("Go to the OU eTMA MOnitoring Training site. The password is 'nicole' without the quotes.");
        this.trainingSite.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.trainingSiteActionPerformed(evt);
            }
        });
        this.listStudents.setBackground(new Color(255, 255, 0));
        this.listStudents.setFont(new Font("Lucida Grande", 1, 10));
        this.listStudents.setText("List Students");
        this.listStudents.setToolTipText("List all the students whose work has been selected.");
        this.listStudents.setName("List Students");
        this.listStudents.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.listStudentsActionPerformed(evt);
            }
        });
        this.openReturnsFolder.setBackground(new Color(255, 102, 0));
        this.openReturnsFolder.setFont(new Font("Lucida Grande", 1, 10));
        this.openReturnsFolder.setText("Open Returns Folder");
        this.openReturnsFolder.setToolTipText("Opens the folder where the zipped files are kept to be returned to MK.");
        this.openReturnsFolder.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openReturnsFolderActionPerformed(evt);
            }
        });
        this.jLabel2.setBackground(new Color(255, 0, 0));
        this.jLabel2.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel2.setText("No of scripts");
        this.jLabel4.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel4.setText("Region");
        this.jLabel6.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel6.setText("Module");
        this.jLabel7.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel7.setText("Version:");
        this.jLabel8.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel8.setText("Pres Code");
        this.jLabel9.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel9.setText("TMA No");
        this.jLabel10.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel10.setText("New:");
        this.jLabel11.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel11.setText("Sub Date:");
        this.jLabel12.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel12.setText("Coll Date");
        this.jLabel13.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel13.setText("Mon level");
        this.jLabel14.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel14.setText("Previous");
        this.jLabel15.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel15.setText("Zip file");
        this.jLabel16.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel16.setText("Zip date");
        this.jLabel17.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel17.setText("Assignment Cut-off Date");
        this.jLabel18.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel18.setText("Zip path");
        this.jLabel19.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel19.setText("Avail");
        this.fontSize.setFont(new Font("Lucida Grande", 0, 10));
        this.fontSize.setModel(new DefaultComboBoxModel<String>(new String[]{"8", "10", "11", "12", "13", "14", "16", "18", "20"}));
        this.fontSize.setToolTipText("Select a font size for the tutor's comments box.");
        this.fontSize.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.fontSizeActionPerformed(evt);
            }
        });
        this.moreDetails.setBackground(new Color(255, 0, 0));
        this.moreDetails.setFont(new Font("Lucida Grande", 0, 10));
        this.moreDetails.setText("More details");
        this.moreDetails.setToolTipText("Shows the full Handler window");
        this.moreDetails.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.moreDetailsActionPerformed(evt);
            }
        });
        this.jLabel22.setFont(new Font("Lucida Grande", 1, 10));
        this.jLabel22.setForeground(new Color(255, 0, 51));
        this.jLabel22.setText("Please provide a comment if any of the checkboxes are left blank in the comment box provided below");
        this.previousReports.setFont(new Font("Lucida Grande", 0, 10));
        this.previousReports.setModel(new DefaultComboBoxModel<String>(new String[]{"Previous reports"}));
        this.previousReports.setToolTipText("Opens any previous reports for this tutort in your browser.");
        this.previousReports.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.previousReportsActionPerformed(evt);
            }
        });
        this.sendEmail.setBackground(new Color(102, 102, 255));
        this.sendEmail.setFont(new Font("Lucida Grande", 0, 10));
        this.sendEmail.setText("Send email");
        this.sendEmail.setToolTipText("Sends an email to this student, with a choice of methods.");
        this.sendEmail.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.sendEmailActionPerformed(evt);
            }
        });
        this.checkSpellingButton.setBackground(new Color(102, 102, 255));
        this.checkSpellingButton.setFont(new Font("Lucida Grande", 0, 10));
        this.checkSpellingButton.setText("Spelling");
        this.checkSpellingButton.setToolTipText("Checks the spelling in the comments field..");
        this.checkSpellingButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.checkSpellingButtonActionPerformed(evt);
            }
        });
        this.backUp.setBackground(new Color(255, 0, 0));
        this.backUp.setFont(new Font("Lucida Grande", 1, 10));
        this.backUp.setText("Backup Reports");
        this.backUp.setToolTipText("Makes a backup copy of your etmamonitoring folder, in the same location as the etmamonitoring folder. May take a minute or two.");
        this.backUp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.backUpActionPerformed(evt);
            }
        });
        this.jLabel30.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel30.setText("Font Size:");
        this.jLabel31.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel31.setText("Module::");
        this.jLabel32.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel32.setText("TMA:");
        GroupLayout jPanel2Layout = new GroupLayout((Container)this.jPanel2);
        this.jPanel2.setLayout((LayoutManager)jPanel2Layout);
        jPanel2Layout.setHorizontalGroup((GroupLayout.Group)jPanel2Layout.createParallelGroup(1).add(0, 0, Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup((GroupLayout.Group)jPanel2Layout.createParallelGroup(1).add(0, 0, Short.MAX_VALUE));
        this.jLabel38.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel38.setText("Monitor details");
        this.jLabel39.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel39.setText("Mon type:");
        this.jLabel1.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel1.setText("Monitor Comment Date");
        this.jLabel40.setFont(new Font("Lucida Grande", 0, 10));
        this.jLabel40.setText("Monitor Return Date");
        this.File.setText("File");
        this.preferencesMenu.setForeground(new Color(255, 0, 0));
        this.preferencesMenu.setText("Preferences");
        this.preferencesMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.preferencesMenuActionPerformed(evt);
            }
        });
        this.File.add(this.preferencesMenu);
        this.savePt3MenuItem.setText("Save report");
        this.savePt3MenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.savePt3MenuItemActionPerformed(evt);
            }
        });
        this.File.add(this.savePt3MenuItem);
        this.collectTmasMenu.setText("Import Monitoring");
        this.collectTmasMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.collectTmasMenuActionPerformed(evt);
            }
        });
        this.File.add(this.collectTmasMenu);
        this.listTmasMenuItem.setText("List Tutors to be monitored");
        this.listTmasMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.listTmasMenuItemActionPerformed(evt);
            }
        });
        this.File.add(this.listTmasMenuItem);
        this.openTmaMenu.setText("Open Tutor's file");
        this.openTmaMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openTmaMenuActionPerformed(evt);
            }
        });
        this.File.add(this.openTmaMenu);
        this.openTmaFolderMenu.setText("Open Tutor's Folder");
        this.openTmaFolderMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openTmaFolderMenuActionPerformed(evt);
            }
        });
        this.File.add(this.openTmaFolderMenu);
        openReturnsFolderMenu.setText("Open Returns Folder");
        openReturnsFolderMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.openReturnsFolderMenuActionPerformed(evt);
            }
        });
        this.File.add(openReturnsFolderMenu);
        this.printDoc.setText("Print main window");
        this.File.add(this.printDoc);
        this.zipFilesMenu.setText("Zip Files");
        this.zipFilesMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.zipFilesMenuActionPerformed(evt);
            }
        });
        this.File.add(this.zipFilesMenu);
        this.createFeedback.setAccelerator(KeyStroke.getKeyStroke(70, 512));
        this.createFeedback.setText("Create/Open feedback script");
        this.createFeedback.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.createFeedbackActionPerformed(evt);
            }
        });
        this.File.add(this.createFeedback);
        this.exitMenuItem.setText("Exit");
        this.exitMenuItem.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.exitMenuItemActionPerformed(evt);
            }
        });
        this.File.add(this.exitMenuItem);
        this.jMenuBar1.add(this.File);
        this.Edit.setText("Edit");
        this.Undo.setText("Undo");
        this.Undo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.UndoActionPerformed(evt);
            }
        });
        this.Edit.add(this.Undo);
        this.Redo.setText("Redo");
        this.Redo.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.RedoActionPerformed(evt);
            }
        });
        this.Edit.add(this.Redo);
        this.copyText.setText("Copy");
        this.copyText.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.copyTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.copyText);
        this.pasteText.setText("Paste");
        this.pasteText.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.pasteTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.pasteText);
        this.selectAllText.setText("Select All");
        this.selectAllText.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.selectAllTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.selectAllText);
        this.cutText.setText("Cut");
        this.cutText.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.cutTextActionPerformed(evt);
            }
        });
        this.Edit.add(this.cutText);
        this.jMenuBar1.add(this.Edit);
        this.jMenu1.setText("Tools");
        this.checkSpelling.setText("Check Spelling");
        this.checkSpelling.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.checkSpellingActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.checkSpelling);
        this.backUpMenu.setText("Backup etmamonitoring Folder...");
        this.backUpMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.backUpMenuActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.backUpMenu);
        this.chooseColor.setText("Choose colours");
        this.chooseColor.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.chooseColorActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.chooseColor);
        this.customize.setText("Customise...");
        this.customize.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.customizeActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.customize);
        this.checkupdates.setText("Check for updates");
        this.checkupdates.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.checkupdatesActionPerformed(evt);
            }
        });
        this.jMenu1.add(this.checkupdates);
        this.jMenuBar1.add(this.jMenu1);
        this.sites.setText("Sites");
        this.monitoringSiteMenu.setText("Go to monitoring Site");
        this.monitoringSiteMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.monitoringSiteMenuActionPerformed(evt);
            }
        });
        this.sites.add(this.monitoringSiteMenu);
        this.trainingSiteMenu.setText("Go to Training Site");
        this.trainingSiteMenu.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.trainingSiteMenuActionPerformed(evt);
            }
        });
        this.sites.add(this.trainingSiteMenu);
        this.jMenuBar1.add(this.sites);
        this.help.setText("Help");
        this.help.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.helpActionPerformed(evt);
            }
        });
        this.etmaHandlerHelp.setText("Monitor Handler help");
        this.etmaHandlerHelp.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.etmaHandlerHelpActionPerformed(evt);
            }
        });
        this.help.add(this.etmaHandlerHelp);
        this.versionNumber.setText("Version info:");
        this.versionNumber.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                EtmaMonitorJ.this.versionNumberActionPerformed(evt);
            }
        });
        this.help.add(this.versionNumber);
        this.jMenuBar1.add(this.help);
        this.setJMenuBar(this.jMenuBar1);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(8, 8, 8).add((Component)this.jLabel32).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.tmaList, -2, 79, -2).add((Component)this.courseList, -2, 120, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.openTutorList, -2, 96, -2).addPreferredGap(0).add((Component)this.listStudents, -2, 126, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.monitoringSite, -2, 108, -2).addPreferredGap(0).add((Component)this.collectTmas))).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.openTutorFolder).addPreferredGap(0).add((Component)this.trainingSite).addPreferredGap(0).add((Component)this.moreDetails)).add((GroupLayout.Group)layout.createSequentialGroup().add(6, 6, 6).add((Component)this.savePt3).addPreferredGap(1).add((Component)this.zipFiles, -2, 97, -2).addPreferredGap(0).add((Component)this.backUp)))).add((GroupLayout.Group)layout.createSequentialGroup().add(4, 4, 4).add((Component)this.jLabel31)).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((Component)this.staff_id, -2, 73, -2).addPreferredGap(0).add((Component)this.tutor_forenames, -2, 111, -2).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(462, 462, 462).add((Component)this.tutor_status, -2, 85, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(18, 18, 18).add((Component)this.tutor_surname, -2, 149, -2).add(18, 18, 18).add((Component)this.checkSpellingButton, -2, 104, -2).addPreferredGap(0).add((Component)this.previousReports, -2, -1, -2).addPreferredGap(0).add((Component)this.openReturnsFolder))))).add(97, 97, 97)).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.basic_monitor_form_path, -2, 30, -2).addPreferredGap(0).add((Component)this.basic_monitor_form_name, -2, 32, -2).addPreferredGap(1).add((Component)this.zip_file, -2, 27, -2).addPreferredGap(0).add((Component)this.download_exe_name, -2, 47, -2).addPreferredGap(0).add((Component)this.staff_tutor_comments, -2, 35, -2).add(6, 6, 6).add((Component)this.personal_id, -2, 33, -2).addPreferredGap(0).add((Component)this.sent_to_tutor_date, -2, 39, -2).add(18, 18, Short.MAX_VALUE).add((Component)this.sendEmail)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2, false).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.monitor_surname, -2, 213, -2).add((Component)this.monitor_forenames, -2, 213, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(8, 8, 8).add((Component)this.jLabel38).addPreferredGap(0, -1, Short.MAX_VALUE).add((Component)this.monitor_staff_id, -2, 84, -2))).add(20, 20, 20))).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0, -1, Short.MAX_VALUE).add((Component)this.late_submission_status, -2, 23, -2).add(29, 29, 29).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.subNo, -2, 10, -2).add((GroupLayout.Group)layout.createParallelGroup(2, false).add(1, (Component)this.tutorList, 0, 0, Short.MAX_VALUE).add(1, (Component)this.tutor_comments, -2, -1, -2))).add(32, 32, 32).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.fhiFileName, -2, 16, -2).add((GroupLayout.Group)layout.createSequentialGroup().add(17, 17, 17).add((Component)this.loadXMLAlt, -2, 30, -2))).add(201, 201, 201)).add((GroupLayout.Group)layout.createSequentialGroup().add(18, 18, 18).add((Component)this.monitor_comments_file, -2, 38, -2).add(142, 142, 142)))).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel10).addPreferredGap(1).add((Component)this.new_tutor, -2, 24, -2).addPreferredGap(0).add((Component)this.jLabel2).addPreferredGap(0).add((Component)this.number_scripts, -2, 40, -2).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0).add((Component)this.jLabel22, -2, 544, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(225, 225, 225).add((Component)this.jLabel30).add(27, 27, 27).add((Component)this.fontSize, -2, -1, -2))).addContainerGap(-1, Short.MAX_VALUE)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1, false).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel17).add(2, (GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(6, 6, 6).add((Component)this.jLabel40)).add((Component)this.jLabel1))).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((Component)this.assgnmt_cut_off_date, -2, 130, -2).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.monitor_return_date, -2, 133, -2).add((Component)this.monitor_comment_date, -2, 133, -2).add(2, (Component)this.staff_tutor_return_date, -2, 134, -2))).add(111, 111, 111).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel4).addPreferredGap(0).add((Component)this.locn_code, -2, 46, -2).addPreferredGap(0).add((Component)this.jLabel6).add(4, 4, 4).add((Component)this.course_code, -2, 61, -2).addPreferredGap(0).add((Component)this.jLabel9).addPreferredGap(0).add((Component)this.assgnmt_suffix, -2, 40, -2).add(18, 18, 18).add((Component)this.jLabel7).addPreferredGap(0).add((Component)this.version_num, -2, 34, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.jLabel12).add((Component)this.jLabel13)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.monitoring_level_code, -2, 32, -2).addPreferredGap(0).add((Component)this.jLabel14).addPreferredGap(0).add((Component)this.previous_monitor_forms, -2, 35, -2).add(18, 18, 18).add((Component)this.jLabel8).addPreferredGap(0).add((Component)this.pres_code, -2, 43, -2).add(18, 18, 18).add((Component)this.jLabel39).addPreferredGap(0).add((Component)this.monitoring_type, -2, 26, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(6, 6, 6).add((Component)this.monitor_collect_date, -2, 192, -2).addPreferredGap(1).add((Component)this.openPreferences)))).add((GroupLayout.Group)layout.createSequentialGroup().add(1, 1, 1).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel16).add((Component)this.jLabel19).add((Component)this.jLabel15)).add(12, 12, 12)).add(2, (GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.jLabel18).add((Component)this.jLabel11)).addPreferredGap(0))).add((GroupLayout.Group)layout.createParallelGroup(2, false).add(1, (Component)this.e_tma_submission_date).add(1, (Component)this.zip_filename).add(1, (Component)this.sample_available_date).add((Component)this.zip_date).add((Component)this.zip_filepath, -2, 191, -2))))).add((Component)this.tmaMarks, -1, 816, Short.MAX_VALUE).add((Component)this.tutor_comments_area)).add(0, 252, Short.MAX_VALUE)))));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.courseList, -2, 23, -2).add((Component)this.jLabel31).add((Component)this.openTutorFolder).add((Component)this.monitoringSite).add((Component)this.collectTmas).add((Component)this.moreDetails).add((Component)this.trainingSite)).add(4, 4, 4).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.openTutorList, -2, 31, -2).add((Component)this.tmaList, -2, 23, -2).add((Component)this.jLabel32, -2, 23, -2).add((Component)this.listStudents).add((Component)this.zipFiles).add((Component)this.savePt3).add((Component)this.backUp, -2, 26, -2)).add(1, 1, 1).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(2, 2, 2).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.tutor_surname, -2, -1, -2).add((Component)this.checkSpellingButton, -2, 25, -2))).add(2, (GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.openReturnsFolder).add((Component)this.previousReports, -2, 24, -2)).add(2, (GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.staff_id, -2, -1, -2).add((Component)this.tutor_forenames, -2, -1, -2))).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.number_scripts, -2, -1, -2).add((Component)this.jLabel2).add((Component)this.jLabel10).add((Component)this.new_tutor, -2, -1, -2)).add(16, 16, 16)).add(2, (GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(4, 4, 4).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel30).add((Component)this.fontSize, -2, 24, -2)).addPreferredGap(0, -1, Short.MAX_VALUE)).add(2, (GroupLayout.Group)layout.createSequentialGroup().addPreferredGap(0, -1, Short.MAX_VALUE).add((Component)this.tutor_status, -2, -1, -2).addPreferredGap(1))).add((Component)this.jLabel22).addPreferredGap(0))).add((Component)this.tmaMarks, -2, 162, -2).add(8, 8, 8).add((Component)this.tutor_comments_area, -2, 188, -2).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(3, 3, 3).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.monitor_comment_date, -2, 19, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.monitor_return_date, -2, -1, -2).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.staff_tutor_return_date, -2, -1, -2).add((Component)this.jLabel40)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.assgnmt_cut_off_date, -2, -1, -2).add((Component)this.jLabel17, -2, 22, -2)).add(24, 24, 24).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.monitor_staff_id, -2, 19, -2).add((Component)this.jLabel38, -1, -1, Short.MAX_VALUE)).add(10, 10, 10).add((Component)this.monitor_forenames, -2, -1, -2).addPreferredGap(0).add((Component)this.monitor_surname, -2, -1, -2))).add(162, 162, 162)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel4, -2, 17, -2).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel6).add((Component)this.locn_code, -2, -1, -2).add((Component)this.course_code, -2, -1, -2).add((Component)this.jLabel9).add((Component)this.assgnmt_suffix, -2, -1, -2).add((Component)this.jLabel7).add((Component)this.version_num, -2, -1, -2))).add(5, 5, 5).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel13).add((Component)this.jLabel14).add((Component)this.previous_monitor_forms, -2, -1, -2).add((Component)this.jLabel8, -2, 13, -2).add((Component)this.pres_code, -2, 19, -2).add((Component)this.monitoring_level_code, -2, -1, -2).add((Component)this.jLabel39).add((Component)this.monitoring_type, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.monitor_collect_date, -2, -1, -2).add((Component)this.jLabel12).add((Component)this.openPreferences)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel16).add((Component)this.zip_date, -2, -1, -2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel19).add((Component)this.sample_available_date, -2, -1, -2)).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.zip_filename, -2, -1, -2).add((Component)this.jLabel15, -2, 14, -2)).add(10, 10, 10).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.e_tma_submission_date, -2, -1, -2).add((Component)this.jLabel11)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.zip_filepath, -2, -1, -2).add((Component)this.jLabel18, -2, 25, -2)).add(0, 0, Short.MAX_VALUE))).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(107, 107, 107).add((Component)this.monitor_comments_file, -2, 10, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add(163, 163, 163).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.zip_file, -2, -1, -2).add((Component)this.basic_monitor_form_path, -2, -1, -2).add((Component)this.basic_monitor_form_name, -2, -1, -2).add((Component)this.download_exe_name, -2, -1, -2).add((Component)this.staff_tutor_comments, -2, -1, -2).add((Component)this.sendEmail, -2, 25, -2).add((Component)this.personal_id, -2, -1, -2).add((Component)this.sent_to_tutor_date, -2, -1, -2)))).add(427, 427, 427).add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add(5, 5, 5).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.fhiFileName, -2, -1, -2).add((Component)this.late_submission_status, -2, -1, -2))).add((GroupLayout.Group)layout.createSequentialGroup().add(34, 34, 34).add((Component)this.loadXMLAlt)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.tutorList, -2, 14, -2).add(21, 21, 21).add((Component)this.tutor_comments, -2, -1, -2).add(13, 13, 13).add((Component)this.subNo, -2, 18, -2)))));
        this.pack();
    }

    private void weightingsWindowClosing(WindowEvent evt) {
    }

    private void customWindowClosing(WindowEvent evt) {
        this.custom.setVisible(false);
        this.buttonHider();
    }

    private void closeCustomizeActionPerformed(ActionEvent evt) {
        this.custom.setVisible(false);
        this.buttonHider();
    }

    private void customizeActionPerformed(ActionEvent evt) {
        this.restoreHidePreferences();
        this.custom.setSize(460, 450);
        this.custom.setVisible(true);
    }

    public void restoreHidePreferences() {
        JCheckBox[] customBoxes = new JCheckBox[]{this.hideEtmaSite, this.hideListTmas, this.hideOpenTmaFolder, this.hideTrainingSite, this.hideSavePt3, this.hideECollectTmas, this.hideZipFiles, this.hideBankComment, this.hideBackupEtmas, this.hideTestJs, this.hideOpenReturnsFolder};
        String buttonPref = this.ourRoot.get("hidingPreferences", "false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false");
        String[] buttonPref1 = buttonPref.split(",");
        Boolean buttonStatus = false;
        for (int i = 0; i < customBoxes.length; ++i) {
            buttonStatus = buttonPref1[i].equals("true") ? Boolean.valueOf(true) : Boolean.valueOf(false);
            customBoxes[i].setSelected(buttonStatus);
        }
    }

    public void exportMarksGrid(JTable marksTable, String marksFile, int headerNo) {
        int returnChar = 13;
    }

    private void zipFilesMenuActionPerformed(ActionEvent evt) {
        this.zipper();
    }

    private void openTmaMenuActionPerformed(ActionEvent evt) {
        this.tmaScriptOpener();
    }

    private void collectTmasMenuActionPerformed(ActionEvent evt) {
        this.monitoringCollector();
    }

    private void openReturnsFolderMenuActionPerformed(ActionEvent evt) {
        this.returnsFolderOpener();
    }

    private void openTmaFolderMenuActionPerformed(ActionEvent evt) {
        this.tutorFolderOpener();
    }

    private void trainingSiteMenuActionPerformed(ActionEvent evt) {
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        String myURI = this.ouTrainingAddress.getText();
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }

    public void selectAllToZip() {
        Object[] options = new Object[]{"Select All", "Select None", "Cancel"};
        Component frame = null;
        int n = JOptionPane.showOptionDialog(frame, "Quick Select:", "", 1, 3, null, options, options[0]);
        int nRow = this.listOfTutors.getRowCount();
        boolean selectAllFlag = true;
        if (n == 1) {
            selectAllFlag = false;
        }
        if (n != 2) {
            boolean noneSelected = true;
            for (int i = 0; i < nRow; ++i) {
                if (this.listOfTutors.getValueAt(i, 0).equals("")) continue;
                if (!this.listOfTutors.getValueAt(i, 7).equals("Unmonitored") && !this.listOfTutors.getValueAt(i, 7).equals("Zipped")) {
                    this.listOfTutors.setValueAt(selectAllFlag, i, 11);
                    noneSelected = false;
                    continue;
                }
                this.listOfTutors.setValueAt(false, i, 11);
            }
            if (noneSelected) {
                JOptionPane.showMessageDialog(null, "There are no completed monitoring records in the list!", "", 1);
            }
        }
    }

    private void monitoringSiteMenuActionPerformed(ActionEvent evt) {
        this.openMainMonitoringSite();
    }

    private void colorWindowSelectorActionPerformed(ActionEvent evt) {
        int selNo;
        this.saveColors(this.currentColorIndex);
        this.currentColorIndex = selNo = this.colorWindowSelector.getSelectedIndex();
        if (selNo > 0) {
            if (this.colorDefaultsFlag[selNo - 1]) {
                this.defaultFlag.setSelected(true);
            } else {
                this.defaultFlag.setSelected(false);
            }
        }
    }

    public void buttonHider() {
        JCheckBox[] customBoxes = new JCheckBox[]{this.hideEtmaSite, this.hideListTmas, this.hideOpenTmaFolder, this.hideTrainingSite, this.hideSavePt3, this.hideECollectTmas, this.hideZipFiles, this.hideBankComment, this.hideBackupEtmas, this.hideSendEmail, this.hideOpenPreferences, this.hideTestJs, this.hideOpenReturnsFolder};
        this.monitoringSite.setVisible(!this.hideEtmaSite.isSelected());
        this.openTutorList.setVisible(!this.hideListTmas.isSelected());
        this.openTutorFolder.setVisible(!this.hideOpenTmaFolder.isSelected());
        this.trainingSite.setVisible(!this.hideTrainingSite.isSelected());
        this.savePt3.setVisible(!this.hideSavePt3.isSelected());
        this.collectTmas.setVisible(!this.hideECollectTmas.isSelected());
        this.zipFiles.setVisible(!this.hideZipFiles.isSelected());
        this.backUp.setVisible(!this.hideBackupEtmas.isSelected());
        this.openPreferences.setVisible(!this.hideOpenPreferences.isSelected());
        this.checkSpellingButton.setVisible(!this.hideTestJs.isSelected());
        this.openReturnsFolder.setVisible(!this.hideOpenReturnsFolder.isSelected());
        Color bgColor = this.listStudents.getBackground();
        if (this.colorRemove.isSelected()) {
            this.monitoringSite.setBackground(bgColor);
            this.openTutorList.setBackground(bgColor);
            this.openTutorFolder.setBackground(bgColor);
            this.trainingSite.setBackground(bgColor);
            this.savePt3.setBackground(bgColor);
            this.collectTmas.setBackground(bgColor);
            this.zipFiles.setBackground(bgColor);
            this.backUp.setBackground(bgColor);
            this.sendEmail.setBackground(bgColor);
            this.openPreferences.setBackground(bgColor);
            this.checkSpellingButton.setBackground(bgColor);
            this.listStudents.setBackground(bgColor);
            this.openReturnsFolder.setBackground(bgColor);
            this.moreDetails.setBackground(bgColor);
            this.setEtmasFolder.setBackground(bgColor);
            this.selectDownloadsFolder.setBackground(bgColor);
            this.selectDictionary.setBackground(bgColor);
            this.wpSelect.setBackground(bgColor);
            this.createMarked.setBackground(bgColor);
            this.checkClosureFlag.setBackground(bgColor);
            this.launchTmaList.setBackground(bgColor);
            this.doubleClickFlag.setBackground(bgColor);
            this.setAudioApp.setBackground(bgColor);
            this.batchZipNew.setBackground(bgColor);
            this.batchZipNew.setEnabled(true);
            this.selectAllFilesToZip.setEnabled(true);
        }
        Object buttonPref = "";
        for (int i = 0; i < customBoxes.length; ++i) {
            buttonPref = (String)buttonPref + customBoxes[i].isSelected() + ",";
        }
        this.ourRoot.putBoolean("colorRemovePreferences", this.colorRemove.isSelected());
        this.ourRoot.put("hidingPreferences", (String)buttonPref);
    }

    private void defaultFlagActionPerformed(ActionEvent evt) {
        int selNo = this.colorWindowSelector.getSelectedIndex();
        if (selNo > 0) {
            this.colorDefaultsFlag[selNo - 1] = this.defaultFlag.isSelected();
            this.ourRoot.putBoolean("colorDefaults0", this.colorDefaultsFlag[0]);
            this.ourRoot.putBoolean("colorDefaults1", this.colorDefaultsFlag[1]);
            this.ourRoot.putBoolean("colorDefaults2", this.colorDefaultsFlag[2]);
        }
    }

    private void colorFrame1WindowClosing(WindowEvent evt) {
        this.saveColors(this.colorWindowSelector.getSelectedIndex());
        this.colorFrame1.setVisible(false);
        if (!this.startUp) {
            JOptionPane.showMessageDialog(null, "You may have to relaunch for the colour changes to come into effect.", "", 1);
        }
    }

    public void setColors() {
        String[] allColors = this.colorPreferences.split(":");
        String[] mainColor = allColors[0].split(";");
        String[] commentsColor = allColors[1].split(";");
        this.gridColor = allColors[2].split(";");
        if (!this.colorDefaultsFlag[0]) {
            this.setBackground(new Color(Integer.parseInt(mainColor[0]), Integer.parseInt(mainColor[1]), Integer.parseInt(mainColor[2])));
        }
        if (!this.colorDefaultsFlag[1]) {
            this.monitor_comments.setBackground(new Color(Integer.parseInt(commentsColor[0]), Integer.parseInt(commentsColor[1]), Integer.parseInt(commentsColor[2])));
        }
        if (!this.colorDefaultsFlag[2]) {
            this.monitoringRatings.setBackground(new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2])));
        }
    }

    public void saveColors(int selNo) {
        Color newColor = null;
        String[] allColors = null;
        String[] mainColor = null;
        String[] commentsColor = null;
        this.gridColor = null;
        if (selNo > 0) {
            Container aFrame;
            if (!this.colorDefaultsFlag[selNo - 1]) {
                newColor = this.jColorChooser1.getSelectionModel().getSelectedColor();
            } else {
                allColors = "219;219;219:255;255;255:184;249;196".split(":");
                String[] tempString = allColors[selNo - 1].split(";");
                int redColor = Integer.parseInt(tempString[0]);
                int blueColor = Integer.parseInt(tempString[1]);
                int greenColor = Integer.parseInt(tempString[2]);
                newColor = new Color(redColor, blueColor, greenColor);
            }
            if (selNo == 1) {
                aFrame = this;
                ((Frame)aFrame).setBackground(newColor);
            }
            if (selNo == 2) {
                aFrame = this.monitor_comments;
                ((JComponent)aFrame).setBackground(newColor);
            }
            if (selNo == 3) {
                aFrame = this.monitoringRatings;
                ((JComponent)aFrame).setBackground(newColor);
            }
            int redColor = newColor.getRed();
            int blueColor = newColor.getBlue();
            int greenColor = newColor.getGreen();
            allColors = this.colorPreferences.split(":");
            mainColor = allColors[0].split(";");
            commentsColor = allColors[1].split(";");
            this.gridColor = allColors[2].split(";");
            allColors[selNo - 1] = redColor + ";" + greenColor + ";" + blueColor;
            this.colorPreferences = allColors[0] + ":" + allColors[1] + ":" + allColors[2];
            this.ourRoot.put("colorPreferences", this.colorPreferences);
        }
    }

    private void chooseColorActionPerformed(ActionEvent evt) {
        this.colorFrame1.setVisible(true);
        this.colorFrame1.setSize(650, 400);
        this.currentColorIndex = this.colorWindowSelector.getSelectedIndex();
    }

    private void studentsListTableMouseReleased(MouseEvent evt) {
        if (this.statusChangeAgree()) {
            this.openSelectedStudent();
            int nScripts = Integer.parseInt(this.number_scripts.getText());
            for (int i = 1; i < nScripts + 1; ++i) {
                this.studentsListTable.setValueAt("No", i, 7);
            }
            this.tutor_status.setText("Unmonitored");
            this.saveDetails();
            this.savedFlag = false;
        } else if (!this.tutor_status.getText().equals("Zipped")) {
            this.openSelectedStudent();
        }
    }

    private void monitor_commentsFocusGained(FocusEvent evt) {
    }

    private void submittedMonitoringWindowClosing(WindowEvent evt) {
        this.saveLocation();
    }

    private void formWindowClosing(WindowEvent evt) {
        this.exitRoutine();
    }

    private void formWindowOpened(WindowEvent evt) {
        if (this.launchTmaList.isSelected() && !this.tmaListError.booleanValue()) {
            this.submittedMonitoring.setVisible(true);
        }
        this.submittedMonitoring.toFront();
        this.tmaListError = false;
    }

    public void setBrowserPreferences() {
        this.ourRoot.put("currentBrowserPreferences", this.currentBrowserPreferences);
        JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentBrowserPreferences = JOptionPane.showInputDialog(null, (Object)"Please enter the short (launch) name of your browser (eg 'firefox').");
            } else {
                _fileChooser.setDialogTitle("Please select application to test students'Javascripts ");
                int path = _fileChooser.showOpenDialog(null);
                File aFile = _fileChooser.getSelectedFile();
                this.currentBrowserPreferences = aFile.getPath();
            }
            this.ourRoot.put("currentBrowserPreferences", this.currentBrowserPreferences);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setWpPreferences() {
        this.ourRoot.put("currentWpPreferences", this.currentWpPreferences);
        JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentWpPath = JOptionPane.showInputDialog(null, (Object)"Please enter the short(launch) name of your Word Processor (eg'ooffice')");
            } else {
                Component frame = null;
                Object[] options = new Object[]{"Default", "Select other"};
                int n = JOptionPane.showOptionDialog(frame, "Do you want to use the system default word processor?", "Default WP", 1, 3, null, options, options[0]);
                if (n == 0) {
                    this.currentWpPath = "System Default";
                    this.currentWpPreferences = "System Default";
                } else {
                    _fileChooser.setDialogTitle("Please select application to open students' scripts: ");
                    int path = _fileChooser.showOpenDialog(null);
                    File aFile = _fileChooser.getSelectedFile();
                    this.currentWpPath = aFile.getPath();
                }
            }
            JOptionPane.showMessageDialog(null, "Word processor path set to " + this.currentWpPath);
            this.wpPath.setText(this.currentWpPath);
            this.ourRoot.put("currentWpPreferences", this.currentWpPath);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void monitor_commentsKeyReleased(KeyEvent evt) {
        KeyStroke thisKey = KeyStroke.getKeyStrokeForEvent(evt);
        KeyStroke commandS = KeyStroke.getKeyStroke(83, 4, true);
        int thisCode = thisKey.getKeyCode();
        if (this.spellCheckFlag.isSelected() && (thisCode == 32 || thisCode == 10)) {
            if (this.suggestFlag.isSelected()) {
                this.liveSpellCheck();
            }
            this.removeHighlights(this.monitor_comments);
            this.highlightAllErrors();
        }
        if (!commandS.equals(thisKey)) {
            this.savedFlag = false;
        }
    }

    public void highlightAllErrors() {
        File aFile = new File(this.dictionaryPath.getText());
        String[] allWords = this.monitor_comments.getText().split(" ");
        int numWords = allWords.length;
        try {
            this.dictionary = new SpellDictionaryHashMap(aFile);
            this.spellChecker = new SpellChecker(this.dictionary);
            for (int i = 0; i <= numWords - 1; ++i) {
                Suggest.SuggestionListener sl = new Suggest.SuggestionListener();
                Suggest.SuggestionListener.wrongWord = "";
                Suggest.SuggestionListener.spellOutputList.clear();
                this.spellChecker.addSpellCheckListener((SpellCheckListener)sl);
                this.spellChecker.checkSpelling((WordTokenizer)new StringWordTokenizer(allWords[i]));
                String wrongWord = Suggest.SuggestionListener.wrongWord;
                this.spellChecker.removeSpellCheckListener((SpellCheckListener)sl);
                if (wrongWord.equals("")) continue;
                this.spellHighlight(wrongWord);
                this.highlight(this.monitor_comments, " " + wrongWord + " ");
            }
        }
        catch (Exception anException) {
            System.out.println("Error 110" + anException);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void liveSpellCheck() {
        String currentText;
        String lastCharacter;
        this.cursorPos = this.monitor_comments.getCaretPosition();
        if (this.cursorPos <= 1 || !(lastCharacter = (currentText = this.monitor_comments.getText().substring(0, this.cursorPos)).substring(currentText.length() - 1)).equals(" ")) return;
        String[] wordList = null;
        wordList = currentText.split(" ");
        String lastWord = wordList[wordList.length - 1];
        try {
            Suggest.SuggestionListener sl = new Suggest.SuggestionListener();
            Suggest.SuggestionListener.wrongWord = "";
            this.spellChecker.addSpellCheckListener((SpellCheckListener)sl);
            this.spellChecker.checkSpelling((WordTokenizer)new StringWordTokenizer(lastWord));
            this.spellReplace();
            String wrongWord = Suggest.SuggestionListener.wrongWord;
            this.spellChecker.removeSpellCheckListener((SpellCheckListener)sl);
            if (wrongWord.equals("")) return;
        }
        catch (Exception anException) {
            System.out.println("Error 111" + anException);
        }
    }

    private void deleteAttachmentActionPerformed(ActionEvent evt) {
        this.attFlag = false;
        String message = this.jTextArea1.getText();
        String attString = "Attachment: " + this.attachmentFile.getName();
        message = message.replaceAll(attString, "");
        this.jTextArea1.setText(message);
    }

    private void addAttachmentButtonActionPerformed(ActionEvent evt) {
        this.addAttachment();
    }

    private void backUpMenuActionPerformed(ActionEvent evt) {
        this.backUp();
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

    private void checkSpellingActionPerformed(ActionEvent evt) {
        this.savedFlag = false;
        this.spellCheckComments();
    }

    private void backUpActionPerformed(ActionEvent evt) {
        this.backUp();
    }

    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        this.messageWindow.setSize(400, 10);
        this.messageWindow.setVisible(true);
        this.messageText.setVisible(true);
        this.messageWindow.setTitle("Please wait.... backing up....");
        this.messageWindow.setLocation(300, 300);
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; ++i) {
                this.copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
            }
        } else {
            int len;
            FileInputStream in = new FileInputStream(sourceLocation);
            FileOutputStream out = new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];
            while ((len = ((InputStream)in).read(buf)) > 0) {
                ((OutputStream)out).write(buf, 0, len);
            }
            ((InputStream)in).close();
            ((OutputStream)out).close();
        }
    }

    public void backUp() {
        ArrayList errorList = new ArrayList();
        File aFile = new File(this.etmaMonitoringFolder.getText());
        JFileChooser _fileChooser = new JFileChooser();
        _fileChooser.setFileSelectionMode(1);
        _fileChooser.setDialogTitle("Choose backup location:");
        int path = _fileChooser.showOpenDialog(null);
        String newLocation = _fileChooser.getSelectedFile().getPath() + "/etmamonitoringBackup_" + this.getDateAndTime();
        File bFile = new File(newLocation);
        Object[] options = new Object[]{"Backup", "Cancel"};
        Component frame = null;
        int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to backup?\nThis could take a couple of minutes.", "Backup", 1, 3, null, options, options[1]);
        if (n == 0) {
            try {
                this.copyDirectory(aFile, bFile);
                this.messageWindow.setVisible(false);
                JOptionPane.showMessageDialog(null, "Backup successful.\nThe backup folder is at " + newLocation);
            }
            catch (Exception anException) {
                this.messageWindow.setVisible(false);
                JOptionPane.showMessageDialog(null, "Backup failed", "", 0);
            }
        }
    }

    private void selectAllTextActionPerformed(ActionEvent evt) {
        Component tempComp = this.getFocusOwner();
        try {
            JTextField tempComp1 = (JTextField)tempComp;
            tempComp1.selectAll();
        }
        catch (Exception anException) {
            this.monitor_comments.selectAll();
        }
    }

    public static void sendKey(int ctrlValue, int keyValue) {
        Robot robot = null;
        try {
            robot = new Robot();
        }
        catch (Throwable e) {
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

    private void pasteTextActionPerformed(ActionEvent evt) {
        int keyModifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        String textToBePasted = this.getClipBoard();
        textToBePasted = textToBePasted.replaceAll(this.rtnString, this.lfString);
        int pastedTextLength = textToBePasted.length();
        int cursorPos1 = this.monitor_comments.getSelectionStart();
        int textLength = this.monitor_comments.getText().length();
        String firstSlice = this.monitor_comments.getText().substring(0, cursorPos1);
        String lastSlice = "";
        try {
            lastSlice = this.monitor_comments.getText().substring(cursorPos1, textLength);
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.monitor_comments.setText(firstSlice + textToBePasted + lastSlice);
        this.monitor_comments.setSelectionStart(cursorPos1 + pastedTextLength);
        this.monitor_comments.setSelectionEnd(cursorPos1 + pastedTextLength);
    }

    private void copyTextActionPerformed(ActionEvent evt) {
        String selectedText = "";
        Component tempComp = this.getFocusOwner();
        try {
            JTextField tempComp1 = (JTextField)tempComp;
            selectedText = tempComp1.getSelectedText();
        }
        catch (Exception anException) {
            selectedText = this.monitor_comments.getSelectedText();
        }
        StringSelection textToCopy = new StringSelection(selectedText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }

    private void RedoActionPerformed(ActionEvent evt) {
        try {
            this.undo.redo();
        }
        catch (CannotUndoException ex) {
            System.out.println("Unable to undo: " + ex);
            ex.printStackTrace();
        }
    }

    private void UndoActionPerformed(ActionEvent evt) {
        try {
            this.undo.undo();
        }
        catch (CannotUndoException ex) {
            System.out.println("Unable to undo: " + ex);
            ex.printStackTrace();
        }
    }

    private void authenticationFlagActionPerformed(ActionEvent evt) {
    }

    private void saveMailPreferencesActionPerformed(ActionEvent evt) {
    }

    private void mailPreferencesActionPerformed(ActionEvent evt) {
    }

    private void sendButtonActionPerformed(ActionEvent evt) {
        String testPreferences = this.smtpHost.getText();
        if (testPreferences.equals("")) {
            JOptionPane.showMessageDialog(null, "Please enter details in 'Mail Preferences'");
        } else {
            try {
                this.postMail(this.emailRecipients, this.messageSubject.getText(), this.jTextArea1.getText(), "michael.hay@btinternet.com");
                JOptionPane.showMessageDialog(null, "Email has been sent!");
                this.attFlag = false;
            }
            catch (Exception anException) {
                JOptionPane.showMessageDialog(null, "Email has NOT been sent! \n" + anException, "", 0);
            }
        }
    }

    public void progressBar(int currValue, JProgressBar pb) {
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

    public JProgressBar progressBarCreate(int max, int min, String barName) {
        this.messageWindow3.setVisible(true);
        this.messageWindow3.setSize(300, 100);
        this.messageWindow3.setLocation(500, 300);
        JProgressBar pb = new JProgressBar(max, min);
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
        JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
        ZipFilter filter1 = new ZipFilter();
        _fileChooser.setFileFilter(filter1);
        _fileChooser.setDialogTitle("Please select a downloaded file to unzip:eg 2008-02-21_2117.zip");
        File aFile = null;
        if (!this.foundItZip) {
            path = _fileChooser.showOpenDialog(null);
            aFile = _fileChooser.getSelectedFile();
        } else {
            aFile = new File(this.fileToUnzip);
        }
        if (path == 1) {
            zipFlag = false;
        } else {
            unzippedFileParent = aFile.getParent();
            try {
                aFile.getName();
                this.unzippedFilePath = aFile.getPath();
                unzippedFileParent = aFile.getParent();
            }
            catch (Exception anException) {
                nullFlag = true;
            }
            this.messageWindow.setSize(400, 10);
            this.messageWindow.setTitle("Please wait.... unzipping....");
            this.messageWindow.setLocation(300, 300);
            this.messageWindow.setAlwaysOnTop(true);
            JProgressBar pb = null;
            try {
                int numberOfFiles = 0;
                ZipFile zf = new ZipFile(aFile.getPath());
                int zfSize = zf.size();
                pb = this.progressBarCreate(0, zfSize, "Unzipping progress");
                Enumeration<? extends ZipEntry> zipEnum = zf.entries();
                Object dir = new String(aFile.getParent());
                if (((String)dir).charAt(((String)dir).length() - 1) != '/') {
                    dir = (String)dir + "/";
                }
                while (zipEnum.hasMoreElements()) {
                    ZipEntry item = zipEnum.nextElement();
                    this.progressBar(++numberOfFiles, pb);
                    this.messageWindow.setTitle("Please wait.... unzipping.... file " + numberOfFiles + " of " + zfSize);
                    if (item.isDirectory()) {
                        String dirPath = (String)dir + item.getName();
                        String reducedPath = dirPath.replace(unzippedFileParent, "");
                        reducedPath = reducedPath.replaceFirst("/", "");
                        reducedPath = reducedPath.replaceFirst("/", "");
                        File newdir = new File(dirPath);
                        if (this.courseDirectory.equals("")) {
                            this.courseDirectory = newdir.getPath();
                        }
                        if (!reducedPath.contains("/") && !reducedPath.contains("\\")) {
                            this.courseDirectoryList.add(newdir.getPath());
                        }
                        newdir.mkdir();
                        continue;
                    }
                    try {
                        int ch;
                        String newfile = (String)dir + item.getName();
                        InputStream is = zf.getInputStream(item);
                        FileOutputStream fos = new FileOutputStream(newfile);
                        while ((ch = is.read()) != -1) {
                            fos.write(ch);
                        }
                        is.close();
                        fos.close();
                    }
                    catch (Exception anException) {
                        System.out.println("Mike error " + anException);
                    }
                }
                zf.close();
                this.messageWindow.setVisible(false);
                if (!this.foundItZip) {
                    JOptionPane.showMessageDialog(null, zfSize + " files unzipped to " + (String)dir);
                    this.messageWindow3.remove(pb);
                }
            }
            catch (Exception e) {
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
            catch (Exception exception) {
                // empty catch block
            }
        }
        return zipFlag;
    }

    private void etmaHandlerHelpActionPerformed(ActionEvent evt) {
        this.helpFrame.setSize(729, 654);
        this.helpFrame.setVisible(true);
    }

    private void helpActionPerformed(ActionEvent evt) {
        this.helpFrame.setVisible(true);
    }

    private void checkSpellingButtonActionPerformed(ActionEvent evt) {
        this.savedFlag = false;
        this.spellCheckComments();
    }

    private void sendEmailActionPerformed(ActionEvent evt) {
        this.chooseEmailMethod();
    }

    public void chooseEmailMethod() {
        Object[] options = new Object[]{"Your POP Client", "Built-in"};
        Component frame = null;
        int n = 0;
        if (this.osName.equals("Mac OS X")) {
            this.sendEmailMethodAlt2();
        } else {
            n = JOptionPane.showOptionDialog(frame, "Send using your own Email client (recommended), or the FileHandler built-in one? ", "Send Email", 1, 3, null, options, options[0]);
            if (n == 0) {
                this.sendEmailMethodAlt();
            } else {
                this.sendEmailMethod();
            }
        }
    }

    public void sendEmailMethod() {
    }

    public void sendEmailMethodAlt() {
    }

    public void sendEmailMethodAlt2() {
    }

    public void addAttachment() {
    }

    public void postMail(String[] recipients, String subject, String message, String from) throws MessagingException {
        boolean debug = false;
    }

    private String addEntries(String sumString) {
        double thisTotal = 0.0;
        sumString = sumString.replace("+", ",");
        String[] sumElements = sumString.split(",");
        for (int i = 0; i < sumElements.length; ++i) {
            thisTotal += Double.parseDouble(sumElements[i]);
        }
        String thisTotalString = String.valueOf(thisTotal);
        thisTotalString = thisTotalString.replace(".0", "");
        return thisTotalString;
    }

    private void previousReportsActionPerformed(ActionEvent evt) {
        block15: {
            File aFile = new File(this.etmaMonitoringFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText() + "/" + this.staff_id.getText() + "/" + this.locn_code.getText() + "/monitor.fhi");
            String parentName = aFile.getParent();
            File bFile = new File(parentName);
            parentName = bFile.getParent();
            String thisPt3 = (String)this.previousReports.getSelectedItem();
            if (thisPt3.endsWith("All")) {
                this.displayAllReports();
            } else {
                Object pt3String = "";
                Runtime thisRun = Runtime.getRuntime();
                try {
                    if (this.osName.equals("Mac OS X") || this.osName.equals("Darwin")) {
                        String[] cmd;
                        pt3String = "file://" + bFile.getPath() + "/" + thisPt3;
                        if (!System.getProperty("java.version").startsWith("1.6.")) {
                            cmd = new String[]{"open", pt3String};
                            thisRun.exec(cmd);
                        } else {
                            cmd = new String[]{"open", pt3String};
                            thisRun.exec(cmd);
                        }
                    }
                    if (this.osName.contains("Windows")) {
                        try {
                            if (!thisPt3.equals("Previous PT3s")) {
                                pt3String = parentName + "\\" + thisPt3;
                                thisRun.exec("explorer.exe " + (String)pt3String);
                            }
                        }
                        catch (Exception cmd) {
                            // empty catch block
                        }
                    }
                    if (!this.osName.contains("Linux")) break block15;
                    Object ppt3 = null;
                    String browser = "firefox";
                    try {
                        if (!thisPt3.equals("Previous PT3s")) {
                            pt3String = parentName + "/" + thisPt3;
                            if (!this.currentBrowserPreferences.equals("")) {
                                browser = this.currentBrowserPreferences;
                            }
                            String[] cmd = new String[]{browser, pt3String};
                            thisRun.exec(cmd);
                        }
                    }
                    catch (Exception anException) {
                        this.progNotFound(browser);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void displayAllReports() {
        BufferedReader buffer = null;
        StringBuilder sb = new StringBuilder(10000);
        Object textOfAllFiles = "";
        int numberOfPt3s = this.previousReports.getItemCount();
        for (int i = 1; i < numberOfPt3s - 1; ++i) {
            Object fileString = "";
            String thisPt3 = (String)this.previousReports.getItemAt(i);
            File aFile = new File(this.parentName1 + "/" + this.locn_code.getText() + "/" + thisPt3);
            try {
                buffer = new BufferedReader(new FileReader(aFile));
                String currentLine = buffer.readLine();
                while (currentLine != null) {
                    fileString = (String)fileString + currentLine + "\n";
                    sb.append(currentLine + "\n");
                    textOfAllFiles = (String)textOfAllFiles + (String)fileString;
                    currentLine = buffer.readLine();
                }
                continue;
            }
            catch (Exception currentLine) {
                continue;
            }
            finally {
                try {
                    buffer.close();
                }
                catch (Exception currentLine) {}
            }
        }
        textOfAllFiles = sb.toString();
        textOfAllFiles = ((String)textOfAllFiles).replaceAll("width=\"100%\" align=\"left\"", "");
        File aFile = new File(this.parentName1 + "/allText.htm");
        BufferedWriter bufferOut = null;
        try {
            bufferOut = new BufferedWriter(new FileWriter(aFile));
            bufferOut.write((String)textOfAllFiles);
        }
        catch (Exception thisPt3) {
        }
        finally {
            try {
                bufferOut.close();
            }
            catch (Exception thisPt3) {}
        }
        Object pt3String = "";
        Runtime thisRun = Runtime.getRuntime();
        try {
            if (this.osName.equals("Mac OS X")) {
                pt3String = "file://" + this.parentName1 + "/allText.htm";
                String[] cmd = new String[]{"open", pt3String};
                thisRun.exec(cmd);
            }
            if (this.osName.contains("Windows")) {
                try {
                    pt3String = this.parentName1 + "\\allText.htm";
                    thisRun.exec("explorer.exe " + (String)pt3String);
                }
                catch (Exception cmd) {
                    // empty catch block
                }
            }
            if (this.osName.contains("Linux")) {
                Object ppt3 = null;
                String browser = "firefox";
                try {
                    pt3String = this.parentName1 + "/allText.htm";
                    if (!this.currentBrowserPreferences.equals("")) {
                        browser = this.currentBrowserPreferences;
                    }
                    String[] cmd = new String[]{browser, pt3String};
                    thisRun.exec(cmd);
                }
                catch (Exception anException) {
                    this.progNotFound(browser);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public long calculateDirectorySize(File aFile) {
        File[] listOfFiles;
        long fileSize = 0L;
        for (File thisFile : listOfFiles = aFile.listFiles()) {
            fileSize += thisFile.length();
        }
        return fileSize;
    }

    private void setupPreviousPt3s() {
        String parentName = "";
        while (this.previousReports.getItemCount() > 1) {
            this.previousReports.removeItemAt(1);
        }
        try {
            File aFile = new File(this.etmaMonitoringFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText() + "/" + this.staff_id.getText() + "/" + this.locn_code.getText() + "/monitor.fhi");
            parentName = aFile.getParent();
            File bFile = new File(parentName);
            this.parentName1 = parentName = bFile.getParent();
            File cFile = new File(parentName);
            Object[] pt3Files1 = bFile.listFiles();
            Arrays.sort(pt3Files1);
            for (int i = 0; i < pt3Files1.length; ++i) {
                if (!((File)pt3Files1[i]).getName().contains("TMA0")) continue;
                this.previousReports.addItem(((File)pt3Files1[i]).getName());
            }
            this.previousReports.addItem("All");
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void moreDetailsActionPerformed(ActionEvent evt) {
        this.size = this.ourRoot.getBoolean("size", true);
        if (this.size) {
            this.smallWindowSize[1] = this.getHeight();
            this.ourRoot.putDouble("smallWindowHeight", this.getHeight());
            this.largeWindowSize[1] = (int)this.ourRoot.getDouble("largeWindowHeight", 740.0);
            this.smallWindowSize[0] = this.getWidth();
            this.ourRoot.putDouble("smallWindowWidth", this.getWidth());
            this.largeWindowSize[0] = this.smallWindowSize[0];
            this.setSize(this.largeWindowSize[0], this.largeWindowSize[1]);
            this.moreDetails.setText("Fewer details");
            this.moreDetails.setToolTipText("Shows the smaller Handler window");
            this.ourRoot.putBoolean("size", false);
            this.size = false;
        } else {
            this.largeWindowSize[1] = this.getHeight();
            this.ourRoot.putDouble("largeWindowHeight", this.getHeight());
            this.smallWindowSize[1] = (int)this.ourRoot.getDouble("smallWindowHeight", 510.0);
            this.largeWindowSize[0] = this.getWidth();
            this.ourRoot.putDouble("largeWindowWidth", this.getWidth());
            this.smallWindowSize[0] = this.largeWindowSize[0];
            this.setSize(this.smallWindowSize[0], this.smallWindowSize[1]);
            this.moreDetails.setText("More details");
            this.moreDetails.setToolTipText("Shows the full Handler window");
            this.ourRoot.putBoolean("size", true);
            this.size = true;
        }
    }

    private void fontSizeActionPerformed(ActionEvent evt) {
        try {
            this.fSize = Integer.parseInt((String)this.fontSize.getSelectedItem());
            this.monitor_comments.setFont(new Font("Lucida Grande", 0, this.fSize));
            if (this.globalFontsPreferences) {
                this.setFonts(this.fSize);
            }
            this.ourRoot.putInt("fontSize", this.fSize);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void setFonts(int fontSize) {
        this.monitoringRatings.setFont(new Font("Lucida Grande", 0, fontSize));
        this.listOfTutors.setFont(new Font("Lucida Grande", 0, fontSize));
        this.courseList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.tmaList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.tutor_forenames.setFont(new Font("Lucida Grande", 0, fontSize));
        this.tutor_surname.setFont(new Font("Lucida Grande", 0, fontSize));
        this.staff_id.setFont(new Font("Lucida Grande", 0, fontSize));
        this.studentsListTable.setFont(new Font("Lucida Grande", 0, fontSize));
        this.monitoringSite.setFont(new Font("Lucida Grande", 0, fontSize));
        this.collectTmas.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTutorList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTutorList.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openTutorFolder.setFont(new Font("Lucida Grande", 0, fontSize));
        this.trainingSite.setFont(new Font("Lucida Grande", 0, fontSize));
        this.savePt3.setFont(new Font("Lucida Grande", 0, fontSize));
        this.previousReports.setFont(new Font("Lucida Grande", 0, fontSize));
        this.listStudents.setFont(new Font("Lucida Grande", 0, fontSize));
        this.checkSpellingButton.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openReturnsFolder.setFont(new Font("Lucida Grande", 0, fontSize));
        this.zipFiles.setFont(new Font("Lucida Grande", 0, fontSize));
        this.backUp.setFont(new Font("Lucida Grande", 0, fontSize));
        this.studentsListTable.setFont(new Font("Lucida Grande", 0, fontSize));
        this.moreDetails.setFont(new Font("Lucida Grande", 0, fontSize));
        this.openReturnsFolder.setFont(new Font("Lucida Grande", 0, fontSize));
        this.number_scripts.setFont(new Font("Lucida Grande", 0, fontSize));
        this.tutor_status.setFont(new Font("Lucida Grande", 0, fontSize));
        this.new_tutor.setFont(new Font("Lucida Grande", 0, fontSize));
        this.jLabel22.setFont(new Font("Lucida Grande", 0, fontSize));
    }

    public static void setUIFont(FontUIResource f) {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (!(value instanceof FontUIResource)) continue;
            UIManager.put(key, f);
        }
    }

    private void tmaSelectMenuActionPerformed(ActionEvent evt) {
        this.filter = (String)this.tmaSelectMenu.getSelectedItem();
        this.openList();
    }

    private void bankComment() {
    }

    private void listTmasMenuItemActionPerformed(ActionEvent evt) {
        this.openList();
    }

    private void savePt3MenuItemActionPerformed(ActionEvent evt) {
        Timer timer1 = new Timer();
        ScheduleRunner task1 = new ScheduleRunner();
        timer1.schedule((TimerTask)task1, 1000L);
        this.saveDetails();
        JOptionPane.showMessageDialog(null, "Changes saved!");
    }

    public void progNotFound(String aString) {
        JOptionPane.showMessageDialog(null, "Sorry, the program '" + aString + "' doesn't seem to exist!\nIf you have set it in Preferences, make sure you've set it to the launcher name.", "", 0);
    }

    public void commentBankOpener() {
    }

    private void preferencesMenuActionPerformed(ActionEvent evt) {
        this.openPrefsWindow();
    }

    public void colorSetter(JTable aTable, final List<Integer> aRow, final List<Integer> bRow, final Color aColor, final Color bColor, final boolean inBold) {
        aTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Font font = comp.getFont();
                if (aRow.contains(row)) {
                    comp.setBackground(aColor);
                    if (inBold) {
                        comp.setFont(font.deriveFont(1));
                    }
                } else if (bRow.contains(row)) {
                    comp.setBackground(bColor);
                    if (inBold) {
                        comp.setFont(font.deriveFont(1));
                    }
                } else {
                    comp.setBackground(new Color(Integer.parseInt(EtmaMonitorJ.this.gridColor[0]), Integer.parseInt(EtmaMonitorJ.this.gridColor[1]), Integer.parseInt(EtmaMonitorJ.this.gridColor[2])));
                    comp.setFont(font.deriveFont(0));
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        });
    }

    public void returnsFolderOpener() {
        String returnsFolder = this.etmaMonitoringFolder.getText() + this.returnsName;
        try {
            this.calculateTotals();
        }
        catch (Exception exception) {
            // empty catch block
        }
        Runtime thisRun = Runtime.getRuntime();
        try {
            Desktop.getDesktop().open(new File(returnsFolder));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void returnsFolderCheckEmpty() {
        File currentReturnsFolder = new File(this.etmaMonitoringFolder.getText() + "/returns");
        File[] returnsFolderContents = currentReturnsFolder.listFiles();
        String todaysDate = this.makeZipFileName().substring(0, 10);
        todaysDate = todaysDate.replace("-", "_");
        ArrayList<File> oldZipFiles = new ArrayList<File>();
        for (int i = 0; i < returnsFolderContents.length; ++i) {
            Path thisPath = returnsFolderContents[i].toPath();
            Calendar cal = Calendar.getInstance();
            cal.add(5, -1);
            Date yesterday = cal.getTime();
            Date lastAccess = null;
            try {
                BasicFileAttributes attr = Files.readAttributes(thisPath, BasicFileAttributes.class, new LinkOption[0]);
                lastAccess = new Date(attr.lastModifiedTime().to(TimeUnit.MILLISECONDS));
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (lastAccess.compareTo(yesterday) > -1) continue;
            oldZipFiles.add(returnsFolderContents[i]);
        }
        if (oldZipFiles.size() > 0) {
            Object[] options = new Object[]{"Delete", "Don't delete"};
            Component frame = null;
            int n = JOptionPane.showOptionDialog(frame, "There are " + oldZipFiles.size() + " files in your returns folder which were produced before today.\nThey may be no longer needed - do you want to delete them?\n(You can turn this alert off in the preferences)", "", 1, 3, null, options, options[1]);
            if (n == 0) {
                for (File dFile : oldZipFiles) {
                    if (!dFile.isDirectory()) {
                        dFile.delete();
                        continue;
                    }
                    EtmaMonitorJ.deleteDir(dFile);
                }
                JOptionPane.showMessageDialog(null, "Files  deleted");
            } else {
                JOptionPane.showMessageDialog(null, "Files not deleted");
            }
        }
    }

    private void openReturnsFolderActionPerformed(ActionEvent evt) {
        this.returnsFolderOpener();
    }

    private void jScrollPane1MouseReleased(MouseEvent evt) {
    }

    private void saveWeightingsActionPerformed(ActionEvent evt) {
        this.saveWeightings();
    }

    public void checkWeightingsCodes() {
    }

    public void saveWeightings() {
    }

    private void exitMenuItemActionPerformed(ActionEvent evt) {
        this.exitRoutine();
    }

    public void exitRoutine() {
        Object[] options = new Object[]{"Save changes and Quit", "Quit without saving changes", "Cancel"};
        Component frame = null;
        this.saveLocation();
        if (!this.tutor_surname.getText().equals("")) {
            int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to quit?", "Quitting " + this.getTitle(), 1, 3, null, options, options[0]);
            if (n == 1) {
                try {
                    this.timer2.cancel();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                System.exit(0);
            }
            if (n == 0) {
                try {
                    this.saveDetails();
                    try {
                        this.timer2.cancel();
                    }
                    catch (Exception exception) {}
                }
                catch (Exception anException) {
                    JOptionPane.showMessageDialog(null, "Changes NOT saved!");
                }
                System.exit(0);
            }
            if (n == 2) {
                this.setVisible(true);
            }
        } else {
            Object[] options1 = new Object[]{"Quit", "Cancel"};
            int n1 = JOptionPane.showOptionDialog(frame, "Are you sure you want to quit?", "Quitting " + this.getTitle(), 1, 3, null, options1, options1[0]);
            if (n1 == 0) {
                try {
                    this.timer2.cancel();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                System.exit(0);
            }
        }
    }

    public void clearZipTicks() {
    }

    private String changeStatus(String mainString, String startTag, String newValue) {
        startTag = "<" + (String)startTag + ">";
        String endTag = ((String)startTag).replace("<", "</");
        int tagPos1 = mainString.indexOf((String)startTag);
        int tagPos2 = mainString.indexOf(endTag);
        String chunk1 = mainString.substring(0, tagPos1 += ((String)startTag).length());
        String chunk2 = mainString.substring(tagPos2, mainString.length());
        String tempString = mainString.substring(tagPos1, tagPos2);
        String newMainString = chunk1 + newValue + chunk2;
        return newMainString;
    }

    public int[] getSelectedFiles() {
        ArrayList<Integer> fileList1 = new ArrayList<Integer>();
        ArrayList fileBool1 = new ArrayList();
        fileList1.clear();
        Integer selFile = 0;
        Integer selFile2 = 0;
        int nRow = this.listOfTutors.getRowCount();
        int[] selectedRows = this.listOfTutors.getSelectedRows();
        Boolean zipSelect1 = false;
        selFile = 0;
        while (selFile < nRow) {
            zipSelect1 = (Boolean)this.listOfTutors.getValueAt(selFile, 11);
            try {
                if (zipSelect1.booleanValue()) {
                    fileList1.add(selFile);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            Integer n = selFile;
            Integer n2 = selFile = Integer.valueOf(selFile + 1);
        }
        int[] selectedRows1 = new int[fileList1.size()];
        for (int i = 0; i < fileList1.size(); ++i) {
            selectedRows1[i] = (Integer)fileList1.get(i);
        }
        return selectedRows1;
    }

    public void openSelectedStudent() {
        try {
            int rowNo = this.studentsListTable.getSelectedRow();
            int colNo = this.studentsListTable.getSelectedColumn();
            String forename = (String)this.studentsListTable.getValueAt(rowNo, 1);
            String surname = (String)this.studentsListTable.getValueAt(rowNo, 2);
            this.scriptTable.setTitle(forename + " " + surname);
            String testForBlank = (String)this.studentsListTable.getValueAt(rowNo, 1);
            if (!testForBlank.equals("")) {
                if (colNo == 3) {
                    this.toggler(this.markingCategories, this.studentsListTable, rowNo, colNo);
                }
                if (colNo == 4) {
                    this.toggler(this.commentingCategories, this.studentsListTable, rowNo, colNo);
                }
                if (colNo == 5) {
                    this.studentsListTable.setValueAt("", rowNo, colNo);
                }
                if (colNo == 7) {
                    this.toggler(this.yesNoCategories, this.studentsListTable, rowNo, colNo);
                }
                if (colNo == 0 || colNo == 1 || colNo == 2) {
                    this.openListofStudentScripts();
                    if (this.checkAnnotated()) {
                        this.studentsListTable.setValueAt("Yes", rowNo, 6);
                    } else {
                        this.studentsListTable.setValueAt("No", rowNo, 6);
                    }
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.checkStatus();
        this.savedFlag = false;
    }

    public void toggler(String[] aString, JTable bTable, int aRow, int aColumn) {
        int newToggle;
        String currentEntry = (String)bTable.getValueAt(aRow, aColumn);
        int lString = aString.length;
        int toggleNo = 0;
        for (int i = 0; i < aString.length; ++i) {
            if (!aString[i].equals(currentEntry)) continue;
            toggleNo = i;
            break;
        }
        if ((newToggle = toggleNo + 1) > lString - 1) {
            newToggle = 0;
        }
        bTable.setValueAt(aString[newToggle], aRow, aColumn);
    }

    public void openListofStudentScripts() {
        String studentPID;
        int rowNo;
        ArrayList<String> filesAwaitingZipping = new ArrayList<String>();
        try {
            File[] ffz;
            for (File file : ffz = new File(this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText()).listFiles()) {
                if (!file.isFile()) continue;
                filesAwaitingZipping.add(file.getName());
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
        this.scriptTable.setVisible(true);
        this.scriptTable.setSize(450, 250);
        this.setSizesOfScriptList();
        this.currentStudentRow = rowNo = this.studentsListTable.getSelectedRow();
        this.currentStudentPID = studentPID = (String)this.studentsListTable.getValueAt(rowNo, 0);
        this.studentFolderPathName = this.etmaMonitoringFolder.getText() + "/";
        this.studentFolderPathName = this.studentFolderPathName + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText() + "/";
        this.studentFolderPathName = this.studentFolderPathName + this.staff_id.getText() + "/" + this.locn_code.getText() + "/" + studentPID;
        ArrayList results = new ArrayList();
        int nRows1 = this.scriptsSummaryTable.getRowCount();
        System.out.println(nRows1);
        try {
            for (int thisRow = 0; thisRow < nRows1; ++thisRow) {
                this.scriptsSummaryTable.setValueAt("", thisRow, 0);
                this.scriptsSummaryTable.setValueAt("", thisRow, 1);
            }
        }
        catch (Exception anException) {
            System.out.println(anException);
        }
        List<String> thisRecord = this.studentFileFullList.get(rowNo - 1);
        Map<String, String> thisAnnotationRecord = this.studentAnnotationMapList.get(rowNo - 1);
        int noOfFiles = thisRecord.size();
        System.out.println(thisRecord);
        for (int thisFile = 0; thisFile < noOfFiles; ++thisFile) {
            String thisPath = thisRecord.get(thisFile);
            String thisAnnotation = "";
            try {
                thisAnnotation = thisAnnotationRecord.get(thisPath);
                if (thisAnnotation.equals("null")) {
                    thisAnnotation = "No";
                }
            }
            catch (Exception e) {
                thisAnnotation = "No";
                System.out.println("Open list error 108" + e);
            }
            File temp = new File(thisPath);
            String thisFileName = temp.getName();
            this.scriptsSummaryTable.setValueAt(thisFileName, thisFile, 0);
            this.scriptsSummaryTable.setValueAt(thisAnnotation, thisFile, 1);
        }
        this.savedFlag = false;
    }

    public void getAllStudentScripts() {
        File[] files;
        String studentPID = (String)this.studentsListTable.getValueAt(1, 0);
        this.studentFolderPathName = this.etmaMonitoringFolder.getText() + "/";
        this.studentFolderPathName = this.studentFolderPathName + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText() + "/";
        this.studentFolderPathName = this.studentFolderPathName + this.staff_id.getText() + "/" + this.locn_code.getText() + "/" + studentPID;
        ArrayList<String> results = new ArrayList<String>();
        for (File file : files = new File(this.studentFolderPathName).listFiles()) {
            if (file.isFile()) {
                results.add(file.getName());
            }
            for (int i = 0; i < results.size(); ++i) {
                this.scriptsSummaryTable.setValueAt(results.get(i), i, 0);
            }
        }
    }

    public void getListOfAllStudents() {
        this.allStudents.clear();
        this.allTmas.clear();
        String courseFolderPath = this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem();
        File courseFolderFile = new File(courseFolderPath);
        File[] tmaFolders = courseFolderFile.listFiles();
        for (int i = 0; i < tmaFolders.length; ++i) {
            File thisFile = tmaFolders[i];
            String thisFileName = thisFile.getName();
            int lenThisFileName = thisFileName.length();
            if (!thisFile.isDirectory() || lenThisFileName >= 3) continue;
            this.allTmas.add(thisFile.getName());
            File[] studentsForThisTma = thisFile.listFiles();
            for (int j = 0; j < studentsForThisTma.length; ++j) {
                if (!studentsForThisTma[j].isDirectory()) continue;
                this.allStudents.add(studentsForThisTma[j].getName());
            }
        }
    }

    public boolean getPartMarks(File aFile, int aRow) {
        return false;
    }

    public void openGradesListAlt() {
    }

    public void openGradesList() {
    }

    private void listStudentsActionPerformed(ActionEvent evt) {
        this.studentSummary.setVisible(true);
    }

    private void resize(JTable table, int newSize, int colNo) {
        table.setAutoResizeMode(0);
        int vColIndex = colNo;
        TableColumn col = table.getColumnModel().getColumn(vColIndex);
        int width = newSize;
        col.setPreferredWidth(width);
        if (!table.equals(this.studentsListTable) || colNo > 3) {
            // empty if block
        }
    }

    private void setSizesOfTmaList() {
        for (int i = 0; i < this.tmaListSizes.length; ++i) {
            this.resize(this.listOfTutors, this.tmaListSizes[i], i);
        }
    }

    private void setSizesOfMonList() {
        for (int i = 0; i < this.monListSizes.length; ++i) {
            this.resize(this.monitoringRatings, this.monListSizes[i], i);
        }
    }

    private void setSizesOfGradesList() {
        for (int i = 0; i < this.studentListSizes.length; ++i) {
            this.resize(this.studentsListTable, this.studentListSizes[i], i);
        }
    }

    private void setSizesOfScriptList() {
        for (int i = 0; i < this.scriptListSizes.length; ++i) {
            this.resize(this.scriptsSummaryTable, this.scriptListSizes[i], i);
        }
    }

    private void setSizesOfStudentList() {
        for (int i = 0; i < this.studentListSizes.length; ++i) {
            this.resize(this.studentsListTable, this.studentListSizes[i], i);
        }
        String[] header = new String[]{"PI", "Forename", "Surname", "Grade", "Summary and Script Comments", " ", "Annotated", "Complete?", "Student sent", "Tutor Received", "", ""};
        for (int i = 0; i < this.studentsListTable.getColumnCount(); ++i) {
            TableColumn column1 = this.studentsListTable.getTableHeader().getColumnModel().getColumn(i);
            column1.setHeaderValue(header[i]);
        }
    }

    private void setSizesOfPartScoresList() {
        for (int i = 0; i < this.partScoresListSizes.length; ++i) {
            this.resize(this.scriptsSummaryTable, this.partScoresListSizes[i], i);
        }
    }

    private void sizeColumns(JTable table) {
        this.studentsListTable.setAutoResizeMode(0);
        TableColumnModel columns = table.getColumnModel();
        TableModel data = table.getModel();
        int margin = columns.getColumnMargin() * 2;
        int columnCount = columns.getColumnCount();
        int rowCount = data.getRowCount();
        for (int col = 0; col < columnCount; ++col) {
            TableColumn column = columns.getColumn(col);
            int modelCol = column.getModelIndex();
            int width = 0;
            for (int row = 0; row < rowCount; ++row) {
                TableCellRenderer r = table.getCellRenderer(row, col);
                int w = r.getTableCellRendererComponent((JTable)table, (Object)data.getValueAt((int)row, (int)modelCol), (boolean)false, (boolean)false, (int)row, (int)col).getPreferredSize().width;
                if (w <= width) continue;
                width = w;
            }
        }
    }

    private void setupWeightings() {
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; ++i) {
                boolean success = EtmaMonitorJ.deleteDir(new File(dir, children[i]));
                if (success) continue;
                return false;
            }
        }
        return dir.delete();
    }

    private void trainingSiteActionPerformed(ActionEvent evt) {
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        String myURI = this.ouTrainingAddress.getText();
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }

    public void openMainMonitoringSite() {
        if (this.autoImportFlag1.isSelected()) {
            this.startImportTimer();
        }
        String myURI = this.ouEtmaAddress.getText();
        try {
            Desktop.getDesktop().browse(new URI(myURI));
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unable to connect to " + myURI, "Sorry!", 2);
        }
    }

    public void startImportTimer() {
        try {
            this.timer2.cancel();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.timer2 = new Timer();
        ScheduleRunner2 task2 = new ScheduleRunner2();
        this.timer2.schedule((TimerTask)task2, 5000L, 5000L);
    }

    private void monitoringSiteActionPerformed(ActionEvent evt) {
        this.openMainMonitoringSite();
    }

    public void linuxOpen(String fileName) {
        int numberOfManagers = this.LINUXFILEMANAGER.length;
        boolean managerFound = false;
        for (int i = 0; i < numberOfManagers; ++i) {
            if (managerFound) continue;
            try {
                Runtime thisRun = Runtime.getRuntime();
                String[] cmd = new String[]{this.LINUXFILEMANAGER[i], fileName};
                thisRun.exec(cmd);
                managerFound = true;
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public List listAllFiles(List<File> fileList) {
        List fileListString = null;
        List<File> innerFileList = null;
        for (File aFile : fileList) {
            fileListString.add(aFile.getPath());
            if (!aFile.isDirectory()) continue;
            innerFileList = Arrays.asList(aFile.listFiles());
            for (File bFile : innerFileList) {
                fileListString.add(bFile.getPath());
            }
        }
        return fileList;
    }

    public void tmaScriptOpener() {
    }

    public String browserOpenString(String fileToOpen) {
        return "";
    }

    public void tmaOpener(String tmaFiletoOpen) {
        block20: {
            System.out.println(tmaFiletoOpen);
            String appPath = this.wpPath.getText();
            String appName = "";
            String bundleId = "";
            String openString = "";
            boolean slashPos = false;
            this.currentStudentScript = tmaFiletoOpen;
            File tmaFile = new File(tmaFiletoOpen);
            long folderSize = this.calculateDirectorySize(tmaFile.getParentFile());
            if (folderSize > 9500000L && this.sizeWarnFlagPreferences) {
                long folderSizeKB = folderSize / 1000L;
                JOptionPane.showMessageDialog(null, "This file is about " + folderSizeKB + "KB!\nIt may be rather big to annotate and return.\nYou can disable this warning in etmaHandlerJ-> Preferences.");
            }
            try {
                String[] cmd3;
                Runtime thisRun = Runtime.getRuntime();
                if (this.osName.equals("Mac OS X")) {
                    if (this.wpPath.getText().equals("System Default")) {
                        try {
                            cmd3 = new String[]{"open", tmaFiletoOpen};
                            thisRun.exec(cmd3);
                        }
                        catch (Exception cmd2) {}
                    } else if (tmaFiletoOpen.endsWith(".htm")) {
                        cmd3 = new String[]{"open", tmaFiletoOpen};
                        thisRun.exec(cmd3);
                    } else {
                        String tmaFiletoOpenMac = tmaFiletoOpen.replace("/", ":");
                        String appPathMac = appPath.replace("/", ":");
                        String argsString = "tell application \"Finder\" to copy name of startup disk to std\ntell application \"Finder\" to open file (std &\"" + tmaFiletoOpenMac + "\")using (std &\"" + appPathMac + "\")";
                        String[] args = new String[]{"osascript", "-e", argsString};
                        thisRun.exec(args);
                    }
                }
                if (this.osName.contains("Darwin")) {
                    try {
                        cmd3 = new String[]{"open", tmaFiletoOpen};
                        thisRun.exec(cmd3);
                    }
                    catch (Exception cmd3) {
                        // empty catch block
                    }
                }
                if (this.osName.contains("Windows")) {
                    String tmaFiletoOpenWindows = tmaFiletoOpen.replace('/', '\\');
                    if (this.currentWpPreferences.equals("System Default") || this.currentWpPreferences.equals("")) {
                        thisRun.exec("explorer.exe " + tmaFiletoOpenWindows);
                    } else {
                        String[] cmd4 = new String[]{appPath, tmaFiletoOpenWindows};
                        thisRun.exec(cmd4);
                    }
                }
                if (!this.osName.contains("Linux")) break block20;
                if (!this.wpPath.getText().equals("")) {
                    this.linuxWP = this.wpPath.getText();
                }
                try {
                    if (tmaFiletoOpen.contains(".htm")) {
                        cmd3 = new String[]{"firefox", tmaFiletoOpen};
                        thisRun.exec(cmd3);
                        break block20;
                    }
                    cmd3 = new String[]{this.linuxWP, tmaFiletoOpen};
                    thisRun.exec(cmd3);
                }
                catch (Exception anException) {
                    this.progNotFound(this.linuxWP);
                }
            }
            catch (Exception anException) {
                JOptionPane.showMessageDialog(null, anException);
            }
        }
    }

    public void mp3Opener(String tmaFiletoOpen) {
        String appPath = this.audioPath.getText();
        String appName = "";
        String bundleId = "";
        String openString = "";
        boolean slashPos = false;
        this.currentStudentScript = tmaFiletoOpen;
        String extension = tmaFiletoOpen.substring(tmaFiletoOpen.lastIndexOf("."));
        if (extension.equals("mp3")) {
            try {
                String[] cmd;
                Runtime thisRun = Runtime.getRuntime();
                if (this.osName.equals("Mac OS X")) {
                    DocumentFile docFile = new DocumentFile(tmaFiletoOpen);
                    if (this.audioPath.getText().equals("System Default")) {
                        docFile.open();
                    } else {
                        docFile.openWith(new File(appPath));
                    }
                }
                if (this.osName.contains("Windows")) {
                    if (this.currentAudioPreferences.equals("System Default") || this.currentAudioPreferences.equals("")) {
                        thisRun.exec("explorer.exe " + tmaFiletoOpen);
                    } else {
                        cmd = new String[]{appPath, tmaFiletoOpen};
                        thisRun.exec(cmd);
                    }
                }
                if (this.osName.contains("Linux")) {
                    if (!this.audioPath.getText().equals("")) {
                        this.linuxAudio = this.audioPath.getText();
                    }
                    try {
                        cmd = new String[]{this.linuxAudio, tmaFiletoOpen};
                        thisRun.exec(cmd);
                    }
                    catch (Exception anException) {
                        this.progNotFound(this.linuxAudio);
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void makeCopy(File src, File dst) throws IOException {
        int len;
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        while ((len = ((InputStream)in).read(buf)) > 0) {
            ((OutputStream)out).write(buf, 0, len);
        }
        ((InputStream)in).close();
        ((OutputStream)out).close();
    }

    public void tutorFolderOpener() {
        File fhiFile = new File(this.fhiFileName.getText());
        String tmaFolder = fhiFile.getParent();
        try {
            Desktop.getDesktop().open(new File(tmaFolder));
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.calculateTotals();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void openTutorFolderActionPerformed(ActionEvent evt) {
        this.tutorFolderOpener();
    }

    public void monitoringCollector() {
        try {
            this.calculateTotals();
        }
        catch (Exception exception) {
            // empty catch block
        }
        boolean zipFlag = true;
        Object[] options = new Object[]{"Yes", "No", "Cancel"};
        Component frame = null;
        int n = JOptionPane.showOptionDialog(frame, "Has the downloaded file from the OU site been Unzipped?", "", 1, 3, null, options, options[0]);
        if (n == 1) {
            zipFlag = this.unZipAlt();
            this.autoImportFlag = true;
        }
        if (n != 2 && zipFlag) {
            if (this.submittedMonitoring.isVisible()) {
                this.saveLocation();
            }
            this.collectMonitoring();
        }
        this.courseList.setSelectedItem(this.courseName);
        if (this.submittedMonitoring.isVisible()) {
            this.openList();
        }
    }

    private void collectTmasActionPerformed(ActionEvent evt) {
        this.monitoringCollector();
    }

    private void collectMonitoring() {
        ArrayList<CallSite> errorList = new ArrayList<CallSite>();
        File aFile = null;
        boolean duplicateFlag = false;
        if (this.pathToDownloadedFile.equals("")) {
            if (!this.autoImportFlag.booleanValue() || this.courseDirectoryList.size() == 0) {
                JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
                _fileChooser.setFileSelectionMode(1);
                _fileChooser.setDialogTitle("Please select downloaded modulefolder (eg 'M150-06J') :");
                int path = _fileChooser.showOpenDialog(null);
                aFile = _fileChooser.getSelectedFile();
                this.courseDirectoryList.add(aFile.getPath());
            } else {
                aFile = new File(this.courseDirectory);
            }
        }
        for (String tempDirectory : this.courseDirectoryList) {
            aFile = new File(tempDirectory);
            boolean continueFlag = true;
            if (aFile.getPath().contains(this.etmaMonitoringFolder.getText())) {
                JOptionPane.showMessageDialog(null, "It looks as though your downloaded file is in your etmamonitoring folder.\nYou must move it elsewhere before you import it.", "", 0);
                continueFlag = false;
                continue;
            }
            if (!aFile.getName().contains("-")) {
                JOptionPane.showMessageDialog(null, "This is not a correctly named module code folder!", "", 0);
                continueFlag = false;
                continue;
            }
            String folderName = aFile.getName();
            String[] hyphenParts = folderName.split("-");
            int hyphenCount = hyphenParts.length;
            String[] spaceParts = folderName.split(" ");
            int spaceCount = spaceParts.length;
            if (hyphenCount > 2 || spaceCount > 1 || aFile.getPath().contains(".") & this.osName.equals("Mac OS X")) {
                JOptionPane.showMessageDialog(null, aFile.getName() + " looks like a duplicate module folder in the same location, with a suffix added!\nIf this is the case, delete it, move or import the other properly named folder\nand download the last batch of  TMA(s) from the OU site again.", "", 0);
                Object[] options = new Object[]{"Cancel", "Yes, I want to go on"};
                Component frame = null;
                int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to continue importing this suspect folder?", "", 1, 3, null, options, options[0]);
                if (n == 0) {
                    continueFlag = false;
                }
            }
            if (!continueFlag) continue;
            File dFile = null;
            this.courseName = aFile.getName();
            String oldPathName = aFile.getParent();
            String fixedFilename = this.fixDuplicate(this.courseName);
            String newPathName = this.etmaMonitoringFolder.getText();
            dFile = this.osName.contains("Windows") ? new File(newPathName + "\\" + fixedFilename) : new File(newPathName + "/" + fixedFilename);
            try {
                dFile.mkdir();
            }
            catch (Exception e) {
                System.out.println("109" + e);
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
            int numberOfFiles = this.tmaNewFiles.size();
            for (int i = 0; i < numberOfFiles; ++i) {
                File bFile = new File(this.tmaNewFiles.get(i));
                File cFile = new File(this.tmaTransFiles.get(i));
                if (!cFile.exists()) {
                    try {
                        if (bFile.isDirectory()) {
                            cFile.mkdir();
                        }
                        this.makeCopy(bFile, cFile);
                    }
                    catch (Exception anException) {
                        System.out.println("Error1 " + anException);
                    }
                    continue;
                }
                if (cFile.isDirectory() || cFile.getName().equals(".DS_Store")) continue;
                duplicateFlag = true;
                errorList.add((CallSite)((Object)("\n" + cFile.getPath())));
            }
            if (!duplicateFlag) {
                JOptionPane.showMessageDialog(null, "New files and folders for module " + aFile.getName() + " were transferred successfully! \nYou may discard the 'old' module folder which will be in the Trash or on the Desktop.");
            } else {
                Object[] options = new Object[]{"Yes", "No"};
                Component frame = null;
                int n = JOptionPane.showOptionDialog(frame, "There may be some duplicate files for module " + aFile.getName() + "  which haven't been replaced!\nDo you want to see a list of these?", "Duplicate files", 1, 3, null, options, options[0]);
                if (n == 0) {
                    JOptionPane.showMessageDialog(null, "Here are the duplicate files: (Press 'return' to continue)\n" + errorList);
                }
            }
            File xFile = null;
            if (this.osName.equals("Mac OS X")) {
                String trashPath = this.findTrash(aFile.getPath());
                xFile = new File(trashPath);
            } else {
                xFile = new File(aFile.getPath() + "old" + this.getDateAndTime());
            }
            aFile.renameTo(xFile);
            if (!this.launchTmaList.isSelected()) continue;
            this.openList();
        }
        try {
            this.setupMenus(this.etmaMonitoringFolder.getText(), this.courseList);
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem(), this.tmaList);
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem(), this.tutorList);
        }
        catch (Exception anException) {
            System.out.println("Error:102" + anException);
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

    public String fixDuplicate(String aFilename) {
        String newFilename = aFilename;
        int fileLength = aFilename.length();
        String penultChar = aFilename.substring(fileLength - 2, fileLength - 1);
        if (penultChar.equals("-") || penultChar.equals(" ")) {
            newFilename = aFilename.substring(0, fileLength - 2);
        }
        return newFilename;
    }

    private void getAllFiles(String dir2add) {
        File thisDir = new File(dir2add);
        String[] dirList = thisDir.list();
        for (int i = 0; i < dirList.length; ++i) {
            File f = new File(thisDir, dirList[i]);
            if (f.isDirectory()) {
                String filePath = f.getPath();
                this.tmaNewFiles.add(filePath);
                this.getAllFiles(filePath);
                continue;
            }
            this.tmaNewFiles.add(f.getPath());
        }
    }

    public String findTrash(String aPath) {
        Object aTrashPath = "";
        String[] pathBits = aPath.split("/");
        int pathLength = pathBits.length;
        aTrashPath = pathBits[1].equals("Users") ? "/" + pathBits[1] + "/" + pathBits[2] + "/.Trash/" + pathBits[pathLength - 1] + "_" + this.getDateAndTime() : aPath + "old" + this.getDateAndTime();
        return aTrashPath;
    }

    private void monitoringRatingsMouseExited(MouseEvent evt) {
    }

    private void monitoringRatingsMouseReleased(MouseEvent evt) {
        int rowNo = this.monitoringRatings.getSelectedRow();
        int colNo = this.monitoringRatings.getSelectedColumn();
        if (colNo == 1 && rowNo < 7) {
            this.toggler(this.monitoringRatingCategories, this.monitoringRatings, rowNo, colNo);
        }
        if (rowNo >= 7 && colNo == 1) {
            this.monitoringRatings.setValueAt("", rowNo, colNo);
        }
        if (this.statusChangeAgree()) {
            int nScripts = Integer.parseInt(this.number_scripts.getText());
            for (int i = 1; i < nScripts + 1; ++i) {
                this.studentsListTable.setValueAt("No", i, 7);
            }
            this.tutor_status.setText("Unmonitored");
            this.saveDetails();
        }
    }

    private void loadXMLAltActionPerformed(ActionEvent evt) {
        String fhiName = "monitor.fhi";
        String fhiPath = this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.tutorList.getSelectedItem() + "/" + this.subNo.getSelectedItem() + "/" + fhiName;
        this.loadPT3(fhiPath);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String loadFhiString(String filePath, boolean lineFeed) {
        Object fhiContents = "";
        File aFile = new File(filePath);
        BufferedReader buffer = null;
        Object currentLine = null;
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            currentLine = buffer.readLine();
            while (currentLine != null) {
                currentLine = (String)currentLine + "\r\n";
                fhiContents = (String)fhiContents + (String)currentLine;
                currentLine = buffer.readLine();
            }
        }
        catch (Exception exception) {
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception exception) {}
        }
        return fhiContents;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveFhiString(String filePath, String fhiString) {
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
            catch (Exception exception) {}
        }
    }

    private void disableScoresEditing(JTable aTable) {
    }

    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public String loadVersion(String aURL) {
        try {
            String str;
            URL url = new URL(aURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            this.latestVersion = "";
            while ((str = in.readLine()) != null) {
                this.latestVersion = this.latestVersion + str;
            }
            in.close();
        }
        catch (MalformedURLException url) {
        }
        catch (IOException url) {
            // empty catch block
        }
        try {
            String[] latestVersionElements = this.latestVersion.split("<latestmonversion>");
            String secondBit = latestVersionElements[1];
            latestVersionElements = secondBit.split("</latestmonversion>");
            this.latestVersion = latestVersionElements[0];
            latestVersionElements = this.latestVersion.split("</span>");
            this.latestVersion = latestVersionElements[1];
            latestVersionElements = this.latestVersion.split(">");
            this.latestVersion = latestVersionElements[1];
            System.out.println(this.latestVersion);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No update information available - please check on the website.");
            this.latestVersion = "0.0";
        }
        if (!this.latestVersion.equals(this.thisVersion) && !this.latestVersion.equals("0.0")) {
            Object[] options = new Object[]{"Yes", "No"};
            Component frame = null;
            int n = JOptionPane.showOptionDialog(frame, "A new version of the monitorhandler (version " + this.latestVersion + ") is available at \nhttp://www.hayfamily.co.uk/etmahandlerpage.html\nYou have version " + this.thisVersion + ".\nWould you like to go to the download page now?", "", 1, 3, null, options, options[0]);
            if (n == 0) {
                String myURI = "http://s376541606.websitehome.co.uk/etmahandlerpage.html";
                try {
                    Desktop.getDesktop().browse(new URI(myURI));
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Unable to connect to " + myURI, "Sorry!", 2);
                }
            }
            if (n != 1) return this.latestVersion;
        }
        if (!this.latestVersion.equals("0.0")) {
            JOptionPane.showMessageDialog(null, "You have the latest version of the monitorhandler (version " + this.thisVersion + ")");
            return this.latestVersion;
        } else {
            JOptionPane.showMessageDialog(null, "Update check hasn't worked.\nDo you have an internet connection?");
        }
        return this.latestVersion;
    }

    public boolean testXML(File fileToBeTested) {
        boolean isValid = true;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder tester = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc1 = null;
            doc1 = tester.parse(fileToBeTested);
            doc1.getDocumentElement().normalize();
        }
        catch (Exception anException) {
            isValid = false;
        }
        return isValid;
    }

    private File loadPT3(String aPathname) {
        for (int i = 0; i < 1; ++i) {
            for (int j = 0; j < 18; ++j) {
                this.monitoringRatings.setValueAt(null, j, i);
            }
        }
        this.clearFields();
        if (this.osName.contains("Windows")) {
            String etmaMonitoringFolderPath = this.etmaMonitoringFolder.getText().replaceAll("/", "\\");
            this.loadRubric(etmaMonitoringFolderPath + "\\monrubricreplacement.txt");
        } else {
            this.loadRubric(this.etmaMonitoringFolder.getText() + "/monrubricreplacement.txt");
        }
        this.attFlag = false;
        this.jTextArea1.setText("");
        File dataFile = new File(aPathname);
        this.fhiFileName.setText(aPathname);
        this.ampersandClean(dataFile);
        dataFile = this.tagClean(dataFile);
        JTextField[] xmlTagsFileNamesTutor = new JTextField[]{this.tutor_forenames, this.tutor_surname, this.staff_id, this.tutor_status, this.locn_code, this.new_tutor, this.number_scripts};
        JTextField[] xmlTagsFileNamesMonitor = new JTextField[]{this.monitoring_level_code, this.previous_monitor_forms, this.monitor_collect_date, this.monitor_forenames, this.monitor_surname, this.sample_available_date, this.monitor_staff_id, this.monitor_comments_file, this.monitor_comment_date, this.monitor_return_date, this.sent_to_tutor_date, this.staff_tutor_comments, this.zip_filepath};
        JTextField[] xmlTagsFileNamesTmas = new JTextField[]{this.course_code, this.assgnmt_cut_off_date, this.monitoring_type, this.assgnmt_suffix, this.pres_code};
        JTextField[] xmlTagsFileNamesZip = new JTextField[]{this.zip_date, this.zip_filepath, this.zip_filename};
        JTextField[] xmlTagsFileNamesSystem = new JTextField[]{this.download_exe_name, this.version_num, this.basic_monitor_form_name, this.basic_monitor_form_path};
        try {
            this.getDetails("tutor_details", this.tutorDetails, dataFile);
            this.putIntoFields(xmlTagsFileNamesTutor, this.xmlTagsStringsTutor, this.tutorDetails);
            this.getDetailsZip("zip_details", this.zipDetails, dataFile);
            this.getDetails("monitoring_details", this.monitorDetails, dataFile);
            this.putIntoFields(xmlTagsFileNamesMonitor, this.xmlTagsStringsMonitor, this.monitorDetails);
            this.getDetails("system_details", this.systemDetails, dataFile);
            this.putIntoFields(xmlTagsFileNamesSystem, this.xmlTagsStringsSystem, this.systemDetails);
            this.getFlags(dataFile);
            this.getDetails("tma_details", this.tmaDetails, dataFile);
            this.putIntoFields(xmlTagsFileNamesTmas, this.xmlTagsStringsTmas, this.tmaDetails);
            this.getStudentsList(dataFile);
        }
        catch (Exception anException) {
            JOptionPane.showMessageDialog(null, "There seems to be a problem reading the PT3 file. " + anException);
        }
        this.monitor_comments.setWrapStyleWord(true);
        this.disableScoresEditing(this.monitoringRatings);
        this.savePt3.setEnabled(true);
        this.zipFiles.setEnabled(true);
        this.openTutorFolder.setEnabled(true);
        this.listStudents.setEnabled(true);
        this.openReturnsFolder.setEnabled(true);
        dataFile = this.tagRestore(dataFile);
        if (this.spellCheckFlag.isSelected()) {
            this.highlightAllErrors();
        }
        this.checkStatus();
        File folderForZipping = new File(this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText());
        folderForZipping.mkdir();
        this.batchZipNew.setEnabled(true);
        this.selectAllFilesToZip.setEnabled(true);
        return dataFile;
    }

    public void makeFolderForZippingDirectory() {
        try {
            File folderForZipping = new File(this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText());
            folderForZipping.mkdir();
        }
        catch (Exception e) {
            System.out.println("Creation error:103 " + e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadRubric(String filePath) {
        File testFile = new File(filePath);
        if (!testFile.exists()) {
            this.loadDefaultRubric();
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath));){
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                this.monitoringRatings.setValueAt(line, 0, 0);
                int i = 1;
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                    this.monitoringRatings.setValueAt(line, i, 0);
                    ++i;
                }
                String string = sb.toString();
            }
            catch (Exception e) {
                System.out.println("rubric error104" + e);
                JFrame frame = new JFrame("Oops!");
                JOptionPane.showMessageDialog(frame, "The file \"monrubricreplacement.txt)\" may be corrupted.\nThe default rubrics will be loaded instead", "Oops!", 0);
                this.loadDefaultRubric();
            }
        }
    }

    public void loadDefaultRubric() {
        int nRubric = this.defaultRubric.length;
        for (int i = 0; i < nRubric; ++i) {
            this.monitoringRatings.setValueAt(this.defaultRubric[i], i, 0);
        }
    }

    public void getThisWeighting(JTable aTable) {
        for (int i = 1; i < 15; ++i) {
            String tempStr = (String)aTable.getValueAt(i, 0);
            try {
                this.weightingsList.add(Integer.parseInt(String.valueOf(tempStr)));
                this.weightingsMap.put((String)this.tmaNumbers.getValueAt(i, 0), Integer.parseInt(String.valueOf(tempStr)));
                continue;
            }
            catch (Exception anException) {
                this.weightingsList.add(0);
                this.weightingsMap.put((String)this.tmaNumbers.getValueAt(i, 0), 0);
            }
        }
    }

    public void getThisMaxScore(JTable aTable) {
    }

    private void getWeightings(String aCourse) {
    }

    private void getMaxScores(String aCourse) {
    }

    public void sortAllRowsBy(DefaultTableModel model, int colIndex, boolean ascending) {
        Vector<Vector> data = model.getDataVector();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
    }

    private void getPreviousScores(String courseCode, String oucu, boolean longFlag, int studNo) {
    }

    private void avePreviousScores(String courseCode) {
    }

    private void getNewRedRows(int nStudents) {
        this.redRows.clear();
        this.blueRows.clear();
        for (int i = 0; i < nStudents; ++i) {
            double studAve = (Double)this.studentsListTable.getValueAt(i, 17);
            if (studAve < (double)this.passMark) {
                this.redRows.add(i);
            }
            this.colorSetter(this.studentsListTable, this.redRows, this.blueRows, Color.PINK, Color.YELLOW, true);
        }
    }

    private void getNewTmaRedRows(int nStudents) {
        this.redRows.clear();
        this.blueRows.clear();
        for (int i = 0; i < nStudents; ++i) {
            String testString = (String)this.listOfTutors.getValueAt(i, 7);
            if (testString.equals("Unmonitored")) {
                this.redRows.add(i);
            }
            if (testString.equals("Monitored")) {
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
            this.colorSetter(this.listOfTutors, this.redRows, this.blueRows, aColor, bColor, inBold);
        }
    }

    private String getStudentsMarks(String oucu, String courseCode, File bFile) {
        List<Object> subFolderList = new ArrayList();
        String thisMark = "";
        String subNo1 = "1";
        String filePath = bFile.getPath();
        String thisFile = filePath + "/" + oucu + "/";
        File subFolders = new File(thisFile);
        subFolderList = Arrays.asList(subFolders.listFiles());
        for (File file : subFolderList) {
            if (!file.isDirectory()) continue;
            subNo1 = file.getName();
        }
        String nextFile = thisFile + subNo1 + "/" + courseCode + "-" + bFile.getName() + "-" + subNo1 + "-" + oucu + ".fhi";
        thisMark = this.getIndividualMark(nextFile);
        return thisMark;
    }

    private String getIndividualMark(String fhiName) {
        this.thisLine.clear();
        File dataFile = new File(fhiName);
        this.getShortDetails("student_details", this.tutorDetailsShort, dataFile);
        this.getShortDetails("tutor_details", this.monitorDetailsShort, dataFile);
        this.getShortDetails("submission_details", this.studentDetailsShort, dataFile);
        this.ampersandCleanAlt(dataFile);
        return this.studentDetailsShort.get("overall_grade_score");
    }

    private File getFile() {
        JFileChooser _fileChooser = new JFileChooser();
        JMenuItem openItem = new JMenuItem("Open...");
        int path = _fileChooser.showOpenDialog(null);
        File aFile = _fileChooser.getSelectedFile();
        this.fhiFileName.setText(aFile.getPath());
        return aFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void putFile1(String pathname, String fixedString) {
        try {
            new FileWriter(pathname).close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (this.osName.equals("Mac OS X")) {
            // empty if block
        }
        File aFile = new File(pathname);
        OutputStreamWriter buffer = null;
        try {
            buffer = new OutputStreamWriter((OutputStream)new FileOutputStream(aFile), "ISO8859_1");
            buffer.flush();
            buffer.write(fixedString);
        }
        catch (Exception anException) {
            System.out.println("Error2: " + anException);
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception anException) {
                System.out.println("Error1: " + anException);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void putFile(String pathname, String fixedString) {
        if (this.osName.equals("Mac OS X")) {
            fixedString = this.fixAccents(this.outString);
        }
        File aFile = new File(pathname);
        OutputStreamWriter buffer = null;
        try {
            buffer = new OutputStreamWriter((OutputStream)new FileOutputStream(aFile), "ISO8859_1");
            buffer.flush();
            buffer.write(fixedString);
        }
        catch (Exception anException) {
            System.out.println("Error3: " + anException);
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception anException) {
                System.out.println("Error4: " + anException);
            }
        }
    }

    private void clearFields() {
        int j;
        int i;
        for (String aTag : this.fieldNames1.keySet()) {
            this.tutorDetails.clear();
            this.monitorDetails.clear();
            this.tmaDetails.clear();
            this.fieldNames1.get(aTag).setText("");
            this.monitor_comments.setText("");
        }
        int nRow = 0;
        int nCol = 0;
        for (i = 0; i < nRow; ++i) {
            for (j = 0; j < nCol; ++j) {
            }
        }
        nRow = this.monitoringRatings.getRowCount();
        nCol = this.monitoringRatings.getColumnCount();
        for (i = 0; i < nRow; ++i) {
            for (j = 0; j < nCol; ++j) {
                this.monitoringRatings.setValueAt("", i, j);
            }
        }
    }

    private void makeMap() {
        int i;
        int lString1 = this.xmlTagsStringsTutor.length;
        int lStringStaff1 = this.xmlTagsStringsMonitor.length;
        int lStringSubs = this.xmlTagsStringsTmas.length;
        int lStringZip = this.xmlTagsStringsZip.length;
        int lStringSystem = this.xmlTagsStringsSystem.length;
        JTextField[] xmlTagsFileNamesTutor = new JTextField[]{this.tutor_forenames, this.tutor_surname, this.staff_id, this.tutor_status, this.locn_code, this.new_tutor, this.number_scripts};
        JTextField[] xmlTagsFileNamesMonitor = new JTextField[]{this.monitoring_level_code, this.previous_monitor_forms, this.monitor_collect_date, this.monitor_forenames, this.monitor_surname, this.sample_available_date, this.monitor_staff_id, this.monitor_comments_file, this.monitor_comment_date, this.monitor_return_date, this.sent_to_tutor_date, this.staff_tutor_comments, this.zip_filepath};
        JTextField[] xmlTagsFileNamesTmas = new JTextField[]{this.course_code, this.assgnmt_cut_off_date, this.monitoring_type, this.assgnmt_suffix, this.pres_code};
        JTextField[] xmlTagsFileNamesZip = new JTextField[]{this.zip_date, this.zip_filepath, this.zip_filename};
        JTextField[] xmlTagsFileNamesSystem = new JTextField[]{this.download_exe_name, this.version_num, this.basic_monitor_form_name, this.basic_monitor_form_path};
        for (i = 0; i < lString1; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsTutor[i], xmlTagsFileNamesTutor[i]);
        }
        for (i = 0; i < lStringStaff1; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsMonitor[i], xmlTagsFileNamesMonitor[i]);
        }
        for (i = 0; i < lStringSubs; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsTmas[i], xmlTagsFileNamesTmas[i]);
        }
        for (i = 0; i < lStringZip; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsZip[i], xmlTagsFileNamesZip[i]);
        }
        for (i = 0; i < lStringSystem; ++i) {
            this.fieldNames1.put(this.xmlTagsStringsSystem[i], xmlTagsFileNamesSystem[i]);
        }
    }

    private void putIntoFields(JTextField[] aField, String[] aString, Map<String, String> aMap) {
        int lField = aField.length;
        for (int i = 0; i < lField; ++i) {
            aField[i] = this.fieldNames1.get(aString[i]);
            aField[i].setText(aMap.get(aString[i]));
        }
    }

    private void putMarks() {
    }

    private void constructQuestionDetails(int aQuestion) {
    }

    private void constructQuestionPartDetails(int aQuestion) {
    }

    public String fixAccents(String commentsString) {
        char cedilla = ']';
        String stringCedilla = "" + cedilla;
        String[] macAccents = new String[]{"a"};
        String[] winAccents = new String[]{"a"};
        for (int i = 0; i < macAccents.length; ++i) {
            String commentsStringOld = commentsString;
            if (commentsStringOld.equals(commentsString = commentsString.replaceAll(macAccents[i], winAccents[i]))) continue;
        }
        return commentsString;
    }

    public String unFixAccents(String commentsString) {
        char[] macAccents = new char[]{'\u00e9'};
        char[] winAccents = new char[]{'\u00e9'};
        for (int i = 0; i < macAccents.length; ++i) {
            char oldChar = macAccents[i];
            char newChar = winAccents[i];
            commentsString = commentsString.replace(newChar, oldChar);
        }
        return commentsString;
    }

    public String replaceFunnyCharacters(String commentsString) {
        int i;
        String newString = commentsString;
        String[] funnyCharactersMac = new String[]{"\u2022", "\u00df", "\u03c0", "\u2202", "\u00b5", "\u00a9", "\u02da", "\u221a", "<", ">", "\u201c", "\u201d", "\u2018", "\u2019", "\u2013"};
        String[] boringCharactersMac = new String[]{"*", "ss", "pi ", "delta ", "mu ", "Copyright", "degrees", "root", this.string02, this.string03, "\"", "\"", "'", "'", "-"};
        String[] funnyCharactersWin = new String[]{"<", ">", "\u201c", "\u201d", "\u2018", "\u2019", "\u2013"};
        String[] boringCharactersWin = new String[]{this.string02, this.string03, "\"", "\"", "'", "'", "-"};
        String[] funnyCharactersLin = new String[]{"<", ">", "\u201c", "\u201d", "\u2018", "\u2019", "\u2013"};
        String[] boringCharactersLin = new String[]{this.string02, this.string03, "\"", "\"", "'", "'", "-"};
        if (this.osName.equals("Mac OS X")) {
            for (i = 0; i < funnyCharactersMac.length; ++i) {
                newString = newString.replaceAll(funnyCharactersMac[i], boringCharactersMac[i]);
            }
        }
        if (this.osName.contains("Windows")) {
            for (i = 0; i < funnyCharactersWin.length; ++i) {
                newString = newString.replaceAll(funnyCharactersWin[i], boringCharactersWin[i]);
            }
            if (this.osName.contains("Linux")) {
                for (i = 0; i < funnyCharactersLin.length; ++i) {
                    newString = newString.replaceAll(funnyCharactersLin[i], boringCharactersLin[i]);
                }
            }
        }
        return newString;
    }

    private String putDetails(Map aMap, String[] aString, String bString) {
        StringBuilder outStringSb = new StringBuilder(1000);
        outStringSb.append(this.outString);
        outStringSb.append(this.addStartTag(bString));
        int lMap = aMap.size();
        for (String thisTag : aString) {
            aMap.put(thisTag, this.fieldNames1.get(thisTag).getText());
            Object thisEntry = aMap.get(thisTag);
            String thisEntryString = (String)thisEntry;
            String thisString = this.addStartTag(thisTag) + thisEntry + this.addEndTag(thisTag);
            outStringSb.append(thisString);
        }
        outStringSb.append(this.addEndTag(bString));
        String chunk = outStringSb.toString();
        return chunk;
    }

    private void getDetails(String aString, Map aMap, File aFile) {
        block7: {
            try {
                this.ampersandClean(aFile);
                aFile = this.tagClean(aFile);
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document doc = null;
                doc = docBuilder.parse(aFile);
                doc.getDocumentElement().normalize();
                NodeList listOfPersons = doc.getElementsByTagName(aString);
                Node firstPersonNode = listOfPersons.item(0);
                NodeList listOfStudentDetails = listOfPersons.item(0).getChildNodes();
                int numberOfStudentItems = listOfStudentDetails.getLength();
                if (firstPersonNode.getNodeType() != 1) break block7;
                for (int i = 0; i < numberOfStudentItems; ++i) {
                    try {
                        Element studentElement = (Element)listOfStudentDetails.item(i);
                        String thisDetail = studentElement.getNodeName();
                        try {
                            NodeList detailList = listOfStudentDetails.item(i).getChildNodes();
                            aMap.put(thisDetail, detailList.item(0).getNodeValue());
                        }
                        catch (Exception e) {
                            aMap.put(thisDetail, "");
                        }
                        aFile = this.tagClean(aFile);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void getDetailsZip(String aString, Map aMap, File aFile) {
        try {
            this.ampersandClean(aFile);
            aFile = this.tagClean(aFile);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = null;
            doc = docBuilder.parse(aFile);
            doc.getDocumentElement().normalize();
            NodeList listOfPersons = doc.getElementsByTagName("tutor_details");
            Node firstPersonNode = listOfPersons.item(0);
            NodeList listOfStudentDetails = listOfPersons.item(0).getChildNodes();
            Node zipNode = listOfStudentDetails.item(7);
            NodeList zipChildren = zipNode.getChildNodes();
            String zipdate = zipChildren.item(0).getTextContent();
            String zipfilepath = zipChildren.item(1).getTextContent();
            String zipfilename = zipChildren.item(2).getTextContent();
            this.zip_date.setText(zipdate);
            this.zip_filepath.setText(zipfilepath);
            this.zip_filename.setText(zipfilename);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void getFlags(File aFile) {
        ArrayList<String> flags;
        block12: {
            flags = new ArrayList<String>();
            try {
                this.ampersandClean(aFile);
                aFile = this.tagClean(aFile);
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                org.w3c.dom.Document doc = null;
                doc = docBuilder.parse(aFile);
                doc.getDocumentElement().normalize();
                NodeList listOfPersons = doc.getElementsByTagName("monitoring_details");
                Node firstPersonNode = listOfPersons.item(0);
                NodeList listOfStudentDetails = listOfPersons.item(0).getChildNodes();
                int numberOfStudentItems = listOfStudentDetails.getLength();
                if (firstPersonNode.getNodeType() != 1) break block12;
                for (int i = 0; i < numberOfStudentItems; ++i) {
                    try {
                        Element studentElement = (Element)listOfStudentDetails.item(i);
                        String thisDetail = studentElement.getNodeName();
                        try {
                            NodeList detailList = listOfStudentDetails.item(i).getChildNodes();
                            if (thisDetail.startsWith("flag")) {
                                flags.add(detailList.item(0).getNodeValue());
                            }
                            if (thisDetail.equals("monitor_comments")) {
                                try {
                                    this.monitor_comments.setText(detailList.item(0).getNodeValue());
                                }
                                catch (Exception exception) {}
                            }
                        }
                        catch (Exception e) {
                            flags.add("");
                        }
                        aFile = this.tagClean(aFile);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            catch (Exception docBuilderFactory) {
                // empty catch block
            }
        }
        for (int rowNo = 1; rowNo < flags.size(); ++rowNo) {
            this.monitoringRatings.setValueAt(flags.get(rowNo - 1), rowNo - 1, 1);
        }
    }

    public void openMonitoringForm() {
        this.saveLocation();
        this.studentIndex = this.listOfTutors.getSelectedRow();
        String tutorPID = (String)this.listOfTutors.getValueAt(this.studentIndex, 0);
        if (!tutorPID.equals("")) {
            try {
                this.savedFlag = false;
                this.studentIndex = this.listOfTutors.getSelectedRow();
                tutorPID = (String)this.listOfTutors.getValueAt(this.studentIndex, 0);
                String courseCode = (String)this.listOfTutors.getValueAt(this.studentIndex, 4);
                String presCode = (String)this.listOfTutors.getValueAt(this.studentIndex, 5);
                String subNoString = (String)this.listOfTutors.getValueAt(this.studentIndex, 3);
                String assNo = (String)this.listOfTutors.getValueAt(this.studentIndex, 6);
                this.overallGrade = (String)this.listOfTutors.getValueAt(this.studentIndex, 4);
                String fhiName = "monitor.fhi";
                String fhiPath = this.etmaMonitoringFolder.getText() + "/" + courseCode + "-" + presCode + "/" + assNo + "/" + tutorPID + "/" + subNoString + "/" + fhiName;
                this.submittedMonitoring.setVisible(false);
                this.loadPT3(fhiPath);
                this.setupPreviousPt3s();
                this.submittedMonitoring.setVisible(false);
            }
            catch (Exception anException) {
                System.out.println(anException);
                this.savedFlag = true;
            }
            this.savedFlag = true;
        }
    }

    private void listOfTutorsMouseReleased(MouseEvent evt) {
        if (this.listOfTutors.getSelectedColumn() != 11) {
            this.batchZipNew.setEnabled(true);
            int numClick = evt.getClickCount();
            int clicksNeeded = 0;
            if (this.doubleClickFlag.isSelected()) {
                clicksNeeded = 1;
            }
            if (numClick > clicksNeeded) {
                this.openMonitoringForm();
            } else {
                int[] selectedRows = this.listOfTutors.getSelectedRows();
                for (int j = 0; j < this.listOfTutors.getRowCount(); ++j) {
                    if (!this.listOfTutors.getValueAt(j, 0).equals("")) continue;
                    this.listOfTutors.setValueAt(false, j, 11);
                }
            }
        }
    }

    private void courseListItemStateChanged(ItemEvent evt) {
        try {
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem(), this.tutorList);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void tmaListItemStateChanged(ItemEvent evt) {
        try {
            this.tmaMenuPreferences = (String)this.tmaList.getSelectedItem();
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem(), this.tutorList);
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.tutorList.getSelectedItem(), this.subNo);
            this.ourRoot.put("tmaMenuPreferences", this.tmaMenuPreferences);
        }
        catch (Exception anException) {
            this.ourRoot.put("tmaMenuPreferences", this.tmaMenuPreferences);
        }
    }

    private void tutorListActionPerformed(ActionEvent evt) {
        try {
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.tutorList.getSelectedItem(), this.subNo);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void openTutorListActionPerformed(ActionEvent evt) {
        this.openList();
    }

    public String fixDate(String oldDate) {
        Object newDate = "";
        String[] dateElements = oldDate.split("-");
        String month1 = dateElements[1];
        String dayOfMonth = dateElements[0];
        Object monthNumber = "";
        String restOfIt = dateElements[2];
        for (int i = 0; i < 12; ++i) {
            if (!month1.equals(this.MONTHLIST[i])) continue;
            monthNumber = String.valueOf(i + 1);
        }
        if (((String)monthNumber).length() == 1) {
            monthNumber = "0" + (String)monthNumber;
        }
        String year1 = restOfIt.substring(0, 4);
        String time1 = restOfIt.substring(5, 13);
        newDate = year1 + "-" + (String)monthNumber + "-" + dayOfMonth + "  " + time1;
        return newDate;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void openList() {
        if (!this.savedFlag) {
            boolean loadFlag = true;
            Component frame = null;
            Object[] options = new Object[]{"Save report", "Don't save", "Cancel"};
            int n = JOptionPane.showOptionDialog(frame, "Do you want to save the current Monitoring report first?", "Current work may not be saved!", 1, 3, null, options, options[0]);
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
        this.toBeMarked = 0;
        this.tmaListError = false;
        this.setSizesOfTmaList();
        this.submittedMonitoring.setTitle("Current Monitoring for module " + this.courseList.getSelectedItem());
        this.listOfTutors.clearSelection();
        int nRow = this.listOfTutors.getRowCount();
        int nColumn = this.listOfTutors.getColumnCount();
        for (int r = 0; r < nRow; ++r) {
            for (int c = 0; c < nColumn - 1; ++c) {
                this.listOfTutors.setValueAt("", r, c);
            }
            this.listOfTutors.setValueAt(false, r, 11);
        }
        String labelText = " on any row to open data for that Tutor; click on any heading to sort by that column.";
        String clickCommand = "Click";
        if (this.doubleClickFlag.isSelected()) {
            clickCommand = "Double-click";
        }
        this.jLabel34.setText(clickCommand + labelText);
        try {
            this.makeTmaTable(1);
            if (!this.startUp) {
                this.submittedMonitoring.setVisible(true);
            }
            this.submittedMonitoring.setSize(800, 325);
            this.submittedMonitoring.setLocation((int)this.tmaListLocX, (int)this.tmaListLocY);
            if (!this.late_submission_status.getText().equals("Y")) {
                // empty if block
            }
        }
        catch (Exception anException) {
            JOptionPane.showMessageDialog(null, "Please select a valid TMA number for this Monitoring task, or,\nIf you haven't yet downloaded any Monitoring, click 'Monit Site'.\nWhen you've downloaded the Monitoring, click 'Import Monitoring',\nwhich will move them to the correct place in the 'etmamonitoring' folder.");
            this.tmaListError = true;
        }
        try {
            this.calculateTotals();
        }
        catch (Exception anException) {
            // empty catch block
        }
        TableColumnModel tcm = this.listOfTutors.getColumnModel();
        DefaultTableModel model = (DefaultTableModel)this.listOfTutors.getModel();
        this.sortAllRowsBy(model, this.sortRow, this.sortPreference);
        try {
            this.setSizesOfTmaList();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.getNewTmaRedRows(this.listOfTutors.getRowCount());
        this.listOfTutors.setVisible(false);
        this.listOfTutors.setVisible(true);
    }

    public List<String> getListOfSubmissions(String aPathName) {
        this.regionList.clear();
        boolean showLatest = this.showLatestFlag.isSelected();
        File aFile = new File(aPathName);
        Object[] fileList = aFile.list();
        Arrays.sort(fileList);
        ArrayList<Object> holdingList = new ArrayList<Object>();
        for (int i = 0; i < fileList.length; ++i) {
            String tempPath = aPathName + "/" + (String)fileList[i];
            File tempFile = new File(tempPath);
            if (!tempFile.isDirectory()) continue;
            holdingList.add(fileList[i]);
        }
        int startList = 0;
        if (showLatest) {
            startList = holdingList.size() - 1;
        }
        for (int i = startList; i < holdingList.size(); ++i) {
            this.regionList.add((String)holdingList.get(i));
        }
        return this.regionList;
    }

    private void makeTmaTable(int version) {
        try {
            int numberOfTutors = this.tutorList.getItemCount();
            int adjustedTutorNo = 0;
            this.redRows.clear();
            this.blueRows.clear();
            for (int tutorNo = 0; tutorNo < numberOfTutors; ++tutorNo) {
                this.getListOfSubmissions(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.tutorList.getItemAt(tutorNo));
                int numberOfSubmissions = this.regionList.size();
                List<Object> tutorEntry = new ArrayList();
                for (int sNo = 0; sNo < numberOfSubmissions; ++sNo) {
                    tutorEntry = this.getTableEntry(tutorNo, sNo);
                    if (!this.filter.equals("All") && !this.displayFlag) continue;
                    if (!this.entryErrorFlag) {
                        for (int entryNo = 0; entryNo < tutorEntry.size(); ++entryNo) {
                            this.listOfTutors.setValueAt(tutorEntry.get(entryNo), adjustedTutorNo, entryNo);
                        }
                        if (this.listOfTutors.getValueAt(adjustedTutorNo, 7).equals("Unmonitored")) {
                            this.redRows.add(adjustedTutorNo);
                            this.blueRows.add(adjustedTutorNo);
                        }
                        this.listOfTutors.setValueAt(false, adjustedTutorNo, 11);
                    } else {
                        this.listOfTutors.setValueAt("Error!", adjustedTutorNo, 0);
                        this.listOfTutors.setValueAt("Student Id:", adjustedTutorNo, 1);
                        this.listOfTutors.setValueAt(this.tutorList.getItemAt(adjustedTutorNo), adjustedTutorNo, 2);
                        this.listOfTutors.setValueAt("check file:", adjustedTutorNo, 3);
                        this.listOfTutors.setValueAt("", adjustedTutorNo, 4);
                    }
                    ++adjustedTutorNo;
                    this.entryErrorFlag = false;
                }
            }
            this.totalTmas.setText(String.valueOf(this.tutorList.getItemCount()));
            this.toBeMarkedTmas.setText(String.valueOf(this.toBeMarked));
            boolean inBold = false;
            Color aColor = new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2]));
            Color bColor = new Color(Integer.parseInt(this.gridColor[0]), Integer.parseInt(this.gridColor[1]), Integer.parseInt(this.gridColor[2]));
            if (this.highlightUnmarked.isSelected()) {
                aColor = Color.PINK;
                bColor = Color.YELLOW;
                inBold = true;
            }
            this.colorSetter(this.listOfTutors, this.redRows, this.blueRows, aColor, bColor, inBold);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private List<String> getTableEntry(int aStudent, int submissionNo) {
        this.thisLine.clear();
        Object fhiName = "monitor.fhi";
        fhiName = this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem() + "/" + this.tmaList.getSelectedItem() + "/" + this.tutorList.getItemAt(aStudent) + "/" + this.regionList.get(submissionNo) + "/" + (String)fhiName;
        File dataFile = new File((String)fhiName);
        dataFile = this.tagClean(dataFile);
        this.readErrorFlag = true;
        this.getShortDetails("tutor_details", this.tutorDetailsShort, dataFile);
        this.getShortDetails("monitoring_details", this.monitorDetailsShort, dataFile);
        this.getShortDetails("student_details", this.studentDetailsShort, dataFile);
        this.getShortDetails("tma_details", this.tmaDetailsShort, dataFile);
        this.thisLine.add(this.tutorDetailsShort.get("staff_id"));
        this.thisLine.add(this.tutorDetailsShort.get("tutor_forenames"));
        this.thisLine.add(this.tutorDetailsShort.get("tutor_surname"));
        this.thisLine.add(this.tutorDetailsShort.get("locn_code"));
        this.thisLine.add(this.tmaDetailsShort.get("course_code"));
        this.thisLine.add(this.tmaDetailsShort.get("pres_code"));
        this.thisLine.add(this.tmaDetailsShort.get("assgnmt_suffix"));
        this.thisLine.add(this.tutorDetailsShort.get("tutor_status"));
        String collectDate = this.monitorDetailsShort.get("monitor_collect_date");
        this.thisLine.add(collectDate);
        this.displayFlag = this.tutorDetailsShort.get("tutor_status").equals(this.filter);
        if (this.tutorDetailsShort.get("tutor_status").equals("Unmonitored")) {
            ++this.toBeMarked;
        }
        this.ampersandCleanAlt(dataFile);
        dataFile = this.tagRestore(dataFile);
        return this.thisLine;
    }

    private void tmaListActionPerformed(ActionEvent evt) {
    }

    private void courseListActionPerformed(ActionEvent evt) {
        try {
            this.courseMenuPreferences = (String)this.courseList.getSelectedItem();
            this.ourRoot.put("menuPreferences", this.courseMenuPreferences);
            this.setupMenus(this.etmaMonitoringFolder.getText() + "/" + this.courseList.getSelectedItem(), this.tmaList);
            if (!this.startUp) {
                this.tmaList.setSelectedIndex(this.tmaList.getItemCount() - 1);
            }
        }
        catch (Exception anException) {
            System.out.println("Error3:" + anException);
        }
    }

    private void setupMenus(String aPathName, JComboBox aMenu) {
        while (aMenu.getItemCount() > 1) {
            aMenu.removeItemAt(1);
        }
        try {
            File aFile = new File(aPathName);
            Object[] fileList = aFile.list();
            Arrays.sort(fileList);
            for (int i = 0; i < fileList.length; ++i) {
                String tempPath = aPathName + "/" + (String)fileList[i];
                File tempFile = new File(tempPath);
                if (!tempFile.isDirectory() || tempFile.getName().equals("returns") || !this.checkForDigit(tempFile.getName())) continue;
                aMenu.addItem(fileList[i]);
            }
            aMenu.updateUI();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (aMenu.getItemCount() > 1) {
            aMenu.removeItemAt(0);
        }
        this.tmaList.updateUI();
        if (aMenu.equals(this.courseList)) {
            // empty if block
        }
        aMenu.updateUI();
    }

    public void selectEtmamonitoringFolder() {
        String choiceTitle = "Please select your etmamonitoring folder - it must be called 'etmamonitoring':";
        int createInt = 0;
        Component frame = null;
        Object[] options = new Object[]{"Yes", "No"};
        createInt = JOptionPane.showOptionDialog(frame, "Do you wish to create an etmamonitoring folder?", "", 1, 2, null, options, options[1]);
        if (createInt == 0) {
            choiceTitle = "Please choose the folder where you wish to create the etmamonitoring folder:";
        }
        JFileChooser _fileChooser = new JFileChooser();
        _fileChooser.setFileSelectionMode(1);
        _fileChooser.setDialogTitle(choiceTitle);
        FileSystemView fsv = _fileChooser.getFileSystemView();
        int path = _fileChooser.showOpenDialog(null);
        File aFile = _fileChooser.getSelectedFile();
        if (createInt == 0) {
            if (!(aFile = new File(aFile.getPath() + "/etmamonitoring/")).exists()) {
                aFile.mkdir();
            } else {
                JOptionPane.showMessageDialog(null, "There is already an etmamonitoring folder in this location!\nA new one will NOT be created, but \nthe path will be set to the existing folder.");
            }
        }
        String fName = aFile.getName();
        while (!fName.equals("etmamonitoring") && !fName.equals("")) {
            JOptionPane.showMessageDialog(null, "Your folder must be called 'etmamonitoring' without the quotes.\nYou have selected a folder called " + fName);
            path = _fileChooser.showOpenDialog(null);
            aFile = _fileChooser.getSelectedFile();
            fName = aFile.getName();
        }
        this.etmaMonitoringFolder.setText(aFile.getPath());
        this.ourRoot.put("etmamonitoringFolder", aFile.getPath());
        try {
            this.setupMenus(this.etmaMonitoringFolder.getText(), this.courseList);
        }
        catch (Exception anException) {
            System.out.println("Error101:" + anException);
        }
    }

    private void openPreferencesActionPerformed(ActionEvent evt) {
        this.openPrefsWindow();
    }

    private void openPrefsWindow() {
        this.preferences.setVisible(true);
        this.preferences.setSize(630, 500);
        this.etmaMonitoringFolder.setText(this.ourRoot.get("etmamonitoringFolder", ""));
        this.downloadsFolder.setText(this.ourRoot.get("downloadsFolder", this.desktopPath));
        this.dictionaryPath.setText(this.ourRoot.get("dictionaryPathPreferences", this.dictionaryLocation));
    }

    private void savePt3ActionPerformed(ActionEvent evt) {
        Timer timer1 = new Timer();
        ScheduleRunner task1 = new ScheduleRunner();
        timer1.schedule((TimerTask)task1, 1000L);
        this.saveDetails();
        JOptionPane.showMessageDialog(null, "Monitoring report saved!");
    }

    private void saveDetails() {
        try {
            this.outString = "";
            this.monitor_comment_date.setText(this.getDateAndTime());
            String headerChunk = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><tutor_sample>";
            String tempString = this.monitor_comments.getText().replaceAll("<", this.string02);
            tempString = tempString.replaceAll(">", this.string03);
            this.monitor_comments.setText(tempString);
            String tutorDetailsChunk = this.putDetails(this.tutorDetails, this.xmlTagsStringsTutor, "tutor_details");
            String zipChunk = this.putDetails(this.zipDetails, this.xmlTagsStringsZip, "zip__details");
            tutorDetailsChunk = tutorDetailsChunk.replace("</tutor_details>", zipChunk + "</tutor_details>");
            String tmaDetailsChunk = this.putDetails(this.tmaDetails, this.xmlTagsStringsTmas, "tma_details");
            String monitorDetailsChunk = this.putDetails(this.monitorDetails, this.xmlTagsStringsMonitor, "monitoring_details");
            String commentText = this.monitor_comments.getText();
            commentText = this.replaceFunnyCharacters(commentText);
            String monitoringCommentsChunk = "<monitor_comments>" + commentText + "</monitor_comments>";
            monitorDetailsChunk = monitorDetailsChunk.replace("</monitoring_level_code>", "</monitoring_level_code>" + monitoringCommentsChunk);
            String studentsChunk = this.getStudentsData();
            String systemDetailsChunk = this.putDetails(this.systemDetails, this.xmlTagsStringsSystem, "system_details");
            String flagChunk = this.putFlags();
            monitorDetailsChunk = monitorDetailsChunk.replace("</monitor_collect_date>", "</monitor_collect_date>" + flagChunk);
            String cleanString = "";
            String footerChunk = "</tutor_sample>";
            this.outString = "";
            this.outString = this.outString + headerChunk;
            this.outString = this.outString + tutorDetailsChunk;
            this.outString = this.outString + tmaDetailsChunk;
            this.outString = this.outString + monitorDetailsChunk;
            this.outString = this.outString + studentsChunk;
            this.outString = this.outString + systemDetailsChunk;
            this.outString = this.outString + footerChunk;
            if (this.osName.equals("Mac OS X")) {
                this.outString = this.fixAccents(this.outString);
            }
            this.makeFolderForZippingDirectory();
            this.tutorMonitorFilePathname = this.etmaMonitoringFolder.getText() + "/";
            this.tutorMonitorFilePathname = this.tutorMonitorFilePathname + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText() + "/";
            this.tutorMonitorFilePathname = this.tutorMonitorFilePathname + this.staff_id.getText() + "/" + this.locn_code.getText() + "/monitor.fhi";
            this.putFile1(this.tutorMonitorFilePathname, this.outString);
            this.putFile1(this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText() + "/monitor.fhi", this.outString);
            this.savedFlag = true;
            File tempFile = new File(this.tutorMonitorFilePathname);
            this.ampersandClean(tempFile);
            tempString = this.monitor_comments.getText().replaceAll(this.string02, "<");
            tempString = tempString.replaceAll(this.string03, ">");
            this.monitor_comments.setText(tempString);
        }
        catch (Exception anException) {
            System.out.println("Saved" + anException);
        }
    }

    public String getStudentsData() {
        Object studentXML = "";
        Object studentString = "<student_list>";
        int numberOfScripts = Integer.parseInt(this.number_scripts.getText()) + 1;
        for (int studentNumber = 1; studentNumber < numberOfScripts; ++studentNumber) {
            int numberOfIndividualScripts = this.studentFileFullList.get(studentNumber - 1).size();
            studentString = (String)studentString + "<student_details>";
            studentString = (String)studentString + "<student_forenames>" + this.studentsListTable.getValueAt(studentNumber, 1) + "</student_forenames>";
            studentString = (String)studentString + "<student_surname>" + this.studentsListTable.getValueAt(studentNumber, 2) + "</student_surname>";
            studentString = (String)studentString + "<student_personal_id>" + this.studentsListTable.getValueAt(studentNumber, 0) + "</student_personal_id>";
            studentString = (String)studentString + "<student_sent_date>" + this.studentsListTable.getValueAt(studentNumber, 8) + "</student_sent_date>";
            studentString = (String)studentString + "<tutor_received_date>" + this.studentsListTable.getValueAt(studentNumber, 9) + "</tutor_received_date>";
            studentString = (String)studentString + "<walton_received_date></walton_received_date>";
            studentString = (String)studentString + "<student_monitoring_details>";
            studentString = (String)studentString + "<annotated>" + this.studentsListTable.getValueAt(studentNumber, 6) + "</annotated>";
            studentString = (String)studentString + "<student_completed>" + this.studentsListTable.getValueAt(studentNumber, 7) + "</student_completed>";
            studentString = (String)studentString + "<marking>" + this.studentsListTable.getValueAt(studentNumber, 3) + "</marking>";
            studentString = (String)studentString + "<script_commenting>" + this.studentsListTable.getValueAt(studentNumber, 5) + "</script_commenting>";
            studentString = (String)studentString + "<pt3_commenting>" + this.studentsListTable.getValueAt(studentNumber, 4) + "</pt3_commenting>";
            studentString = (String)studentString + "</student_monitoring_details>";
            studentString = (String)studentString + "<file_list>";
            for (int i = 0; i < numberOfIndividualScripts; ++i) {
                studentString = (String)studentString + "<file_details>";
                studentString = (String)studentString + "<file_name></file_name>";
                String thisFile = this.studentFileFullList.get(studentNumber - 1).get(i);
                studentString = (String)studentString + "<file_path>" + this.studentFileFullList.get(studentNumber - 1).get(i) + "</file_path>";
                studentString = (String)studentString + "<file_annotated>" + this.studentAnnotationMapList.get(studentNumber - 1).get(thisFile) + "</file_annotated>";
                studentString = (String)studentString + "</file_details>";
            }
            studentString = (String)studentString + "</file_list>";
            studentString = (String)studentString + "</student_details>";
        }
        studentXML = studentString;
        studentXML = (String)studentXML + "</student_list>";
        return studentXML;
    }

    public String putFlags() {
        Object flagString = "";
        for (int i = 0; i < 17; ++i) {
            String taga = "<flag" + (i + 1) + ">";
            String tagb = "</flag" + (i + 1) + ">";
            String value = (String)this.monitoringRatings.getValueAt(i, 1);
            String entry = taga + value + tagb;
            flagString = (String)flagString + entry;
        }
        return flagString;
    }

    private String addStartTag(String aString) {
        return "<" + aString + ">";
    }

    private String addEndTag(String aString) {
        return "</" + aString + ">";
    }

    private void monitoringRatingsInputMethodTextChanged(InputMethodEvent evt) {
        this.checkRange();
        this.calculateTotals();
        this.savedFlag = false;
    }

    public boolean totalEntry() {
        return false;
    }

    public void displayCurrentEntry() {
        int xCoord1 = this.monitoringRatings.getSelectedRow();
        int yCoord1 = this.monitoringRatings.getSelectedColumn();
        this.additionField.setText("Current entry: " + (String)this.monitoringRatings.getValueAt(xCoord1, yCoord1));
    }

    private void monitoringRatingsKeyReleased(KeyEvent evt) {
        this.monitoringRatings.setSurrendersFocusOnKeystroke(false);
        int nRow = this.monitoringRatings.getSelectedRow();
        int nCol = this.monitoringRatings.getSelectedColumn();
        if (nRow == 2) {
            // empty if block
        }
        this.savedFlag = false;
    }

    private void monitoringRatingsKeyPressed(KeyEvent evt) {
        this.monitoringRatings.setSurrendersFocusOnKeystroke(false);
        this.savedFlag = false;
    }

    private boolean statusChangeAgree() {
        int createInt = 0;
        if (this.tutor_status.getText().equals("Zipped")) {
            Component frame = null;
            Object[] options = new Object[]{"Yes", "No"};
            createInt = JOptionPane.showOptionDialog(frame, "This will reset the status to 'Unmonitored'!\nDo you wish to do this?", "", 1, 2, null, options, options[1]);
            return createInt == 0;
        }
        return false;
    }

    private String checkStatus() {
        String status = this.tutor_status.getText();
        if (!status.equals("Zipped")) {
            status = "Monitored";
        }
        int numberOfScripts = Integer.parseInt(this.number_scripts.getText());
        for (int i = 1; i < numberOfScripts + 1; ++i) {
            String thisStatus = (String)this.studentsListTable.getValueAt(i, 7);
            if (thisStatus.startsWith("Yes")) continue;
            status = "Unmonitored";
        }
        if (!this.testGradingStatus()) {
            status = "Unmonitored";
        }
        this.tutor_status.setText(status);
        return status;
    }

    private void tooMany(int actual, int max) {
    }

    private String checkQuestionStatus(int aQuestionNumber) {
        String questionStatus = "Marked";
        return questionStatus;
    }

    public boolean testNumeric(String aString) {
        boolean isNumeric = true;
        double aNumber = 0.0;
        if (!aString.contains("+")) {
            try {
                aNumber = Double.valueOf(aString);
            }
            catch (Exception anException) {
                isNumeric = false;
            }
        }
        return isNumeric;
    }

    private void checkEntries() {
    }

    private boolean checkRange() {
        boolean inRangeFlag = true;
        int colNo = this.monitoringRatings.getSelectedColumn();
        int rowNo = this.monitoringRatings.getSelectedRow();
        int noOfLines = this.partStarts.get(this.partStarts.size() - 1);
        String currentValue = (String)this.monitoringRatings.getValueAt(rowNo, colNo);
        if (rowNo > noOfLines - 1) {
            for (int i = noOfLines; i < this.monitoringRatings.getRowCount(); ++i) {
                this.monitoringRatings.setValueAt(null, i, 2);
            }
            inRangeFlag = false;
        }
        return inRangeFlag;
    }

    public void autoFillScores() {
    }

    private void calculateTotals() {
    }

    public void updatePreviousScores() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File ampersandClean(File aFile) {
        BufferedReader buffer = null;
        Object fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            String currentLine = buffer.readLine();
            while (currentLine != null) {
                fileString = (String)fileString + currentLine + "\n";
                currentLine = buffer.readLine();
            }
        }
        catch (Exception currentLine) {
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception currentLine) {}
        }
        char char18 = '\u0012';
        String string18 = "" + char18;
        if (((String)fileString).contains("&") || ((String)fileString).contains(string18) || ((String)fileString).contains(this.string02) || ((String)fileString).contains(this.string03)) {
            fileString = ((String)fileString).replaceAll(string18, "&");
            fileString = ((String)fileString).replaceAll("&amp;", "xyzpqt");
            fileString = ((String)fileString).replaceAll("&", "&amp;");
            fileString = ((String)fileString).replaceAll("xyzpqt", "&amp;");
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write((String)fileString);
            }
            catch (Exception exception) {
            }
            finally {
                try {
                    bufferOut.close();
                }
                catch (Exception exception) {}
            }
        }
        return aFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File tagClean(File aFile) {
        BufferedReader buffer = null;
        Object fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            String currentLine = buffer.readLine();
            while (currentLine != null) {
                fileString = (String)fileString + currentLine + "\n";
                currentLine = buffer.readLine();
            }
        }
        catch (Exception currentLine) {
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception currentLine) {}
        }
        String fileStringOld = fileString;
        char thisChar = '\u0000';
        Object thisString = "";
        for (int charNo = 14; charNo < 32; ++charNo) {
            thisChar = (char)charNo;
            thisString = "" + thisChar;
            fileString = ((String)fileString).replaceAll((String)thisString, "");
        }
        char char12 = '\f';
        String string12 = "" + char12;
        if (((String)(fileString = ((String)fileString).replaceAll(string12, ""))).contains(this.string02) || ((String)fileString).contains(this.string03) || !((String)fileString).equals(fileStringOld)) {
            fileString = ((String)fileString).replaceAll(this.string02, "xtagstart");
            fileString = ((String)fileString).replaceAll(this.string03, "xtagend");
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write((String)fileString);
            }
            catch (Exception exception) {
            }
            finally {
                try {
                    bufferOut.close();
                }
                catch (Exception exception) {}
            }
        }
        return aFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File tagRestore(File aFile) {
        BufferedReader buffer = null;
        Object fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            String currentLine = buffer.readLine();
            while (currentLine != null) {
                fileString = (String)fileString + currentLine + "\n";
                currentLine = buffer.readLine();
            }
        }
        catch (Exception currentLine) {
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception currentLine) {}
        }
        String fileStringOld = fileString;
        if (((String)fileString).contains("xtagstart") || ((String)fileString).contains("xtagend") || !((String)fileString).equals(fileStringOld)) {
            fileString = ((String)fileString).replaceAll("xtagstart", this.string02);
            fileString = ((String)fileString).replaceAll("xtagend", this.string03);
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write((String)fileString);
            }
            catch (Exception exception) {
            }
            finally {
                try {
                    bufferOut.close();
                }
                catch (Exception exception) {}
            }
            String tempString = this.monitor_comments.getText();
            tempString = tempString.replaceAll("xtagstart", "<");
            tempString = tempString.replaceAll("xtagend", ">");
            this.monitor_comments.setText(tempString);
        }
        return aFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File ampersandCleanAlt(File aFile) {
        BufferedReader buffer = null;
        Object fileString = "";
        try {
            buffer = new BufferedReader(new FileReader(aFile));
            String currentLine = buffer.readLine();
            while (currentLine != null) {
                fileString = (String)fileString + currentLine + "\n";
                currentLine = buffer.readLine();
            }
        }
        catch (Exception currentLine) {
        }
        finally {
            try {
                buffer.close();
            }
            catch (Exception currentLine) {}
        }
        if (((String)fileString).contains("&amp;")) {
            fileString = ((String)fileString).replaceAll("&amp;", "&");
            BufferedWriter bufferOut = null;
            try {
                bufferOut = new BufferedWriter(new FileWriter(aFile));
                bufferOut.write((String)fileString);
            }
            catch (Exception exception) {
            }
            finally {
                try {
                    bufferOut.close();
                }
                catch (Exception exception) {}
            }
        }
        return aFile;
    }

    private void getShortDetails(String aString, Map aMap, File aFile) {
        try {
            org.w3c.dom.Document doc;
            block10: {
                this.entryErrorFlag = false;
                this.ampersandClean(aFile);
                aFile = this.tagClean(aFile);
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                doc = null;
                try {
                    doc = docBuilder.parse(aFile);
                }
                catch (Exception anException) {
                    if (!this.readErrorFlag) break block10;
                    this.entryErrorFlag = true;
                }
            }
            this.readErrorFlag = false;
            doc.getDocumentElement().normalize();
            NodeList listOfPersons = doc.getElementsByTagName(aString);
            Node firstPersonNode = listOfPersons.item(0);
            NodeList listOfStudentDetails = listOfPersons.item(0).getChildNodes();
            int numberOfStudentItems = listOfStudentDetails.getLength();
            if (firstPersonNode.getNodeType() == 1) {
                for (int i = 0; i < numberOfStudentItems; ++i) {
                    try {
                        Element studentElement = (Element)listOfStudentDetails.item(i);
                        String thisDetail = studentElement.getNodeName();
                        try {
                            NodeList detailList = listOfStudentDetails.item(i).getChildNodes();
                            aMap.put(thisDetail, detailList.item(0).getNodeValue());
                        }
                        catch (Exception e) {
                            aMap.put(thisDetail, "");
                        }
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            aFile = this.tagClean(aFile);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void getMarks2(File aFile) {
    }

    public String getTagValue(String sTag, Element eElement) {
        String thisValue = "";
        try {
            NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
            Node nValue = nlList.item(0);
            thisValue = nValue.getNodeValue();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return thisValue;
    }

    public void clearAll(JTable aTable) {
        int nRows = aTable.getRowCount();
        int nColumns = aTable.getColumnCount();
        for (int i = 0; i < nRows - 1; ++i) {
            for (int j = 0; j < nColumns; ++j) {
                aTable.setValueAt("", i, j);
            }
        }
    }

    public void getStudentsList(File aFile) {
        this.studentFileFullList.clear();
        this.studentAnnotationMapList.clear();
        this.studentSummary.setVisible(true);
        this.studentSummary.setTitle("Students' scripts submitted for this tutor");
        this.studentSummary.setSize(860, 200);
        this.studentsListTable.setSize(800, 150);
        this.setSizesOfStudentList();
        this.clearAll(this.studentsListTable);
        aFile = this.tagClean(aFile);
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(aFile);
            this.getQuestionNumbers().clear();
            this.partStarts.clear();
            this.numberOfParts.clear();
            doc.getDocumentElement().normalize();
            NodeList studentBlock = doc.getElementsByTagName("student_list");
            NodeList studentDetailsNode = studentBlock.item(0).getChildNodes();
            this.numberOFStudents = studentDetailsNode.getLength();
            String studentSurname = "";
            String studentForeNames = "";
            Object listOfNodes = null;
            String qpc = "";
            String studQnScore = "";
            Element studentElement = null;
            String annotatedFlag = "";
            String currentPath = "";
            int placeHolder = 0;
            for (int studentNumber = 0; studentNumber < this.numberOFStudents; ++studentNumber) {
                ArrayList<String> annotationList = new ArrayList<String>();
                ArrayList currentPathList = new ArrayList();
                HashMap<String, String> annotationLineMap = new HashMap<String, String>();
                HashMap<String, String> annotationOldMap = new HashMap<String, String>();
                Node thisNode = studentDetailsNode.item(studentNumber);
                if (!thisNode.getNodeName().equals("student_details")) continue;
                ++placeHolder;
                try {
                    try {
                        studentElement = (Element)studentDetailsNode.item(studentNumber);
                    }
                    catch (Exception e) {
                        System.out.println("here1" + e);
                    }
                    NodeList fileElements = studentElement.getElementsByTagName("file_list");
                    int numberOfFileBits = fileElements.item(0).getChildNodes().getLength();
                    for (int thisFile = 0; thisFile < numberOfFileBits; ++thisFile) {
                        Node fileElement = fileElements.item(0).getChildNodes().item(thisFile);
                        NodeList fileBits = fileElement.getChildNodes();
                        int numberOfNodes = fileBits.getLength();
                        for (int i = 0; i < numberOfNodes; ++i) {
                            Node currentNode = fileBits.item(i);
                            if (currentNode.getNodeType() != 1) continue;
                            if (currentNode.getNodeName().equals("file_annotated")) {
                                try {
                                    annotatedFlag = currentNode.getTextContent();
                                    annotationList.add(annotatedFlag);
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                            }
                            if (currentNode.getNodeName().equals("file_path")) {
                                try {
                                    currentPath = currentNode.getTextContent();
                                }
                                catch (Exception exception) {
                                    // empty catch block
                                }
                            }
                            annotationOldMap.put(currentPath, annotatedFlag);
                        }
                    }
                    studentSurname = studentElement.getElementsByTagName("student_surname").item(0).getTextContent();
                    studentForeNames = studentElement.getElementsByTagName("student_forenames").item(0).getTextContent();
                    this.studentsListTable.setValueAt(studentForeNames, placeHolder, 1);
                    this.studentsListTable.setValueAt(studentSurname, placeHolder, 2);
                    this.studentsListTable.setValueAt(this.getTagValue("annotated", studentElement), placeHolder, 6);
                    this.studentsListTable.setValueAt(this.getTagValue("marking", studentElement), placeHolder, 3);
                    this.studentsListTable.setValueAt(this.getTagValue("pt3_commenting", studentElement), placeHolder, 4);
                    this.studentsListTable.setValueAt(this.getTagValue("script_commenting", studentElement), placeHolder, 5);
                    this.studentsListTable.setValueAt(this.getTagValue("student_completed", studentElement), placeHolder, 7);
                    this.studentsListTable.setValueAt(this.getTagValue("student_personal_id", studentElement), placeHolder, 0);
                    String studentPID = this.getTagValue("student_personal_id", studentElement);
                    String pathToStudentsFolder = this.etmaMonitoringFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText() + "/" + this.staff_id.getText() + "/" + this.locn_code.getText() + "/" + studentPID + "/";
                    File studentsFolder = new File(pathToStudentsFolder);
                    File[] studentsFolderContents = studentsFolder.listFiles();
                    ArrayList<String> fileList = new ArrayList<String>();
                    int numberOfStudentFiles = studentsFolderContents.length;
                    for (int fileNumber = 0; fileNumber < numberOfStudentFiles; ++fileNumber) {
                        File thisStudentFile = studentsFolderContents[fileNumber];
                        if (thisStudentFile.getName().equals(".DS_Store")) continue;
                        fileList.add(studentsFolderContents[fileNumber].getPath());
                    }
                    int nFiles = fileList.size();
                    int numberOfAnnotations = annotationList.size();
                    boolean annotationBlank = true;
                    for (int thisAnn = 0; thisAnn < numberOfAnnotations; ++thisAnn) {
                        if (((String)annotationList.get(thisAnn)).equals("")) continue;
                        annotationBlank = false;
                    }
                    if (numberOfAnnotations < nFiles) {
                        for (int k = 0; k < nFiles - numberOfAnnotations; ++k) {
                            annotationList.add("");
                        }
                    }
                    for (int fileNumber = 0; fileNumber < numberOfStudentFiles; ++fileNumber) {
                        annotationLineMap.put(studentsFolderContents[fileNumber].getPath(), (String)annotationOldMap.get(studentsFolderContents[fileNumber].getPath()));
                    }
                    this.studentFileFullList.add(fileList);
                    this.studentAnnotationMapList.add(annotationLineMap);
                    this.studentsListTable.setValueAt(this.getTagValue("student_sent_date", studentElement), placeHolder, 8);
                    this.studentsListTable.setValueAt(this.getTagValue("tutor_received_date", studentElement), placeHolder, 9);
                    continue;
                }
                catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void zipDir(String dir2zip, ZipOutputStream zos, int nesting) {
        try {
            File zipDir = new File(dir2zip);
            String[] dirList = zipDir.list();
            byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            for (int i = 0; i < dirList.length; ++i) {
                File f = new File(zipDir, dirList[i]);
                if (f.isDirectory()) {
                    String filePath = f.getPath();
                    this.zipDir(filePath, zos, nesting);
                    continue;
                }
                FileInputStream fis = new FileInputStream(f);
                String anEntryString = f.getPath();
                String tempString = this.fixString(anEntryString, nesting);
                ZipEntry aNewEntry = new ZipEntry(tempString);
                String firstChar = tempString.substring(0, 1);
                if (!firstChar.equals(".") && !anEntryString.contains("Icon\r")) {
                    zos.putNextEntry(aNewEntry);
                    while ((bytesIn = fis.read(readBuffer)) != -1) {
                        zos.write(readBuffer, 0, bytesIn);
                    }
                }
                fis.close();
            }
        }
        catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }

    private String fixString(String aString, int nesting) {
        String newString = "";
        String tempString = "";
        String etmamonitoringRoot = this.etmaMonitoringFolder.getText();
        int etmamonitoringRootLength = etmamonitoringRoot.length();
        newString = aString.substring(aString.indexOf(etmamonitoringRoot) + etmamonitoringRootLength + 1);
        for (int k = 0; k < nesting; ++k) {
            try {
                if (!this.osName.contains("Windows")) {
                    tempString = newString.substring(0, newString.indexOf("/") + 1);
                    newString = newString.replace(tempString, "");
                    continue;
                }
                tempString = newString.substring(0, newString.indexOf("\\") + 1);
                newString = newString.replace(tempString, "");
                continue;
            }
            catch (Exception anException) {
                System.out.println("Error105 " + anException);
            }
        }
        return newString;
    }

    public boolean checkScriptClosure() {
        Component frame;
        boolean continueFlag = true;
        int continueInt = 0;
        Object openWarning = "Before you zip, DO check that you have saved the current monitoring record\nand ALL open annotated scripts, if appropriate.\nTake particular care that you have saved the final version\nof the current script!";
        String cmd = "";
        if (!this.savedFlag) {
            Object[] options = new Object[]{"Continue without saving", "Save", "Cancel"};
            frame = null;
            continueInt = JOptionPane.showOptionDialog(frame, "You are advised to save the current work before continuing", "Careful...!", 1, 2, null, options, options[2]);
        }
        if (continueInt == 0) {
            if (this.osName.equals("Mac OS X")) {
                cmd = "lsof -n ";
                String listOfApps = "";
                if (this.osName.equals("Mac OS X") && this.checkClosureFlagPreferences) {
                    boolean tempFlag;
                    try {
                        this.messageWindow.setSize(400, 10);
                        this.messageWindow.setVisible(true);
                        this.messageWindow.setTitle("Please wait.... checking files....");
                        this.messageWindow.setLocation(300, 300);
                        Runtime thisRun = Runtime.getRuntime();
                        Process proc = thisRun.exec(cmd);
                        InputStream is = proc.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        StringBuilder sb = new StringBuilder(10000);
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                        listOfApps = sb.toString();
                    }
                    catch (Exception e) {
                        System.out.println(e);
                    }
                    this.messageWindow.setVisible(false);
                    boolean bl = tempFlag = listOfApps.contains(this.currentStudentScript) && !this.currentStudentScript.equals("");
                    if (listOfApps.contains(this.markedString) || tempFlag) {
                        String insert = "one";
                        if (tempFlag) {
                            insert = "the current";
                        }
                        openWarning = "You still appear to have at least " + insert + " student's Marked script open!\nIt's safer to close all the scripts before you zip.\n";
                    }
                }
            } else if (this.checkClosureFlagPreferences) {
                File aFile;
                boolean openFlag = false;
                if (!this.currentStudentScript.equals("") && (openFlag = (aFile = new File(this.currentStudentScript)).renameTo(aFile))) {
                    openWarning = "The current script appears to be open.\nIt's safer to close it or at least check it needs to be saved\nbefore you zip!\n";
                }
            }
            Object[] options = new Object[]{"Continue", "Cancel"};
            frame = null;
            String alertText = openWarning;
            int n = JOptionPane.showOptionDialog(frame, alertText, "Before you zip...", 1, 3, null, options, options[0]);
            if (n == 1) {
                continueFlag = false;
            }
        }
        if (continueInt == 1) {
            this.saveDetails();
            continueFlag = false;
        }
        if (continueInt == 2) {
            continueFlag = false;
        }
        return continueFlag;
    }

    public boolean testGradingStatus() {
        boolean graded = true;
        int nScripts = Integer.parseInt(this.number_scripts.getText());
        for (int studentRow = 1; studentRow < nScripts + 1; ++studentRow) {
            for (int col = 3; col < 5; ++col) {
                String thisGrade = (String)this.studentsListTable.getValueAt(studentRow, col);
                if (!thisGrade.equals("")) continue;
                graded = false;
            }
        }
        return graded;
    }

    public List findFilesToBeMoved() {
        ArrayList<String> filesToBeMoved = new ArrayList<String>();
        filesToBeMoved.clear();
        int numberOfLists = this.studentFileFullList.size();
        for (int i = 0; i < numberOfLists; ++i) {
            Map<String, String> thisAnnotationLine = this.studentAnnotationMapList.get(i);
            System.out.println(thisAnnotationLine);
            List<String> thisLine = this.studentFileFullList.get(i);
            int listLength = thisLine.size();
            for (int j = 0; j < listLength; ++j) {
                try {
                    String thisPath = thisLine.get(j);
                    String annFlag = thisAnnotationLine.get(thisPath);
                    if (!annFlag.equals("Yes")) continue;
                    filesToBeMoved.add(thisPath);
                    continue;
                }
                catch (Exception anException) {
                    System.out.println("Missing file");
                }
            }
        }
        return filesToBeMoved;
    }

    public void moveFiles() {
        List filesToBeMoved = new ArrayList();
        String sourcePath = "";
        Object destinationPath = "";
        filesToBeMoved = this.findFilesToBeMoved();
        int numberOfFiles = filesToBeMoved.size();
        for (int thisFile = 0; thisFile < numberOfFiles; ++thisFile) {
            sourcePath = (String)filesToBeMoved.get(thisFile);
            File sourceFile = new File(sourcePath);
            String studentPID = sourceFile.getParentFile().getName();
            String destinationFileName = sourceFile.getName();
            destinationPath = this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText() + "/" + studentPID + destinationFileName;
            File destinationFile = new File((String)destinationPath);
            File destinationFolder = destinationFile.getParentFile();
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
            try {
                this.makeCopy(sourceFile, destinationFile);
                continue;
            }
            catch (Exception e) {
                System.out.println("Copy 106 " + e);
            }
        }
    }

    public void zipper() {
        boolean finishedGrading = this.testGradingStatus();
        if (!finishedGrading) {
            JOptionPane.showMessageDialog(null, "You haven't finished grading all the scripts!", "", 2);
        } else if (!this.tutor_status.getText().equals("Monitored") && !this.tutor_status.getText().equals("Zipped")) {
            JOptionPane.showMessageDialog(null, "You haven't marked each script as complete!", "", 2);
        } else {
            boolean continueFlag;
            int n = 0;
            if (this.monitor_comments.getText().equals("")) {
                Object[] options = new Object[]{"Yes", "No"};
                Component frame = null;
                n = JOptionPane.showOptionDialog(frame, "You don't seem to have commented on this monitoring. \nDo you still wish to proceed and zip?", "", 1, 3, null, options, options[1]);
            }
            if (n == 0 && (continueFlag = this.checkScriptClosure())) {
                this.messageWindow.setSize(400, 10);
                this.messageWindow.setVisible(true);
                this.messageWindow.setTitle("Please wait.... zipping files....");
                this.messageWindow.setLocation(300, 300);
                try {
                    String returnsPath = this.etmaMonitoringFolder.getText() + this.returnsName;
                    File returnsFolder = new File(returnsPath);
                    returnsFolder.mkdir();
                }
                catch (Exception returnsPath) {
                    // empty catch block
                }
                this.moveFiles();
                this.saveDetails();
                File etmamonitoringBase = new File(this.etmaMonitoringFolder.getText());
                File tempBase = new File(etmamonitoringBase + this.tempName);
                File monitoredZip = new File(etmamonitoringBase + this.tempName + "/Monitored.zip");
                String pathName = this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText();
                File bFile = new File(pathName);
                String bFileName = bFile.getParent();
                File aFile = bFile.getParentFile();
                try {
                    tempBase.mkdir();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.zip_date.setText(this.getDateAndTime());
                String name1 = "Monitored";
                String name2 = this.getZipfileNameNonBatch2();
                this.zip_filename.setText(name2);
                this.saveDetails();
                boolean success1 = this.zipDirectory(bFile, this.etmaMonitoringFolder.getText() + this.tempName + name1, 4);
                File thisFile = new File(this.etmaMonitoringFolder.getText() + this.tempName);
                String fileToBeReturned = this.etmaMonitoringFolder.getText() + this.returnsName + name2;
                this.zip_filepath.setText(fileToBeReturned);
                this.saveDetails();
                boolean success2 = this.zipDirectory(thisFile, fileToBeReturned, 3);
                if (success1 && success2) {
                    Timer timer = new Timer();
                    ScheduleRunner task = new ScheduleRunner();
                    this.messageWindow.setVisible(false);
                    this.tutor_status.setText("Zipped");
                    EtmaMonitorJ.copyString(fileToBeReturned + ".zip");
                    EtmaMonitorJ.copyString(name2 + ".zip");
                    this.returnMonitoring("Files have been zipped!");
                } else {
                    this.zip_filename.setText("");
                    this.zip_date.setText("");
                    this.tutor_status.setText("");
                    this.messageWindow.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Error - files have NOT been zipped!", "", 0);
                }
                monitoredZip.delete();
                this.saveDetails();
                boolean bl = EtmaMonitorJ.deleteDir(bFile);
            }
        }
        if (this.submittedMonitoring.isVisible()) {
            this.openList();
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    EtmaMonitorJ.deleteFolder(f);
                    continue;
                }
                f.delete();
            }
        }
    }

    public void returnMonitoring(String errorMessage) {
        String returnAddress = "";
        Object[] options = new Object[]{"Return zipped files now ", "Don't return files at present"};
        Component frame = null;
        int n = JOptionPane.showOptionDialog(frame, errorMessage + "\nThe path to the zipped file has been copied to the clipboard", "", 1, 3, null, options, options[0]);
        if (n == 0) {
            returnAddress = this.ouEtmaAddress.getText();
            String cb = this.course_code.getText();
            if (cb.contains("COM100")) {
                returnAddress = this.ouTrainingAddress.getText();
            }
            String myURI = returnAddress;
            try {
                Desktop.getDesktop().browse(new URI(myURI));
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unable to connect to " + myURI, "Sorry!", 2);
            }
        }
    }

    public static void copyString(String data) {
        EtmaMonitorJ.copyTransferableObject(new StringSelection(data));
    }

    public static void copyTransferableObject(Transferable contents) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
    }

    private void zipFilesActionPerformed(ActionEvent evt) {
        this.zipper();
    }

    private String getZipfileName1(String subNo) {
        int subNoInt = Integer.parseInt(subNo);
        List<String> tmaDetails = this.getTableEntry(this.studentIndex, subNoInt);
        Object firstString = "";
        firstString = tmaDetails.get(0) + "-" + tmaDetails.get(7) + "-" + tmaDetails.get(8) + "-" + tmaDetails.get(9) + "-" + tmaDetails.get(3) + "-" + tmaDetails.get(6);
        return firstString;
    }

    private String getZipfileName2(String subNo) {
        int subNoInt = Integer.parseInt(subNo);
        List<String> tmaDetails = this.getTableEntry(this.tutorList.getSelectedIndex(), subNoInt);
        Object secondString = "";
        secondString = tmaDetails.get(7) + "-" + tmaDetails.get(8) + "-" + this.getDateAndTime() + "_M";
        return secondString;
    }

    private String getZipfileName3(String subNo) {
        int subNoInt = 0;
        List<String> tmaDetails = this.getTableEntry(this.tutorList.getSelectedIndex(), subNoInt);
        Object secondString = "";
        secondString = tmaDetails.get(4) + "-" + tmaDetails.get(5) + "-" + tmaDetails.get(6) + "-" + this.getDateAndTime() + "_M";
        return secondString;
    }

    private String getZipfileNameNonBatch1(String subNo) {
        int subNoInt = Integer.parseInt(subNo);
        List<String> tmaDetails = this.getTableEntry(this.studentIndex, subNoInt);
        Object firstString = "";
        firstString = this.staff_id.getText() + "-" + this.course_code.getText() + "-" + this.pres_code.getText() + "-" + this.assgnmt_suffix.getText() + "-" + this.staff_id.getText() + "-" + this.new_tutor.getText();
        return firstString;
    }

    private String getZipfileNameNonBatch2() {
        String secondString = "monitored_" + this.getDateAndTime();
        return secondString;
    }

    public String addZeros(int anInt, int numberOfDigits) {
        String oldString = String.valueOf(anInt);
        Object newString = oldString;
        if (oldString.length() < numberOfDigits) {
            for (int i = 0; i < numberOfDigits - oldString.length(); ++i) {
                newString = "0" + (String)newString;
            }
        }
        return newString;
    }

    private String getDateAndTime() {
        Object theDateAndTime = "";
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        String stime = calendar.get(1) + "_" + this.addZeros(calendar.get(2) + 1, 2) + "_" + this.addZeros(calendar.get(5), 2) + "-" + this.addZeros(calendar.get(11), 2) + "_" + this.addZeros(calendar.get(12), 2) + "_" + this.addZeros(calendar.get(13), 2);
        theDateAndTime = stime;
        return theDateAndTime;
    }

    private boolean zipDirectory(File aFile, String pathName, int nesting) {
        boolean successFlag = true;
        try {
            File bFile = new File(pathName);
            ZipOutputStream zos = null;
            String currentOutputPath = bFile.getPath() + ".zip";
            if (!currentOutputPath.contains(".fhi")) {
                // empty if block
            }
            zos = new ZipOutputStream(new FileOutputStream(currentOutputPath));
            zos.setMethod(8);
            zos.setLevel(-1);
            this.zipDir(aFile.getPath(), zos, nesting);
            zos.close();
            return successFlag;
        }
        catch (Exception e) {
            successFlag = false;
            return successFlag;
        }
    }

    private void printTmaListActionPerformed(ActionEvent evt) {
        try {
            this.listOfTutors.print();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void printGradesActionPerformed(ActionEvent evt) {
        try {
            this.studentsListTable.print();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void createFeedbackActionPerformed(ActionEvent evt) {
        this.createFeedback();
    }

    private void highlightUnmarkedActionPerformed(ActionEvent evt) {
        this.highlightedFlag = this.highlightUnmarked.isSelected();
        this.ourRoot.putBoolean("highlightedFlag", this.highlightedFlag);
        if (this.highlightUnmarked.isSelected()) {
            this.getNewTmaRedRows(this.listOfTutors.getRowCount());
        }
        this.openList();
    }

    public void distributeFile() {
        if (this.course_code.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "You must load a PT3 first!");
        } else {
            JFileChooser _fileChooser = new JFileChooser(this.downloadsFolder.getText());
            try {
                _fileChooser.setDialogTitle("Please select document to be distributed to all students' folders:");
                int path = _fileChooser.showOpenDialog(null);
                int n2 = 0;
                this.distFile = _fileChooser.getSelectedFile();
                String distFileName = this.distFile.getName();
                String distFilePath = this.distFile.getPath();
                if (distFileName.contains(".fhi") || distFileName.contains(".app") || distFileName.contains(".exe")) {
                    JOptionPane.showMessageDialog(null, "This is either an etma system file or an application - you can't distribute it!");
                } else {
                    double fileSize = this.distFile.length();
                    if (fileSize > 1000000.0) {
                        Object[] options2 = new Object[]{"Continue", "Stop!"};
                        Component frame2 = null;
                        n2 = JOptionPane.showOptionDialog(frame2, "This file is " + this.distFile.length() + "bytes long!\nAre you sure you want to distribute it to all students?", "", 1, 3, null, options2, options2[1]);
                    }
                    if (n2 != 1) {
                        Object[] options = new Object[]{"OK - Distribute", "Stop!"};
                        Component frame = null;
                        int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to distribute the file\n" + distFilePath + "\nto the folders of all " + this.course_code.getText() + " students who have submitted TMA" + this.assgnmt_suffix.getText() + "\nThis is not easily undoable!", "", 1, 3, null, options, options[1]);
                        if (n != 1) {
                            String pathToCurrentTmaFolder = this.etmaMonitoringFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText();
                            File thisTmaFolder = new File(pathToCurrentTmaFolder);
                            boolean existsFlag = false;
                            File[] tmaStudentList = thisTmaFolder.listFiles();
                            for (int i = 0; i < tmaStudentList.length; ++i) {
                                if (!tmaStudentList[i].isDirectory()) continue;
                                String studName = "";
                                File[] subNos = tmaStudentList[i].listFiles();
                                for (int j = 0; j < subNos.length; ++j) {
                                    String submNo = subNos[j].getName();
                                    if (!subNos[j].isDirectory()) continue;
                                    String insertFilePath = tmaStudentList[i] + "/" + submNo + "/" + distFileName;
                                    File bFile = new File(insertFilePath);
                                    if (bFile.exists()) {
                                        Object[] options1 = new Object[]{"Replace", "Don't replace"};
                                        Object frame1 = null;
                                        int n1 = JOptionPane.showOptionDialog(frame, "The file " + distFilePath + "\nalready exists in " + tmaStudentList[i].getName() + studName + "\nDo you want to replace it?", "", 1, 3, null, options1, options1[1]);
                                        if (n1 == 1) {
                                            existsFlag = true;
                                            continue;
                                        }
                                        this.makeCopy(this.distFile, bFile);
                                        continue;
                                    }
                                    this.makeCopy(this.distFile, bFile);
                                }
                            }
                            if (!existsFlag) {
                                JOptionPane.showMessageDialog(null, "The file " + distFilePath + " has been distributed to all students.");
                            } else {
                                JOptionPane.showMessageDialog(null, "The file " + distFilePath + " was distributed to students,\nexcept those where you decided not to replace the file.");
                            }
                        }
                    }
                }
            }
            catch (Exception anException) {
                System.out.println(anException);
            }
        }
    }

    public void undoDistribution() {
        try {
            boolean n2 = false;
            String distFileName = this.distFile.getName();
            String distFilePath = this.distFile.getPath();
            if (distFileName.contains(".fhi") || distFileName.contains(".app") || distFileName.contains(".exe")) {
                JOptionPane.showMessageDialog(null, "This is either an etma system file or an application - you can't delete it!");
            } else {
                Object[] options = new Object[]{"OK - Delete", "Stop!"};
                Component frame = null;
                int n = JOptionPane.showOptionDialog(frame, "Are you sure you want to delete the file\n" + distFilePath + "\nin the folders of all " + this.course_code.getText() + " students who have submitted TMA" + this.assgnmt_suffix.getText() + "\nThis is not undoable!", "", 1, 3, null, options, options[1]);
                if (n != 1) {
                    String pathToCurrentTmaFolder = this.etmaMonitoringFolder.getText() + "/" + this.course_code.getText() + "-" + this.pres_code.getText() + "/" + this.assgnmt_suffix.getText();
                    File thisTmaFolder = new File(pathToCurrentTmaFolder);
                    boolean preserveFlag = false;
                    File[] tmaStudentList = thisTmaFolder.listFiles();
                    for (int i = 0; i < tmaStudentList.length; ++i) {
                        if (!tmaStudentList[i].isDirectory()) continue;
                        String studName = "";
                        File[] subNos = tmaStudentList[i].listFiles();
                        for (int j = 0; j < subNos.length; ++j) {
                            String submNo = subNos[j].getName();
                            if (!subNos[j].isDirectory()) continue;
                            String insertFilePath = tmaStudentList[i] + "/" + submNo + "/" + distFileName;
                            File bFile = new File(insertFilePath);
                            if (bFile.exists()) {
                                Object[] options1 = new Object[]{"Delete", "Don't delete"};
                                Object frame1 = null;
                                int n1 = JOptionPane.showOptionDialog(frame, "The file " + distFilePath + "\nexists in " + tmaStudentList[i].getName() + studName + "\nDo you want to delete it?", "", 1, 3, null, options1, options1[1]);
                                if (n1 == 1) {
                                    preserveFlag = true;
                                    continue;
                                }
                                bFile.delete();
                                continue;
                            }
                            bFile.delete();
                        }
                    }
                    if (!preserveFlag) {
                        JOptionPane.showMessageDialog(null, "The file " + distFilePath + " has been deleted in all students' folders.");
                    } else {
                        JOptionPane.showMessageDialog(null, "The file " + distFilePath + " was deleted in students' folders,\nexcept those where you decided not to delete the file.");
                    }
                }
            }
        }
        catch (Exception anException) {
            System.out.println(anException);
            JOptionPane.showMessageDialog(null, "You can only undo a distribution immediately after you've carried it out.\nYou will have to distribute the file again, then immediately undo it.");
        }
    }

    public int askFrom2Options(String option0, String option1, String message, int defaultOption, String title) {
        Object[] options = new Object[]{option0, option1};
        Component frame = null;
        int n = JOptionPane.showOptionDialog(frame, message, title, 1, 3, null, options, options[defaultOption]);
        return n;
    }

    public int askFrom3Options(String option0, String option1, String option2, String message, int defaultOption, String title) {
        Object[] options = new Object[]{option0, option1, option2};
        Component frame = null;
        int n = JOptionPane.showOptionDialog(frame, message, title, 1, 3, null, options, options[defaultOption]);
        return n;
    }

    private void monitor_commentsKeyPressed(KeyEvent evt) {
    }

    private void cutTextActionPerformed(ActionEvent evt) {
        String selectedText = "";
        Component tempComp = this.getFocusOwner();
        try {
            JTextField tempComp1 = (JTextField)tempComp;
            selectedText = tempComp1.getSelectedText();
        }
        catch (Exception anException) {
            selectedText = this.monitor_comments.getSelectedText();
        }
        this.monitor_comments.replaceSelection("");
        StringSelection textToCopy = new StringSelection(selectedText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(textToCopy, textToCopy);
    }

    private void monitor_commentsMouseReleased(MouseEvent evt) {
        if (this.statusChangeAgree()) {
            int nScripts = Integer.parseInt(this.number_scripts.getText());
            for (int i = 1; i < nScripts + 1; ++i) {
                this.studentsListTable.setValueAt("No", i, 7);
            }
            this.tutor_status.setText("Unmonitored");
            this.saveDetails();
            this.savedFlag = false;
        }
        if (evt.isPopupTrigger()) {
            this.showPopupMenu(evt);
        }
    }

    private void scriptsSummaryTableMouseReleased(MouseEvent evt) {
        this.scriptsSummaryMouseReleased();
    }

    public boolean checkAnnotated() {
        boolean isItAnnotated = false;
        int tableSize = this.scriptsSummaryTable.getRowCount();
        try {
            for (int thisRow = 0; thisRow < tableSize; ++thisRow) {
                String annotation = (String)this.scriptsSummaryTable.getValueAt(thisRow, 1);
                if (!annotation.equals("Yes")) continue;
                isItAnnotated = true;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return isItAnnotated;
    }

    public void scriptsSummaryMouseReleased() {
        try {
            int rowNo = this.scriptsSummaryTable.getSelectedRow();
            int colNo = this.scriptsSummaryTable.getSelectedColumn();
            File destinationFile = null;
            String testForBlank = (String)this.scriptsSummaryTable.getValueAt(rowNo, 0);
            if (!testForBlank.equals("")) {
                String thisFileName = (String)this.scriptsSummaryTable.getValueAt(rowNo, 0);
                String fileToOpen = this.studentFolderPathName + "/" + thisFileName;
                if (colNo == 1) {
                    String thisFilePath = this.studentFileFullList.get(this.currentStudentRow - 1).get(rowNo);
                    Map<String, String> thisMap = this.studentAnnotationMapList.get(this.currentStudentRow - 1);
                    String thisAnnotation = thisMap.get(thisFilePath);
                    this.toggler(this.yesNoCategories, this.scriptsSummaryTable, rowNo, colNo);
                    String annStatus = (String)this.scriptsSummaryTable.getValueAt(rowNo, 1);
                    File sourceFile = new File(fileToOpen);
                    if (!sourceFile.isDirectory()) {
                        String sourceFileName = sourceFile.getName();
                        destinationFile = new File(this.folderForZippingPathname + this.staff_id.getText() + "." + this.course_code.getText() + "-" + this.pres_code.getText() + "." + this.assgnmt_suffix.getText() + "/" + this.currentStudentPID + sourceFileName);
                        this.studentAnnotationMapList.get(this.currentStudentRow - 1).put(thisFilePath, annStatus);
                        Map<String, String> map = this.studentAnnotationMapList.get(this.currentStudentRow - 1);
                    } else {
                        this.scriptsSummaryTable.setValueAt("No", rowNo, 1);
                        this.studentAnnotationMapList.get(this.currentStudentRow - 1).put(thisFilePath, "No");
                        JOptionPane.showMessageDialog(null, "You can't annotate a directory!\nIf you want to annotate a file within the directory:\nPlease click \"Open tutor's folder\" then move it out of the inner directory.\nYou will have to quit the app and relaunch before the moved file becomes visible.\nThen reslect the annotated files to be returned.", "", 2);
                    }
                }
                if (colNo == 0) {
                    this.tmaOpener(fileToOpen);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (this.checkAnnotated()) {
            this.studentsListTable.setValueAt("Yes", this.currentStudentRow, 6);
        } else {
            this.studentsListTable.setValueAt("No", this.currentStudentRow, 6);
        }
    }

    private void hideTestJsActionPerformed(ActionEvent evt) {
    }

    private void colorRemoveActionPerformed(ActionEvent evt) {
    }

    private void scriptsSummaryTableMousePressed(MouseEvent evt) {
    }

    private void jTextField1ActionPerformed(ActionEvent evt) {
    }

    private void checkReturnsFlagActionPerformed(ActionEvent evt) {
        this.checkReturnsFlagPreferences = this.checkReturnsFlag.isSelected();
        this.ourRoot.putBoolean("checkReturnsFlagPreferences", this.checkReturnsFlagPreferences);
    }

    private void globalFontsActionPerformed(ActionEvent evt) {
        this.globalFontsPreferences = this.globalFonts.isSelected();
        this.ourRoot.putBoolean("globalFonts", this.globalFonts.isSelected());
        if (this.globalFontsPreferences) {
            this.setFonts(this.fontPreferences);
        } else {
            this.setFonts(10);
        }
    }

    private void autoImportFlag1ActionPerformed(ActionEvent evt) {
        String df = this.downloadsFolder.getText();
        if (df.equals("")) {
            JOptionPane.showMessageDialog(null, "Auto import will only work if you set the location of\nyour browser's download folder in the Filehandler preferences\n(second button from the top).");
            this.autoImportFlag1.setSelected(false);
        } else {
            this.autoImportFlag = this.autoImportFlag1.isSelected();
            this.ourRoot.putBoolean("autoImportFlag", this.autoImportFlag1.isSelected());
            if (this.autoImportFlag1.isSelected()) {
                this.startImportTimer();
            } else {
                try {
                    this.timer2.cancel();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    private void sizeWarnFlagActionPerformed(ActionEvent evt) {
        this.sizeWarnFlagPreferences = this.sizeWarnFlag.isSelected();
        this.ourRoot.putBoolean("sizeWarnFlagPreferences", this.sizeWarnFlagPreferences);
    }

    private void autoMp3ActionPerformed(ActionEvent evt) {
        this.autoMp3Preferences = this.autoMp3.isSelected();
        this.ourRoot.putBoolean("autoMp3", this.autoMp3.isSelected());
    }

    private void setAudioAppActionPerformed(ActionEvent evt) {
        this.ourRoot.put("currentAudioPreferences", this.currentAudioPreferences);
        JFileChooser _fileChooser = new JFileChooser("/Applications");
        try {
            if (this.osName.contains("Linux")) {
                this.currentAudioPath = JOptionPane.showInputDialog(null, (Object)"Please enter the short(launch) name of the application you wish the filehandler to use to open mp3s");
            } else {
                Component frame = null;
                Object[] options = new Object[]{"Default", "Select other"};
                int n = JOptionPane.showOptionDialog(frame, "Do you want to use the system default mp3 application?", "Default Audio", 1, 3, null, options, options[0]);
                if (n == 0) {
                    this.currentAudioPath = "System Default";
                    this.currentAudioPreferences = "System Default";
                } else {
                    _fileChooser.setDialogTitle("Please select application to open students' mp3s: ");
                    int path = _fileChooser.showOpenDialog(null);
                    File aFile = _fileChooser.getSelectedFile();
                    this.currentAudioPath = aFile.getPath();
                }
            }
            JOptionPane.showMessageDialog(null, "Mp3 app path set to " + this.currentAudioPath);
            this.audioPath.setText(this.currentAudioPath);
            this.ourRoot.put("currentAudioPreferences", this.currentAudioPath);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void suggestFlagActionPerformed(ActionEvent evt) {
        this.ourRoot.putBoolean("suggestFlagPreferences", this.suggestFlag.isSelected());
    }

    private void startupScreenFlagActionPerformed(ActionEvent evt) {
        this.startupScreenFlagPreferences = this.startupScreenFlag.isSelected();
        this.ourRoot.putBoolean("startupScreenPreferences", this.startupScreenFlag.isSelected());
    }

    private void showLatestFlagActionPerformed(ActionEvent evt) {
        this.ourRoot.putBoolean("showLatestFlag", this.showLatestFlag.isSelected());
    }

    private void doubleClickFlagActionPerformed(ActionEvent evt) {
        this.ourRoot.putBoolean("doubleClickFlag", this.doubleClickFlag.isSelected());
    }

    private void launchTmaListActionPerformed(ActionEvent evt) {
        this.launchTmaListFlagPreferences = this.launchTmaList.isSelected();
        this.ourRoot.putBoolean("launchTmaListFlagPreferences", this.launchTmaList.isSelected());
    }

    private void checkClosureFlagActionPerformed(ActionEvent evt) {
        this.ourRoot.putBoolean("checkClosureFlagPreferences", this.checkClosureFlag.isSelected());
        this.checkClosureFlagPreferences = this.checkClosureFlag.isSelected();
    }

    private void wpSelectActionPerformed(ActionEvent evt) {
        this.setWpPreferences();
    }

    private void selectDictionaryActionPerformed(ActionEvent evt) {
        JFileChooser _fileChooser = new JFileChooser(this.etmaMonitoringFolder.getText());
        _fileChooser.setDialogTitle("Please select the dictionary file for the Spellchecker");
        int path = _fileChooser.showOpenDialog(null);
        File aFile = _fileChooser.getSelectedFile();
        this.dictionaryPath.setText(aFile.getPath());
        dictionaryPathPreferences = this.dictionaryPath.getText();
        this.ourRoot.put("dictionaryPathPreferences", aFile.getPath());
    }

    private void spellCheckFlagActionPerformed(ActionEvent evt) {
        this.ourRoot.putBoolean("spellCheckFlagPreferences", this.spellCheckFlag.isSelected());
        if (!this.spellCheckFlag.isSelected()) {
            this.suggestFlag.setSelected(false);
            this.suggestFlag.setEnabled(false);
        } else {
            this.suggestFlag.setEnabled(true);
        }
    }

    private void toolTipFlagActionPerformed(ActionEvent evt) {
        this.toolTipFlagPreferences = this.toolTipFlag.isSelected();
        this.ourRoot.putBoolean("toolTipFlagPreferences", this.toolTipFlag.isSelected());
        ToolTipManager.sharedInstance().setEnabled(this.toolTipFlag.isSelected());
    }

    private void autoFillFlagActionPerformed(ActionEvent evt) {
        this.autoFillFlagPreferences = this.autoFillFlag.isSelected();
        this.ourRoot.putBoolean("autoFillFlagPreferences", this.autoFillFlag.isSelected());
    }

    private void saveAddressActionPerformed(ActionEvent evt) {
        this.ourRoot.put("etmaAddress", this.ouEtmaAddress.getText());
        this.ourRoot.put("trainingAddress", this.ouTrainingAddress.getText());
    }

    private void createMarkedMouseReleased(MouseEvent evt) {
        this.ourRoot.putBoolean("createMarked", this.createMarked.isSelected());
    }

    private void selectDownloadsFolderActionPerformed(ActionEvent evt) {
        boolean okFlag = false;
        while (!okFlag) {
            JFileChooser _fileChooser = new JFileChooser();
            _fileChooser.setFileSelectionMode(1);
            _fileChooser.setDialogTitle("Please select your usual Browser Downloads folder");
            int path = _fileChooser.showOpenDialog(null);
            File aFile = _fileChooser.getSelectedFile();
            String thisPath = aFile.getPath();
            if (!thisPath.contains(this.etmaMonitoringFolder.getText())) {
                okFlag = true;
                this.downloadsFolder.setText(thisPath);
                this.ourRoot.put("downloadsFolder", aFile.getPath());
                continue;
            }
            JOptionPane.showMessageDialog(null, "It's not a good idea to download directly into your 'etmamonitoring' folder!\nPlease choose another location:");
        }
    }

    private void setEtmasFolderActionPerformed(ActionEvent evt) {
        this.selectEtmamonitoringFolder();
    }

    private void closePreferencesActionPerformed(ActionEvent evt) {
        this.preferences.setVisible(false);
        this.ourRoot.put("etmamonitoringFolder", this.etmaMonitoringFolder.getText());
    }

    private void batchZipNewActionPerformed(ActionEvent evt) {
        ArrayList<File> fileDeleteList = new ArrayList<File>();
        ArrayList<File> directoryDeleteList = new ArrayList<File>();
        int[] rowsSelected = this.getSelectedFiles();
        if (rowsSelected.length == 0) {
            this.messageWindow.setVisible(false);
            JOptionPane.showMessageDialog(null, "Please select  files to be zipped by ticking the last column!");
        } else {
            boolean continueFlag = this.checkScriptClosure();
            if (continueFlag) {
                Object module = "";
                String region = "";
                String pres = "";
                String tmaNo = "";
                String subNo1 = "";
                Object zipFileName1 = "";
                String pid = "";
                String status = "";
                int numberZipped = 0;
                boolean notReadyFlag = false;
                boolean success1 = false;
                boolean success2 = false;
                String etmaMonitoringBase = this.etmaMonitoringFolder.getText();
                String fhiString = "";
                File tempBase = new File(etmaMonitoringBase + this.tempName);
                File returnsBase = new File(etmaMonitoringBase + this.returnsName);
                Object monitoringFolderPath = "";
                int studNo = 0;
                this.messageWindow.setSize(400, 10);
                this.messageWindow.setVisible(true);
                this.messageWindow.setTitle("Please wait.... zipping files....");
                this.messageWindow.setLocation(300, 300);
                try {
                    tempBase.mkdir();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                try {
                    returnsBase.mkdir();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                for (studNo = 0; studNo < rowsSelected.length; ++studNo) {
                    this.moveFiles();
                    region = (String)this.listOfTutors.getValueAt(rowsSelected[studNo], 3);
                    pres = (String)this.listOfTutors.getValueAt(rowsSelected[studNo], 5);
                    module = (String)this.listOfTutors.getValueAt(rowsSelected[studNo], 4);
                    tmaNo = (String)this.listOfTutors.getValueAt(rowsSelected[studNo], 6);
                    pid = (String)this.listOfTutors.getValueAt(rowsSelected[studNo], 0);
                    status = (String)this.listOfTutors.getValueAt(rowsSelected[studNo], 7);
                    module = (String)module + "-" + pres;
                    monitoringFolderPath = etmaMonitoringBase + "/" + (String)module + "/" + tmaNo + "/" + pid + "/" + region + "/";
                    String pathName = this.folderForZippingPathname + pid + "." + (String)module + "." + tmaNo;
                    this.loadPT3((String)monitoringFolderPath + "monitor.fhi");
                    this.moveFiles();
                    this.saveDetails();
                    fhiString = this.loadFhiString((String)monitoringFolderPath + "monitor.fhi", false);
                    boolean noCommentFlag = false;
                    if (fhiString.contains("<monitor_comments></monitor_comments")) {
                        noCommentFlag = true;
                    }
                    if (this.monitor_comments.getText().length() < 5) {
                        noCommentFlag = true;
                    }
                    if ((status.equals("Monitored") || status.equals("Zipped")) && !noCommentFlag && this.testGradingStatus()) {
                        ++numberZipped;
                        fhiString = this.changeStatus(fhiString, "tutor_status", "Zipped");
                        fhiString = this.changeStatus(fhiString, "zip_date", this.getDateAndTime());
                        fhiString = this.changeStatus(fhiString, "zip_filename", this.getZipfileName3(subNo1));
                        fhiString = fhiString.replaceAll("&", "&amp;");
                        File aFile = new File(pathName);
                        zipFileName1 = pid + "-" + (String)module + "-" + tmaNo + "-" + region;
                        success1 = !this.osName.contains("Windows") ? this.zipDirectory(aFile, this.etmaMonitoringFolder.getText() + this.tempName + (String)zipFileName1, 4) : this.zipDirectory(aFile, this.etmaMonitoringFolder.getText() + "\\temp\\" + (String)zipFileName1, 4);
                        String pathToBeDeleted = this.etmaMonitoringFolder.getText() + this.tempName + (String)zipFileName1;
                        File file = new File(pathToBeDeleted);
                        this.listOfTutors.setValueAt("Zipped", rowsSelected[studNo], 7);
                        this.saveFhiString((String)monitoringFolderPath + "monitor.fhi", fhiString);
                        directoryDeleteList.add(aFile);
                        fileDeleteList.add(file);
                    } else {
                        notReadyFlag = true;
                    }
                    System.out.println(notReadyFlag);
                }
                File thisFile = new File(this.etmaMonitoringFolder.getText() + this.tempName);
                String fileToBeReturned = this.etmaMonitoringFolder.getText() + this.returnsName + this.getZipfileName3(subNo1);
                String nameOfFileToBeReturned = this.getZipfileName3(subNo1);
                success2 = this.zipDirectory(thisFile, this.etmaMonitoringFolder.getText() + this.returnsName + this.getZipfileName3(subNo1), 3);
                this.messageWindow.setVisible(false);
                if (numberZipped > 0) {
                    if (success1 && success2) {
                        String errorMessage = notReadyFlag ? "WARNING: Some files were not zipped, since they aren't completed or they have no monitor comments!" : "All files have been successfully zipped!";
                        this.zip_date.setText(this.getDateAndTime());
                        EtmaMonitorJ.copyString(fileToBeReturned + ".zip");
                        EtmaMonitorJ.copyString(nameOfFileToBeReturned + ".zip");
                        this.returnMonitoring(errorMessage);
                    } else {
                        fhiString = this.loadFhiString((String)monitoringFolderPath + (String)module + "-" + tmaNo + "-" + subNo1 + "-" + region + ".fhi", false);
                        fhiString = this.changeStatus(fhiString, "submission_status", "");
                        fhiString = this.changeStatus(fhiString, "zip_date", "");
                        fhiString = this.changeStatus(fhiString, "zip_file", "");
                        this.saveFhiString((String)monitoringFolderPath + (String)module + "-" + tmaNo + "-" + subNo1 + "-" + region + ".fhi", fhiString);
                        this.listOfTutors.setValueAt("marked", rowsSelected[studNo], 5);
                        JOptionPane.showMessageDialog(null, "Error - files have NOT been zipped!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "There were no completed reports selected to be zipped!\nMake sure all gradingss are completed \nand that the monitor comment box is not empty.", "Alert", 2);
                }
                File[] listOfZippedFilesInTemp = thisFile.listFiles();
                try {
                    for (File deletingFile : listOfZippedFilesInTemp) {
                        deletingFile.delete();
                    }
                }
                catch (Exception exception) {
                    System.out.println(exception);
                }
                for (File dFile : directoryDeleteList) {
                    EtmaMonitorJ.deleteDir(dFile);
                }
                this.openList();
            }
        }
    }

    private void selectAllFilesToZipActionPerformed(ActionEvent evt) {
        this.selectAllToZip();
    }

    private void hideOpenTmaFolderActionPerformed(ActionEvent evt) {
    }

    private void versionNumberActionPerformed(ActionEvent evt) {
        JOptionPane.showMessageDialog(null, "This is version " + this.thisVersion, "Version Number", 1);
    }

    private void jMenuItemUndoActionPerformed(ActionEvent evt) {
        this.UndoActionPerformed(evt);
    }

    private void jMenuItemRedoActionPerformed(ActionEvent evt) {
        this.RedoActionPerformed(evt);
    }

    private void jMenuItemCopyActionPerformed(ActionEvent evt) {
        this.copyTextActionPerformed(evt);
    }

    private void jMenuItemPasteActionPerformed(ActionEvent evt) {
        this.pasteTextActionPerformed(evt);
    }

    private void jMenuItemSelectAllActionPerformed(ActionEvent evt) {
        this.selectAllTextActionPerformed(evt);
    }

    private void jMenuItemCutActionPerformed(ActionEvent evt) {
        this.cutTextActionPerformed(evt);
    }

    private void showPopupMenu(MouseEvent e) {
        this.jPopupMenu1.show(this.monitor_comments, e.getX(), e.getY());
    }

    private void monitor_commentsMousePressed(MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            this.showPopupMenu(evt);
        }
    }

    private void checkupdatesActionPerformed(ActionEvent evt) {
        this.loadVersion("http://s376541606.websitehome.co.uk/etmahandlerpage.html");
    }

    public void createLog() {
        try {
            FileHandler handler = new FileHandler("my.log");
            Logger logger = Logger.getLogger("com.mycompany");
            logger.addHandler(handler);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    public void createFeedback() {
        File fhiFile = new File(this.fhiFileName.getText());
        String tmaFolder = fhiFile.getParent();
        String fbString = tmaFolder + "/tutorcomments.rtf";
        String rtfString = "testing" + this.lf;
        rtfString = "{\\rtf1\\ansi\\ansicpg1252\\cocoartf949\\cocoasubrtf270" + this.lf + "{\\fonttbl}" + this.lf + "{\\colortbl;\\red255\\green255\\blue255;}" + this.lf + "\\paperw11900\\paperh16840\\margl1440\\margr1440\\vieww12000\\viewh14000\\viewkind0" + this.lf + "}";
        File testFile = new File(fbString);
        if (testFile.exists()) {
            Component frame = null;
            int n = 0;
            Object[] options = new Object[]{"Create new", "Open current", "Cancel"};
            n = JOptionPane.showOptionDialog(frame, "File exists - create new one and LOSE current comments?", "", 1, 3, null, options, options[1]);
            if (n == 0) {
                this.putFile1(fbString, rtfString);
                this.tmaOpener(fbString);
            }
            if (n == 1) {
                this.tmaOpener(fbString);
            }
        } else {
            this.putFile1(fbString, rtfString);
            this.tmaOpener(fbString);
        }
    }

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                new EtmaMonitorJ().setVisible(true);
            }
        });
    }

    public void undoCode() {
        this.monitor_comments.getDocument().addUndoableEditListener(new UndoableEditListener(){

            @Override
            public void undoableEditHappened(UndoableEditEvent evt) {
                EtmaMonitorJ.this.undo.addEdit(evt.getEdit());
            }
        });
        this.monitor_comments.getActionMap().put("Undo", new AbstractAction("Undo"){

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (EtmaMonitorJ.this.undo.canUndo()) {
                        EtmaMonitorJ.this.undo.undo();
                    }
                }
                catch (CannotUndoException cannotUndoException) {
                    // empty catch block
                }
            }
        });
        this.monitor_comments.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        this.monitor_comments.getActionMap().put("Redo", new AbstractAction("Redo"){

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (EtmaMonitorJ.this.undo.canRedo()) {
                        EtmaMonitorJ.this.undo.redo();
                    }
                }
                catch (CannotRedoException cannotRedoException) {
                    // empty catch block
                }
            }
        });
        this.monitor_comments.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
    }

    static {
        alertFlag = false;
        wordToAdd = "";
    }

    public class etmaPrinter
    implements Printable,
    ActionListener {
        public JFrame printFrame = null;
        public double aScaling = 1.0;

        public etmaPrinter(JFrame aFrame, double scale) {
            this.printFrame = aFrame;
            this.aScaling = scale;
        }

        @Override
        public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
            if (page > 0) {
                return 1;
            }
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pf.getImageableX(), pf.getImageableY());
            g2d.scale(this.aScaling, this.aScaling);
            this.printFrame.printAll(g);
            return 0;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat pf = job.defaultPage();
            pf.setOrientation(2);
            job.setPrintable(this, pf);
            boolean ok = job.printDialog();
            if (ok) {
                try {
                    job.print();
                }
                catch (PrinterException printerException) {
                    // empty catch block
                }
            }
        }
    }

    public class DocumentFilter
    extends FileFilter {
        @Override
        public boolean accept(File f) {
            String extension = "";
            if (f.isDirectory()) {
                return true;
            }
            extension = f.getName();
            try {
                extension = extension.substring(extension.lastIndexOf("."));
            }
            catch (Exception exception) {
                // empty catch block
            }
            boolean acceptFlag = false;
            if (extension != null) {
                for (String accExt : EtmaMonitorJ.this.acceptableFilesSet) {
                    if (!extension.equals(accExt)) continue;
                    acceptFlag = true;
                }
            }
            return acceptFlag;
        }

        @Override
        public String getDescription() {
            return "Only recommended formats";
        }
    }

    public class ZipFilter
    extends FileFilter {
        @Override
        public boolean accept(File f) {
            String extension = "";
            if (f.isDirectory()) {
                return true;
            }
            extension = f.getName();
            try {
                extension = extension.substring(extension.lastIndexOf("."));
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (extension != null) {
                return extension.equals(".zip");
            }
            return false;
        }

        @Override
        public String getDescription() {
            return "Only Zipped Files";
        }
    }

    public class GACellEditor
    extends DefaultCellEditor {
        private Component editorComponent1;
        private JTextField tf;

        public GACellEditor(JTextField t) {
            super(t);
            this.tf = null;
            this.editorComponent1 = t;
            this.tf = (JTextField)this.editorComponent1;
            this.tf.setFont(new Font("Lucida Grande", 0, 9));
            this.tf.setCaretPosition(this.tf.getText().length());
            this.tf.addKeyListener(new KeyListener(){

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    String currentText = GACellEditor.this.tf.getText();
                    String currentPart = (String)EtmaMonitorJ.this.monitoringRatings.getValueAt(EtmaMonitorJ.this.xCoord, 1);
                    if (currentText.contains("+")) {
                        EtmaMonitorJ.this.additionField.setText("Current entry for part " + currentPart + ": " + currentText);
                    } else {
                        EtmaMonitorJ.this.additionField.setText("");
                    }
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    System.out.println("3test3");
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            ((JTextField)this.editorComponent1).setText(value.toString());
            table.repaint();
            this.tf.selectAll();
            return this.editorComponent1;
        }
    }

    public class ColumnSorter
    implements Comparator {
        int colIndex;
        boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        public int compare(Object a, Object b) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
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
            if (this.ascending) {
                return o1.toString().compareTo(o2.toString());
            }
            return o2.toString().compareTo(o1.toString());
        }
    }

    class MyHighlightPainter
    extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }

    class ScheduleRunner2
    extends TimerTask {
        ScheduleRunner2() {
        }

        @Override
        public void run() {
            try {
                String dfPath = EtmaMonitorJ.this.downloadsFolder.getText();
                File dfFile = new File(dfPath);
                File[] thisFile = dfFile.listFiles();
                EtmaMonitorJ.this.pathToDownloadedFile = "";
                boolean foundIt = false;
                EtmaMonitorJ.this.foundItZip = false;
                for (int count1 = 0; count1 < thisFile.length; ++count1) {
                    String lastFour = "*&%\u00a3";
                    int nameLength = thisFile[count1].getName().length();
                    if (nameLength > 4) {
                        lastFour = thisFile[count1].getName().substring(nameLength - 4, nameLength);
                    }
                    if (!EtmaMonitorJ.this.acceptableEnds.contains(lastFour)) continue;
                    boolean folderLooksOk = true;
                    File[] fileContentsList = thisFile[count1].listFiles();
                    for (int i = 0; i < fileContentsList.length; ++i) {
                        String fName = fileContentsList[i].getName();
                        if (fName.length() == 2 || fName.equals(".DS_Store")) continue;
                        folderLooksOk = false;
                    }
                    if (!folderLooksOk) continue;
                    EtmaMonitorJ.this.pathToDownloadedFile = thisFile[count1].getPath();
                    foundIt = true;
                }
                if (foundIt) {
                    EtmaMonitorJ.this.courseDirectoryList.clear();
                    EtmaMonitorJ.this.courseDirectoryList.add(EtmaMonitorJ.this.pathToDownloadedFile);
                    EtmaMonitorJ.this.collectMonitoring();
                    EtmaMonitorJ.this.courseList.setSelectedItem(EtmaMonitorJ.this.courseName);
                    EtmaMonitorJ.this.foundItZip = false;
                } else if (!EtmaMonitorJ.this.foundItZip) {
                    for (int count = 0; count < thisFile.length; ++count) {
                        if (!thisFile[count].getName().contains(EtmaMonitorJ.this.zipFileName)) continue;
                        EtmaMonitorJ.this.fileToUnzip = thisFile[count].getPath();
                        if (!EtmaMonitorJ.this.fileToUnzip.contains(".zip") || EtmaMonitorJ.this.fileToUnzip.contains("Imported")) continue;
                        EtmaMonitorJ.this.foundItZip = true;
                        boolean zipFlag = EtmaMonitorJ.this.unZipAlt();
                        EtmaMonitorJ.this.foundItZip = false;
                    }
                }
            }
            catch (Exception anException) {
                System.out.println(anException);
            }
        }
    }

    class ScheduleRunner
    extends TimerTask {
        ScheduleRunner() {
        }

        @Override
        public void run() {
            try {
                Robot robot = new Robot();
                robot.keyPress(10);
                robot.keyRelease(10);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }
}
