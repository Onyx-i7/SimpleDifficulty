package com.charles445.simpledifficulty.asm.helper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingMethodAdapter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * From the very helpful ASMHelper pack
 * https://github.com/squeek502/ASMHelper
 */
public class ASMHelper {
    
    public static final InsnComparator insnComparator = new InsnComparator();
    private static final Multimap<String, String> INTERFACE_LOOKUP_CACHE = HashMultimap.create();
    private static final Printer PRINTER = new Textifier();
    private static final TraceMethodVisitor METHOD_PRINTER = new TraceMethodVisitor(PRINTER);

    public static String toInternalClassName(String className) {
        return className.replace('.', '/');
    }

    public static boolean isDescriptor(String descriptor) {
        return descriptor.length() == 1 || (descriptor.startsWith("L") && descriptor.endsWith(";"));
    }

    public static String toDescriptor(String className) {
        return isDescriptor(className) ? className : "L" + toInternalClassName(className) + ";";
    }

    public static String toMethodDescriptor(String returnType, String... paramTypes) {
        StringBuilder paramDescriptors = new StringBuilder();
        for (String paramType : paramTypes) {
            paramDescriptors.append(toDescriptor(paramType));
        }
        return "(" + paramDescriptors.toString() + ")" + toDescriptor(returnType);
    }

    public static ClassNode readClassFromBytes(byte[] bytes) {
        return readClassFromBytes(bytes, 0);
    }

