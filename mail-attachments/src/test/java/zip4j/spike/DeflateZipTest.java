package zip4j.spike;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.zeroturnaround.zip.ZipUtil;

public class DeflateZipTest {

	@Test
	public void test() throws IOException {
		List<String> fileNames = new LinkedList<String>();
		InputStream is = new FileInputStream(
				"target/out/zip/AAA010101AAA12122014_1_13_93336.zip");
		ZipUtil.unpack(is, new File("target/out/zip/"), (name) -> {
			fileNames.add(name);
			return name;
		});
		System.out.println("Messages: " + fileNames);
	}

}
