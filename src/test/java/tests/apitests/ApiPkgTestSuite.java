package tests.apitests;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Gorka Olalde on 9/5/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({PSPortSSLTest.class, PSPortTCPTest.class})
public class ApiPkgTestSuite {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

}
