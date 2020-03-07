package bibleReader;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import bibleReader.model.BibleReaderModel;
import bibleReader.model.NavigableResults;
import bibleReader.model.Reference;
import bibleReader.model.ReferenceList;
import bibleReader.model.ResultType;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * The display panel for the Bible Reader.
 * 
 * @author cusack
 * @author Josiah Brett and Gabriel DeJongh, modified 2018
 * @author modified again by Gabriel DeJongh and Andrew Levering 03/2018
 */
public class ResultView extends JPanel {

	// Navigable results so we can limit the results shown at a time
	private NavigableResults results;

	// for displaying the search result summary
	JTextPane summaryResults;
	JTextPane displayResults;

	// for displaying the search results
	JScrollPane scrollPane;
	public JEditorPane output;
	JPanel navigationPanel;
	// JPanel displayPanel;

	// the model that ResultView takes
	BibleReaderModel model;

	// Previous and next buttons
	public JButton nextButton;
	public JButton previousButton;

	// Fields to help store some relevant information
	String input;
	ResultType type;
	ReferenceList references;

	/**
	 * Construct a new ResultView and set its model to myModel. It needs to
	 * model to look things up.
	 * 
	 * @param myModel
	 *            The model this view will access to get information.
	 */
	public ResultView(BibleReaderModel myModel) {
		super();
		model = myModel;
		this.setLayout(new BorderLayout());
		// editorPane.setLayout(new BorderLayout());

		previousButton = new JButton("Previous");
		nextButton = new JButton("Next");
		// create a panel to add the previous and next buttons too
		navigationPanel = new JPanel();
		navigationPanel.setVisible(false);
		// displayPanel = new JPanel();

		nextButton.setName("NextButton");
		previousButton.setName("PreviousButton");

		summaryResults = new JTextPane();
		summaryResults.setEditable(false);
		displayResults = new JTextPane();
		displayResults.setEditable(false);
		output = new JEditorPane("text/html", "");
		output.setEditable(false);
		output.setSize(1000, 600);
		output.setName("OutputEditorPane");
		// add the buttons to the navigation panel
		navigationPanel.add(displayResults, BorderLayout.WEST);
		navigationPanel.add(previousButton);
		navigationPanel.add(nextButton);

		scrollPane = new JScrollPane(output);

		this.add(summaryResults, BorderLayout.NORTH);
		// this.add(displayResults, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(navigationPanel, BorderLayout.SOUTH);

		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (results.hasNextResults()) {
					setResults(input, results.nextResults());
				}
			}
		});
		
		previousButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (results.hasPreviousResults()) {
					setResults(input, results.previousResults());
				}
			}
		});
	}

	/**
	 * displays the specified verses in the search results output field and
	 * displays the summary of the the search (i.e. how many verses contained
	 * the input
	 * 
	 * @param verses
	 *            VerseList of verses that are to be displayed
	 * @param searchInput
	 *            the String that was inputed for the search
	 */
	public void setResults(String searchInput, ReferenceList references) {
		// Decide what should be visible and enabled
		try {
			if (this.references.size() < 20) {
				setNavPanelVisibleFalse();
			} else {
				setNavPanelVisibleTrue();
			}
			if (!results.hasPreviousResults()) {
				previousButton.setEnabled(false);
			} else if (!results.hasNextResults()) {
				nextButton.setEnabled(false);
			}

			// begin to display the GUI
			summaryResults
					.setText("Searched for: " + "'" + input + "'. " + this.references.size() + " results were found.");
			displayResults.setText("Displaying page " + results.getPageNumber() + " of " + results.getNumberPages());

			// initialize string buffer
			StringBuffer sb = new StringBuffer("");

			// if its a passage search, display the passage currently on the
			// resultView
			if (results.getType() == (ResultType.PASSAGE)) {
				Reference ref1 = results.currentResults().get(0);
				Reference ref2 = results.currentResults().get(results.currentResults().size() - 1);
				int chap = ref2.getChapter();
				int verse = ref2.getVerse();
				if (ref1.getChapter() == ref2.getChapter()) {
					sb.append("<center><b>" + ref1.toString() + "-" + verse + "</b></center>");
				} else {
					sb.append("<center><b>" + ref1.toString() + "-" + chap + ":" + verse + "</b></center>");
				}
			}
			// Set up the table
			sb.append("<table>");
			// Different types of searches require different action to be taken,
			// check what kind of seach it is
			if (results.getType() == ResultType.SEARCH) {
				sb.append("<tr><td>Verse</td>");
				for (String version : model.getVersions()) {
					sb.append("<td>" + version + "</td>");
				}
				sb.append("</tr>");
				for (int i = 0; i < 20 && i < references.size(); i++) {
					Reference ref = (Reference) references.get(i);
					// Enters the verse's reference information
					sb.append("<tr><td valign=\"top\">");
					sb.append(ref.toString());
					sb.append("</td>");

					// Enters each version's text for the verse
					for (String version : model.getVersions()) {
						sb.append("<td valign=\"top\">");
						sb.append(model.getText(version, ref));
						// System.out.print(version);
						sb.append("</td>");
					}
					// Close the verse before moving onto the next one
					sb.append("</tr>");
				}
			} else {
				// It was a passage search
				// display the different versions that have been loaded
				sb.append("<tr>");
				for (String version : model.getVersions()) {
					sb.append("<td>" + version + "</td>");
				}
				sb.append("</tr>");
				// Enters all the text for each version one version at a time.
				for (String version : model.getVersions()) {
					sb.append("<td valign=\"top\" style=\"width:100%\">");
					for (int i = 0; i < 20 && i < references.size(); i++) {
						Reference ref = (Reference) references.get(i);
						// decide what superscript to use depending on whether
						// of
						// not it's verse 1 of a chapter
						if (ref.getVerse() != 1 && model.getBible(version).isValid(ref)) {
							sb.append("<sup>" + ref.getVerse() + "</sup>");
						} else {
							if (i != 0) {
								sb.append("<br><br><br>");
							}
							sb.append("<sup><b>" + ref.getChapter() + "</b></sup>");
						}
						sb.append(model.getText(version, ref));
					}
					// We're done with this version so move on to the next if
					// there
					// is one
					sb.append("</td>");
				}
			}
			// Finish the StringBuffer.
			sb.append("</table>");
			String text = sb.toString();
			// Bolden keywords if search was done by phrase
			if (results.getType() == (ResultType.SEARCH)) {
				text = text.replaceAll("(?i)" + input, "<b>$0</b>");
			}
			output.setText(text);
			output.setCaretPosition(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Just a simple helper method that makes the buttons visible as well as the
	 * navigationalPanel.
	 */
	public void setNavPanelVisibleTrue() {
		navigationPanel.setVisible(true);
		nextButton.setEnabled(true);
		previousButton.setEnabled(true);
	}

	/**
	 * Same as the method above but opposite.
	 */
	public void setNavPanelVisibleFalse() {
		navigationPanel.setVisible(false);
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);
	}

	/**
	 * Sets the parameters when new searches are done.
	 * 
	 * @param searchInput
	 *            the keyphrase or passage being searched
	 * @param references
	 *            the list of references returned from the search
	 * @param type
	 *            the type of search that was done.
	 */
	public void setParameters(String searchInput, ReferenceList references, ResultType type) {
		input = searchInput;
		this.type = type;
		this.references = references;
		results = new NavigableResults(references, searchInput, type);
	}

}
