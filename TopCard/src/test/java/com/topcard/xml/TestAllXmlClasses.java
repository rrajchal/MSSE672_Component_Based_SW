package com.topcard.xml;

import com.topcard.xml.dom.DomParserUtilTest;
import com.topcard.xml.sax.PlayerSaxHandlerTest;
import com.topcard.xml.sax.SaxParserUtilTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({DomParserUtilTest.class, PlayerSaxHandlerTest.class, SaxParserUtilTest.class, PlayerXmlWriterTest.class})
public class TestAllXmlClasses {
}
