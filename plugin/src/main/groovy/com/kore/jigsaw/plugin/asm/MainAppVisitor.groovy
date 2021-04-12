package com.kore.jigsaw.plugin.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author koreq* @date 2021-04-12
 * @description 向 @MainApp 注解的类中插入代码
 */
class MainAppVisitor extends ClassVisitor {
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
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, false)
            case "attachBaseContext(Landroid/content/Context;)V":
                return new InsertMethodVisitor(methodVisitor, name, descriptor, true, false)
            case "onConfigurationChanged(Landroid/content/res/Configuration;)V":
                return new InsertMethodVisitor(methodVisitor, name, descriptor, true, false)
            case "onLowMemory()V":
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, false)
            case "onTerminate()V":
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, false)
            case "onTrimMemory(I)V":
                return new InsertMethodVisitor(methodVisitor, name, descriptor, false, true)
        }
        return methodVisitor;
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
