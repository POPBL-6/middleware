package tests.datatests;

import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by Gorka Olalde on 9/5/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({MessagesTests.class})
public class DataPkgTestSuite {
    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);

}
