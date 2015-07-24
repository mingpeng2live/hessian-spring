package com.yunat.base.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.Deflater;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {


	public static void main(String[] args) throws IOException {
//		 unZip("f:/test.zip" , "f:/");
		zip("F:/gitreposity", "F:/hunteron.zip");
	}

	public static void zip(String sourceDir, String zipFile) throws IOException{
		zip(new File(sourceDir), zipFile);
	}

	/**
	 * 功能：把 sourceDir 目录下的所有文件进行 zip 格式的压缩，保存为指定 zip 文件 create date:2009- 6- 9
	 * author:Administrator
	 *
	 * @param sourceDir
	 *            E:// 我的备份
	 * @param zipFile
	 *            格式： E://stu //zipFile.zip 注意：加入 zipFile 我们传入的字符串值是 ：
	 *            "E://stu //" 或者 "E://stu " 如果 E 盘已经存在 stu 这个文件夹的话，那么就会出现
	 *            java.io.FileNotFoundException: E:/stu ( 拒绝访问。 )
	 *            这个异常，所以要注意正确传参调用本函数哦
	 *
	 */
	private static void zip(File sourceFile, String outZipFile) {
		OutputStream os;
		try {
			os = new FileOutputStream(outZipFile);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			ZipOutputStream zos = new ZipOutputStream(bos);
			String basePath = sourceFile.isDirectory() ? sourceFile.getPath() : sourceFile.getParent();
			zipFile(sourceFile, basePath, zos);
			zos.closeEntry();
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	static Pattern pattern = Pattern.compile("(Servers|RemoteSystemsTempFiles|.amateras|.metadata|.settings|.git|.classpath|.project|.gitignore|target)$");

	/**
	 *
	 * create date:2009- 6- 9 author:Administrator
	 *
	 * @param source
	 * @param basePath
	 * @param zos
	 * @throws IOException
	 */
	private static void zipFile(File source, String basePath, ZipOutputStream zos) {
		File[] files = new File[0];
		zos.setLevel(Deflater.BEST_COMPRESSION);
		if (source.isDirectory()) {
			files = source.listFiles();
		} else {
			files = new File[1];
			files[0] = source;
		}

		String pathName;
		byte[] buf = new byte[1024];
		int length = 0;
		try {
			// 大于3是因为文件在跟目录下的情况
			int index = basePath.length()>3 ? basePath.length() + 1 : basePath.length();
			for (File file : files) {
				if (!pattern.matcher(file.getPath()).find()) {
					pathName = file.getPath().substring(index);
					if (file.isDirectory()) {
						zos.putNextEntry(new ZipEntry(pathName + "/"));
						zipFile(file, basePath, zos);
					} else {
						InputStream is = new FileInputStream(file);
						BufferedInputStream bis = new BufferedInputStream(is);
						zos.putNextEntry(new ZipEntry(pathName));
						while ((length = bis.read(buf)) > 0) {
							zos.write(buf, 0, length);
						}
						bis.close();
						is.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void unZip(String zipfile, String destDir) throws IOException{
		unZip(new ZipFile(new File(zipfile), "UTF-8"), destDir);
	}

	public static void unZip(File file, String destDir) throws IOException{
		unZip(new ZipFile(file, "UTF-8"), destDir);
	}


	/**
	 * 解压 zip 文件，注意不能解压 rar 文件哦，只能解压 zip 文件 解压 rar 文件 会出现 java.io.IOException:
	 * Negative seek offset 异常 create date:2009- 6- 9 author:Administrator
	 *
	 * @param zipfile
	 *            zip 文件，注意要是正宗的 zip 文件哦，不能是把 rar 的直接改为 zip 这样会出现
	 *            java.io.IOException: Negative seek offset 异常
	 * @param destDir
	 * @throws IOException
	 */
	private static void unZip(ZipFile zipFile, String destDir) {
		destDir = destDir.endsWith("//") ? destDir : destDir + "//";
		byte b[] = new byte[1024];
		int length;
		try {
			Enumeration<?> enumeration = zipFile.getEntries();
			ZipEntry zipEntry = null;
			while (enumeration.hasMoreElements()) {
				zipEntry = (ZipEntry) enumeration.nextElement();
				File loadFile = new File(destDir + zipEntry.getName());
				if (zipEntry.isDirectory()) {
					// 这段都可以不要，因为每次都貌似从最底层开始遍历的
					loadFile.mkdirs();
				} else {
					if (!loadFile.getParentFile().exists())
						loadFile.getParentFile().mkdirs();

					OutputStream outputStream = new FileOutputStream(loadFile);
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					while ((length = inputStream.read(b)) > 0)
						outputStream.write(b, 0, length);
					outputStream.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



//	private void unZipBy(String zipFilePath, String outPath)throws Exception{
//		 BufferedOutputStream dest = null;
//        BufferedInputStream is = null;
//        ZipEntry entry;
//        java.util.zip.ZipFile zipfile = new java.util.zip.ZipFile(zipFilePath);
//        Enumeration<?> e = zipfile.entries();
//        while(e.hasMoreElements()) {
//           entry = (ZipEntry) e.nextElement();
//           System.out.println("Extracting: " +entry);
//           is = new BufferedInputStream (zipfile.getInputStream(entry));
//           int count;
//           byte data[] = new byte[BUFFER];
//           FileOutputStream fos = new FileOutputStream(outPath+entry.getName());
//           dest = new BufferedOutputStream(fos, BUFFER);
//           while ((count = is.read(data, 0, BUFFER)) != -1) {
//              dest.write(data, 0, count);
//           }
//           dest.flush();
//           dest.close();
//           is.close();
//        }
//	}
//
//	private void zipBy(String outname, String inputFilePath)throws Exception{
//		BufferedInputStream origin = null;
//	    FileOutputStream dest = new  FileOutputStream(outname);
//	    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
//	    //out.setMethod(ZipOutputStream.DEFLATED);
//	    byte data[] = new byte[BUFFER];
//	    // get a list of files from current directory
//	    File f = new File(inputFilePath);
//
//	    if (f.isDirectory()){
//		    String files[] = f.list();
//		    for (int i=0; i < files.length; i++) {
//		       System.out.println("Adding: "+files[i]);
//		       FileInputStream fi = new FileInputStream(inputFilePath+"\\"+files[i]);
//		       origin = new BufferedInputStream(fi, BUFFER);
//		       ZipEntry entry = new ZipEntry(inputFilePath.substring(4)+"\\"+files[i]);
//		       out.putNextEntry(entry);
//		       int count;
//		       while((count = origin.read(data, 0, BUFFER)) != -1) {
//		          out.write(data, 0, count);
//		       }
//		       origin.close();
//		    }
//	    }else{
//			FileInputStream fi = new FileInputStream(f.getAbsolutePath());
//			origin = new BufferedInputStream(fi, BUFFER);
//			ZipEntry entry = new ZipEntry(f.getName());
//			out.putNextEntry(entry);
//			int count;
//			while((count = origin.read(data, 0, BUFFER)) != -1) {
//			   out.write(data, 0, count);
//			}
//			origin.close();
//	    }
//	    out.close();
//	}
}
