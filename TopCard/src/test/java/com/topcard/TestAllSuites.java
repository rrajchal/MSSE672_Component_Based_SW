package com.topcard;

import com.topcard.business.TestBusinessTestSuite;
import com.topcard.service.AllServiceTestsSuite;
import com.topcard.domain.AllDomainTestsSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllServiceTestsSuite.class, AllDomainTestsSuite.class, TestBusinessTestSuite.class})
public class TestAllSuites {

}
