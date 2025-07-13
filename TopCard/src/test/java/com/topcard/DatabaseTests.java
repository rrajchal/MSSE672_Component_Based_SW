package com.topcard;

import com.topcard.dao.player.PlayerDaoImplTest;
import com.topcard.util.HibernateUtilTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({HibernateUtilTest.class, PlayerDaoImplTest.class})
public class DatabaseTests {
}
