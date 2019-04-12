package org.springframework.cloud.config.server;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.cloud.config.server.environment.*;

/**
 * A test suite for probing weird ordering problems in the tests.
 *
 * @author Dave Syer
 */
@RunWith(Suite.class)
@SuiteClasses({ MultipleJGitEnvironmentRepositoryIntegrationTests.class,
		JGitEnvironmentRepositoryIntegrationTests.class, EnvironmentEncryptorEnvironmentRepositoryTests.class,
		NativeEnvironmentRepositoryTests.class, SVNKitEnvironmentRepositoryIntegrationTests.class })
@Ignore
public class AdhocTestSuite {

}
