package com.kore.jigsaw.plugin.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.util.jar.JarEntry
import java.util.jar.JarFile

public class Decompression {

    protected static Log log = LogFactory.getLog(Decompression.class);

    @SuppressWarnings("resource")
    public static void uncompress(File jarFile, File tarDir) throws IOException {
        JarFile jfInst = new JarFile(jarFile);
        Enumeration<JarEntry> enumEntry = jfInst.entries();
        while (enumEntry.hasMoreElements()) {
            JarEntry jarEntry = enumEntry.nextElement();
            File tarFile = new File(tarDir, jarEntry.getName());
            if(jarEntry.getName().contains("META-INF")){
                File miFile = new File(tarDir, "META-INF");
                if(!miFile.exists()){
                    miFile.mkdirs();
                }

            }
            makeFile(jarEntry, tarFile);
            if (jarEntry.isDirectory()) {
                continue;
            }
            FileChannel fileChannel = new FileOutputStream(tarFile).getChannel();
            InputStream ins = jfInst.getInputStream(jarEntry);
            transferStream(ins, fileChannel);
        }
    }

    /**
     * 流交换操作
     * @param ins 输入流
     * @param channel 输出流
     */
    private static void transferStream(InputStream ins, FileChannel channel) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);
        ReadableByteChannel rbcInst = Channels.newChannel(ins);
        try {
            while (-1 != (rbcInst.read(byteBuffer))) {
                byteBuffer.flip();
                channel.write(byteBuffer);
                byteBuffer.clear();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (null != rbcInst) {
                try {
                    rbcInst.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 打印jar文件内容信息
     * @param file jar文件
     */
    public static void printJarEntry(File file) {
        JarFile jfInst = null;;
        try {
            jfInst = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration enumEntry = jfInst.entries();
        while (enumEntry.hasMoreElements()) {
            log.info((enumEntry.nextElement()));
        }
    }

    /**
     * 创建文件
     * @param jarEntry jar实体
     * @param fileInst 文件实体
     * @throws IOException 抛出异常
     */
    public static void makeFile(JarEntry jarEntry, File fileInst) {
        if (!fileInst.exists()) {
            if (jarEntry.isDirectory()) {
                fileInst.mkdirs();
            } else {
                try {
//                    fileInst.createNewFile();
                    fileInst.getParentFile().mkdirs()
                    log.info("解压文件：".concat(fileInst.getPath()));
                } catch (IOException e) {
                    log.error("创建文件失败>>>".concat(fileInst.getPath()));
                }
            }
        }
    }
}