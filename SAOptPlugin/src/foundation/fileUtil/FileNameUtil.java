package foundation.fileUtil;


/**
 * @author nyc 文件名工具类
 */
public class FileNameUtil {

	/**
	 * 
	 * @param fileFullName
	 *            :带完整路径的文件名
	 * @return：一个字符串数组，第0个元素是路径名，第1个元素是文件名，第2个元素是扩展名
	 */
	public static String[] getFileNameInfo(String fileFullName) {
		String[] names = new String[3];
		String internalNames[] = fileFullName.split("\\\\|/");
		String directorName = "";
		String fileName = "";
		String fileExtensionName = "";
		int len = internalNames.length;
		for (int j = 0; j < len; j++) {
			if (j < len - 1) {
				if (j == 0) {
					directorName += internalNames[j];
				} else {
					directorName += "\\" + internalNames[j];
				}
			} else {
				directorName += "\\";
				String tempStr1 = internalNames[j];
				String tempStrs[] = tempStr1.split("\\.");
				fileName += tempStrs[0];
				fileExtensionName = tempStrs[1];
			}
		}
		names[0] = directorName;
		names[1] = fileName;
		names[2] = fileExtensionName;
		return names;
	}

	/**
	 * 返回指定文件名中的id号
	 * 
	 * @param fileFullName
	 *            :带完整路径的文件名，形如a/b/c/xxxx__2-2-3.yyy
	 * @return：文件id号的字符串，如"2-2-3"
	 */
	public static String getSufix(String fileFullName) {
		String[] internalNames = getFileNameInfo(fileFullName);
		String fileNameNoPath = internalNames[1];
		int pos = fileNameNoPath.indexOf("__");
		String fileSufixStr = "";
		if (pos != -1) {
			int len = fileNameNoPath.length();
			fileSufixStr = fileNameNoPath.substring(pos+2, len);
		}
		return fileSufixStr;
	}

	/**
	 * @param fileName
	 *            ：带完整路径的文件名
	 * @return：去除以文件名中以“__”开始的部分，
	 *          例如：a/b/c/xxxx__2-3.yyy,
	 *          将返回a/b/c/xxxx.yyy
	 */
	public static String delSufix(String fileFullName) {
		String[] internalNames = getFileNameInfo(fileFullName);
		String fileNameWithoutPath = internalNames[1];
		int pos = fileNameWithoutPath.indexOf("__");
		String flNmWithoutSufix = internalNames[0] + fileNameWithoutPath;
		if (pos > -1) {
			flNmWithoutSufix = internalNames[0]
					+ fileNameWithoutPath.substring(0, pos);
		}
		flNmWithoutSufix += "." + internalNames[2];
		return flNmWithoutSufix;

	}

	/*
	 * 获取项目的根目录
	 */
	public static String getPrjPath() {
		// 获取本项目的绝对路径
		StringBuffer prjAbsPathStrBf = new StringBuffer(Thread.currentThread()
				.getContextClassLoader().getResource("").getPath());
		// 去除起始的/符号
		prjAbsPathStrBf.replace(0, 1, "");
		// 去除/bin符号
		int pos = prjAbsPathStrBf.indexOf("/bin");
		String absPathName = prjAbsPathStrBf.replace(pos, pos + 4, "") + "";
		absPathName=absPathName.replace("/", "\\");
		return absPathName;
	}
}
