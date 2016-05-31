package tests;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import tests.apitests.ApiPkgTestSuite;
import tests.datatests.DataPkgTestSuite;
import tests.sockettests.SocketPkgTestSuite;
import tests.utilstests.UtilsPkgTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ApiPkgTestSuite.class, DataPkgTestSuite.class, SocketPkgTestSuite.class, UtilsPkgTestSuite.class})
public class RunAllTests {



}