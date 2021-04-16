package com.kore.jigsaw.plugin.asm


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author koreq* @date 2021-04-16
 * @description 自定义的 jRouter visitor 类，用于向该类中插入代码
 */
public class JRouterVisitor extends ClassVisitor {
    List<String> mList

    JRouterVisitor() {
        super(Opcodes.ASM5)
    }

    JRouterVisitor(ClassVisitor classVisitor, List<String> list) {
        super(Opcodes.ASM5, classVisitor)
        println("JRouterVisitor size = ${list.size()} list = $list")
        mList = list
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("JRouterVisitor visit class name = $name")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        println("JRouterClassVisitor visiting method: $access, $name, $descriptor, $signature, $exceptions")
        if (access == Opcodes.ACC_PRIVATE && name == "<init>" && descriptor == "()V") {
            // 无参构造方法
            return new JRouterConstructorVisitor(methodVisitor, mList)
        }
        return methodVisitor;
    }

    /**
     * @author koreq* @date 2021-04-16
     * @description 向 JRouter 的构造方法中插入代码
     */
    class JRouterConstructorVisitor extends MethodVisitor {
        List<String> mList

        JRouterConstructorVisitor() {
            super(Opcodes.ASM5)
        }

        JRouterConstructorVisitor(MethodVisitor methodVisitor, List<String> list) {
            super(Opcodes.ASM5, methodVisitor)
            mList = list
        }

        @Override
        void visitEnd() {
            super.visitEnd()
        }

        @Override
        void visitInsn(int opcode) {
            println("JRouterConstructorVisitor visitInsn opcode = $opcode")
            if (opcode == Opcodes.RETURN) {
                println("JRouterConstructorVisitor visitInsn mList  = $mList")
                mList.each { it ->
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                    mv.visitFieldInsn(Opcodes.GETFIELD, "com/kore/jigsaw/core/router/JRouter", "mModuleList", "Ljava/util/List;")
                    mv.visitTypeInsn(Opcodes.NEW, it)
                    mv.visitInsn(Opcodes.DUP)
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, it, "<init>", "()V", false)
                    mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true)
                    mv.visitInsn(Opcodes.POP)
                }
            }
            super.visitInsn(opcode)
        }

        @Override
        void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(0, 0)
        }
    }
}
