package com.enonic.wem.core.plugin.ext;


import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.plugin.ext.Extension;

import static org.junit.Assert.*;

public abstract class ExtensionPointTest<E extends Extension, P extends ExtensionPoint<E>>
{
    protected P extensions;

    protected E ext1;

    protected E ext2;

    private final Class<E> type;

    public ExtensionPointTest( final Class<E> type )
    {
        this.type = type;
    }

    @Before
    public final void setUp()
    {
        this.extensions = createExtensionPoint();
        this.ext1 = createOne();
        this.ext2 = createTwo();
    }

    protected abstract P createExtensionPoint();

    protected abstract E createOne();

    protected abstract E createTwo();

    @Test
    public void testName()
    {
        final String name = this.extensions.getName();
        assertNotNull( name );
        assertEquals( this.type.getSimpleName(), name );
    }

    @Test
    public void testEmpty()
    {
        assertTrue( this.extensions.isEmpty() );
        this.extensions.addExtension( this.ext1 );
        assertFalse( this.extensions.isEmpty() );
        this.extensions.removeExtension( this.ext1 );
        assertTrue( this.extensions.isEmpty() );
    }

    @Test
    public void testIterate()
    {
        final Iterator<E> it1 = this.extensions.iterator();
        assertFalse( it1.hasNext() );

        this.extensions.addExtension( this.ext2 );
        this.extensions.addExtension( this.ext1 );

        final Iterator<E> it2 = this.extensions.iterator();
        assertSame( this.ext1, it2.next() );
        assertSame( this.ext2, it2.next() );
        assertFalse( it2.hasNext() );
    }
}
