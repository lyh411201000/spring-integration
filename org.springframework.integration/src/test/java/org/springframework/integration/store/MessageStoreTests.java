/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.integration.store;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.springframework.integration.core.Message;
import org.springframework.integration.message.StringMessage;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Dave Syer
 */
public class MessageStoreTests {


	@Test
	public void shouldRegisterCallbacks() throws Exception {
		TestMessageStore store = new TestMessageStore();
		store.setExpiryCallbacks(Arrays.<MessageGroupCallback>asList(new MessageGroupCallback() {
			public void execute(MessageGroup group) {
			}
		}));
		assertEquals(1, ((Collection<?>)ReflectionTestUtils.getField(store, "expiryCallbacks")).size());
	}

	@Test
	public void shouldExpireMessageGroup() throws Exception {

		TestMessageStore store = new TestMessageStore();
		final List<String> list = new ArrayList<String>();
		store.registerExpiryCallback(new MessageGroupCallback() {
			public void execute(MessageGroup group) {
				list.add(group.getOne().getPayload().toString());
			}
		});

		store.expireMessageGroups(-10000);
		assertEquals("[foo]", list.toString());
		assertEquals(0, store.getMessageGroup("bar").size());

	}
	
	private static class TestMessageStore extends AbstractMessageGroupStore {

		MessageGroup testMessages = new SimpleMessageGroup(Arrays.asList(new StringMessage("foo")), "bar");
		private boolean removed = false;

		@Override
		public Iterator<MessageGroup> iterator() {
			return Arrays.asList(testMessages).iterator();
		}

		public void addMessageToGroup(Object correlationKey, Message<?> message) {
		}

		public MessageGroup getMessageGroup(Object correlationKey) {
			return removed ? new SimpleMessageGroup(correlationKey) : testMessages;
		}

		public void markMessageGroup(MessageGroup group) {
		}

		public void removeMessageGroup(Object correlationKey) {
			if (correlationKey.equals(testMessages.getCorrelationKey())) {
				removed = true;
			}
		}
		
	}

}