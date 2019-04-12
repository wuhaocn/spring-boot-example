/*
 * Copyright 2012-2015 the original author or authors.
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

package org.springframework.cloud.config.server.encryption;

import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.util.Map;

/**
 * @author Dave Syer
 *
 */
public class LocatorTextEncryptor implements TextEncryptor {

	private EnvironmentPrefixHelper helper = new EnvironmentPrefixHelper();

	private TextEncryptorLocator locator;

	public LocatorTextEncryptor(TextEncryptorLocator locator) {
		this.locator = locator;
	}

	@Override
	public String encrypt(String text) {
		Map<String, String> keys = helper.getEncryptorKeys("configserver", "default",
				text);
		return getLocator().locate(keys).encrypt(helper.stripPrefix(text));
	}

	private TextEncryptorLocator getLocator() {
		return locator;
	}

	@Override
	public String decrypt(String encryptedText) {
		Map<String, String> keys = helper.getEncryptorKeys("configserver", "default",
				encryptedText);
		return getLocator().locate(keys).decrypt(helper.stripPrefix(encryptedText));
	}

}