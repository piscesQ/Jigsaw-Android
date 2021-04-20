package com.kore.jiasaw.apt;

import com.google.auto.service.AutoService;
import com.kore.jiasaw.apt.utils.AnnoUtils;
import com.kore.jigsaw.anno.router.Autowired;
import com.kore.jigsaw.anno.utils.TypeKind;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
import static com.kore.jigsaw.anno.utils.Constants.BUNDLE;
import static com.kore.jigsaw.anno.utils.Constants.INTERFACE_AUTOWIRED;
import static com.kore.jigsaw.anno.utils.Constants.SUFFIX_AUTOWIRED;

/**
 * @author koreq
 * @date 2021-04-13
 * @description 处理 @Autowired 注解
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_AUTOWIRED})
public class AutowiredProcessor extends BaseProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        logger.info(">>> AutowiredProcessor init <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        logger.info(">>> AutowiredProcessor process <<<");
        if (CollectionUtils.isNotEmpty(set)) {          // 集合非空时，才进行处理
            /*
            1、获取所有被 @Autowired 注解的属性
            2、根据属性所属的类，进行分组，并存入 Map<TypeElement, Set<Element>>
            3、遍历 Map 生成辅助类，并在辅助类中构建 inject 和 preCheck
                （1）根据 classElement 生成对应的辅助类，并创建两个方法
                （2）遍历类中的属性，确定其类型、入参的key，然后在 inject 中将其给原属性进行赋值
                （3）遍历类中的属性，确定该属性是否是必要字段，如果是必要字段，则在 preCheck 方法中进行校验，如果缺少则校验不通过
             */
            Map<TypeElement, Set<Element>> classMap = new HashMap<>();
            try {
                handleAllAutowired(roundEnvironment, classMap);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }

            genHelperClass(classMap);

            return true;
        }

        return false;
    }

    /**
     * 步骤1：获取所有被 @Autowired 注解的元素，该注解只能标记成员变量
     * 步骤2：遍历注解，找到其父类，并且将其根据父类进行分类
     *
     * @param roundEnvironment
     * @param classMap
     * @throws IllegalAccessException
     */
    private void handleAllAutowired(RoundEnvironment roundEnvironment, Map<TypeElement, Set<Element>> classMap)
            throws IllegalAccessException {
        Set<? extends Element> annoSet = roundEnvironment.getElementsAnnotatedWith(Autowired.class);

        for (Element ele : annoSet) {
            TypeElement parentEle = (TypeElement) ele.getEnclosingElement();     // 获取元素所在的类

            if (ele.getModifiers().contains(Modifier.PRIVATE)) {    // 属性不能是私有属性
                throw new IllegalAccessException("The autowired filed can't be PRIVATE! " + parentEle.getSimpleName()
                        + "#" + ele.getSimpleName() + ", if filed in kotlin, the filed must add @JvmFiled!");
            }

            if (!classMap.containsKey(parentEle)) {
                classMap.put(parentEle, new HashSet<>());
            }
            Set<Element> elements = classMap.get(parentEle);
            elements.add(ele);
        }
    }

    /**
     * 步骤3：根据上面的 map 生成对应的辅助类
     *
     * @param classMap
     */
    private void genHelperClass(Map<TypeElement, Set<Element>> classMap) {
        for (TypeElement classEle : classMap.keySet()) {
            String filename = classEle.getSimpleName().toString();
            TypeSpec.Builder helperClass = TypeSpec.classBuilder(filename + SUFFIX_AUTOWIRED)
                    .superclass(ClassName.get(elements.getTypeElement(INTERFACE_AUTOWIRED)))
                    .addModifiers(Modifier.PUBLIC);
            String fullName = classEle.getQualifiedName().toString();
            String packageName = fullName.substring(0, fullName.lastIndexOf("."));

            MethodSpec methodInject = genInjectMethod(classEle, classMap.get(classEle));
            MethodSpec methodPreCheck = genPreCheckMethod(classEle, classMap.get(classEle));
            helperClass.addMethod(methodInject);
            helperClass.addMethod(methodPreCheck);
            JavaFile javaFile = JavaFile.builder(packageName, helperClass.build()).build();
            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 构造 inject 方法
     *
     * @param classEle class 对应的的 element
     * @param attrSet  class 中所有被 @Autowired 注解的属性的 set
     * @return MethodSpec
     */
    private MethodSpec genInjectMethod(TypeElement classEle, Set<Element> attrSet) {
        TypeMirror tmAct = elements.getTypeElement(ACTIVITY).asType();
        ParameterSpec actParamSpec = AnnoUtils.generateMethodParameterSpec(tmAct, "activity");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.VOID)
                .addParameter(actParamSpec);
        /*
        TargetActivity act = (TargetActivity)activity;
        act.value1 = act.getIntent().getStringExtra("name1");
        act.value2 = act.getIntent().getIntExtra("name2", 0);
         */
        String simpleName = classEle.getSimpleName().toString();
        builder.addStatement("$L act = ($L) activity", simpleName, simpleName);
        for (Element attrEle : attrSet) {
            Autowired fieldAnno = attrEle.getAnnotation(Autowired.class);       // 获得元素的注解类
            String attrName = attrEle.getSimpleName().toString();               // 元素名称

            TypeName attrTypeName = ClassName.get(attrEle.asType());
            int attrTypeNum = typeUtils.typeExchange(attrEle);

            StringBuilder statBuilder = new StringBuilder();
            statBuilder.append("act.$L = ");
            statBuilder.append(genStatementByType(attrTypeNum, "act." + attrName));

            String keyName = StringUtils.isEmpty(fieldAnno.name()) ? attrName : fieldAnno.name();       // 传入的 key
            if (attrTypeNum == TypeKind.OBJECT.ordinal()) {
                builder.addStatement(statBuilder.toString(), attrName, keyName, attrTypeName);
            } else {
                builder.addStatement(statBuilder.toString(), attrName, keyName);
            }
        }
        return builder.build();
    }

    private String genStatementByType(int type, String defaultValue) {
        StringBuilder builder = new StringBuilder();

        String strIntent = "act.getIntent()";
        String prefix = strIntent + ".";
        if (TypeKind.OBJECT.ordinal() == type) {
            builder.append("getObjFromJson(").append(strIntent).append(", $S, $T.class, ").append(defaultValue).append(")");
            return builder.toString();
        }

        if (TypeKind.STRING.ordinal() == type) {
            builder.append("getNonNullStr(").append(strIntent).append(", $S, ").append(defaultValue).append(")");
        } else if (TypeKind.CHAR.ordinal() == type) {
            builder.append(prefix).append("getCharExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.BYTE.ordinal() == type) {
            builder.append(prefix).append("getByteExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.SHORT.ordinal() == type) {
            builder.append(prefix).append("getShortExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.INT.ordinal() == type) {
            builder.append(prefix).append("getIntExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.LONG.ordinal() == type) {
            builder.append(prefix).append("getLongExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.FLOAT.ordinal() == type) {
            builder.append(prefix).append("getFloatExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.DOUBLE.ordinal() == type) {
            builder.append(prefix).append("getDoubleExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.BOOLEAN.ordinal() == type) {
            builder.append(prefix).append("getBooleanExtra($S, ").append(defaultValue).append(")");
        } else if (TypeKind.PARCELABLE.ordinal() == type) {
            builder.append("getParcelableObj(").append(strIntent).append(", $S, ").append(defaultValue).append(")");
        } else if (TypeKind.SERIALIZABLE.ordinal() == type) {       // serializable 类型 需要增加强转
            builder.append("getSerializableObj(").append(strIntent).append(", $S, ").append(defaultValue).append(")");
        }
        return builder.toString();
    }

    /**
     * 构造 preCheck 方法
     *
     * @param classEle class 对应的的 element
     * @param attrSet  class 中所有被 @Autowired 注解的属性的 set
     * @return MethodSpec
     */
    private MethodSpec genPreCheckMethod(TypeElement classEle, Set<Element> attrSet) {

         /*
        方法代码样例：
         public boolean preCheck(Bundle bundle){
            if(!bundle.containsKey("p1")){
                return  false;
            }
            if(!bundle.containsKey("p2")){
                return  false;
            }
            return true;
        }
         */

        TypeMirror tmBundle = elements.getTypeElement(BUNDLE).asType();
        ParameterSpec bundleParaSpec = AnnoUtils.generateMethodParameterSpec(tmBundle, "bundle");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("preCheck")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addParameter(bundleParaSpec);

        for (Element attrEle : attrSet) {
            Autowired fieldAnno = attrEle.getAnnotation(Autowired.class);       // 获得元素的注解类
            String attrName = attrEle.getSimpleName().toString();               // 元素名称
            boolean isRequired = fieldAnno.required();

            if (isRequired) {
                builder.beginControlFlow("if (!bundle.containsKey($S))", attrName);
                builder.addStatement("return false");
                builder.endControlFlow();
            }
        }
        builder.addStatement("return true");
        return builder.build();
    }
}