    public static ClassNode readClassFromBytes(byte[] bytes, int flags) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, flags);
        return classNode;
    }

    public static byte[] writeClassToBytes(ClassNode classNode) {
        return writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS);
    }

    public static byte[] writeClassToBytes(ClassNode classNode, int flags) {
        if (ObfHelper.isObfuscated() && !ObfHelper.runsAfterDeobfRemapper()) {
            ClassWriter writer = new ObfRemappingClassWriter(flags);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        return writeClassToBytesNoDeobf(classNode, flags);
    }

    public static byte[] writeClassToBytesNoDeobf(ClassNode classNode) {
        return writeClassToBytesNoDeobf(classNode, ClassWriter.COMPUTE_MAXS);
    }

    public static byte[] writeClassToBytesNoDeobf(ClassNode classNode, int flags) {
        ClassWriter writer = new ClassWriter(flags);
        classNode.accept(writer);
        return writer.toByteArray();
    }
    
    public static void writeClassToFile(ClassNode classNode, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(writeClassToBytes(classNode));
        }
    }

    public static InputStream getClassAsStreamFromClassLoader(String className, ClassLoader classLoader) {
        return classLoader.getResourceAsStream(className.replace('.', '/') + ".class");
    }

    public static ClassReader getClassReaderForClassName(String className) throws IOException {
        return new ClassReader(getClassAsStreamFromClassLoader(className, ASMHelper.class.getClassLoader()));
    }

    public static boolean classHasSuper(ClassReader classReader) {
        return classReader.getSuperName() != null && !classReader.getSuperName().equals("java/lang/Object");
    }

    private static Collection<String> findAllInterfaces(ClassReader classReader) {
        Set<String> interfaces = Sets.newHashSet(classReader.getInterfaces());
        try {
            if (classHasSuper(classReader)) {
                String className2 = ObfHelper.getInternalClassName(classReader.getSuperName());
                if (INTERFACE_LOOKUP_CACHE.containsKey(className2)) {
                    interfaces.addAll(INTERFACE_LOOKUP_CACHE.get(className2));
                } else {
                    interfaces.addAll(findAllInterfaces(getClassReaderForClassName(className2)));
                }
            }
        } catch (IOException e) {
            // Ignored: Triggered when super class is abstract
        }

        INTERFACE_LOOKUP_CACHE.putAll(classReader.getClassName(), interfaces);
        return interfaces;
    }

    public static boolean doesClassImplement(ClassReader classReader, String targetInterfaceInternalClassName) {
        if (!INTERFACE_LOOKUP_CACHE.containsKey(classReader.getClassName())) {
            findAllInterfaces(classReader);
        }
        return INTERFACE_LOOKUP_CACHE.get(classReader.getClassName()).contains(targetInterfaceInternalClassName);
    }

    public static boolean doesClassExtend(ClassReader classReader, String targetSuperInternalClassName) {
        if (!classHasSuper(classReader)) {
            return false;
        }

        String immediateSuperName = ObfHelper.getInternalClassName(classReader.getSuperName());
        if (immediateSuperName.equals(targetSuperInternalClassName)) {
            return true;
        }

        try {
            return doesClassExtend(getClassReaderForClassName(immediateSuperName), targetSuperInternalClassName);
        } catch (IOException e) {
            // Ignored: Triggered when super class is abstract
        }
        return false;
    }

    public static boolean isLabelOrLineNumber(AbstractInsnNode insn) {
        return insn.getType() == AbstractInsnNode.LABEL || insn.getType() == AbstractInsnNode.LINE;
    }

    public static AbstractInsnNode getOrFindInstructionOfType(AbstractInsnNode firstInsnToCheck, int type) {
        return getOrFindInstructionOfType(firstInsnToCheck, type, false);
    }

    public static AbstractInsnNode getOrFindInstructionOfType(AbstractInsnNode firstInsnToCheck, int type, boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (instruction.getType() == type) {
                return instruction;
            }
        }
        return null;
    }

    public static AbstractInsnNode getOrFindInstructionWithOpcode(AbstractInsnNode firstInsnToCheck, int opcode) {
        return getOrFindInstructionWithOpcode(firstInsnToCheck, opcode, false);
    }

    public static AbstractInsnNode getOrFindInstructionWithOpcode(AbstractInsnNode firstInsnToCheck, int opcode, boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (instruction.getOpcode() == opcode) {
                return instruction;
            }
        }
        return null;
    }

    public static AbstractInsnNode getOrFindLabelOrLineNumber(AbstractInsnNode firstInsnToCheck) {
        return getOrFindInstruction(firstInsnToCheck, false);
    }

    public static AbstractInsnNode getOrFindLabelOrLineNumber(AbstractInsnNode firstInsnToCheck, boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (isLabelOrLineNumber(instruction)) {
                return instruction;
            }
        }
        return null;
    }

    public static AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck) {
        return getOrFindInstruction(firstInsnToCheck, false);
    }

    public static AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext()) {
            if (!isLabelOrLineNumber(instruction)) {
                return instruction;
            }
        }
        return null;
    }

    public static AbstractInsnNode findFirstInstruction(MethodNode method) {
        return getOrFindInstruction(method.instructions.getFirst());
    }

    public static AbstractInsnNode findFirstInstructionWithOpcode(MethodNode method, int opcode) {
        return getOrFindInstructionWithOpcode(method.instructions.getFirst(), opcode);
    }

    public static AbstractInsnNode findLastInstructionWithOpcode(MethodNode method, int opcode) {
        return getOrFindInstructionWithOpcode(method.instructions.getLast(), opcode, true);
    }

    public static AbstractInsnNode findNextInstruction(AbstractInsnNode instruction) {
        return getOrFindInstruction(instruction.getNext());
    }

    public static AbstractInsnNode findNextInstructionWithOpcode(AbstractInsnNode instruction, int opcode) {
        return getOrFindInstructionWithOpcode(instruction.getNext(), opcode);
    }

    public static AbstractInsnNode findNextLabelOrLineNumber(AbstractInsnNode instruction) {
        return getOrFindLabelOrLineNumber(instruction.getNext());
    }

    public static AbstractInsnNode findPreviousInstruction(AbstractInsnNode instruction) {
        return getOrFindInstruction(instruction.getPrevious(), true);
    }

    public static AbstractInsnNode findPreviousInstructionWithOpcode(AbstractInsnNode instruction, int opcode) {
        return getOrFindInstructionWithOpcode(instruction.getPrevious(), opcode, true);
    }

    public static AbstractInsnNode findPreviousLabelOrLineNumber(AbstractInsnNode instruction) {
        return getOrFindLabelOrLineNumber(instruction.getPrevious(), true);
    }

    public static MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName) && (methodDesc == null || method.desc.equals(methodDesc))) {
                return method;
            }
        }
        return null;
    }

    public static MethodNode findMethodNodeOfClass(ClassNode classNode, String srgMethodName, String mcpMethodName, String methodDesc) {
        for (MethodNode method : classNode.methods) {
            if ((method.name.equals(srgMethodName) || method.name.equals(mcpMethodName)) && (methodDesc == null || method.desc.equals(methodDesc))) {
                return method;
            }
        }
        return null;
    }

    public static boolean isMethodAbstract(MethodNode method) {
        return (method.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    public static LabelNode findEndLabel(MethodNode method) {
        for (AbstractInsnNode instruction = method.instructions.getLast(); instruction != null; instruction = instruction.getPrevious()) {
            if (instruction instanceof LabelNode) {
                return (LabelNode) instruction;
            }
        }
        return null;
    }

    public static int removeFromInsnListUntil(InsnList insnList, AbstractInsnNode startInclusive, AbstractInsnNode endNotInclusive) {
        AbstractInsnNode insnToRemove = startInclusive;
        int numDeleted = 0;
        while (insnToRemove != null && insnToRemove != endNotInclusive) {
            numDeleted++;
            insnToRemove = insnToRemove.getNext();
            insnList.remove(insnToRemove.getPrevious());
        }
        return numDeleted;
    }

    public static void skipInstructions(InsnList insnList, AbstractInsnNode startInclusive, AbstractInsnNode endNotInclusive) {
        LabelNode skipLabel = new LabelNode();
        insnList.insertBefore(startInclusive, new JumpInsnNode(Opcodes.GOTO, skipLabel));
        insnList.insertBefore(endNotInclusive, skipLabel);
    }

    public static AbstractInsnNode move(final AbstractInsnNode start, int distance) {
        AbstractInsnNode movedTo = start;
        int absDistance = Math.abs(distance);
        for (int i = 0; i < absDistance && movedTo != null; i++) {
            movedTo = distance > 0 ? movedTo.getNext() : movedTo.getPrevious();
        }
        return movedTo;
    }

    public static boolean instructionsMatch(AbstractInsnNode first, AbstractInsnNode second) {
        return insnComparator.areInsnsEqual(first, second);
    }

    public static boolean patternMatches(InsnList checkFor, AbstractInsnNode checkAgainst) {
        return checkForPatternAt(checkFor, checkAgainst).getFirst() != null;
    }

    public static InsnList checkForPatternAt(InsnList checkFor, AbstractInsnNode checkAgainst) {
        InsnList foundInsnList = new InsnList();
        boolean firstNeedleFound = false;
        for (AbstractInsnNode lookFor = checkFor.getFirst(); lookFor != null;) {
            if (checkAgainst == null) {
                return new InsnList();
            }
            if (isLabelOrLineNumber(lookFor)) {
                lookFor = lookFor.getNext();
                continue;
            }
            if (isLabelOrLineNumber(checkAgainst)) {
                if (firstNeedleFound) {
                    foundInsnList.add(checkAgainst);
                }
                checkAgainst = checkAgainst.getNext();
                continue;
            }
            if (!instructionsMatch(lookFor, checkAgainst)) {
                return new InsnList();
            }
            foundInsnList.add(checkAgainst);
            lookFor = lookFor.getNext();
            checkAgainst = checkAgainst.getNext();
            firstNeedleFound = true;
        }
        return foundInsnList;
    }

    public static InsnList findAndGetFoundInsnList(AbstractInsnNode haystackStart, InsnList needle) {
        int needleStartOpcode = needle.getFirst().getOpcode();
        for (AbstractInsnNode checkAgainstStart = getOrFindInstructionWithOpcode(haystackStart, needleStartOpcode); checkAgainstStart != null; checkAgainstStart = findNextInstructionWithOpcode(checkAgainstStart, needleStartOpcode)) {
            InsnList found = checkForPatternAt(needle, checkAgainstStart);
            if (found.getFirst() != null) {
                return found;
            }
        }
        return new InsnList();
    }

    public static AbstractInsnNode find(InsnList haystack, InsnList needle) {
        return find(haystack.getFirst(), needle);
    }

    public static AbstractInsnNode find(AbstractInsnNode haystackStart, InsnList needle) {
        if (needle.getFirst() == null) {
            return null;
        }
        return findAndGetFoundInsnList(haystackStart, needle).getFirst();
    }

    public static AbstractInsnNode find(InsnList haystack, AbstractInsnNode needle) {
        return find(haystack.getFirst(), needle);
    }

    public static AbstractInsnNode find(AbstractInsnNode haystackStart, AbstractInsnNode needle) {
        InsnList insnList = new InsnList();
        insnList.add(needle);
        return find(haystackStart, insnList);
    }

    public static AbstractInsnNode findAndReplace(InsnList haystack, InsnList needle, InsnList replacement) {
        return findAndReplace(haystack, needle, replacement, haystack.getFirst());
    }

    public static AbstractInsnNode findAndReplace(InsnList haystack, InsnList needle, InsnList replacement, AbstractInsnNode haystackStart) {
        InsnList found = findAndGetFoundInsnList(haystackStart, needle);
        if (found.getFirst() != null) {
            haystack.insertBefore(found.getFirst(), replacement);
            AbstractInsnNode afterNeedle = found.getLast().getNext();
            removeFromInsnListUntil(haystack, found.getFirst(), afterNeedle);
            return afterNeedle;
        }
        return null;
    }

    public static int findAndReplaceAll(InsnList haystack, InsnList needle, InsnList replacement) {
        return findAndReplaceAll(haystack, needle, replacement, haystack.getFirst());
    }

    public static int findAndReplaceAll(InsnList haystack, InsnList needle, InsnList replacement, AbstractInsnNode haystackStart) {
        int numReplaced = 0;
        while ((haystackStart = findAndReplace(haystack, needle, cloneInsnList(replacement), haystackStart)) != null) {
            numReplaced++;
        }
        return numReplaced;
    }

    public static InsnList cloneInsnList(InsnList source) {
        InsnList clone = new InsnList();
        Map<LabelNode, LabelNode> labelMap = new HashMap<>();

        for (AbstractInsnNode instruction = source.getFirst(); instruction != null; instruction = instruction.getNext()) {
            if (instruction instanceof LabelNode) {
                labelMap.put(((LabelNode) instruction), new LabelNode());
            }
        }

        for (AbstractInsnNode instruction = source.getFirst(); instruction != null; instruction = instruction.getNext()) {
            clone.add(instruction.clone(labelMap));
        }
        return clone;
    }

    public static LocalVariableNode findLocalVariableOfMethod(MethodNode method, String varName, String varDesc) {
        for (LocalVariableNode localVar : method.localVariables) {
            if (localVar.name.equals(varName) && localVar.desc.equals(varDesc)) {
                return localVar;
            }
        }
        return null;
    }

    public static MethodNode copyAndRenameMethod(ClassNode classNode, MethodNode method, String newMethodName) {
        MethodVisitor methodCopyVisitor = classNode.visitMethod(method.access, newMethodName, method.desc, method.signature, method.exceptions.toArray(new String[0]));
        method.accept(new RemappingMethodAdapter(method.access, method.desc, methodCopyVisitor, new Remapper() {}));
        return methodCopyVisitor instanceof MethodNode ? (MethodNode) methodCopyVisitor : null;
    }

    public static String getInsnListAsString(InsnList insnList) {
        insnList.accept(METHOD_PRINTER);
        StringWriter sw = new StringWriter();
        PRINTER.print(new PrintWriter(sw));
        PRINTER.getText().clear();
        return sw.toString();
    }

    public static String getMethodAsString(MethodNode method) {
        method.accept(METHOD_PRINTER);
        StringWriter sw = new StringWriter();
        PRINTER.print(new PrintWriter(sw));
        PRINTER.getText().clear();
        return sw.toString();
    }
}
