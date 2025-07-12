package net.adamgoodridge.sequencetrackplayer.mock;


import org.mockito.*;

import java.io.*;

import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class ThreadSafeMockedConstruction {
	private static final ThreadLocal<MockedConstruction<File>> threadLocalMock = new ThreadLocal<>();

	public static void setupMock(String path, String[] files) {
		threadLocalMock.set(mockConstruction(File.class, (mock, context) -> {
			when(mock.list()).thenReturn(path.equals(context.arguments().get(0)) ? files : new String[0]);
		}));
	}

	public static void cleanupMock() {
		MockedConstruction<File> mock = threadLocalMock.get();
		if (mock != null) {
			mock.close();
			threadLocalMock.remove();
		}
	}
}