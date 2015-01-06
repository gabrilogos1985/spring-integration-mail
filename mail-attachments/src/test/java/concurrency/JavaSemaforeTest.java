package concurrency;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.junit.Test;

public class JavaSemaforeTest {
	Integer counter;
	Executor executor = Executors.newFixedThreadPool(3);
	private int numberRequest = 10;
	private volatile int counterKeys;

	@Test
	public void test() {
		Map<Integer, Semaphore> mapa = new HashMap<Integer, Semaphore>();
		Map<Integer, String> mapaResources = new HashMap<Integer, String>();
		for (counter = 0; counter < numberRequest; counter++) {
			new Thread() {
				Integer keyThread;
				Semaphore threadSemaphore;
				{
					keyThread = counter;
					threadSemaphore = new Semaphore(1);
					mapa.put(counter, threadSemaphore);
				}

				@Override
				public void run() {
					retrieveValue();
					try {
						System.out.println("Waiting: " + keyThread);
						threadSemaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					assertEquals(keyThread.toString(),
							mapaResources.get(keyThread));
					System.out.println("Done: " + mapaResources.get(keyThread));
					counterKeys++;
				}

				private void retrieveValue() {
					executor.execute(new Runnable() {
						Integer keyToRetrieve;
						Semaphore semaphoreToRetrieve;
						{
							keyToRetrieve = keyThread;
							System.out.println("Retrieve lock: "
									+ keyToRetrieve);
							semaphoreToRetrieve = mapa.get(keyToRetrieve);
							try {
								semaphoreToRetrieve.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

						@Override
						public void run() {
							mapaResources.put(keyToRetrieve,
									keyToRetrieve.toString());
							try {
								Thread.currentThread().sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.out.println("Releasing: " + keyToRetrieve);
							semaphoreToRetrieve.release();
						}
					});
				}
			}.start();
		}
		while (counterKeys != numberRequest)
			;
	}
}
