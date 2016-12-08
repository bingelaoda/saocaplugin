package foundation.fileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * @author nyc 用于读写文本文件的工具类
 */
public class TxtFileUtil {

	/**
	 * 读TXT文件内容
	 * 
	 * @param txtFile
	 * @return
	 */
	public static String readTxtFile(File txtFile) throws Exception {
		String result = "";
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(txtFile);
			bufferedReader = new BufferedReader(fileReader);
			try {
				String read = null;
				while ((read = bufferedReader.readLine()) != null) {
					if (!read.equals("\r\n")) {
						result = result + read + "\r\n";
					} else {
						result = result + read;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return result;
	}

	/**
	 * 文件内容比较
	 * 
	 * @param srcFile
	 *            ：源文件
	 * @param desFile
	 *            ：目标文件
	 * @return：如果源文件与目标文件完全相同，返回真，否则返回假
	 */
	public static boolean compareFiles(File srcFile, File desFile) {

		return getFileMD5(srcFile).equals(getFileMD5(desFile));

	}

	/**
	 * 获取文件的MD5值
	 * 
	 * @param file
	 *            ：文件
	 * @return：文件对应MD5值
	 */
	private static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}

	/**
	 * 追加到内容到原文件尾部
	 * 
	 * @param content
	 *            ：字符内容
	 * @param txtFile
	 *            ：追加到文本文件 @rturn：如果追回成功返回真，原文件内容发生变化；否则返回假。
	 */

	public static boolean appendToFile(String content, File txtFile) {
		boolean append = false;
		boolean result = false;
		try {
			if (txtFile.exists())
				append = true;
			FileWriter fw = new FileWriter(txtFile, append);// 同时创建新文件
			// 创建字符输出流对象
			BufferedWriter bf = new BufferedWriter(fw);
			// 创建缓冲字符输出流对象
			bf.append(content);
			result = true;
			bf.flush();
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 将源文件内容追加到目标文件中
	 * 
	 * @param srcfile
	 *            ：源文件
	 * @param destfile
	 *            ：目标文件 @return：如果追回成功返回真，目标文件内容发生变化；否则返回假。
	 */
	public static boolean appendFiles(File srcfile, File destfile) {
		boolean append = false;
		boolean result = false;
		String content = "";

		if (srcfile.exists() && destfile.exists())
			append = true;
		try {
			content = readTxtFile(srcfile);
			FileWriter fw = new FileWriter(destfile, append);// 同时创建新文件
			// 创建字符输出流对象
			BufferedWriter bf = new BufferedWriter(fw);
			// 创建缓冲字符输出流对象
			bf.append(content);
			result = true;
			bf.flush();
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 将字符内容追加到目标文件的最后一行的下一行
	 * 
	 * @param content
	 *            ：字符串内容
	 * @param destfile
	 *            ：目标文件 @return：如果追回成功返回真，目标文件内容发生变化；否则返回假。
	 */
	public static boolean appendlastToFile(String content, File destfile) {
		boolean result = false;
		try {
			String lastStr = "\n" + content;
			FileWriter writer = null;
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(destfile, true);
			writer.write(lastStr);
			result = true;
			writer.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 获取文件的最后一行内容
	 * 
	 * @param srcfile
	 *            ：源文件
	 * @return：返回源文件最后一行的内容
	 */
	public static String getLastInFile(File srcfile) {
		String result = "";
		String lastStr = "";
		try {
			FileReader fr = new FileReader(srcfile);
			BufferedReader br = new BufferedReader(fr);
			String str = null;
			while ((str = br.readLine()) != null) {
				lastStr = str;
			}
			br.close();
			fr.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		String facts[] = lastStr.split(",");
		result = facts[0];
		return result;

	}

	/**
	 * 判断指定内容在文件中是否存在
	 * 
	 * @param content
	 * @param destfile
	 * @return
	 */
	public static boolean isExistInFile(String content, File destfile) {
		boolean result = false;
		try {
			FileReader fr = new FileReader(destfile);
			BufferedReader br = new BufferedReader(fr);
			String str = null;
			while ((str = br.readLine()) != null) {
				if (str.contains(content)) {
					result = true;
					break;
				}
			}
			br.close();
			fr.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 将内容写到TXT文件，覆盖原来内容
	 * 
	 * @param content
	 *            ：写入的字符串
	 * @param txtFile
	 *            ：文本文件 @return： 是否写入成功
	 * @throws Exception
	 *             ：抛出异常
	 */
	public static boolean writeTxtFile(String content, File txtFile) throws Exception {
		// RandomAccessFile mm = null;
		boolean flag = false;
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(txtFile);
			if (txtFile.exists()) {
				txtFile.delete();
			}

			outStream.write((new String("")).getBytes());
			outStream.flush();
			outStream.write(content.getBytes("utf8"));
			outStream.close();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 复制文件
	 * 
	 * @param frmFile
	 *            ：源文件
	 * @param toFile
	 *            ：目标文件 若目标文件存在，原内容将覆盖
	 */
	public static void copyFile(File frmFile, File toFile) {

		String content = "";
		try {
			content = readTxtFile(frmFile);
			if (toFile.exists()) {
				toFile.delete();
			}
			writeTxtFile(content, toFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 序列化对象到文本文件，若原文件存在则将被覆盖
	 * 
	 * @param serialFileName:对象序列化文本文件的全路径名
	 * @param obj：需序列化的对象
	 */
	public static void serialize(String serialFileName, Object obj) {
		if (obj != null) {
			try {
				File txtFile = new File(serialFileName);
				if (txtFile.exists()) {
					txtFile.delete();
				}
				FileOutputStream fileOut = new FileOutputStream(txtFile);
				ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
				objOut.writeObject(obj);
				objOut.flush();
				objOut.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从文件中反序列化出对象
	 * 
	 * @param serialFileName：对象序列化文本文件的全路径名
	 * @return:若反序列化失败返回null,否则返回反序列化出的对象
	 */
	public static Object deSerialize(String serialFileName) {
		File serialFile = new File(serialFileName);
		Object obj = null;
		if (serialFile.exists()) {
			try {
				FileInputStream fleInStream = new FileInputStream(serialFile);
				ObjectInputStream objInput = new ObjectInputStream(fleInStream);
				obj = objInput.readObject();
				objInput.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}


	public static void deleteFile(String folder, String ext) {

		GenericExtFilter filter = new GenericExtFilter(ext);
		File dir = new File(folder);

		// list out all the file name with .ext extension
		String[] list = dir.list(filter);

		if (list.length == 0)
			return;

		File fileDelete;

		for (String file : list) {
			String temp = new StringBuffer(folder).append(File.separator).append(file).toString();
			fileDelete = new File(temp);
			fileDelete.delete();
			
		}
	}

	public static ArrayList<String> findFileNms(String folder, String ext) {

		ArrayList<String> fileNames = new ArrayList<String>();
		GenericExtFilter filter = new GenericExtFilter(ext);
		File dir = new File(folder);
		File[] files = dir.listFiles();
		for (File f : files) {
			String fileNm = f.getName();
			if (filter.accept(dir, fileNm)) {
				fileNames.add(fileNm);
			}

		}
		return fileNames;
	}

	// inner class, generic extension filter
	public static class GenericExtFilter implements FilenameFilter {

		private String ext;

		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return (name.endsWith(ext));
		}
	}

	
	
	/**
	 * 复制一个文件夹所有内容到另一个文件夹
	 * @param oldPath
	 * @param newPath
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}
	
	/**
	 * 删除指定文件夹下所有文件，不包括文件夹
	 * 
	 * @param path
	 *            文件夹完整路径
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
		}
	}
}
