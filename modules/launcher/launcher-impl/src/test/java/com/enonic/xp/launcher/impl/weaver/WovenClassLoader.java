package com.enonic.xp.launcher.impl.weaver;

/**
 * Class loader that defines a single class from (woven) bytes and delegates everything else to the parent.
 */
final class WovenClassLoader
    extends ClassLoader
{
    private final String className;

    private final byte[] bytes;

    WovenClassLoader( final String className, final byte[] bytes, final ClassLoader parent )
    {
        super( parent );
        this.className = className;
        this.bytes = bytes;
    }

    @Override
    protected Class<?> loadClass( final String name, final boolean resolve )
        throws ClassNotFoundException
    {
        if ( this.className.equals( name ) )
        {
            synchronized ( getClassLoadingLock( name ) )
            {
                Class<?> loaded = findLoadedClass( name );
                if ( loaded == null )
                {
                    loaded = defineClass( name, this.bytes, 0, this.bytes.length );
                }
                if ( resolve )
                {
                    resolveClass( loaded );
                }
                return loaded;
            }
        }
        return super.loadClass( name, resolve );
    }
}
