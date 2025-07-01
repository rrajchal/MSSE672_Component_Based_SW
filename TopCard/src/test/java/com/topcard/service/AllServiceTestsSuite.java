package com.topcard.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The AllServiceTestsSuite class is a test suite that groups together all
 * service layer test classes in the application.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CardServiceTest.class, PlayerServiceTest.class, GameServiceTest.class, ServiceFactoryTest.class})
public class AllServiceTestsSuite {
    // This class remains empty. Nothing needs to be written.
}
