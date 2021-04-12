package com.kore.jigsaw.plugin.asm

import com.kore.jigsaw.plugin.bean.PriorityModuleApp
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author koreq* @date 2021-04-12
 * @description 自定义的 jigsaw visitor 类，用于向该类中插入代码
 */
public class JigsawVisitor extends ClassVisitor {
    List<PriorityModuleApp> mModuleAppList

    JigsawVisitor() {
        super(Opcodes.ASM5)
    }

    JigsawVisitor(ClassVisitor classVisitor, List<PriorityModuleApp> moduleAppList) {
        super(Opcodes.ASM5, classVisitor)
        println("JigsawVisitor size = ${moduleAppList.size()} moduleAppList = $moduleAppList")
        mModuleAppList = moduleAppList
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        println("JigsawVisitor visit class name = $name")
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        println("JigsawClassVisitor visiting method: $access, $name, $descriptor, $signature, $exceptions")
        if (access == Opcodes.ACC_PRIVATE && name == "<init>" && descriptor == "()V") {
            // 无参构造方法
            return new JigsawConstructorVisitor(methodVisitor, mModuleAppList)
        }
        return methodVisitor;
    }

    /**
     * @author koreq* @date 2021-04-12
     * @description 向 Jigsaw 的构造方法中插入代码
     */
    class JigsawConstructorVisitor extends MethodVisitor {
        List<PriorityModuleApp> mModuleAppList

        JigsawConstructorVisitor() {
            super(Opcodes.ASM5)
        }

        JigsawConstructorVisitor(MethodVisitor methodVisitor, List<PriorityModuleApp> list) {
            super(Opcodes.ASM5, methodVisitor)
            mModuleAppList = list
        }

        @Override
        void visitEnd() {
            super.visitEnd()
        }

        @Override
        void visitInsn(int opcode) {
            println("JigsawConstructorVisitor visitInsn opcode = $opcode")
            if (opcode == Opcodes.RETURN) {

                // 优先级大的排在前面
                mModuleAppList.sort { a, b -> b.priority <=> a.priority }

                println("JigsawConstructorVisitor visitInsn mModuleAppList  = $mModuleAppList")
                mModuleAppList.each { it ->
                    mv.visitVarInsn(Opcodes.ALOAD, 0)
                    mv.visitFieldInsn(Opcodes.GETFIELD, "com/kore/jigsaw/core/Jigsaw", "mModuleAppList", "Ljava/util/List;")
                    mv.visitTypeInsn(Opcodes.NEW, it.className)
                    mv.visitInsn(Opcodes.DUP)
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, it.className, "<init>", "()V", false)
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
