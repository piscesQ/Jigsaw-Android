package com.kore.jigsaw.plugin.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author koreq* @date 2021-04-12
 * @description 向 @MainApp 注解的类中插入代码
 */
class MainAppVisitor extends ClassVisitor {

    /*
    下面方法在源码中是否被实现过
     */
    boolean mOnCreateDefined
    boolean mAttachBaseContextDefined
    boolean mOnConfigChangedDefined
    boolean mOnLowMemoryDefined
    boolean mOnTerminateDefined
    boolean mOnTrimMemoryDefined

    MainAppVisitor() {
        super(Opcodes.ASM5)
    }

    MainAppVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor)
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("MainAppVisitor visit class name = $name")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        println("MainAppVisitor visiting method: $access, $name, $descriptor, $signature, $exceptions")
        String nameDesc = name + descriptor
        switch (nameDesc) {
            case "onCreate()V":
                mOnCreateDefined = true
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, false)
            case "attachBaseContext(Landroid/content/Context;)V":
                mAttachBaseContextDefined = true
                return new InsertMethodVisitor(methodVisitor, name, descriptor, true, false)
            case "onConfigurationChanged(Landroid/content/res/Configuration;)V":
                mOnConfigChangedDefined = true
                return new InsertMethodVisitor(methodVisitor, name, descriptor, true, false)
            case "onLowMemory()V":
                mOnLowMemoryDefined = true
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, false)
            case "onTerminate()V":
                mOnTerminateDefined = true
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, false)
            case "onTrimMemory(I)V":
                mOnTrimMemoryDefined = true
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, true)
        }
        return methodVisitor;
    }

    @Override
    void visitEnd() {               // 如果代码中找不到下面方法，则向其中添加对应的方法
        println("MainAppVisitor visitEnd")
        if (!mAttachBaseContextDefined) {
            defineMethod(Opcodes.ACC_PROTECTED, "attachBaseContext", "(Landroid/content/Context;)V", true, false)
        }
        if (!mOnCreateDefined) {
            defineMethod(Opcodes.ACC_PUBLIC, "onCreate", "()V", false, false)
        }
        if (!mOnConfigChangedDefined) {
            defineMethod(Opcodes.ACC_PUBLIC, "onConfigurationChanged", "(Landroid/content/res/Configuration;)V", true, false)
        }
        if (!mOnLowMemoryDefined) {
            defineMethod(Opcodes.ACC_PUBLIC, "onLowMemory", "()V", false, false)
        }
        if (!mOnTerminateDefined) {
            defineMethod(Opcodes.ACC_PUBLIC, "onTerminate", "()V", false, false)
        }
        if (!mOnTrimMemoryDefined) {
            defineMethod(Opcodes.ACC_PUBLIC, "onTrimMemory", "(I)V", false, true)
        }
        super.visitEnd()
    }

    void defineMethod(int access, String name, String descriptor, boolean loadParaRef, boolean loadParaInt) {
        println("MainAppVisitor defineMethod method: $access, $name, $descriptor")
        MethodVisitor methodVisitor = this.visitMethod(access, name, descriptor, null, null)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        if (loadParaRef) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        }
        if (loadParaInt) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
        }
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "android/app/Application", name, descriptor, false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    /**
     * @author koreq* @date 2021-04-12
     * @description 向被 @MainApp 注解的类中对应的方法内，插入代码
     */
    class InsertMethodVisitor extends MethodVisitor {
        String mMethodName
        String mMethodDesc
        boolean mLoadParaRef
        boolean mLoadParaInt

        InsertMethodVisitor(MethodVisitor methodVisitor, String name, String desc, boolean loadParaRef, boolean loadParaInt) {
            super(Opcodes.ASM5, methodVisitor)
            mMethodName = name
            mMethodDesc = desc
            mLoadParaRef = loadParaRef
            mLoadParaInt = loadParaInt
        }

        @Override
        void visitEnd() {
            super.visitEnd()
        }

        @Override
        void visitInsn(int opcode) {
            println("MainAppVisitor visitInsn opcode = $opcode, mMethodName = $mMethodName, mMethodDesc = $mMethodDesc")
            if (opcode == Opcodes.RETURN) {
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/kore/jigsaw/core/Jigsaw", "get", "()Lcom/kore/jigsaw/core/Jigsaw;", false);
                if (mLoadParaRef) {
                    mv.visitVarInsn(Opcodes.ALOAD, 1)       // 加载方法参数（引用类型）
                }
                if (mLoadParaInt) {
                    mv.visitVarInsn(Opcodes.ILOAD, 1)       // 加载方法参数（int 类型）
                }
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/kore/jigsaw/core/Jigsaw", mMethodName, mMethodDesc, false);
            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0)
        }
    }
}
