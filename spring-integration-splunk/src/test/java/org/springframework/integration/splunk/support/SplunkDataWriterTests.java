/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.integration.splunk.support;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.integration.splunk.core.Connection;
import org.springframework.integration.splunk.core.ConnectionFactory;
import org.springframework.integration.splunk.event.SplunkEvent;

import com.splunk.Args;
import com.splunk.Receiver;
import com.splunk.Service;

/**
 * @author Jarred Li
 * @since 1.0
 *
 */
public class SplunkDataWriterTests {

	private AbstractSplunkDataWriter writer;

	private static Receiver receiver = mock(Receiver.class);

	private Args args;
	@Before
	public void before() {
		args = new Args();
		writer = new SplunkSubmitWriter(new TestConnectionFactory(),args);
		writer.start();
	}

	/**
	 * Test method for {@link org.springframework.integration.splunk.support.AbstractSplunkDataWriter#write(org.springframework.integration.splunk.event.SplunkEvent)}.
	 * @throws Exception
	 */
	@Test
	public void testWrite() throws Exception {
		 

		SplunkEvent sd = new SplunkEvent("spring", "spring:example");
		sd.setCommonDesc("description");
		writer.write(sd);
		
		verify(receiver).submit(eq(args), matches(".*spring:example.*\n"));
		
		writer.stop();
	}

	public static class TestConnectionFactory implements ConnectionFactory<Service> {

		/* (non-Javadoc)
		 * @see org.springframework.integration.splunk.core.ConnectionFactory#getConnection()
		 */
		public Connection<Service> getConnection() throws Exception {
			return new TestConnection();
		}
	}

	public static class TestConnection implements Connection<Service> {

		private Service service = mock(Service.class);

		/* (non-Javadoc)
		 * @see org.springframework.integration.splunk.core.Connection#getTarget()
		 */
		public Service getTarget() {
			service.setToken("token");
			when(service.getReceiver()).thenReturn(receiver);
			return service;
		}

		/* (non-Javadoc)
		 * @see org.springframework.integration.splunk.core.Connection#close()
		 */
		public void close() {

		}

		/* (non-Javadoc)
		 * @see org.springframework.integration.splunk.core.Connection#isOpen()
		 */
		public boolean isOpen() {
			return true;
		}

	}
}
