package com.enonic.xp.launcher.impl.weaver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;

/**
 * Rewrites classes containing {@code @Traced} methods.
 * <p>
 * For every traced method {@code m}, the method body is moved to a private synthetic method {@code m$xpTraced}
 * and {@code m} is replaced by:
 * <pre>{@code
 * return TraceSupport.trace( "<name>", () -> m$xpTraced( <captured this and arguments> ) );
 * }</pre>
 * The lambda is created with an {@code invokedynamic} instruction bound to {@link java.lang.invoke.LambdaMetafactory},
 * so no auxiliary classes need to be injected into the bundle - the JVM spins the capturing class at link time.
 * Checked exceptions, primitive return values and generic signatures are preserved; method annotations stay on
 * the visible wrapper method.
 */
final class TraceWeaverTransformer
{
    static final String TRACED_DESCRIPTOR = "Lcom/enonic/xp/trace/Traced;";

    static final String BODY_METHOD_SUFFIX = "$xpTraced";

    private static final Logger LOG = LoggerFactory.getLogger( TraceWeaverTransformer.class );

    private static final int API = Opcodes.ASM9;

    private static final String TRACER_INTERNAL_NAME = "com/enonic/xp/trace/Tracer";

    private static final String TRACE_DESCRIPTOR = "Lcom/enonic/xp/trace/Trace;";

    private static final String SUPPORT_INTERNAL_NAME = "com/enonic/xp/trace/TraceSupport";

    private static final String CALL_INTERNAL_NAME = SUPPORT_INTERNAL_NAME + "$TracedCall";

    private static final String VOID_CALL_INTERNAL_NAME = SUPPORT_INTERNAL_NAME + "$TracedVoidCall";

