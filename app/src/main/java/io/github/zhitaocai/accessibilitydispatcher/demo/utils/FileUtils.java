package io.github.zhitaocai.accessibilitydispatcher.demo.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 文件操作类
 *
 * @author jen
 */
public class FileUtils {
	
	private final static int BUFFER = 1024;
	
	/**
	 * 改变原始文件的权限
	 *
	 * @param file
	 * @param destFilePermission
	 *
	 * @return
	 */
	public static boolean chmod(File file, String destFilePermission) {
		try {
			
			if (file == null) {
				return false;
			}
			
			if (!file.exists()) {
				return false;
			}
			
			if (destFilePermission != null) {
				DLog.i("chmod file: %s permission is %s", file.getAbsolutePath(), destFilePermission);
				
				StringBuilder sb = new StringBuilder(100);
				sb.append("chmod ").append(destFilePermission).append(" ").append(file.getAbsolutePath());
				String cmd = sb.toString();
				Runtime.getRuntime().exec(cmd);
				
				DLog.i("chmod cmd is:[%s]", destFilePermission);
				return true;
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
		
	}
	
	/**
	 * 移动文件，成功之后会删除原始文件
	 *
	 * @param srcFile
	 * @param destFile
	 *
	 * @return
	 */
	public static boolean mv(File srcFile, File destFile) {
		try {
			
			if (srcFile == null || destFile == null) {
				DLog.i("move file failed: src file or dest file is null");
				return false;
			}
			
			if (!srcFile.exists()) {
				DLog.i("move file failed: src file is exists == false");
				return false;
			}
			
			if (srcFile.renameTo(destFile)) {
				DLog.i("move file success: srcFile.renameTo destFile");
				return true;
			}
			
			if (cp(srcFile, destFile)) {
				DLog.i("move file: copy file success");
				
				try {
					// 删除原始文件
					if (srcFile.delete()) {
						DLog.i("move file: delete src file success");
					} else {
						DLog.i("move file: delete src file failed");
					}
				} catch (Throwable e) {
					DLog.e(e);
				}
				return true;
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		
		return false;
	}
	
	/**
	 * 复制文件
	 *
	 * @param srcFile
	 * @param destFile
	 *
	 * @return
	 */
	public static boolean cp(File srcFile, File destFile) {
		
		FileOutputStream fos = null;
		FileInputStream fis = null;
		
		long startTime = System.currentTimeMillis();
		long fileLen = 0;
		String fileNameSrc = null;
		String fileNameDest = null;
		try {
			if (srcFile == null) {
				return false;
			}
			
			if (!srcFile.exists()) {
				return false;
			}
			
			if (destFile == null) {
				return false;
			}
			try {
				
				fileLen = srcFile.length();
				fileNameSrc = srcFile.getAbsolutePath();
				fileNameDest = destFile.getAbsolutePath();
				
			} catch (Throwable e) {
				DLog.e(e);
			}
			
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			
			byte[] buff = new byte[1024];
			int len = 0;
			
			while ((len = fis.read(buff)) > 0) {
				fos.write(buff, 0, len);
			}
			
			fos.flush();
			fos.close();
			fos = null;
			return true;
			
		} catch (Throwable e) {
			DLog.e(e);
		} finally {
			IoUtils.close(fos);
			IoUtils.close(fis);
			long nt = System.currentTimeMillis();
			long span = nt - startTime;
			DLog.i("copy file from [%s] to [%s] , length is [%d] B , cost [%d] ms", fileNameSrc, fileNameDest, fileLen, span);
		}
		return false;
	}
	
	/**
	 * 判断assets中是否存在指定的文件
	 *
	 * @param context
	 * @param fileName
	 *
	 * @return
	 */
	public static boolean isFileExistAssets(Context context, String fileName) {
		try {
			if (context == null || TextUtils.isEmpty(fileName)) {
				return false;
			}
			// faster than getting the whole asset's folder as list, and check the containment
			InputStream is = context.getAssets().open(fileName);
			if (is != null) {
				IoUtils.close(is);
				return true;
			} else {
				return false;
			}
		} catch (Throwable e) {
			DLog.e(e);
			return false;
		}
	}
	
	/**
	 * （请使用线程执行）从Assets中复制文件
	 *
	 * @param context
	 * @param srcFileName
	 * @param destFile
	 *
	 * @return
	 */
	public static boolean cpFromAssets(Context context, String srcFileName, File destFile) {
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			if (context == null || srcFileName == null || destFile == null) {
				return false;
			}
			
			inputStream = context.getAssets().open(srcFileName);
			outputStream = new FileOutputStream(destFile);
			int len = 0;
			byte[] buff = new byte[1024];
			while ((len = inputStream.read(buff)) > 0) {
				outputStream.write(buff, 0, len);
			}
			
			outputStream.flush();
			outputStream.close();
			outputStream = null;
			
			return true;
			
		} catch (Throwable e) {
			DLog.e(e);
		} finally {
			IoUtils.close(outputStream);
			IoUtils.close(inputStream);
		}
		return false;
	}
	
	/**
	 * （同步）支持删除文件或者文件夹，使用时请注意启用线程
	 * <p/>
	 * 使用时<strong>请注意启用线程</strong>
	 *
	 * @param filePath 要删除的文件或者目录
	 *
	 * @return true or false
	 */
	public static boolean delete(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return false;
		}
		return delete(new File(filePath));
	}
	
	/**
	 * （同步）支持删除文件或者文件夹，使用时请注意启用线程
	 * <p/>
	 * 使用时<strong>请注意启用线程</strong>
	 *
	 * @param file 要删除的文件或者目录
	 *
	 * @return true or false
	 */
	public static boolean delete(File file) {
		try {
			if (file == null) {
				return false;
			}
			if (file.exists()) {
				if (file.isFile()) {
					boolean isSuccess = file.delete();
					if (isSuccess) {
						DLog.i("删除成功： %s", file.getAbsolutePath());
					} else {
						DLog.e("删除失败： %s", file.getAbsolutePath());
					}
					return isSuccess;
				} else if (file.isDirectory()) {
					for (File f : file.listFiles()) {
						if (!delete(f)) {
							return false;
						}
					}
					boolean isSuccess = file.delete();
					if (isSuccess) {
						DLog.i("删除成功： %s", file.getAbsolutePath());
					} else {
						DLog.e("删除失败： %s", file.getAbsolutePath());
					}
					return isSuccess;
				}
			} else {
				// 因为最终目的是令该文件不存在，所以如果文件一开始就不存在，那么也就意味着删除成功
				return true;
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 获取指定的路径的文件，如果没有会创建(支持自动补全所有父目录)
	 *
	 * @param path 文件路径
	 */
	public final static File getVaildFile(String path) {
		return getVaildFile(new File(path));
	}
	
	/**
	 * 获取指定的路径的文件，如果没有会创建(支持自动补全所有父目录)
	 *
	 * @param file 文件
	 */
	public final static File getVaildFile(File file) {
		try {
			if (file == null) {
				return null;
			}
			if (file.exists()) {
				//  如果文件存在
				if (file.isFile()) {
					return file;
				}
				if (file.isDirectory()) {
					return null;
				}
			} else {
				// 如果文件不存在，则创建
				
				// 检查是否存在父目录
				// 如果不存在父目录的话, 自动补全所有的根目录,然后创建
				if (!file.getParentFile().exists()) {
					
					DLog.w("当前文件[%s]不存在父目录[%s]，将补全", file.getAbsolutePath(), file.getParent());
					boolean isMkdirsSuccess = file.getParentFile().mkdirs();
					
					if (!isMkdirsSuccess) {
						DLog.w("补全父目录[%s]失败", file.getParent());
						return null;
					} else {
						if (!file.getParentFile().exists()) {
							DLog.w("补全父目录[%s]失败", file.getParent());
							return null;
						}
						DLog.i("补全父目录[%s]成功", file.getParent());
					}
				}
				boolean isSuccess = file.createNewFile();
				if (isSuccess) {
					return file;
				} else {
					return null;
				}
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	@Deprecated
	public static boolean copyStream(InputStream inputStream, OutputStream outputStream) {
		try {
			
			if (inputStream == null) {
				return false;
			}
			
			if (outputStream == null) {
				return false;
			}
			
			int len = 0;
			
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
}
