/*
 *  Copyright (C) 2012-2015 Jason Fang ( ifangyucun@gmail.com )
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.github.zhitaocai.accessibilitydispatcher.demo.utils;

import android.database.Cursor;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

public final class IoUtils {
	
	public static void close(Object... objs) {
		for (Object obj : objs) {
			close(obj);
		}
	}
	
	public static void closeOS(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	public static void closeIS(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	public static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	public static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	public static void closeFile(RandomAccessFile file) {
		if (file != null) {
			try {
				file.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	public static void closeSocket(Socket socket) {
		if (socket != null) {
			if (socket.isConnected()) {
				try {
					socket.close();
				} catch (IOException e) {
					DLog.e(e);
				}
			}
		}
	}
	
	public static void closeServerSocket(ServerSocket socket) {
		if (socket != null && !socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	public static void closeProcess(Process process) {
		if (process != null) {
			process.destroy();
		}
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	
	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				DLog.e(e);
			}
		}
	}
	
	private static void close(Object obj) {
		if (obj == null) {
			return;
		}
		
		if (obj instanceof InputStream) {
			closeIS((InputStream) obj);
		} else if (obj instanceof OutputStream) {
			closeOS((OutputStream) obj);
		} else if (obj instanceof Writer) {
			closeWriter((Writer) obj);
		} else if (obj instanceof Reader) {
			closeReader((Reader) obj);
		} else if (obj instanceof RandomAccessFile) {
			closeFile((RandomAccessFile) obj);
		} else if (obj instanceof Socket) {
			closeSocket((Socket) obj);
		} else if (obj instanceof ServerSocket) {
			closeServerSocket((ServerSocket) obj);
		} else if (obj instanceof Process) {
			closeProcess((Process) obj);
		} else if (obj instanceof Cursor) {
			closeCursor((Cursor) obj);
		} else if (obj instanceof Closeable) {
			close((Closeable) obj);
		} else {
			DLog.e("不支持的关闭!");
			throw new RuntimeException("不支持的关闭!");
		}
	}
	
	public static InputStream getPhoneLogs() throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder("logcat", "-d");
		builder.redirectErrorStream(true);
		Process process = builder.start();
		//process.waitFor();
		return process.getInputStream();
	}
	
	public static void copyFile(File src, File dest) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
		
	}
	
	@SuppressWarnings("TryFinallyCanBeTryWithResources")
	public static void writeByteArrayToFile(File file, byte[] bytes) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);
		try {
			fout.write(bytes);
		} finally {
			fout.close();
		}
	}
	
	/**
	 * Get string from stream!
	 */
	public static String getStringFromStream(InputStream in) {
		if (in == null) {
			return null;
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in), 16 * 1024);
		StringBuilder text = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				text.append(line);
				text.append("\n");
			}
		} catch (IOException e) {
			DLog.e(e);
		} finally {
			close(reader, in);
		}
		return text.toString();
	}
	
	private IoUtils() {/*Do not new me*/}
}
