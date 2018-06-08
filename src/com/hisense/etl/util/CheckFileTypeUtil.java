package com.hisense.etl.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class CheckFileTypeUtil {
    // 缓存文件头信息-文件头信息
    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
    static {
        // images
        mFileTypes.put("jpg", "FFD8FF");
        mFileTypes.put("png", "89504E47");
        mFileTypes.put("gif", "47494638");
        mFileTypes.put("tif", "49492A00");
        mFileTypes.put("bmp", "424D");
        //
        mFileTypes.put("dwg", "41433130"); // CAD
        mFileTypes.put("psd", "38425053");
        mFileTypes.put("rtf", "7B5C727466"); // 日记本
        mFileTypes.put("xml", "3C3F786D6C");
        mFileTypes.put("html", "68746D6C3E");
        mFileTypes.put("eml", "44656C69766572792D646174653A"); // 邮件
        mFileTypes.put("xls", "D0CF11E0");//excel2003版本文件
//        mFileTypes.put("doc", "D0CF11E0");
        mFileTypes.put("mdb", "5374616E64617264204A");
        mFileTypes.put("ps", "252150532D41646F6265");
        mFileTypes.put("pdf", "255044462D312E");
//        mFileTypes.put("docx", "504B0304");
        mFileTypes.put("xlsx", "504B0304");//excel2007以上版本文件
        mFileTypes.put("rar", "52617221");
        mFileTypes.put("wav", "57415645");
        mFileTypes.put("avi", "41564920");
        mFileTypes.put("rm", "2E524D46");
        mFileTypes.put("mpg", "000001BA");
        mFileTypes.put("mpg", "000001B3");
        mFileTypes.put("mov", "6D6F6F76");
        mFileTypes.put("asf", "3026B2758E66CF11");
        mFileTypes.put("mid", "4D546864");
        mFileTypes.put("gz", "1F8B08");
    }

    /**
     *
     * 方法描述：根据文件路径获取文件头信息
     * @param filePath 文件路径
     * @return 文件头信息
     */
//    public static String getFileType(String filePath) {
//        return mFileTypes.get(getFileHeader(filePath));
//    }
    public static boolean belong2Excel(String filePath) throws IllegalArgumentException{
        return belong2FileType("xls",filePath) || belong2FileType("xlsx",filePath);
    }

    public static boolean belong2FileType(final String fileType,String filePath) throws IllegalArgumentException{
        String suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        if(fileType.equalsIgnoreCase(suffix)){
            return true;
        }
        return false;
    }

//    public static boolean belong2FileType(final String fileType,String filePath) throws IllegalArgumentException{
//        if(fileType==null || (!mFileTypes.keySet().contains(fileType)))
//            throw new IllegalArgumentException("illegal argument fileType:"+fileType);
//        String tmp=getFileHeader(filePath);
//        if(tmp!=null && mFileTypes.get(fileType).contains(tmp)) return true;
//        return false;
//    }

    /**
     *
     * 方法描述：根据文件路径获取文件头信息
     * @param filePath 文件路径
     * @return 文件头信息
     */
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[4];
            /*
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     *
     * 方法描述：将要读取文件头信息的文件的byte数组转换成string类型表示
     * @param src 要读取文件头信息的文件的byte数组
     * @return   文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println(builder.toString());
        return builder.toString();
    }
    /**
     *
     * 方法描述：测试
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//        final String fileType = getFileType("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\自来水信息\\水卡档案（西区）7.20上报\\水卡档案（海王所）.xls");
//        final String fileType = getFileType("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\高校新生信息2017\\滨海学院2017级新生信息.xlsx");
//        final String fileType = getFileType("C:\\Users\\lv.Hisense-PC\\AppData\\Roaming\\feiq\\Recv Files\\2017社会信息采集\\7月\\开发区分局7月份社会信息\\九小场所信息.xls");
//        System.out.println(fileType);
    }
}
