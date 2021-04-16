package com.kore.jigsaw.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.kore.jigsaw.plugin.asm.JRouterVisitor
import com.kore.jigsaw.plugin.asm.JigsawVisitor
import com.kore.jigsaw.plugin.asm.MainAppVisitor
import com.kore.jigsaw.plugin.asm.TraverseClassVisitor
import com.kore.jigsaw.plugin.asm.VisitorCallback
import com.kore.jigsaw.plugin.bean.PriorityModuleApp
import com.kore.jigsaw.plugin.util.Compressor
import com.kore.jigsaw.plugin.util.Decompression
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

/**
 * @author koreq* @date 2021-04-09
 * @description 处理字节码，将 @ModuleApp 的实例按优先级注入到 com.kore.jigsaw.core.Jigsaw 类中，
 * 并在 @MainApp 类中调用各个模块的 Application 的对应方法
 */
class JigsawTransform extends Transform {
    def mProject
    def mMainAppMap = [:]               // 存放 @MainApp 的注解类 Map<InputFile : OutputFile>
    def mModuleAppList = []             // 存放 @ModuleApp 的注解类 List<PriorityModuleApp> 用于排序
    def mJRouterTableList = []          // 存放 @RouteTable 的注解类
    def mJigsawMap = [:]                // 存放 jigsaw 类的位置 Map<InputFile : OutputFile>
    def mJRouterMap = [:]               // 存放 jRouter 类的位置 Map<InputFile : OutputFile>

    JigsawTransform(Project project) {
        this.mProject = project
    }

    /**
     * Transform 的核心方法
     * @param transformInvocation
     * @throws TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        println("===Kore lifecycle JigsawTransform transform start..... project = ${mProject}")

        /*
        1、遍历所有的 class 文件，收集被 @ModuleApp @MainApp 注解的类
        2、按照优先级生成 @ModuleApp 注解类的实例，并将所有实例添加到  com.kore.jigsaw.core.Jigsaw#mModuleAppList 中
        3、在 @MainApp 注解类的的 attachBaseContext、onCreate 等方法中调用 Jigsaw 的对应方法
        4、完成输出、打包工作
         */

        def moduleJarList = []          // 存放 module 对应的 jar
        def jigsawCore = []             // 存放 jigsaw 核心库对应的 jar
        def compressTaskMap = [:]       // 存放最后需要压缩的任务 Map<JarInput, OutputFile>
        transformInvocation.inputs.each { input ->
            input.jarInputs.each { jarInput ->      // 包含外部依赖的 jar ，也包括依赖 library 的 class.jar
                def name = jarInput.name
                // TODO 如果之后 library 改成外部依赖的 aar ，则此处要修改，可以根据 aar 依赖的 group 来判断，需要有传入途径
                if (name.startsWith(":jigsaw-core")) {  // 如果该 jar 是来自于依赖的 jigsaw-core 源码 library
                    jigsawCore.clear()  // 如果之前添加了外部的 jigsaw-core 依赖，则清空列表
                    jigsawCore.add(jarInput)
                } else if (name.startsWith(":")) {              // 如果该 jar 来自于依赖的 library
                    moduleJarList.add(jarInput)
                } else if (name.contains("jigsaw-core")) {      // 如果引用了 jigsaw-core 的外部依赖
                    if (jigsawCore.size() == 0) {
                        jigsawCore.add(jarInput)
                    }
                } else {        // 如果是其他 jar ，直接复制到 dest 目录即可
                    def types = jarInput.contentTypes
                    def scopes = jarInput.scopes
                    def dest = transformInvocation.outputProvider.getContentLocation(name, types, scopes, Format.JAR)
                    FileUtils.copyFile(jarInput.file, dest)
                }
            }

            input.directoryInputs.each { dirInput ->
                int pathBitLen = dirInput.file.toString().length()

                def types = dirInput.contentTypes
                def scopes = dirInput.scopes
                def name = dirInput.name
                def outDir = transformInvocation.outputProvider.getContentLocation(name, types, scopes, Format.DIRECTORY)

                def callback = { File it ->
                    if (it.exists()) {
                        def path = it.toString().substring(pathBitLen)
                        if (it.isDirectory()) {         // 如果是目录，则直接创建该目录
                            new File(outDir, path).mkdirs()
                        } else {
                            def output = new File(outDir, path)
                            findAppClass(it, output)     // 在当前 app 中寻找注解
                            if (!output.parentFile.exists()) output.parentFile.mkdirs()
                            output.bytes = it.bytes                 // 直接复制一份到 output
                        }
                    }
                }

                if (dirInput.changedFiles != null && !dirInput.changedFiles.isEmpty()) {
                    dirInput.changedFiles.keySet().each(callback)
                }
                if (dirInput.file != null && dirInput.file.exists()) {
                    dirInput.file.traverse(callback)
                }
            }
        }

