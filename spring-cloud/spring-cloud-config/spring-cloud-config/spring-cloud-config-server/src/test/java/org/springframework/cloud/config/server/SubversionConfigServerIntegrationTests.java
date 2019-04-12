/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.config.server;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.SvnKitEnvironmentRepository;
import org.springframework.cloud.config.server.test.ConfigServerTestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * @author Michael Prankl
 * @author Dave Syer
 * @author Roy Clarkson
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigServerApplication.class,
	properties = { "spring.config.name:configserver",
		"spring.cloud.config.server.svn.uri:file:///./target/repos/svn-config-repo" },
	webEnvironment = RANDOM_PORT)
@ActiveProfiles("subversion")
public class SubversionConfigServerIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private ApplicationContext context;

	@BeforeClass
	public static void init() throws Exception {
		ConfigServerTestUtils.prepareLocalSvnRepo("src/test/resources/svn-config-repo",
				"target/repos/svn-config-repo");
	}

	@Test
	public void contextLoads() {
		Environment environment = new TestRestTemplate().getForObject("http://localhost:"
				+ this.port + "/foo/development/", Environment.class);
		assertFalse(environment.getPropertySources().isEmpty());
		assertEquals("overrides", environment.getPropertySources().get(0).getName());
		assertEquals("{spring.cloud.config.enabled=true}", environment
				.getPropertySources().get(0).getSource().toString());
	}

	@Test
	public void defaultLabel() throws Exception {
		SvnKitEnvironmentRepository repository = this.context
				.getBean(SvnKitEnvironmentRepository.class);
		assertEquals("trunk", repository.getDefaultLabel());
	}

	@Test
	public void updateUnavailableRepo() throws IOException {
		contextLoads();
		assertTrue(ConfigServerTestUtils.deleteLocalRepo("svn-config-repo"));
		contextLoads();
	}

}
