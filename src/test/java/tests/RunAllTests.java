package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import tests.apiTests.ApiPkgTestSuite;
import tests.dataTests.DataPkgTestSuite;
import tests.socketTests.SocketPkgTestSuite;
import tests.utilsTests.UtilsPkgTestSuite;

@RunWith(Suite.class)
@SuiteClasses({ApiPkgTestSuite.class, DataPkgTestSuite.class, SocketPkgTestSuite.class, UtilsPkgTestSuite.class})
public class RunAllTests {

}