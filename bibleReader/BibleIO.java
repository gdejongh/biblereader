package bibleReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;

import bibleReader.model.Bible;
import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * A utility class that has useful methods to read/write Bibles and Verses.
 * 
 * @author cusack
 */
public class BibleIO {

	/**
	 * Read in a file and create a Bible object from it and return it.
	 * 
	 * @param bibleFile
	 * @return
	 */
	// This method is complete, but it won't work until the methods it uses are
	// implemented.
	public static VerseList readBible(File bibleFile) { // Get the extension of
														// the file
		String name = bibleFile.getName();
		String extension = name.substring(name.lastIndexOf('.') + 1, name.length());

		// Call the read method based on the file type.
		if ("atv".equals(extension.toLowerCase())) {
			return readATV(bibleFile);
		} else if ("xmv".equals(extension.toLowerCase())) {
			return readXMV(bibleFile);
		} else {
			return null;
		}
	}

	/**
	 * Read in a Bible that is saved in the "ATV" format. The format is
	 * described below.
	 * 
	 * @param bibleFile
	 *            The file containing a Bible with .atv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if
	 *         there was an error reading the file.
	 */
	private static VerseList readATV(File bibleFile) {
		try {
			FileReader inStream = new FileReader(bibleFile);
			BufferedReader inData = new BufferedReader(inStream);
			String line1 = inData.readLine();
			String[] firstLine = line1.split(": ");
			VerseList list;
			if (line1.contains(": ") && !firstLine[1].isEmpty()) {
				list = new VerseList(firstLine[0], firstLine[1]);
			} else if (!line1.contains(": ") && !firstLine[0].isEmpty()) {
				list = new VerseList(line1, "");
			} else {
				list = new VerseList("unknown", "");
			}
			String refString;
			while ((refString = inData.readLine()) != null) {
				String[] contents = refString.split("@");
				if (contents.length == 3) {
					String[] refNumbers = contents[1].split(":");
					int chapter = Integer.parseInt(refNumbers[0]);
					int verse = Integer.parseInt(refNumbers[1]);
					if (BookOfBible.getBookOfBible(contents[0]) != null & refNumbers.length == 2) {
						Reference ref = new Reference(BookOfBible.getBookOfBible(contents[0]), chapter, verse);
						Verse bibleVerse = new Verse(ref, contents[2]);
						list.add(bibleVerse);
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
			inData.close();
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Read in the Bible that is stored in the XMV format. The XMV format is as
	 * follows: First line is "<Version " followed by the version the a colon
	 * and a description Lines where a new book start are "<Book "book name here
	 * ", "short description of book">" Lines with a new chapter are simply
	 * "<Chapter "chapter #>" Lines with a new verse are "<Verse "verse#>
	 * "verse text">"
	 * 
	 * @param bibleFile
	 *            The file containing a Bible with .xmv extension.
	 * @return A Bible object constructed from the file bibleFile, or null if
	 *         there was an error reading the file.
	 */
	private static VerseList readXMV(File bibleFile) {
		if (bibleFile.exists()) {
			try {
				FileReader inStream = new FileReader(bibleFile);
				BufferedReader inData = new BufferedReader(inStream);
				String line1 = inData.readLine().trim();
				if (!line1.isEmpty()) {
					String[] firstLine = line1.split(": ");
					VerseList list = new VerseList(firstLine[0].split(" ")[1], firstLine[1]);
					String theLine;
					BookOfBible book = null;
					int chapter = 0;
					while ((theLine = inData.readLine()) != null) {
						if (theLine.startsWith("<Book")) {
							// <Book Genesis,
							String[] bookArray = theLine.split(",", 2);
							book = BookOfBible.getBookOfBible(bookArray[0].split(" ", 2)[1]);
						} else if (theLine.startsWith("<Chapter")) {
							char[] chap = theLine.trim().toCharArray();
							StringBuffer chapString = new StringBuffer();
							for (Character c : chap) {
								if (Character.isDigit(c)) {
									chapString.append(c);
								}
							}
							chapter = Integer.parseInt(chapString.toString().trim());
						} else if (theLine.startsWith("<Verse")) {
							String[] parts = theLine.split(">", 2);
							char[] verseArray = parts[0].trim().toCharArray();
							StringBuffer verseString = new StringBuffer();
							for (Character c : verseArray) {
								if (Character.isDigit(c)) {
									verseString.append(c);
								}
							}
							int verse = Integer.parseInt(verseString.toString().trim());
							list.add(new Verse(book, chapter, verse, parts[1].trim()));
						}
					}
					inData.close();
					return list;
				} else {
					inData.close();
					return new VerseList("unknown", "");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Write out the Bible in the ATV format.
	 * 
	 * @param file
	 *            The file that the Bible should be written to.
	 * @param bible
	 *            The Bible that will be written to the file.
	 */
	public static void writeBibleATV(File file, Bible bible) {
		writeVersesATV(file, (bible.getVersion() + ": " + bible.getTitle()), bible.getAllVerses());
	}

	/**
	 * Write out the given verses in the ATV format, using the description as
	 * the first line of the file.
	 * 
	 * @param file
	 *            The file that the Bible should be written to.
	 * @param description
	 *            The contents that will be placed on the first line of the
	 *            file, formatted appropriately.
	 * @param verses
	 *            The verses that will be written to the file.
	 */
	public static void writeVersesATV(File file, String description, VerseList verses) {
		// if (file.exists()) {
		try {
			FileWriter fw = new FileWriter(file);
				fw.write(description + "\n");
			for (Verse verse : verses) {
				Reference ref = verse.getReference();
				fw.write(ref.getBook() + "@" + ref.getChapter() + ":" + ref.getVerse() + "@"
				+ verse.getText() + "\n");
				// pw.println();
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
	}

	/**
	 * Write the string out to the given file. It is presumed that the string is
	 * an HTML rendering of some verses, but really it can be anything.
	 * 
	 * @param file
	 * @param text
	 */
	public static void writeText(File file, String text) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(text);
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
