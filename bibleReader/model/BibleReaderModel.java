package bibleReader.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The model of the Bible Reader. It stores the Bibles and has methods for
 * searching for verses based on words or references.
 * 
 * @author cusack
 * @author Josiah Brett and Gabriel DeJongh, modified 2018
 * @author Modified again Andrew Levering and Gabe DeJongh 03/2018
 */
public class BibleReaderModel implements MultiBibleModel {

	private ArrayList<Bible> bibles;

	private ArrayList<Concordance> concordances;
	public static final String number = "\\s*(\\d+)\\s*";
	public static Pattern bookPattern = Pattern.compile("\\s*((?:1|2|3|I|II|III)\\s*\\w+|(?:\\s*[a-zA-Z]+)+)\\s*(.*)");
	// Possible patterns that are valid.
	// This one matches things like "3:4-7:3"
	public static Pattern cvcvPattern = Pattern.compile(number + ":" + number + "-" + number + ":" + number);
	// This one matches things like "3-5"
	public static Pattern ccPattern = Pattern.compile(number + "-" + number);
	// This one matches things like "3:3-5"
	public static Pattern cvvPattern = Pattern.compile(number + ":" + number + "-" + number);
	// This one matches things like "3"
	public static Pattern cPattern = Pattern.compile(number);
	// This one matches things like 3:4
	public static Pattern cvPattern = Pattern.compile(number + ":" + number);
	// This one matches things like 3-4:5
	public static Pattern ccvPattern = Pattern.compile(number + "-" + number + ":" + number);

	/**
	 * Default constructor. You probably need to instantiate objects and do
	 * other assorted things to set up the model.
	 */
	public BibleReaderModel() {
		bibles = new ArrayList<Bible>();
		concordances = new ArrayList<Concordance>();
	}

	/**
	 * @return an array containing the abbreviations for all of the Bibles
	 *         currently stored in the model in alphabetical order.
	 */
	@Override
	public String[] getVersions() {
		String[] versionList = new String[bibles.size()];
		for (int i = 0; i < bibles.size(); i++) {
			versionList[i] = bibles.get(i).getVersion();
		}
		Arrays.sort(versionList);
		return versionList;
	}

	/**
	 * @return the number of versions currently stored in the model.
	 */
	@Override
	public int getNumberOfVersions() {
		return bibles.size();
	}

	/**
	 * Add a Bible to the model.
	 * 
	 * @param bible
	 *            The bible you want to add to the model.
	 */
	@Override
	public void addBible(Bible bible) {
		bibles.add(bible);
		concordances.add(BibleFactory.createConcordance(bible));
	}

