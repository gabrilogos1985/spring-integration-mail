package org.springframework.integration.samples.mailattachments.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.integration.samples.mailattachments.Main;
import org.springframework.integration.samples.mailattachments.StubZipEmailSenderTest.MyCustomSemaphore;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.zeroturnaround.zip.ZipUtil;

public class ZipAttachmentsDecompressor {
	public static ConcurrentHashMap<String, MyCustomSemaphore> facturaThreadsSemaphores = new ConcurrentHashMap<String, MyCustomSemaphore>();

	public Message<?> decompressFiles(Message<?> messageZip)
			throws FileNotFoundException {
		System.out.println(messageZip.getPayload().getClass());
		List<String> fileNames = new LinkedList<String>();
		String facturasDirectory = "target/out/zip/";
		ZipUtil.unpack(
				new ByteArrayInputStream((byte[]) messageZip.getPayload()),
				new File(facturasDirectory), (name) -> {
					fileNames.add(name);
					return name;
				});
		System.out.println("Facturas: " + fileNames);
		String xmlFilename = fileNames.stream()
				.filter((filename -> filename.endsWith(".xml"))).findFirst()
				.get();
		Message<?> xmlMessage = MessageBuilder.withPayload(
				new File(facturasDirectory, xmlFilename)).build();
		return xmlMessage;
	}

	public void allocateBill(Message<?> factura) throws InterruptedException {
		System.out.println("Message: " + factura);
		MyCustomSemaphore notifier = ZipAttachmentsDecompressor.facturaThreadsSemaphores
				.get(factura.getHeaders().get("facturaId"));
		System.out.println("Doing task...");		
		Thread.currentThread().sleep(17000);
		notifier.pathResource = ((File) factura.getPayload()).getAbsolutePath();
		notifier.release();
	}

	public static void waitToQuit() {
		final Scanner scanner = new Scanner(System.in);

		Main.LOGGER.info(Main.HORIZONTAL_LINE + "\n"
				+ "\n    Please press 'q + Enter' to quit the application.    "
				+ "\n" + Main.HORIZONTAL_LINE);

		while (true) {

			final String input = scanner.nextLine();

			if ("q".equals(input.trim())) {
				break;
			}

		}

		Main.LOGGER.info("Exiting application...bye.");

		System.exit(0);
	}
}
