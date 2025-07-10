package com.topcard;

import com.topcard.util.DatabaseConnectionTest;
import com.topcard.dao.player.PlayerDaoImplTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({DatabaseConnectionTest.class, PlayerDaoImplTest.class})
public class DatabaseTests {
}
