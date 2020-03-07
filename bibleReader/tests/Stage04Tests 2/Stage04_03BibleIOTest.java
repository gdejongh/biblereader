package bibleReader.tests;

// If you organize imports, the following import might be removed and you will
// not be able to find certain methods. If you can't find something, copy the
// commented import statement below, paste a copy, and remove the comments.
// Keep this commented one in case you organize imports multiple times.
//
// import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import bibleReader.BibleIO;
import bibleReader.model.BookOfBible;
import bibleReader.model.Verse;
import bibleReader.model.VerseList;

/**
 * Test the BibleIO class. Notice that I don't use a Bible
 * 
 * @author Chuck Cusack, February 11, 2013
 */
public class Stage04_03BibleIOTest {
	private VerseList versesFromFile;
	private Verse jn1_3_21;
	private Verse gen1_1;
	private Verse rev22_21;
	private Verse hab3_17;
	private String habText = "Although the fig tree shall not blossom, neither shall fruit be in the vines; "
			+ "the labour of the olive shall fail, and the fields shall yield no meat; "
			+ "the flock shall be cut off from the fold, and there shall be no herd in the stalls:";
	private String genText = "And God said, Let there be light: and there was light.";

	@Test(timeout = 5000)
	public void testReadATVFormat_BasicStuff() {
		// Do we have the right number of verses?
		assertEquals(31102, versesFromFile.size());

		// Did we get the first and last, and are they in the right spot in the
		// ArrayList?
		assertEquals(0, versesFromFile.indexOf(gen1_1));
		assertEquals(31101, versesFromFile.indexOf(rev22_21));
		
		// Let's check one in the middle.
		assertEquals(22785, versesFromFile.indexOf(hab3_17));

		// Is the following verse there?
		assertTrue(versesFromFile.contains(jn1_3_21));

		for (Verse v : versesFromFile) {
			assertNotNull(v);
		}
	}

	@Test(timeout = 5000)
	public void testReadATV_ParsingColons() {
		// These verses have colons in the text. If the lines are parsed
		// incorrectly the text of these will be incorrect.

		// Hab 3:17 should be at index 22,785.
		assertEquals(habText, versesFromFile.get(22785).getText());

		// Gen 1:3 should be at index 3.
		assertEquals(genText, versesFromFile.get(2).getText());
	}

	@Test(timeout = 5000)
	public void testReadATV_InvalidFile() {
		File file = new File("idontexist.atv");
		versesFromFile = BibleIO.readBible(file);
		assertEquals(null, versesFromFile);
	}

	@Test(timeout = 5000)
	public void testReadATV_VersionAndDescription() {
		assertEquals("KJV",versesFromFile.getVersion());
		assertEquals("Holy Bible, Authorized (King James) Version",versesFromFile.getDescription());
	}

	@Before
	public void setUp() throws Exception {
		File file = new File("kjv.atv");

		// Notice that I call readBible and not one of the specific versions
		// This is because the file extension is used in readBible to
		// determine which version to call.
		versesFromFile = BibleIO.readBible(file);
		jn1_3_21 = new Verse(BookOfBible.John1, 3, 21,
				"Beloved, if our heart condemn us not, then have we confidence toward God.");
		gen1_1 = new Verse(BookOfBible.Genesis, 1, 1, "In the beginning God created the heaven and the earth.");
		rev22_21 = new Verse(BookOfBible.Revelation, 22, 21,
				"The grace of our Lord Jesus Christ be with you all. Amen.");
		hab3_17 = new Verse(BookOfBible.Habakkuk, 3, 17,new String(habText));
	}
}