        // 遍历 moduleJarList
        moduleJarList.each { jarInput ->
            def unzipFile = traversalJar(jarInput, { File it -> return findAppClass(it, it) })
            compressorDir(transformInvocation, jarInput, unzipFile)
        }

        // 找到 Jigsaw 所在的位置
        jigsawCore.each { jarInput ->
            def unzipFile = traversalJar(jarInput, { File it -> return findJigsaw(it, it) })
            compressTaskMap[jarInput] = unzipFile
        }

        // 将 moduleApp 的实例添加到 Jigsaw 的 mModuleAppList 中
        mJigsawMap.each { inputFile, outputFile ->
            insertCodeToJigsaw(inputFile, outputFile)
        }

        // 将 @RouteTable 的实例添加到 JRouter 的 mList 中
        mJRouterMap.each { inputFile, outputFile ->
            insertCodeToJRouter(inputFile, outputFile)
        }

        // 在 mainApp 中插入调用 Jigsaw 对应的代码，并将其输出到对应为目录
        mMainAppMap.each { inputFile, outputFile ->
            insertCodeToMainApp(inputFile, outputFile)
        }

        // 修改完成代码之后，将对应的 jar 包进行压缩
        compressTaskMap.each { jarInput, unzipDir ->
            compressorDir(transformInvocation, jarInput, unzipDir)
        }

