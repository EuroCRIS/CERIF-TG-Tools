package org.eurocris.cerif.model.toad;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestExtractInfo {

	@Test
	public void testDeprecated00() {
		assertNull( ToadModelParser.extractInfo( "deprecated", null, null ) );
	}

	@Test
	public void testDeprecated01() {
		assertNull( ToadModelParser.extractInfo( "deprecated", "", null ) );
	}

	@Test
	public void testDeprecated02() {
		assertNull( ToadModelParser.extractInfo( "deprecated", "xyz", null ) );
	}

	@Test
	public void testDeprecated1() {
		assertEquals( "1.6", ToadModelParser.extractInfo( "deprecated", "{@deprecated 1.6}", null ) );
	}

	@Test
	public void testDeprecated2() {
		assertEquals( "1.6", ToadModelParser.extractInfo( "deprecated", "blablabla {@deprecated 1.6}", null ) );
	}

	@Test
	public void testDeprecated3() {
		assertEquals( "1.6", ToadModelParser.extractInfo( "deprecated", "{@deprecated 1.6} blablabla", null ) );
	}

	@Test
	public void testDeprecated4() {
		assertEquals( "1.6", ToadModelParser.extractInfo( "deprecated", "blabla {@deprecated 1.6} bla", null ) );
	}

	@Test
	public void testFlag00() {
		assertNull( ToadModelParser.extractInfo( "flag", null, null ) );
	}

	@Test
	public void testFlag01() {
		assertNull( ToadModelParser.extractInfo( "flag", "", null ) );
	}

	@Test
	public void testFlag02() {
		assertNull( ToadModelParser.extractInfo( "flag", "blabla bla", null ) );
	}

	@Test
	public void testFlag1() {
		assertEquals( "", ToadModelParser.extractInfo( "flag", "{@flag}", null ) );
	}

	@Test
	public void testFlag2() {
		assertEquals( "", ToadModelParser.extractInfo( "flag", "blablabla {@flag}", null ) );
	}

	@Test
	public void testFlag3() {
		assertEquals( "", ToadModelParser.extractInfo( "flag", "{@flag} blablabla", null ) );
	}

	@Test
	public void testFlag4() {
		assertEquals( "", ToadModelParser.extractInfo( "flag", "blabla {@flag} bla", null ) );
	}

}
