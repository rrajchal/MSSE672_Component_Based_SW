package com.topcard.xml;

import com.topcard.domain.Player;
import com.topcard.domain.PlayerTest;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerXmlWriterTest {

    String destination = "src/test/resources/output.xml";

    @Test
    public void testWritePlayersToXml() throws ParserConfigurationException, TransformerException {
        PlayerTest playerTest = new PlayerTest();
        List<Player> players = playerTest.generatePlayers();
        PlayerXmlWriter.writePlayersToXml(players, destination);

        File file = new File(destination);

        assertTrue(file.isFile());
        assertTrue(file.length() > 0);
    }
}