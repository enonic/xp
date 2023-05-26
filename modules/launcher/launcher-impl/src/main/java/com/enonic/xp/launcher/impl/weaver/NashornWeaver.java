package com.enonic.xp.launcher.impl.weaver;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.startlevel.BundleStartLevel;

import net.bytebuddy.jar.asm.ClassReader;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.ClassWriter;
import net.bytebuddy.jar.asm.commons.ClassRemapper;
import net.bytebuddy.jar.asm.commons.Remapper;

public class NashornWeaver
    implements WeavingHook
{
    private final long systemStartLevel;

    public NashornWeaver( final long systemStartLevel )
    {
        this.systemStartLevel = systemStartLevel;
    }

    public void weave( final WovenClass wovenClass )
    {
        if ( wovenClass.getBundleWiring().getBundle().adapt( BundleStartLevel.class ).getStartLevel() > systemStartLevel )
        {
            AtomicBoolean classModified = new AtomicBoolean();

            ClassReader cr = new ClassReader( wovenClass.getBytes() );
            ClassWriter cw = new ClassWriter( cr, ClassWriter.COMPUTE_MAXS );

            ClassVisitor cv = new ClassRemapper( cw, new Remapper()
            {
                @Override
                public String map( String internalName )
                {
                    if ( internalName.startsWith( "jdk/nashorn/" ) )
                    {
                        classModified.set( true );
                        return "org/openjdk/" + internalName.substring( "jdk/".length() );
                    }
                    else
                    {
                        return internalName;
                    }
                }
            } );

            cr.accept( cv, ClassReader.EXPAND_FRAMES );

            if ( classModified.get() )
            {
                byte[] bytes = cw.toByteArray();

                wovenClass.setBytes( bytes );
            }
        }
    }
}
