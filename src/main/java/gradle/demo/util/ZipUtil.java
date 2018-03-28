package gradle.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by wangkg on 2017/8/30.
 */
@Slf4j
public class ZipUtil {
    /**
     * 日志跟踪
     */
    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);
    private static final int BUFFER = 1024;
    private static final String CHARSET = "UTF-8";

    private ZipUtil() {

    }

    /**
     * 返回压缩后的字节数组，压缩文件内只有一个名为{fileName}的文件
     * @param byteArray xml文件的字节数组(UTF-8)
     * @param fileName 压缩包内文件的名称
     * @return 压缩后的字节数组
     * @throws IOException
     */
    public static byte[] compress(byte[] byteArray, String fileName) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipArchiveOutputStream archive = new ZipArchiveOutputStream(out);
        try {
            compress(byteArray, fileName, archive);
        } catch (Exception ex) {
            logger.error("解压缩数据流失败,错误信息：" , ex);
        } finally {
            if (archive != null) {
                archive.close();
            }
        }
        return out.toByteArray();
    }

    /**
     * 返回压缩后的字节数组，压缩文件内只有一个名为compress的文件
     * @param byteArray xml文件的字节数组(UTF-8)
     * @return 压缩后的字节数组
     * @throws IOException
     */
    public static byte[] compress(byte[] byteArray) throws IOException {
        return compress(byteArray, "compress");
    }

    /**
     * @param byteArray xml文件的字节数组(UTF-8)
     * @param fileName 压缩包内文件的名称
     * @param archive 压缩文件输出流
     * @throws IOException
     */
    public static void compress(byte[] byteArray, String fileName, ZipArchiveOutputStream archive) throws IOException {
        archive.setEncoding(CHARSET);
        if (logger.isDebugEnabled()) {
            logger.debug(new String(byteArray, CHARSET));
        }
        ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
        archive.putArchiveEntry(entry);
        archive.write(byteArray);
        archive.closeArchiveEntry();
    }

    /**
     * 对压缩的字节数组进行解压缩，并返回解压后的字节数组
     * @param byteArray 压缩文件的字节数组
     * @return 解压缩后的字节数组
     * @throws IOException
     */
    public static byte[] unCompress(byte[] byteArray) {
        ByteArrayInputStream bin = new ByteArrayInputStream(byteArray);
        ZipArchiveInputStream archive = new ZipArchiveInputStream(bin);
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER);
        try {
            ArchiveEntry arc = archive.getNextEntry();
            byte[] buf = new byte[BUFFER];
            int index;
            while ((index = archive.read(buf)) != -1) {
                out.write(buf, 0, index);
            }
        } catch (IOException e) {
            logger.error("解压缩数据流失败,错误信息：" , e);
        } finally {
            try {
                archive.close();
            } catch (IOException e) {
                logger.error("输入流关闭失败：" , e);
            }
        }
        return out.toByteArray();
    }

    /**
     * 功能： 解压接口
     *
     * @param inData
     *            需要解压缩的数据流
     * @param outDetailList
     *            解压缩成功的结果数据，包括： detailID：明细ID，比如一个申报表的文件名，包含压缩时的相对目录名；
     *            detail：请求的明细内容；
     * @return 运算结果，包括压缩后的结果数据
     */

    public static void deCompression(byte[] inData, ArrayList outDetailList) {
        ByteArrayInputStream bais = new ByteArrayInputStream(inData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipInputStream zis = new ZipInputStream(bais);
        BufferedOutputStream zos = new BufferedOutputStream(baos);
        outDetailList.clear();
        ArrayList arrayList = null;

        ZipEntry ze = null;
        byte[] data = new byte[4096];
        int readCount;
        int UnzipSize = 0;
        try {
            while ((ze = zis.getNextEntry()) != null) {
                while ((readCount = zis.read(data, 0, 4096)) != -1) {
                    zos.write(data, 0, readCount);
                }
                zos.flush();
                arrayList = new ArrayList();
                arrayList.add(ze.getName());
                arrayList.add(baos.toByteArray());
                outDetailList.add(arrayList);
                UnzipSize += baos.size();

                baos.reset();
            }
        } catch (Exception ex) {
            if (logger.isErrorEnabled()) {
                logger.info("解压缩数据流失败,错误信息：" , ex);
            }
        } finally {
            try {
                baos.close();
                zis.close();
                bais.close();
                zos.close();
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.info("数据流关闭失败,错误信息：" , e);
                }
            }
        }
    }

    public static  byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

}
