package com.kore.jiasaw.apt;

import com.google.auto.service.AutoService;
import com.kore.jigsaw.anno.router.Autowired;
import com.kore.jigsaw.anno.router.Route;
import com.kore.jigsaw.anno.router.RouteTable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import static com.kore.jigsaw.anno.utils.Constants.ACTIVITY;
import static com.kore.jigsaw.anno.utils.Constants.ANNOTATION_TYPE_AUTOWIRED;
import static com.kore.jigsaw.anno.utils.Constants.ANNOTATION_TYPE_ROUTE;
import static com.kore.jigsaw.anno.utils.Constants.BASE_MODULE_ROUTER;
import static com.kore.jigsaw.anno.utils.Constants.JIGSAW_CORE_PKG;
import static com.kore.jigsaw.anno.utils.Constants.KEY_MODULE_NAME;
import static com.kore.jigsaw.anno.utils.Constants.SUFFIX_ROUTER_MAP;

/**
 * @author koreq
 * @date 2021-04-13
 * @description 处理 @Route 注解
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOWIRED})
public class RouteProcessor extends BaseProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger.info(">>> RouteProcessor init <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        logger.info(">>> RouteProcessor process <<<");
        if (CollectionUtils.isNotEmpty(set)) {          // 集合非空时，才进行处理
           /*
            1、获取所有被 @Route 注解的类
            2、解析 @Route 注解的 path, desc 属性，并解析类中的，所有被 @Autowired 标记的属性和类型，并生成参数列表
            4、获取 build.gradle 中 module 的值，并生成对应的 java 文件，继承自 BaseModuleRouter
            5、将路由表和对应的属性列表加到 java 文件的 addPathMap() 方法
            */
            Map<Element, Set<Element>> routeMap = new HashMap<>();
            genRouteInfo(roundEnvironment, routeMap);
            genHelperClass(roundEnvironment, routeMap);

            return true;
        }

        return false;
    }

    private void genRouteInfo(RoundEnvironment roundEnvironment, Map<Element, Set<Element>> routeMap) {
        TypeElement activityElement = elements.getTypeElement(ACTIVITY);   // 根据 name 获取其唯一的 TypeElement 对象

        Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        for (Element ele : routeElements) {
            if (types.isSubtype(ele.asType(), activityElement.asType())) {      // 第一个参数为子类，第二个是父类
                if (!routeMap.containsKey(ele)) {
                    routeMap.put(ele, new HashSet<>());
                }
                Set<Element> filedSet = routeMap.get(ele);

                TypeElement typeEle = (TypeElement) ele;
                List<? extends Element> memberEles = typeEle.getEnclosedElements();     // 获取内部的所有成员
                for (Element memberEle : memberEles) {
                    if (memberEle.getKind().isField() && memberEle.getAnnotation(Autowired.class) != null) {
                        filedSet.add(memberEle);
                    }
                }
            } else {
                logger.error("ele with @Route isn't a Activity!");
            }
        }
    }

    private void genHelperClass(RoundEnvironment roundEnvironment, Map<Element, Set<Element>> routeMap) {
        Map<String, String> options = processingEnv.getOptions();       // 获取 @SupportedOptions 中配置的字段
        String moduleName = "";
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }
        String filename = firstCharUpperCase(moduleName) + SUFFIX_ROUTER_MAP;
        TypeName superClass = ClassName.get(elements.getTypeElement(BASE_MODULE_ROUTER).asType());
        TypeSpec.Builder helperClass = TypeSpec.classBuilder(filename)
                .addAnnotation(RouteTable.class)
                .addModifiers(Modifier.PUBLIC).superclass(superClass);

        MethodSpec getModuleNameMethod = genModuleNameMethod(moduleName);
        MethodSpec addRouteTableMethod = genRouteTableMethod(routeMap);

        helperClass.addMethod(addRouteTableMethod);
        helperClass.addMethod(getModuleNameMethod);
        JavaFile javaFile = JavaFile.builder(JIGSAW_CORE_PKG, helperClass.build()).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成方法：public String getModuleName(){ return "xxx";}
     *
     * @param moduleName
     * @return
     */
    private MethodSpec genModuleNameMethod(String moduleName) {
        TypeMirror tmString = elements.getTypeElement("java.lang.String").asType();
        TypeName returnType = TypeName.get(tmString);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getModuleName")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement("return $S", moduleName);
        return builder.build();
    }

    /**
     * 生成方法：public void addRouteTableMethod(){}
     *
     * @return
     */
    private MethodSpec genRouteTableMethod(Map<Element, Set<Element>> routeMap) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("addRouteTable")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC);
        for (Element classEle : routeMap.keySet()) {
            StringBuilder strBuilder = new StringBuilder();
            Route annoRoute = classEle.getAnnotation(Route.class);
            String path = annoRoute.path();
            String desc = annoRoute.desc();
            TypeMirror tmClass = classEle.asType();
            TypeName tnClass = ClassName.get(tmClass);
            builder.addStatement("addPathMap($S, $T.class)", path, tnClass);
            for(Element filedEle : routeMap.get(classEle)){     // 向类中添加属性
                Autowired annoAutowired = filedEle.getAnnotation(Autowired.class);
                String name = annoAutowired.name();
                String fieldDesc = annoAutowired.desc();
                int attrTypeNum = typeUtils.typeExchange(filedEle);
                builder.addStatement("addParamMap($T.class, $S, $L)", tnClass, name, String.valueOf(attrTypeNum));
            }
        }
        return builder.build();
    }

    public static String firstCharUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