	/**
	 * @param version
	 *            the abbreviation for the version of the Bible that you want.
	 * @return the Bible that has the abbreviation "version", or null if it
	 *         isn't in the model.
	 */
	@Override
	public Bible getBible(String version) {
		for (Bible bible : bibles) {
			if (bible.getVersion().equalsIgnoreCase(version)) {
				return bible;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all references <i>r</i> such that <i>words</i> is
	 * contained in <i>r</i> for at least one version of the Bible. In other
	 * words, it finds the references for all verses in each version that
	 * contain <i>words</i> and combines the results together so that a single
	 * list if references is returned. The references are in the order they
	 * occur in the Bible and references are only listed once no matter how many
	 * versions had a match for that reference.
	 * 
	 * @param words
	 *            The words to search for.
	 * @return a ReferenceList containing the results, or an empty list.
	 */
	@Override
	public ReferenceList getReferencesContaining(String words) {
		TreeSet<Reference> set = new TreeSet<Reference>();
		if (!words.isEmpty()) {
			for (Bible bible : bibles) {
				set.addAll(bible.getReferencesContaining(words));
			}
		}
		return new ReferenceList(set);
	}

	/**
	 * @param version
	 *            The version of the Bible to look in.
	 * @param references
	 *            The set of References we want the full verses for.
	 * @return The verses from the given Bible for the given list of references,
	 *         or null if the Bible isn't in the model. Since this is a delegate
	 *         method, see the getVerses method from the Bible interface for
	 *         details of how the list is constructed.
	 */
	@Override
	public VerseList getVerses(String version, ReferenceList references) {
		return getBible(version).getVerses(references);
	}
	// ---------------------------------------------------------------------

	@Override
	public String getText(String version, Reference reference) {
		// TODO Implement me: Stage 7
		try {
			String text = getBible(version).getVerseText(reference);
			if (text != null) {
				return text;
			}
			return "";
		} catch (NullPointerException e) {
			return "";
		}
	}

	@Override
	public ReferenceList getReferencesForPassage(String reference) {
		try {
			String theRest = null;
			String book = null;
			int chapter1, chapter2, verse1, verse2;

			// First, split the input into the book and the rest, if possible.
			Matcher m = bookPattern.matcher(reference);

			// Now see if it matches.
			if (m.matches()) {
				// It matches. Good.
				book = m.group(1);
				theRest = m.group(2);
				// Now we need to parse theRest to see what format it is.
				// Notice that I have omitted some of the cases.
				// You should think about whether or not the order the
				// possibilities occurs matters if you use this and add more
				// cases.
				if (theRest.length() == 0) {
					// It looks like they want a whole book.
					return getBookReferences(BookOfBible.getBookOfBible(book));
				} else if ((m = cvcvPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = Integer.parseInt(m.group(2));
					chapter2 = Integer.parseInt(m.group(3));
					verse2 = Integer.parseInt(m.group(4));
					// They want something of the form book
					// chapter1:verse1-chapter2:verse2
					return getPassageReferences(BookOfBible.getBookOfBible(book), chapter1, verse1, chapter2, verse2);
				} else if ((m = ccPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					chapter2 = Integer.parseInt(m.group(2));
					// They want something of the form book chapter1-chapter2
					return getChapterReferences(BookOfBible.getBookOfBible(book), chapter1, chapter2);
				} else if ((m = cvvPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = Integer.parseInt(m.group(2));
					verse2 = Integer.parseInt(m.group(3));
					// They want something of the form book
					// chapter:verse1-verse2
					return getPassageReferences(BookOfBible.getBookOfBible(book), chapter1, verse1, verse2);
				} else if ((m = cPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					// They want something of the form book chapter
					return getChapterReferences(BookOfBible.getBookOfBible(book), chapter1);
				} else if ((m = cvPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					verse1 = Integer.parseInt(m.group(2));
					// They want something of the form book chapter:verse
					return getVerseReferences(BookOfBible.getBookOfBible(book), chapter1, verse1);
				} else if ((m = ccvPattern.matcher(theRest)).matches()) {
					chapter1 = Integer.parseInt(m.group(1));
					chapter2 = Integer.parseInt(m.group(2));
					verse1 = Integer.parseInt(m.group(3));
					// They want something of the form book
					// chapter-chapter:verse
					return getPassageReferences(BookOfBible.getBookOfBible(book), chapter1, 1, chapter2, verse1);
				} else {
					// They didn't format their search correctly.
					return new ReferenceList();
				}
			} else {
				// It doesn't match the overall format of "BOOK Stuff".
			}
			return null;
		} catch (Exception e) {
			return new ReferenceList();
		}
	}

	// -----------------------------------------------------------------------------
	// The next set of methods are for use by the getReferencesForPassage method
	// above.
	// After it parses the input string it will call one of these.
	//
	// These methods should be somewhat easy to implement. They are kind of
	// delegate
	// methods in that they call a method on the Bible class to do most of the
	// work.
	// However, they need to do so for every version of the Bible stored in the
	// model.
	// and combine the results.
	//
	// Once you implement one of these, the rest of them should be fairly
	// straightforward.
	// Think before you code, get one to work, and then implement the rest based
	// on
	// that one.
	// -----------------------------------------------------------------------------
	@Override
	public ReferenceList getVerseReferences(BookOfBible book, int chapter, int verse) {
		try {
			TreeSet<Reference> set = new TreeSet<Reference>();
			Reference ref = new Reference(book, chapter, verse);
			for (Bible bible : bibles) {
				set.add(bible.getVerse(ref).getReference());
			}
			return new ReferenceList(set);
		} catch (Exception e) {
			return new ReferenceList();
		}
	}

	@Override
	public ReferenceList getPassageReferences(Reference startVerse, Reference endVerse) {
		// TODO Implement me: Stage 7
		TreeSet<Reference> set = new TreeSet<Reference>();
		for (Bible bible : bibles) {
			set.addAll(bible.getReferencesInclusive(startVerse, endVerse));
		}
		return new ReferenceList(set);
	}

	@Override
	public ReferenceList getBookReferences(BookOfBible book) {
		TreeSet<Reference> set = new TreeSet<Reference>();
		for (Bible bible : bibles) {
			set.addAll(bible.getReferencesForBook(book));
		}
		return new ReferenceList(set);
	}

	@Override
	public ReferenceList getChapterReferences(BookOfBible book, int chapter) {
		TreeSet<Reference> set = new TreeSet<Reference>();
		for (Bible bible : bibles) {
			set.addAll(bible.getReferencesForChapter(book, chapter));
		}
		return new ReferenceList(set);
	}

	@Override
	public ReferenceList getChapterReferences(BookOfBible book, int chapter1, int chapter2) {
		TreeSet<Reference> set = new TreeSet<Reference>();
		for (Bible bible : bibles) {
			set.addAll(bible.getReferencesForChapters(book, chapter1, chapter2));
		}
		return new ReferenceList(set);
	}

	@Override
	public ReferenceList getPassageReferences(BookOfBible book, int chapter, int verse1, int verse2) {
		TreeSet<Reference> set = new TreeSet<Reference>();
		for (Bible bible : bibles) {
			set.addAll(bible.getReferencesForPassage(book, chapter, verse1, verse2));
		}
		return new ReferenceList(set);
	}

	@Override
	public ReferenceList getPassageReferences(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		TreeSet<Reference> set = new TreeSet<Reference>();
		for (Bible bible : bibles) {
			set.addAll(bible.getReferencesForPassage(book, chapter1, verse1, chapter2, verse2));
		}
		return new ReferenceList(set);
	}

	// ------------------------------------------------------------------
	// These are the better searching methods.
	//
	@Override
	public ReferenceList getReferencesContainingWord(String word) {
		// TODO Implement me: Stage 12
		ReferenceSet set = new ReferenceSet();
		for (Concordance c : concordances) {
			set.addAll(c.getReferencesContaining(word));
		}
		ReferenceList list = new ReferenceList(set);
		Collections.sort(list);
		return list;
	}

	@Override
	public ReferenceList getReferencesContainingAllWords(String words) {
		// TODO Implement me: Stage 12
		ArrayList<String> text = Concordance.extractWords(words);
		ReferenceSet set = new ReferenceSet();
		if (!words.isEmpty()) {
			for (Concordance c : concordances) {
				set.addAll(c.getReferencesContainingAll(text));
			}
			ReferenceList list = new ReferenceList(set);
			Collections.sort(list);
			return list;
		}
		return new ReferenceList();
	}

	@Override
	public ReferenceList getReferencesContainingAllWordsAndPhrases(String words) {
		// TODO Implement me: Stage 12
		return getReferencesContainingAllWords(words);
	}
}
