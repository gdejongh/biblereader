package bibleReader.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Concordance is a class which implements a concordance for a Bible. In other
 * words, it allows the easy lookup of all references which contain a given
 * word.
 * 
 * @author Chuck Cusack, March 2013 (Provided the interface)
 * @author Gabe DeJongh
 */
public class Concordance {
	private HashMap<String, TreeSet<Reference>> wordMap;
	/**
	 * Construct a concordance for the given Bible.
	 */
	public Concordance(Bible bible) {
		// TODO: Implement me.
		wordMap = new HashMap<String, TreeSet<Reference>>();
		for (Verse verse : bible.getAllVerses()) {
			for (String word : extractWords(verse.getText())) {
				TreeSet<Reference> set;
				if (wordMap.get(word) == null) {
					set = new TreeSet<Reference>();
					set.add(verse.getReference());
					wordMap.put(word, set);
				} else if (wordMap.get(word) != null) {
					wordMap.get(word).add(verse.getReference());
				}
			}
		}
	}

	/**
	 * Return the list of references to verses that contain the word 'word'
	 * (ignoring case) in the version of the Bible that this concordance was
	 * created with.
	 * 
	 * @param word
	 *            a single word (no spaces, etc.)
	 * @return the list of References of verses from this version that contain
	 *         the word, or an empty list if no verses contain the word.
	 */
	public ReferenceList getReferencesContaining(String word) {
		word = word.toLowerCase();
		if (wordMap.get(word) != null) {
			return new ReferenceList(wordMap.get(word));
		}
		return new ReferenceList();
	}

	/**
	 * Given an array of Strings, where each element of the array is expected to
	 * be a single word (with no spaces, etc., but ignoring case), return a
	 * ReferenceList containing all of the verses that contain <i>all of the
	 * words</i>.
	 * 
	 * @param words
	 *            A list of words.
	 * @return An ReferenceList containing references to all of the verses that
	 *         contain all of the given words, or an empty list if
	 */
	public ReferenceList getReferencesContainingAll(ArrayList<String> words) {
		ReferenceSet refSet = new ReferenceSet();
		refSet.addAll(new ReferenceList(getReferencesContaining(words.get(0))));
		for (int i = 1; i < words.size(); i++) {
			ReferenceSet set = new ReferenceSet(getReferencesContaining(words.get(i)));
			Iterator<Reference> it = refSet.iterator();
			while (it.hasNext()) {
				if(!set.contains(it.next())){
					it.remove();
				}
			}
		}
		ReferenceList toRet = new ReferenceList(refSet);
		Collections.sort(toRet);
		return toRet;
	}

	/**
	 * Helper method that formats the text correctly by removing html tags,
	 * commas, apostrophes, and other unneccessary things.
	 * 
	 * @param text
	 *            The text that is going to be formatted
	 * @return An ArrayList where each entry is a word.
	 */
	public static ArrayList<String> extractWords(String text) {
		text = text.toLowerCase();
		text = text.replaceAll("(<sup>[,\\w]*?</sup>|'s|'s|&#\\w*;)", " ");
		text = text.replaceAll(",", "");
		String[] words = text.split("\\W+");
		ArrayList<String> toRet = new ArrayList<String>(Arrays.asList(words));
		toRet.remove("");
		return toRet;

	}
}
