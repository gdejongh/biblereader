package bibleReader.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface)
 * @author ? (provided the implementation)
 */
public class TreeMapBible implements Bible {

	// The Fields
	private String version;
	private String title;
	private TreeMap<Reference, String> theVerses;

	// Or replace the above with:
	// private TreeMap<Reference, Verse> theVerses;
	// Add more fields as necessary.

	/**
	 * Create a new Bible with the given verses.
	 * 
	 * @param version
	 *            the version of the Bible (e.g. ESV, KJV, ASV, NIV).
	 * @param verses
	 *            All of the verses of this version of the Bible.
	 */
	public TreeMapBible(VerseList verses) {
		// TODO Implement me: Stage 11
		version = verses.getVersion();
		title = verses.getDescription();
		theVerses = new TreeMap<Reference, String>();
		for (Verse verse : verses) {
			theVerses.put(verse.getReference(), verse.getText());
		}

	}

	@Override
	public int getNumberOfVerses() {
		// TODO Implement me: Stage 11
		return theVerses.size();
	}

	@Override
	public VerseList getAllVerses() {
		// TODO Implement me: Stage 11
		VerseList verses = new VerseList(version, "TreeMapBible");
		Set<Map.Entry<Reference, String>> mySet = theVerses.entrySet();
		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			verses.add(aVerse);
		}
		return verses;
	}

	@Override
	public String getVersion() {
		// TODO Implement me: Stage 11
		return version;
	}

	@Override
	public String getTitle() {
		// TODO Implement me: Stage 11
		return title;
	}

	@Override
	public boolean isValid(Reference ref) {
		// TODO Implement me: Stage 11
		try{
		return theVerses.get(ref) != null && ref != null;
		} catch (Exception e){
			return false;
		}
	}

	@Override
	public String getVerseText(Reference r) {
		// TODO Implement me: Stage 11
		if (isValid(r)) {
			return theVerses.get(r).toString();
		} else {
			return null;
		}
	}

	@Override
	public Verse getVerse(Reference r) {
		// TODO Implement me: Stage 11
		if (isValid(r)) {
			Verse v = new Verse(r, theVerses.get(r));
			return v;
		} else {
			return null;
		}
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		// TODO Implement me: Stage 11
		Reference r = new Reference(book, chapter, verse);
		if (isValid(r)) {
			return getVerse(r);
		} else {
			return null;
		}
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for stage 4.
	//
	// For Stage 11 the first two methods below will be implemented as specified
	// in the comments.
	// Do not over think these methods. All three should be pretty
	// straightforward to implement.
	// For Stage 8 (give or take a 1 or 2) you will re-implement them so they
	// work better.
	// At that stage you will create another class to facilitate searching and
	// use it here.
	// (Essentially these two methods will be delegate methods.)
	// ---------------------------------------------------------------------------------------------

	@Override
	public VerseList getVersesContaining(String phrase) {
		// TODO Implement me: Stage 11
		VerseList verses = new VerseList(version, "Verses containing '" + phrase + "'");
		if (phrase.length() > 0) {
			phrase = phrase.toLowerCase();
			Set<Map.Entry<Reference, String>> mySet = theVerses.entrySet();
			for (Map.Entry<Reference, String> element : mySet) {
				if (element.getValue().toLowerCase().contains(phrase)) {
					verses.add(new Verse(element.getKey(), element.getValue()));
				}
			}
		}
		return verses;
	}

	@Override
	public ReferenceList getReferencesContaining(String phrase) {
		// TODO Implement me: Stage 11
		ReferenceList list = new ReferenceList();
		if (phrase.length() > 0) {
			phrase = phrase.toLowerCase();
			Set<Map.Entry<Reference, String>> mySet = theVerses.entrySet();
			for (Map.Entry<Reference, String> element : mySet) {
				if (element.getValue().toLowerCase().contains(phrase)) {
					list.add(element.getKey());
				}
			}
		}
		return list;
	}

	@Override
	public VerseList getVerses(ReferenceList references) {
		VerseList list = new VerseList(version, title);
		for (Reference r : references) {
			if (isValid(r)) {
				Verse verse = new Verse(r, theVerses.get(r));
				list.add(verse);
			} else {
				list.add(null);
			}
		}
		return list;
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for Stage 11.
	//
	// HINT: Do not reinvent the wheel. Some of these methods can be implemented
	// by looking up
	// one or two things and calling another method to do the bulk of the work.
	// ---------------------------------------------------------------------------------------------

	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		// TODO Implement me: Stage 11
		ArrayList<Verse> list = new ArrayList<Verse>();
		for (Verse verse : getAllVerses()) {
			if (verse.getReference().getBookOfBible().equals(book) && verse.getReference().getChapter() == chapter) {
				list.add(verse);
			} else if (!(verse.getReference().getChapter() == chapter) && !list.isEmpty()) {
				return list.get(list.size() - 1).getReference().getVerse();
			}
		}
		if (list.isEmpty()) {
			return -1;
		} else {
			return list.get(list.size() - 1).getReference().getVerse();
		}
	}

	@Override
	public int getLastChapterNumber(BookOfBible book) {
		// TODO Implement me: Stage 11
		ArrayList<Verse> list = new ArrayList<Verse>();
		for (Verse verse : getAllVerses()) {
			if (verse.getReference().getBookOfBible().equals(book)) {
				list.add(verse);
			} else if (!verse.getReference().getBookOfBible().equals(book) && list.size() > 0) {
				return list.get(list.size() - 1).getReference().getChapter();
			}
		}
		if (list.isEmpty()) {
			return -1;
		} else {
			return list.get(list.size() - 1).getReference().getChapter();
		}
	}

	@Override
	public ReferenceList getReferencesInclusive(Reference firstVerse, Reference lastVerse) {
		ReferenceList list = new ReferenceList();
		lastVerse = new Reference(lastVerse.getBookOfBible(), lastVerse.getChapter(), lastVerse.getVerse() + 1);
		if (firstVerse.compareTo(lastVerse) > 0) {
			return list;
		}
		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);
		Set<Reference> mySet = s.keySet();
		for (Reference element : mySet) {
			Reference ref = new Reference(element.getBookOfBible(), element.getChapter(), element.getVerse());
			list.add(ref);
		}
		return list;
	}

	@Override
	public ReferenceList getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ReferenceList list = new ReferenceList();
		if (firstVerse.compareTo(lastVerse) > 0) {
			return list;
		}
		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);
		Set<Reference> mySet = s.keySet();
		for (Reference element : mySet) {
			Reference ref = new Reference(element.getBookOfBible(), element.getChapter(), element.getVerse());
			list.add(ref);
		}
		return list;
	}

	@Override
	public ReferenceList getReferencesForBook(BookOfBible book) {
		// TODO Implement me: Stage 11
		if (book != null) {
			Reference firstVerse = new Reference(book, 1, 1);
			Reference lastVerse = new Reference(BookOfBible.nextBook(book), 1, 1);
			return getReferencesExclusive(firstVerse, lastVerse);
		}
		return new ReferenceList();
	}

	@Override
	public ReferenceList getReferencesForChapter(BookOfBible book, int chapter) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter, 1);
		Reference lastVerse = new Reference(book, chapter, getLastVerseNumber(book, chapter));
		return getReferencesInclusive(firstVerse, lastVerse);
	}

	@Override
	public ReferenceList getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter1, 1);
		Reference lastVerse = new Reference(book, chapter2, getLastVerseNumber(book, chapter2));
		return getReferencesInclusive(firstVerse, lastVerse);
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter, verse1);
		Reference lastVerse = new Reference(book, chapter, verse2);
		return getReferencesInclusive(firstVerse, lastVerse);
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter1, verse1);
		Reference lastVerse = new Reference(book, chapter2, verse2);
		return getReferencesInclusive(firstVerse, lastVerse);
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		VerseList someVerses = new VerseList(getVersion(), firstVerse + "-" + lastVerse);
		lastVerse = new Reference(lastVerse.getBookOfBible(), lastVerse.getChapter(), lastVerse.getVerse() + 1);
		if (firstVerse.compareTo(lastVerse) > 0) {
			return someVerses;
		}
		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);
		Set<Map.Entry<Reference, String>> mySet = s.entrySet();
		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			someVerses.add(aVerse);
		}
		return someVerses;
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		// Implementation of this method provided by Chuck Cusack.
		// This is provided so you have an example to help you get started
		// with the other methods.

		// We will store the resulting verses here. We copy the version from
		// this Bible and set the description to be the passage that was
		// searched for.
		VerseList someVerses = new VerseList(getVersion(), firstVerse + "-" + lastVerse);

		// Make sure the references are in the correct order. If not, return an
		// empty list.
		if (firstVerse.compareTo(lastVerse) > 0) {
			return someVerses;
		}
		// Return the portion of the TreeMap that contains the verses between
		// the first and the last, not including the last.
		SortedMap<Reference, String> s = theVerses.subMap(firstVerse, lastVerse);

		// Get the entries from the map so we can iterate through them.
		Set<Map.Entry<Reference, String>> mySet = s.entrySet();

		// Iterate through the set and put the verses in the VerseList.
		for (Map.Entry<Reference, String> element : mySet) {
			Verse aVerse = new Verse(element.getKey(), element.getValue());
			someVerses.add(aVerse);
		}
		return someVerses;
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		// TODO Implement me: Stage 11
		if (book != null) {
			Reference firstVerse = new Reference(book, 1, 1);
			Reference lastVerse = new Reference(BookOfBible.nextBook(book), 1, 1);
			return getVersesExclusive(firstVerse, lastVerse);
		}
		return new VerseList("", "");
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter, 1);
		Reference lastVerse = new Reference(book, chapter, getLastVerseNumber(book, chapter));
		return getVersesInclusive(firstVerse, lastVerse);
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter1, 1);
		Reference lastVerse = new Reference(book, chapter2, getLastVerseNumber(book, chapter2));
		return getVersesInclusive(firstVerse, lastVerse);
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter, verse1);
		Reference lastVerse = new Reference(book, chapter, verse2);
		return getVersesInclusive(firstVerse, lastVerse);
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		// TODO Implement me: Stage 11
		Reference firstVerse = new Reference(book, chapter1, verse1);
		Reference lastVerse = new Reference(book, chapter2, verse2);
		return getVersesInclusive(firstVerse, lastVerse);
	}

}
