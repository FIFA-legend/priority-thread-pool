package com.itechart.threadpool;

import com.itechart.threadpool.pool.PriorityThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PriorityThreadPoolExecutorTests {

	@Test
	public void differentPriorityInRunnableViaExecuteTest() throws InterruptedException {
		PriorityThreadPoolExecutor pool = new PriorityThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES);

		int count = 10;
		StringBuffer buffer = new StringBuffer();

		for (int i = count - 1; i >= 0; i--) {
			int priority = i;

			pool.execute(() -> {
				try {
					Thread.sleep(10);
					buffer.append(priority).append(" ");
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}, priority);
		}

		while (pool.getCompletedTaskCount() != count) {
			Thread.sleep(1000);
		}

		assertThat(buffer.toString().trim()).isEqualTo("9 0 1 2 3 4 5 6 7 8");
	}

	@Test
	public void differentPriorityInRunnableViaSubmitTest() throws InterruptedException, ExecutionException {
		PriorityThreadPoolExecutor pool = new PriorityThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES);

		int count = 10;
		List<Future<?>> futures = new ArrayList<>(count);
		StringBuffer buffer = new StringBuffer();

		for (int i = count - 1; i >= 0; i--) {
			int priority = i;

			Future<?> future = pool.submit(() -> {
				try {
					Thread.sleep(10);
					buffer.append(priority).append(" ");
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}, priority);

			futures.add(future);
		}

		for (Future<?> future : futures) {
			future.get();
		}

		assertThat(buffer.toString().trim()).isEqualTo("9 0 1 2 3 4 5 6 7 8");
	}

	@Test
	public void differentPriorityInCallableTest() throws InterruptedException, ExecutionException {
		PriorityThreadPoolExecutor pool = new PriorityThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES);

		int count = 10;
		List<Future<Integer>> futures = new ArrayList<>(count);
		StringBuffer buffer = new StringBuffer();

		for (int i = count - 1; i >= 0; i--) {
			int priority = i;

			Future<Integer> future = pool.submit(() -> {
				Thread.sleep(10);
				buffer.append(priority).append(" ");
				return priority;
			}, priority);

			futures.add(future);
		}

		for (Future<Integer> future : futures) {
			future.get();
		}
		assertThat(buffer.toString().trim()).isEqualTo("9 0 1 2 3 4 5 6 7 8");
	}

}
