package bibleReader.tests;

// If you organize imports, the following import might be removed and you will
// not be able to find certain methods. If you can't find something, copy the
// commented import statement below, paste a copy, and remove the comments.
// Keep this commented one in case you organize imports multiple times.
//
// import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import bibleReader.BibleIO;
import bibleReader.model.Bible;
import bibleReader.model.BibleFactory;
import bibleReader.model.BibleReaderModel;
import bibleReader.model.BookOfBible;
import bibleReader.model.Reference;
import bibleReader.model.ReferenceList;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * Tests the GetReferencesForPassage method from the BibleReaderModel class.
 * Project 6.
 *
 * @author Gabriel DeJongh & William Ash, 2018
 */
public class TestGetReferencesForPassage {
	private static VerseList versesFromFile;
	private BibleReaderModel model;
	private ReferenceList testList;

	@BeforeClass
	public static void readFile() {
		// Our tests will be based on the KJV version for now.
		File file = new File("kjv.atv");
		// We read the file here so it isn't done before every test.
		versesFromFile = BibleIO.readBible(file);
	}

	@Before
	public void setUp() throws Exception {
		// Make a shallow copy of the verses.
		ArrayList<Verse> copyOfList = new ArrayList<Verse>(versesFromFile);
		// Now make a copy of the VerseList
		VerseList copyOfVerseList = new VerseList(versesFromFile.getVersion(), versesFromFile.getDescription(),
				copyOfList);

		Bible testBible = BibleFactory.createBible(copyOfVerseList);
		model = new BibleReaderModel();
		model.addBible(testBible);
	}

	@Test
	public void testOneResult() {
		testList = model.getReferencesForPassage("John 3:16");
		assertEquals(1, testList.size());
		testList = model.getReferencesForPassage("Lev 1:1");
		assertEquals(1, testList.size());
	}

	@Test
	public void testReferencesInsideOfList() {
		testList = model.getReferencesForPassage("John 3 : 16");
		// String testString
		assertEquals("John 3:16", testList.get(0).toString());
		testList = model.getReferencesForPassage("Gen 1");
		ReferenceList compareList = new ReferenceList();
		int i = 0;
		while (i < 31) {
			i++;
			compareList.add(new Reference(BookOfBible.Genesis, 1, i));
		}
		assertEquals(compareList, testList);

		testList = model.getReferencesForPassage("John 3:1 - 3:10");
		ReferenceList compareList2 = new ReferenceList();
		i = 0;
		while (i < 10) {
			i++;
			compareList2.add(new Reference(BookOfBible.John, 3, i));
		}
		assertEquals(compareList2, testList);

	}

	@Test
	public void testIncorrectInput() {
		//testList = model.getReferencesForPassage("num 1:1 - 10000:10000");
		//assertEquals(1288, testList.size());
		testList = model.getReferencesForPassage("George Washington");
		assertEquals(0, testList.size());
		testList = model.getReferencesForPassage("Gen 1:1 -  Lev 10:13");
		assertEquals(0, testList.size());
		testList = model.getReferencesForPassage("3200 Kings 1");
		assertEquals(0, testList.size());
		testList = model.getReferencesForPassage("John 3:16 - John 4:1");
		assertEquals(0, testList.size());
	}

	@Test
	public void testForWholeBook() {
		testList = model.getReferencesForPassage(" Jeremiah ");
		assertEquals(1364, testList.size());
		testList = model.getReferencesForPassage("Luke");
		assertEquals(1151, testList.size());
		testList = model.getReferencesForPassage("Ezekiel");
		assertEquals(1273, testList.size());
		testList = model.getReferencesForPassage("Exodus");
		assertEquals(1213, testList.size());
	}

	@Test
	public void testInsideSingleChapter() {
		testList = model.getReferencesForPassage("Exodus 1:1 - 1:5");
		assertEquals(5, testList.size());
		testList = model.getReferencesForPassage("Gen 1 : 1 - 1:7");
		assertEquals(7, testList.size());
	}

	@Test
	public void testMultipleChapters() {
		testList = model.getReferencesForPassage("Gen 1:1 - 50:26");
		assertEquals(1533, testList.size());
		testList = model.getReferencesForPassage("John 3:16 - 4:1");
		assertEquals(22, testList.size());
	}
}
