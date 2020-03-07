package bibleReader.model;

import java.util.ArrayList;

/**
 * A class that stores a version of the Bible.
 * 
 * @author Chuck Cusack (Provided the interface). Modified February 9, 2015.
 * @author ? (provided the implementation)
 */
public class ArrayListBible implements Bible {

	// The Fields
	private String version;
	private String title;
	private ArrayList<Verse> theVerses;

	/**
	 * Create a new Bible with the given verses.
	 * 
	 * @param verses
	 *            All of the verses of this version of the Bible.
	 */
	public ArrayListBible(VerseList verses) {
		// TODO Implement me: Stage 2
		theVerses = new ArrayList<Verse>(verses);
		title = verses.getDescription();
		version = verses.getVersion();
	}
	
	

	@Override
	public int getNumberOfVerses() {
		// TODO Implement me: Stage 2
		return theVerses.size();
	}

	@Override
	public String getVersion() {
		// TODO Implement me: Stage 2
		return version;
	}

	@Override
	public String getTitle() {
		// TODO Implement me: Stage 2
		return title;
	}

	@Override
	public boolean isValid(Reference ref) {
		// TODO Implement me: Stage 2
		for (Verse verse : theVerses) {
			if (verse.getReference().equals(ref)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getVerseText(Reference r) {
		// TODO Implement me: Stage 2
		for (Verse verse : theVerses) {
			if (verse.getReference().equals(r)) {
				return verse.getText();
			}
		}
		return null;
	}

	@Override
	public Verse getVerse(Reference r) {
		// TODO Implement me: Stage 2
		for (Verse verse : theVerses) {
			if (verse.getReference().equals(r)) {
				return verse;
			}
		}
		return null;
	}

	@Override
	public Verse getVerse(BookOfBible book, int chapter, int verse) {
		// TODO Implement me: Stage 2
		Reference ref = new Reference(book, chapter, verse);
		return getVerse(ref);
	}

	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for stage 4.
	// See the Bible interface for the documentation of these methods.
	// Do not over think these methods. All three should be pretty
	// straightforward to implement.
	// For Stage 8 (give or take a 1 or 2) you will re-implement them so they
	// work better.
	// At that stage you will create another class to facilitate searching and
	// use it here.
	// (Essentially these two methods will be delegate methods.)
	// ---------------------------------------------------------------------------------------------

	@Override
	public VerseList getAllVerses() {
		// TODO Implement me: Stage 4
		return new VerseList(version, title, theVerses);
	}

	@Override
	public VerseList getVersesContaining(String phrase) {
		// TODO Implement me: Stage 4
		VerseList list = new VerseList(version, "Verses containing: " + phrase);
		String thePhrase = phrase.toLowerCase();
		for (Verse verse : theVerses) {
			String content = verse.getText().toLowerCase();
			if (content.contains(thePhrase) && thePhrase.length() > 0) {
				list.add(verse);
			}
		}
		return list;
	}

	@Override
	public ReferenceList getReferencesContaining(String phrase) {
		// TODO Implement me: Stage 4
		ReferenceList list = new ReferenceList();
		String thePhrase = phrase.toLowerCase();
		for (Verse verse : theVerses) {
			String verseText = verse.getText().toLowerCase();
			if (verseText.contains(thePhrase) && thePhrase.length() > 0) {
				list.add(verse.getReference());
			}
		}
		return list;
	}

	@Override
	public VerseList getVerses(ReferenceList references) {
		VerseList list = new VerseList(version, title);
		for (Reference ref : references) {
			int index = 0;
			int startSize = list.size();
			for (Verse verse : theVerses) {
				index++;
				if (ref.equals(verse.getReference()))
					list.add(verse);
				else if (index == theVerses.size() && startSize == list.size()) {
					list.add(null);
				}
			}
		}
		return list;
	}
	// ---------------------------------------------------------------------------------------------
	// The following part of this class should be implemented for Stage 7.
	//
	// HINT: Do not reinvent the wheel. Some of these methods can be implemented
	// by looking up
	// one or two things and calling another method to do the bulk of the work.
	// ---------------------------------------------------------------------------------------------

	@Override
	public int getLastVerseNumber(BookOfBible book, int chapter) {
		// TODO Implement me: Stage 7
		ArrayList<Verse> list = new ArrayList<Verse>();
		for (Verse verse : theVerses) {
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
		// TODO Implement me: Stage 7
		ArrayList<Verse> list = new ArrayList<Verse>();
		for (Verse verse : theVerses) {
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
		ReferenceList results = new ReferenceList();
		if (isValid(firstVerse) && isValid(lastVerse) && (firstVerse.compareTo(lastVerse) <= 0)) {
			int index1 = 0;
			while (!theVerses.get(index1).getReference().equals(firstVerse)) {
				index1++;
			}
			int index2 = index1;
			while (!theVerses.get(index2).getReference().equals(lastVerse)) {
				index2++;
			}
			for (int i = index1; i <= index2; i++) {
				results.add(theVerses.get(i).getReference());
			}
		}
		return results;
	}

	@Override
	public ReferenceList getReferencesExclusive(Reference firstVerse, Reference lastVerse) {
		ReferenceList list = new ReferenceList();
		if (!(firstVerse.compareTo(lastVerse) > 0) && isValid(firstVerse)) {
			if (isValid(lastVerse)) {
				list = getReferencesInclusive(firstVerse, lastVerse);
				list.remove(list.size() - 1);
				return list;
			} else {
				Reference ref = new Reference(lastVerse.getBookOfBible(), lastVerse.getChapter(),
						lastVerse.getVerse() - 1);
				if (isValid(ref)) {
					return getReferencesInclusive(firstVerse, ref);
				} else {
					BookOfBible book = firstVerse.getBookOfBible();
					int chap = getLastChapterNumber(book);
					int verse = getLastVerseNumber(book, chap);
					lastVerse = new Reference(book, chap, verse);
					return getReferencesInclusive(firstVerse, lastVerse);
				}
			}
		}
		return new ReferenceList();
	}

	@Override
	public ReferenceList getReferencesForBook(BookOfBible book) {
		// TODO Implement me: Stage 7
		Reference ref1 = new Reference(book, 1, 1);
		int chap = getLastChapterNumber(book);
		int verse = getLastVerseNumber(book, chap);
		Reference ref2 = new Reference(book, chap, verse);
		return getReferencesInclusive(ref1, ref2);
	}

	@Override
	public ReferenceList getReferencesForChapter(BookOfBible book, int chapter) {
		Reference ref1 = new Reference(book, chapter, 1);
		int verse = getLastVerseNumber(book, chapter);
		Reference ref2 = new Reference(book, chapter, verse);
		return getReferencesInclusive(ref1,ref2);
	}

	@Override
	public ReferenceList getReferencesForChapters(BookOfBible book, int chapter1, int chapter2) {
		ReferenceList rList = new ReferenceList();
		for (int i = 0; i < theVerses.size(); i++) {
			if (theVerses.get(i).getReference().getBookOfBible().equals(book)
					&& theVerses.get(i).getReference().getChapter() >= chapter1
					&& theVerses.get(i).getReference().getChapter() <= chapter2) {
				rList.add(theVerses.get(i).getReference());
			}
		}
		return rList;
	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		ReferenceList rList = new ReferenceList();
		for (int i = 0; i < theVerses.size(); i++) {
			if (theVerses.get(i).getReference().getBookOfBible().equals(book)
					&& theVerses.get(i).getReference().getChapter() == chapter
					&& theVerses.get(i).getReference().getVerse() >= verse1
					&& theVerses.get(i).getReference().getVerse() <= verse2) {
				rList.add(theVerses.get(i).getReference());
			}
		}
		return rList;
	}

	@Override
	public VerseList getVersesInclusive(Reference firstVerse, Reference lastVerse) {
		// TODO Implement me: Stage 7
		String description = (firstVerse.toString() + " - " + lastVerse.toString());
		VerseList list = new VerseList(this.getVersion(), description);
		if (firstVerse.equals(lastVerse)) {
			for (Verse verse : theVerses) {
				if (verse.getReference().equals(firstVerse)) {
					list.add(verse);
					return list;
				}
			}
		} else if (firstVerse.compareTo(lastVerse) > 0) {
			return list;
		}
		for (int i = 0; i < theVerses.size(); i++) {
			if (theVerses.get(i).getReference().equals(firstVerse)) {
				list.add(theVerses.get(i));
				for (int index = i + 1; index < theVerses.size(); index++) {
					if (theVerses.get(index).getReference().equals(lastVerse)) {
						list.add(theVerses.get(index));
						return list;
					}
					list.add(theVerses.get(index));
				}
			}
		}
		if (list.contains(lastVerse)) {
			return list;
		} else {
			return new VerseList("", "");
		}
	}

	@Override
	public VerseList getVersesExclusive(Reference firstVerse, Reference lastVerse) {
		String description = (firstVerse.toString() + " - " + lastVerse.toString() + " excluding the last verse.");
		VerseList list = new VerseList(this.getVersion(), description);
		if (firstVerse.equals(lastVerse)) {
			return list;
		} else if (firstVerse.compareTo(lastVerse) > 0) {
			return list;
		}
		for (int i = 0; i < theVerses.size(); i++) {
			if (theVerses.get(i).getReference().equals(firstVerse)) {
				list.add(theVerses.get(i));
				for (int index = i + 1; index < theVerses.size(); index++) {
					if (theVerses.get(index).getReference().equals(lastVerse)) {
						return list;
					}
					list.add(theVerses.get(index));
				}
			}
		}
		VerseList validList = new VerseList(this.getVersion(), description);
		Reference validRef = new Reference(lastVerse.getBookOfBible(), lastVerse.getChapter(),
				lastVerse.getVerse() - 1);
		if (list.contains(validRef)) {
			for (Verse verse : list) {
				if (verse.getReference().equals(validRef)) {
					validList.add(verse);
					return validList;
				}
				validList.add(verse);
			}
		} else {
			for (Verse verse : list) {
				if (firstVerse.getBookOfBible().equals(verse.getReference().getBookOfBible())) {
					validList.add(verse);
				} else if (!firstVerse.getBookOfBible().equals(verse.getReference().getBookOfBible())
						&& !validList.isEmpty()) {
					return validList;
				}
			}
		}
		return validList;
	}

	@Override
	public VerseList getBook(BookOfBible book) {
		try {
			VerseList vList = new VerseList(this.getVersion(), book.name());
			for (Verse verse : theVerses) {
				if (verse.getReference().getBookOfBible().equals(book)) {
					vList.add(verse);
				}
			}
			return vList;
		} catch (Exception e) {
			return new VerseList("", "");
		}
	}

	@Override
	public VerseList getChapter(BookOfBible book, int chapter) {
		VerseList vList = new VerseList(this.getVersion(), book.name() + chapter);
		for (Verse verse : theVerses) {
			if (verse.getReference().getBookOfBible().equals(book) && verse.getReference().getChapter() == chapter) {
				vList.add(verse);
			}
		}
		return vList;
	}

	@Override
	public VerseList getChapters(BookOfBible book, int chapter1, int chapter2) {
		String description = book.name() + " " + chapter1 + "-" + chapter2;
		VerseList vList = new VerseList(this.getVersion(), description);
		for (int i = 0; i < theVerses.size(); i++) {
			if (theVerses.get(i).getReference().getBookOfBible().equals(book)
					&& theVerses.get(i).getReference().getChapter() >= chapter1
					&& theVerses.get(i).getReference().getChapter() <= chapter2) {
				vList.add(theVerses.get(i));
			}
		}
		return vList;
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter, int verse1, int verse2) {
		String description = book.name() + " " + chapter + ":" + verse1 + "-" + verse2;
		VerseList vList = new VerseList(this.version, description);
		for (int i = 0; i < theVerses.size(); i++) {
			if (theVerses.get(i).getReference().getBookOfBible().equals(book)
					&& theVerses.get(i).getReference().getChapter() == chapter
					&& theVerses.get(i).getReference().getVerse() >= verse1
					&& theVerses.get(i).getReference().getVerse() <= verse2) {
				vList.add(theVerses.get(i));
			}
		}
		return vList;
	}

	@Override
	public VerseList getPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		String description = book.name() + " " + chapter1 + ":" + verse1 + " - " + chapter2 + ":" + verse2;
		Reference ref1 = new Reference(book, chapter1, verse1);
		Reference ref2 = new Reference(book, chapter2, verse2);
		VerseList list = new VerseList(this.getVersion(), description, getVersesInclusive(ref1, ref2));
		return list;

	}

	@Override
	public ReferenceList getReferencesForPassage(BookOfBible book, int chapter1, int verse1, int chapter2, int verse2) {
		Reference ref1 = new Reference(book, chapter1, verse1);
		Reference ref2 = new Reference(book, chapter2, verse2);
		ReferenceList list = new ReferenceList(getReferencesInclusive(ref1, ref2));
		return list;
	}
}
