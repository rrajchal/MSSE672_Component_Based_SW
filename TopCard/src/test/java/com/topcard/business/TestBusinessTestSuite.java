package com.topcard.business;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 * The TestBusinessTestSuite class is a test suite that groups together all
 * business layer test classes in the application.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CardManagerTest.class, GameManagerTest.class})
public class TestBusinessTestSuite {

}