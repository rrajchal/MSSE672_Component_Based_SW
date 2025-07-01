package com.topcard.domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * The AllDomainTestsSuite class is a test suite that groups together all
 * domain layer test classes in the application.
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({CardTest.class, DeckTest.class, PlayerTest.class, GameTest.class})
public class AllDomainTestsSuite {
    // This class remains empty. Nothing needs to be written.
}
