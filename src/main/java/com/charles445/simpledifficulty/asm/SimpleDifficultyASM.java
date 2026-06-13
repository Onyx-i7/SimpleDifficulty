package com.charles445.simpledifficulty.asm;

import com.charles445.simpledifficulty.asm.helper.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleDifficultyASM implements IClassTransformer {

    // This is what happens when you're too stubborn to add hard dependencies
    private static final Logger LOGGER = LogManager.getLogger("SimpleDifficultyASM");
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }

        if (transformedName.startsWith("com.charles445.simpledifficulty.compat.mod.")) {
            return redirectShadowed(basicClass);
        }
        
        return basicClass;
    }
    
    public byte[] redirectShadowed(byte[] basicClass) {
        ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);
        List<AnnotationNode> annotations = clazzNode.visibleAnnotations;
        
        if (annotations == null) {
            return basicClass;
        }
        
        for (AnnotationNode annotation : annotations) {
            if (annotation != null && "Lcom/charles445/simpledifficulty/compat/HasShadows;".equals(annotation.desc)) {
                LOGGER.info("Found class with shadows: {}", clazzNode.name);
                
                if (clazzNode.interfaces != null) {
                    List<String> readdedInterfaces = new ArrayList<>();
                    Iterator<String> it = clazzNode.interfaces.iterator();
                    while (it.hasNext()) {
                        readdedInterfaces.add(swapOwner(it.next()));
                        it.remove();
                    }
                    
                    clazzNode.interfaces.addAll(readdedInterfaces);
                }
                
                if (clazzNode.fields != null) {
                    for (FieldNode fNode : clazzNode.fields) {
                        fNode.desc = swapDesc(fNode.desc);
                    }
                }
                
                if (clazzNode.methods != null) {
                    for (MethodNode mNode : clazzNode.methods) {
                        mNode.desc = swapDesc(mNode.desc);
                        
                        if (mNode.localVariables != null) {
                            for (LocalVariableNode lvn : mNode.localVariables) {
                                lvn.desc = swapDesc(lvn.desc);
                            }
                        }
                        
                        if (mNode.instructions != null) {
                            AbstractInsnNode anchor = mNode.instructions.getFirst();
                            while (anchor != null) {
                                shadowInsnNode(anchor);
                                anchor = anchor.getNext();
                            }
                        }
                    }
                }
                
                LOGGER.info("Rewriting class with shadows: {}", clazzNode.name);
                return ASMHelper.writeClassToBytes(clazzNode, ClassWriter.COMPUTE_MAXS);
            }
        }
        return basicClass;
    }

    public void shadowInsnNode(AbstractInsnNode anchor) {
        int type = anchor.getType();
        if (type == AbstractInsnNode.FIELD_INSN) {
            FieldInsnNode node = (FieldInsnNode) anchor;
            node.owner = swapOwner(node.owner);
            node.desc = swapDesc(node.desc);
        } else if (type == AbstractInsnNode.METHOD_INSN) {
            MethodInsnNode node = (MethodInsnNode) anchor;
            node.owner = swapOwner(node.owner);
            node.desc = swapDesc(node.desc);
        } else if (type == AbstractInsnNode.TYPE_INSN) {
            TypeInsnNode node = (TypeInsnNode) anchor;
            node.desc = swapOwner(node.desc);
        } else if (type == AbstractInsnNode.FRAME) {
            FrameNode frame = (FrameNode) anchor;
            
            if (frame.local != null) {
                List<Object> replaceList = new ArrayList<>();
                for (int i = 0; i < frame.local.size(); i++) {
                    Object o = frame.local.get(i);
                    if (o instanceof String) {
                        replaceList.add(swapOwner((String) o));
                    } else {
                        replaceList.add(o);
                    }
                }
                frame.local.clear();
                frame.local.addAll(replaceList);
            }
            
            if (frame.stack != null) {
                List<Object> replaceList = new ArrayList<>();
                for (int i = 0; i < frame.stack.size(); i++) {
                    Object o = frame.stack.get(i);
                    if (o instanceof String) {
                        replaceList.add(swapOwner((String) o));
                    } else {
                        replaceList.add(o);
                    }
                }
                frame.stack.clear();
                frame.stack.addAll(replaceList);
            }
        }
    }
    
    @Nullable
    private String swapOwner(String owner) {
        if (owner == null) {
            return null;
        }
        String swap = ShadowMap.ownerMap.get(owner);
        return swap != null ? swap : owner;
    }

    @Nullable
    private String swapDesc(String desc) {
        if (desc == null) {
            return null;
        }
        String result = desc;
        for (Map.Entry<String, String> entry : ShadowMap.descMap.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
