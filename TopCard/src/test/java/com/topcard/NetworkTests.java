package com.topcard;

import com.topcard.network.GameClientTest;
import com.topcard.network.GameServerTest;
import com.topcard.network.SocketGameControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({GameServerTest.class, SocketGameControllerTest.class, GameClientTest.class})
public class NetworkTests {
}
