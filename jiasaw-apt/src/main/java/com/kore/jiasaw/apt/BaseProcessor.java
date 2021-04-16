package com.kore.jiasaw.apt;

import com.kore.jiasaw.apt.utils.Logger;
import com.kore.jiasaw.apt.utils.TypeUtils;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.kore.jigsaw.anno.utils.Constants.KEY_MODULE_NAME;

/**
 * @author koreq
 * @date 2021-04-13
 * @description  Processor 的父类
 */
public abstract class BaseProcessor extends AbstractProcessor {

    protected Filer mFiler;           // 文件生成器 类/资源
    protected Types types;            // type(类信息)工具类
    protected Elements elements;      // 节点工具类 (类、函数、属性都是节点)
    protected Logger logger;
    protected TypeUtils typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        mFiler = processingEnv.getFiler();                  // Generate class.
        types = processingEnv.getTypeUtils();               // Get type utils.
        elements = processingEnv.getElementUtils();         // Get class meta.
        logger = new Logger(processingEnv.getMessager());   // Package the log utils.
        typeUtils = new TypeUtils(types, elements);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>() {{
            this.add(KEY_MODULE_NAME);
        }};
    }
}
