package org.springframework.integration.samples.mailattachments;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.samples.mailattachments.support.ZipAttachmentsDecompressor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:META-INF/spring/integration/*-context.xml")
public class StubZipEmailSenderTest {
	public static class MyCustomSemaphore extends Semaphore {
		public Thread thread;
		public volatile String pathResource;

		public MyCustomSemaphore(int permits) {
			super(permits);
		}

	}

	@Autowired
	@Qualifier("facturaIdChannel")
	MessageChannel inputChannel;

	@Test
	public void test() throws InterruptedException {
		MyCustomSemaphore semaphore = new MyCustomSemaphore(1);
		semaphore.acquire();
		semaphore.thread = Thread.currentThread();
		new Thread() {
			@Override
			public void run() {
				ZipAttachmentsDecompressor.facturaThreadsSemaphores.put(
						"2014-12-31T11:36:27", semaphore);
				Message<?> message = MessageBuilder
						.withPayload(
								new File(
										"target/out/zip/GEI960731EL631122014_1_33_113646.xml"))
						.build();
				inputChannel.send(message);
			};
		}.start();
		
		try {
			System.out.println("Waiting for...");
			semaphore.tryAcquire(13, TimeUnit.SECONDS);
//			semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Async Result: " + semaphore.pathResource);
	}

}