        println("===Kore lifecycle JigsawTransform transform end..... project = ${mProject}")
    }

    /**
     * 遍历目录下的所有文件，找到 @MainApp, @ModuleApp 的注解类，并保存到对应变量
     *
     * @param inputFile 需要搜索的文件
     * @param outputFile 输出的文件
     */
    void findAppClass(File inputFile, File outputFile) {
        if (!inputFile.exists() || !inputFile.name.endsWith(".class")) {
            return
        }
        def inputStream = new FileInputStream(inputFile)
        ClassReader cr = new ClassReader(inputStream)       // 使用 classReader 遍历字节码，找到对应注解
        def visitor = new TraverseClassVisitor("findModuleAppClass")
        visitor.callback = new VisitorCallback() {
            void visitMainApp(String name) {
                println("===Kore find findMainAppClass name = ${name}")
                mMainAppMap[inputFile] = outputFile
            }

            @Override
            void visitModuleApp(String name) {
                println("===Kore find findModuleAppClass name = ${name}")
                addToModuleAppList(mModuleAppList, name)
            }

            @Override
            void visitAnnoAttrs(String className, String attrName, Object attrValue) {
                PriorityModuleApp curClass = mModuleAppList.find { it ->
                    it.className == className
                }
                curClass.priority = attrValue
            }

            @Override
            void visitRouteTable(String name) {     // 将找到的 XxxJRouterMap.java 加入到 list 中
                println("===Kore find findRouteTable name = ${name}")
                mJRouterTableList.add(name)
            }
        }
        cr.accept(visitor, 0)
        inputStream.close()
    }

    void addToModuleAppList(List<PriorityModuleApp> moduleAppList, String name) {
        PriorityModuleApp curObj = moduleAppList.find { it ->
            it.className == name
        }
        if (curObj == null) moduleAppList.add(new PriorityModuleApp(name))
    }

    /**
     * 找到核心类 - Jigsaw, JRouter
     *
     * @param inputFile
     * @param outputFile
     */
    void findJigsaw(File inputFile, File outputFile) {
        if (!inputFile.exists() || !inputFile.name.endsWith(".class")) {
            return
        }

        def inputStream = new FileInputStream(inputFile)
        ClassReader cr = new ClassReader(inputStream)       // 使用 classReader 遍历字节码，找到对应注解
        def visitor = new TraverseClassVisitor("findJigsaw")
        visitor.callback = new VisitorCallback() {
            @Override
            void visitJigsaw(String name) {
                mJigsawMap[inputFile] = outputFile
                println("===Kore find findJigsaw!")
            }

            @Override
            void visitJRouter(String name) {
                mJRouterMap[inputFile] = outputFile
                println("===Kore find findJRouter!")
            }
        }
        cr.accept(visitor, 0)
        inputStream.close()
    }

    /**
     * 向 Jigsaw 类中插入代码
     * @param inputFile
     * @param outputFile
     */
    void insertCodeToJigsaw(File inputFile, File outputFile) {
        def fileInputStream = new FileInputStream(inputFile)
        def reader = new ClassReader(fileInputStream)
        def writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        def visitor = new JigsawVisitor(writer, mModuleAppList)
        reader.accept(visitor, 0)
        outputFile.bytes = writer.toByteArray()
        fileInputStream.close()
    }

    /**
     * 向 JRouter 类中插入代码
     * @param jigsawFile
     */
    void insertCodeToJRouter(File inputFile, File outputFile) {
        def fileInputStream = new FileInputStream(inputFile)
        def reader = new ClassReader(fileInputStream)
        def writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        def visitor = new JRouterVisitor(writer, mJRouterTableList)
        reader.accept(visitor, 0)
        outputFile.bytes = writer.toByteArray()
        fileInputStream.close()
    }

    /**
     * 向 @MainApp 注解的类中插入代码
     * @param mainAppFile
     */
    void insertCodeToMainApp(File inputFile, File outputFile) {
        def fileInputStream = new FileInputStream(inputFile)
        def reader = new ClassReader(fileInputStream)
        def writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
        def visitor = new MainAppVisitor(writer)
        reader.accept(visitor, 0)
        outputFile.bytes = writer.toByteArray()
        fileInputStream.close()
    }

    /**
     * 解压 jar 包，然后根据闭包的条件遍历文件
     *
     * @param jarInput
     * @param closure
     * @return jar 解压的目录对应的 File 文件
     */
    File traversalJar(JarInput jarInput, Closure closure) {
        def jarName = jarInput.name

        println("===Kore traversalJar jarName = ${jarName}")
        File unzipDir = new File(jarInput.file.getParent(), jarName.replace(":", "") + "_unzip")
        if (unzipDir.exists()) {            // 删除旧文件
            unzipDir.delete()
        }
        unzipDir.mkdirs()
        Decompression.uncompress(jarInput.file, unzipDir)

        unzipDir.eachFileRecurse(FileType.FILES, { File it ->
            closure.call(it)
        })

        return unzipDir
    }

    /**
     * 压缩目录，并输出到 dest
     *
     * @param transformInvocation
     * @param jarInput
     * @param unzipDir
     */
    void compressorDir(transformInvocation, jarInput, unzipDir) {
        def dest = transformInvocation.outputProvider.getContentLocation(
                jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        Compressor zc = new Compressor(dest.getAbsolutePath())
        zc.compress(unzipDir.getAbsolutePath())
    }


    @Override
    String getName() {
        return "JigsawTransform"
    }

    /**
     * Transform 处理的输入类型: {@link QualifiedContent.DefaultContentType} 此处输入是 class 文件
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    /**
     * Transform 输入文件所属的范围, gradle 支持多工程编译
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否增量编译
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }
}
