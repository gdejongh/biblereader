package bibleReader.model;

import java.util.HashSet;
import java.util.Collection;

/**
 * A list of references. This is just a subclass of ArrayList. It is mostly for
 * convenience. This is done as a subclass rather than a wrapper class so that
 * all of the ArrayList methods are available.
 * 
 * @author cusack, January 21, 2013
 */
public class ReferenceSet extends HashSet<Reference> {
	public ReferenceSet() {
		// Needs to be here so we can call the default constructor.
	}

	public ReferenceSet(int initialSize) {
		super(initialSize);
	}

	public ReferenceSet(Collection<? extends Reference> list) {
		super(list);
	}

}