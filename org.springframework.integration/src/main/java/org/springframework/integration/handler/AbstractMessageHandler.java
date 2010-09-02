/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.integration.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.Ordered;
import org.springframework.integration.core.Message;
import org.springframework.integration.core.MessagingException;
import org.springframework.integration.message.MessageHandler;
import org.springframework.integration.message.MessageHandlingException;
import org.springframework.util.Assert;

/**
 * Base class for MessageHandler implementations that provides basic
 * validation and error handling capabilities. Asserts that the incoming
 * Message is not null and that it does not contain a null payload. Converts
 * checked exceptions into runtime {@link MessagingException}s. 
 * 
 * @author Mark Fisher
 */
public abstract class AbstractMessageHandler implements MessageHandler, Ordered {

	protected final Log logger = LogFactory.getLog(this.getClass());

	private volatile int order = Ordered.LOWEST_PRECEDENCE;


	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return this.order;
	}

	public final void handleMessage(Message<?> message) {
		Assert.notNull(message == null, "Message must not be null");
		Assert.notNull(message.getPayload(), "Message payload must not be null");
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(this + " received message: " + message);
		}
		try {
			this.handleMessageInternal(message);
		}
		catch (Exception e) {
			if (e instanceof MessagingException) {
				throw (MessagingException) e;
			}
			throw new MessageHandlingException(message,
					"error occurred in message handler [" + this + "]", e);
		}
	}

	protected abstract void handleMessageInternal(Message<?> message) throws Exception;

}