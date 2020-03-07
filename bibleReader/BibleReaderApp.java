package bibleReader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import bibleReader.model.ArrayListBible;
import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.BookOfBible;
import bibleReader.model.Concordance;
import bibleReader.model.ReferenceList;
import bibleReader.model.ResultType;
import bibleReader.model.VerseList;

/**
 * The main class for the Bible Reader Application.
 * 
 * @author cusack
 * @author Josiah Brett and Gabriel DeJongh, modified 2018
 */
public class BibleReaderApp extends JFrame {
	public static final int width = 800;
	public static final int height = 600;

	public static void main(String[] args) {
		new BibleReaderApp();
	}

	// Fields
	private BibleReaderModel model;
	private ResultView resultView;

	private JPanel inputPanel;
	public JTextField inputText;
	public JButton searchButton;
	public JButton passageButton;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenuItem exitItem;
	private JMenuItem aboutItem;
	private JMenuItem openItem;
	private JFileChooser fileChooser;

	/**
	 * Default constructor. We may want to replace this with a different one.
	 */
	public BibleReaderApp() {
		setUpLookAndFeel();
		model = new BibleReaderModel();
		File kjvFile = new File("kjv.atv");
		File esvFile = new File("esv.atv");
		File asvFile = new File("asv.xmv");

		VerseList kjvVerses = BibleIO.readBible(kjvFile);
		VerseList esvVerses = BibleIO.readBible(esvFile);
		VerseList asvVerses = BibleIO.readBible(asvFile);

		Bible kjv = BibleFactory.createBible(kjvVerses);
		Bible esv = BibleFactory.createBible(esvVerses);
		Bible asv = BibleFactory.createBible(asvVerses);

		model.addBible(kjv);
		model.addBible(esv);
		model.addBible(asv);
		
		for(String version : model.getVersions()){
			System.out.println(version+"\n");
		}

		resultView = new ResultView(model);

		setupGUI();
		pack();
		setSize(width, height);

		// So the application exits when you click the "x".
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Set up the main GUI. Make sure you don't forget to put resultView
	 * somewhere!
	 */
	private void setupGUI() {

		this.setLayout(new BorderLayout());
		Container contents = this.getContentPane();

		inputPanel = new JPanel();
		inputText = new JTextField(10);
		searchButton = new JButton("Search");
		passageButton = new JButton("Search For Passage");
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		helpMenu = new JMenu("Help");
		openItem = new JMenuItem("Open");
		exitItem = new JMenuItem("Exit");
		aboutItem = new JMenuItem("About");

		inputText.setName("InputTextField");
		searchButton.setName("SearchButton");
		passageButton.setName("PassageButton");

		contents.add(resultView, BorderLayout.CENTER);
		contents.add(inputPanel, BorderLayout.NORTH);
		this.setJMenuBar(menuBar);

		helpMenu.add(aboutItem);
		fileMenu.add(openItem);
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		inputPanel.add(inputText);
		inputPanel.add(searchButton);
		inputPanel.add(passageButton);

		inputText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = inputText.getText();
				String[] parts = text.trim().split(" ");
				if (text.contains(":") || text.contains("-") || BookOfBible.getBookOfBible(text.trim()) != null
						|| Character.isDigit(text.trim().charAt(0)) || BookOfBible.getBookOfBible(parts[0]) != null) {
					searchForPassage(text);
				} else {
					searchForText(text);
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchForText(inputText.getText());
			}
		});

		passageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchForPassage(inputText.getText());
			}
		});

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"Written by Gabe DeJongh for CSCI 235. \nUse File-->Open to load a different translation.");
			}
		});

		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readFile();
			}
		});

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		// The stage numbers below may change, so make sure to pay attention to
		// what the assignment says.
		// TODO Add passage lookup: Stage ?
		// TODO Add 2nd version on display: Stage ?
		// TODO Limit the displayed search results to 20 at a time: Stage ?
		// TODO Add 3rd versions on display: Stage ?
		// TODO Format results better: Stage ?
		// TODO Display cross references for third version: Stage ?
		// TODO Save/load search results: Stage ?
		// exitItem.addKeyListener(KeyEvent.VK_Q);
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Method that is used to enhace the look of the application
	 * 
	 * @author cusack
	 */
	private void setUpLookAndFeel() {
		UIManager.put("control", new Color(140,90,45));
		UIManager.put("nimbusLightBackground", new Color(220,220,200));
		UIManager.put("nimbusFocus", new Color(200,200,200));
		try{
			for(LookAndFeelInfo info: UIManager.getInstalledLookAndFeels()){
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e){
			// Use the default look and feel.
		}
	}

	/**
	 * searches for all verses in the specified version of the Bible and
	 * displays the results and result summary
	 * 
	 * @param input
	 *            What was searched in the input text field
	 */
	public void searchForText(String input) {
		ReferenceList references = model.getReferencesContainingAllWords(input);
		resultView.setParameters(input, references, ResultType.SEARCH);
		resultView.setResults(input, references);
	}

	public void searchForPassage(String input) {
		ReferenceList references = model.getReferencesForPassage(input);
		resultView.setParameters(input, references, ResultType.PASSAGE);
		resultView.setResults(input, references);
	}

	/**
	 * Reads a bible if the format is supported by ArrayList bible
	 */
	private void readFile() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		int returnVal = fileChooser.showDialog(this, "Read File");

		// We check whether or not they clicked the "Open" button
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// We get a reference to the file that the user selected.
			File file = fileChooser.getSelectedFile();
			// Make sure it actually exists.
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this, "That file does not exist!", "File not found",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					VerseList verses = BibleIO.readBible(file);
					Bible bible = new ArrayListBible(verses);
					model.addBible(bible);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "Warning! Error parsing file.", "Error",
							JOptionPane.WARNING_MESSAGE);
					e.printStackTrace();
				}
			}
		}
	}
}
