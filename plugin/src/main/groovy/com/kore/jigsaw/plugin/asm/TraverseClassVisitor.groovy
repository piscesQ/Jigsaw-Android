package com.kore.jigsaw.plugin.asm


import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * @author koreq* @date 2021-04-10
 * @description 自定义的 visitor 类 - 用于遍历所有的 class 文件，找到被目标注解标记的类
 */
public class TraverseClassVisitor extends ClassVisitor {
    private static final String NAME_JIGSAW_CLASS = "com/kore/jigsaw/core/Jigsaw"
    private static final String NAME_JROUTER_CLASS = "com/kore/jigsaw/core/router/JRouter"
    private static final String SIGN_MAIN_APP = "Lcom/kore/jigsaw/anno/MainApp;"
    private static final String SIGN_MODULE_APP = "Lcom/kore/jigsaw/anno/ModuleApp;"
    private static final String SIGN_ROUTE_TABLE = "Lcom/kore/jigsaw/anno/router/RouteTable;"

    private String mSource = ""
    private String mCurVisitName = ""           // 当前访问的 class 的 name
    private VisitorCallback mCallback;

    public TraverseClassVisitor(String source) {
        super(Opcodes.ASM5)
        mSource = source
    }

    public TraverseClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        println(mSource + " visit class name = $name")
        mCurVisitName = name
        if (name == NAME_JIGSAW_CLASS) {
            if (mCallback != null) {
                mCallback.visitJigsaw(name)
            }
        } else if (name == NAME_JROUTER_CLASS) {
            if (mCallback != null) {
                mCallback.visitJRouter(name)
            }
        }
    }

    @Override
    AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        println(mSource + " visitAnnotation desc = $desc")

        switch (desc) {
            case SIGN_MODULE_APP:
                if (mCallback != null) {
                    mCallback.visitModuleApp(mCurVisitName)
                }
                return new ModuleAppAnnoVisitor()
            case SIGN_MAIN_APP:
                if (mCallback != null) {
                    mCallback.visitMainApp(mCurVisitName)
                }
                break
            case SIGN_ROUTE_TABLE:
                if (mCallback != null) {
                    mCallback.visitRouteTable(mCurVisitName)
                }
                break
        }
        return super.visitAnnotation(desc, visible)
    }


    VisitorCallback getCallback() {
        return mCallback
    }

    void setCallback(VisitorCallback callback) {
        mCallback = callback
    }

    class ModuleAppAnnoVisitor extends AnnotationVisitor {
        ModuleAppAnnoVisitor() {
            super(Opcodes.ASM5)
        }

        @Override
        void visit(String name, Object value) {     // 解析注解ModuleSpec的属性
            println(mSource + " visitAnnotationAttr name = $name, value = $value")
            if (mCallback != null) {
                mCallback.visitAnnoAttrs(mCurVisitName, name, value)
            }
            super.visit(name, value)
        }
    }
}