    private static final Handle METAFACTORY = new Handle( Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
                                                          "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                                          false );

    private TraceWeaverTransformer()
    {
    }

    /**
     * Transforms the given class bytes.
     *
     * @param bytes original class bytes
     * @return woven class bytes, or {@code null} if the class has no methods to weave
     */
    static byte[] transform( final byte[] bytes )
    {
        final ClassReader reader = new ClassReader( bytes );

        final Map<String, String> tracedMethods = collectTracedMethods( reader );
        if ( tracedMethods.isEmpty() )
        {
            return null;
        }

        final ClassWriter writer = new ClassWriter( reader, ClassWriter.COMPUTE_MAXS );
        reader.accept( new WeavingClassVisitor( writer, tracedMethods ), 0 );
        return writer.toByteArray();
    }

    private static Map<String, String> collectTracedMethods( final ClassReader reader )
    {
        final String className = reader.getClassName();
        final String simpleName = className.substring( className.lastIndexOf( '/' ) + 1 );

        final Map<String, String> tracedMethods = new HashMap<>();
        final Set<String> allMethods = new HashSet<>();

        reader.accept( new ClassVisitor( API )
        {
            @Override
            public MethodVisitor visitMethod( final int access, final String name, final String descriptor, final String signature,
                                              final String[] exceptions )
            {
                allMethods.add( name + descriptor );

                return new MethodVisitor( API )
                {
                    @Override
                    public AnnotationVisitor visitAnnotation( final String annotationDescriptor, final boolean visible )
                    {
                        if ( !TRACED_DESCRIPTOR.equals( annotationDescriptor ) )
                        {
                            return null;
                        }

                        if ( ( access & ( Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE ) ) != 0 || name.charAt( 0 ) == '<' )
                        {
                            LOG.warn( "@Traced is not supported on {}.{}{} - skipping", className, name, descriptor );
                            return null;
                        }

                        final String key = name + descriptor;
                        tracedMethods.put( key, simpleName + "." + name );

                        return new AnnotationVisitor( API )
                        {
                            @Override
                            public void visit( final String elementName, final Object value )
                            {
                                if ( "value".equals( elementName ) && value instanceof String traceName && !traceName.isEmpty() )
                                {
                                    tracedMethods.put( key, traceName );
                                }
                            }
                        };
                    }
                };
            }
        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES );

        // Safety: never weave twice. If a body method already exists, the corresponding method is left as-is.
        tracedMethods.keySet().removeIf( key -> {
            final int descStart = key.indexOf( '(' );
            return allMethods.contains( key.substring( 0, descStart ) + BODY_METHOD_SUFFIX + key.substring( descStart ) );
        } );

        return tracedMethods;
    }

    private static final class WeavingClassVisitor
        extends ClassVisitor
    {
        private final Map<String, String> tracedMethods;

        private String internalName;

        private boolean isInterface;

        WeavingClassVisitor( final ClassVisitor classVisitor, final Map<String, String> tracedMethods )
        {
            super( API, classVisitor );
            this.tracedMethods = tracedMethods;
        }

        @Override
        public void visit( final int version, final int access, final String name, final String signature, final String superName,
                           final String[] interfaces )
        {
            this.internalName = name;
            this.isInterface = ( access & Opcodes.ACC_INTERFACE ) != 0;
            super.visit( version, access, name, signature, superName, interfaces );
        }

        @Override
        public MethodVisitor visitMethod( final int access, final String name, final String descriptor, final String signature,
                                          final String[] exceptions )
        {
            final String traceName = this.tracedMethods.get( name + descriptor );
            if ( traceName == null )
            {
                return super.visitMethod( access, name, descriptor, signature, exceptions );
            }

            // Visible wrapper method: keeps name, access flags (including ACC_SYNCHRONIZED, which participates in
            // serialVersionUID computation and reflective modifiers), signature, declared exceptions and
            // annotations. The body method keeps ACC_SYNCHRONIZED too; the re-acquire is reentrant and cheap.
            final MethodVisitor wrapper = super.visitMethod( access, name, descriptor, signature, exceptions );

            final int bodyAccess = Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC |
                ( access & ( Opcodes.ACC_STATIC | Opcodes.ACC_SYNCHRONIZED | Opcodes.ACC_VARARGS ) );
            final MethodVisitor body =
                super.visitMethod( bodyAccess, name + BODY_METHOD_SUFFIX, descriptor, null, exceptions );

            return new SplittingMethodVisitor( wrapper, body, this.internalName, this.isInterface, access, name, descriptor, traceName );
        }
    }

    /**
     * Routes method header events (annotations, parameters) to the wrapper method and the code to the body
     * method, then generates the wrapper bytecode.
     */
    private static final class SplittingMethodVisitor
        extends MethodVisitor
    {
        private final MethodVisitor wrapper;

        private final String owner;

        private final boolean ownerIsInterface;

        private final int access;

        private final String name;

        private final String descriptor;

        private final String traceName;

        private int firstLine = -1;

        SplittingMethodVisitor( final MethodVisitor wrapper, final MethodVisitor body, final String owner, final boolean ownerIsInterface,
                                final int access, final String name, final String descriptor, final String traceName )
        {
            super( API, body );
            this.wrapper = wrapper;
            this.owner = owner;
            this.ownerIsInterface = ownerIsInterface;
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.traceName = traceName;
        }

        @Override
        public void visitParameter( final String parameterName, final int parameterAccess )
        {
            this.wrapper.visitParameter( parameterName, parameterAccess );
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault()
        {
            return this.wrapper.visitAnnotationDefault();
        }

        @Override
        public AnnotationVisitor visitAnnotation( final String annotationDescriptor, final boolean visible )
        {
            return this.wrapper.visitAnnotation( annotationDescriptor, visible );
        }

        @Override
        public AnnotationVisitor visitTypeAnnotation( final int typeRef, final net.bytebuddy.jar.asm.TypePath typePath,
                                                      final String annotationDescriptor, final boolean visible )
        {
            return this.wrapper.visitTypeAnnotation( typeRef, typePath, annotationDescriptor, visible );
        }

        @Override
        public void visitAnnotableParameterCount( final int parameterCount, final boolean visible )
        {
            this.wrapper.visitAnnotableParameterCount( parameterCount, visible );
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation( final int parameter, final String annotationDescriptor,
                                                           final boolean visible )
        {
            return this.wrapper.visitParameterAnnotation( parameter, annotationDescriptor, visible );
        }

        @Override
        public void visitAttribute( final net.bytebuddy.jar.asm.Attribute attribute )
        {
            this.wrapper.visitAttribute( attribute );
        }

        @Override
        public void visitLineNumber( final int line, final Label start )
        {
            if ( this.firstLine < 0 )
            {
                this.firstLine = line;
            }
            super.visitLineNumber( line, start );
        }

        @Override
        public void visitEnd()
        {
            generateWrapper();
            super.visitEnd();
            this.wrapper.visitEnd();
        }

        private void generateWrapper()
        {
            final Type returnType = Type.getReturnType( this.descriptor );
            final Type[] argumentTypes = Type.getArgumentTypes( this.descriptor );
            final boolean isStatic = ( this.access & Opcodes.ACC_STATIC ) != 0;
            final boolean isVoid = returnType.getSort() == Type.VOID;

            this.wrapper.visitCode();

            final Label start = new Label();
            this.wrapper.visitLabel( start );
            if ( this.firstLine >= 0 )
            {
                this.wrapper.visitLineNumber( this.firstLine, start );
            }

            // Fast path: tracing disabled and no enclosing trace to shield - invoke the body directly, with no
            // lambda allocation. When an enclosing trace exists, the slow path is taken even if tracing was just
            // disabled, so the trace scope (bound to null) still shields the enclosing trace from enrichment.
            final Label slowPath = new Label();
            this.wrapper.visitMethodInsn( Opcodes.INVOKESTATIC, TRACER_INTERNAL_NAME, "isEnabled", "()Z", false );
            this.wrapper.visitJumpInsn( Opcodes.IFNE, slowPath );
            this.wrapper.visitMethodInsn( Opcodes.INVOKESTATIC, TRACER_INTERNAL_NAME, "current", "()" + TRACE_DESCRIPTOR, false );
            this.wrapper.visitJumpInsn( Opcodes.IFNONNULL, slowPath );

            loadReceiverAndArguments( argumentTypes, isStatic );
            this.wrapper.visitMethodInsn( isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKESPECIAL, this.owner,
                                          this.name + BODY_METHOD_SUFFIX, this.descriptor, this.ownerIsInterface );
            this.wrapper.visitInsn( returnType.getOpcode( Opcodes.IRETURN ) );

            this.wrapper.visitLabel( slowPath );
            this.wrapper.visitFrame( Opcodes.F_SAME, 0, null, 0, null );

            this.wrapper.visitLdcInsn( this.traceName );

            // Load receiver and arguments as lambda captures.
            loadReceiverAndArguments( argumentTypes, isStatic );

            final Type[] capturedTypes;
            if ( isStatic )
            {
                capturedTypes = argumentTypes;
            }
            else
            {
                capturedTypes = new Type[argumentTypes.length + 1];
                capturedTypes[0] = Type.getObjectType( this.owner );
                System.arraycopy( argumentTypes, 0, capturedTypes, 1, argumentTypes.length );
            }

            final String samInternalName = isVoid ? VOID_CALL_INTERNAL_NAME : CALL_INTERNAL_NAME;
            final Type samMethodType = isVoid ? Type.getMethodType( "()V" ) : Type.getMethodType( "()Ljava/lang/Object;" );
            final Handle bodyHandle =
                new Handle( isStatic ? Opcodes.H_INVOKESTATIC : Opcodes.H_INVOKESPECIAL, this.owner, this.name + BODY_METHOD_SUFFIX,
                            this.descriptor, this.ownerIsInterface );

            this.wrapper.visitInvokeDynamicInsn( "invoke",
                                                 Type.getMethodDescriptor( Type.getObjectType( samInternalName ), capturedTypes ),
                                                 METAFACTORY, samMethodType, bodyHandle, samMethodType );

            this.wrapper.visitMethodInsn( Opcodes.INVOKESTATIC, SUPPORT_INTERNAL_NAME, "trace", isVoid
                ? "(Ljava/lang/String;L" + VOID_CALL_INTERNAL_NAME + ";)V"
                : "(Ljava/lang/String;L" + CALL_INTERNAL_NAME + ";)Ljava/lang/Object;", false );

            emitReturn( returnType );

            this.wrapper.visitMaxs( 0, 0 );
        }

        private void loadReceiverAndArguments( final Type[] argumentTypes, final boolean isStatic )
        {
            int slot = 0;
            if ( !isStatic )
            {
                this.wrapper.visitVarInsn( Opcodes.ALOAD, 0 );
                slot = 1;
            }
            for ( final Type argumentType : argumentTypes )
            {
                this.wrapper.visitVarInsn( argumentType.getOpcode( Opcodes.ILOAD ), slot );
                slot += argumentType.getSize();
            }
        }

        private void emitReturn( final Type returnType )
        {
            switch ( returnType.getSort() )
            {
                case Type.VOID:
                    this.wrapper.visitInsn( Opcodes.RETURN );
                    break;
                case Type.BOOLEAN:
                    unboxAndReturn( "java/lang/Boolean", "booleanValue", "()Z", Opcodes.IRETURN );
                    break;
                case Type.CHAR:
                    unboxAndReturn( "java/lang/Character", "charValue", "()C", Opcodes.IRETURN );
                    break;
                case Type.BYTE:
                    unboxAndReturn( "java/lang/Byte", "byteValue", "()B", Opcodes.IRETURN );
                    break;
                case Type.SHORT:
                    unboxAndReturn( "java/lang/Short", "shortValue", "()S", Opcodes.IRETURN );
                    break;
                case Type.INT:
                    unboxAndReturn( "java/lang/Integer", "intValue", "()I", Opcodes.IRETURN );
                    break;
                case Type.FLOAT:
                    unboxAndReturn( "java/lang/Float", "floatValue", "()F", Opcodes.FRETURN );
                    break;
                case Type.LONG:
                    unboxAndReturn( "java/lang/Long", "longValue", "()J", Opcodes.LRETURN );
                    break;
                case Type.DOUBLE:
                    unboxAndReturn( "java/lang/Double", "doubleValue", "()D", Opcodes.DRETURN );
                    break;
                default:
                    if ( !"java/lang/Object".equals( returnType.getInternalName() ) )
                    {
                        this.wrapper.visitTypeInsn( Opcodes.CHECKCAST, returnType.getInternalName() );
                    }
                    this.wrapper.visitInsn( Opcodes.ARETURN );
                    break;
            }
        }

        private void unboxAndReturn( final String boxInternalName, final String unboxMethod, final String unboxDescriptor,
                                     final int returnOpcode )
        {
            this.wrapper.visitTypeInsn( Opcodes.CHECKCAST, boxInternalName );
            this.wrapper.visitMethodInsn( Opcodes.INVOKEVIRTUAL, boxInternalName, unboxMethod, unboxDescriptor, false );
            this.wrapper.visitInsn( returnOpcode );
        }
    }
}
