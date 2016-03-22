package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class BuiltinContentTypesTest
{
    @Test
    public void testBuiltin()
    {
        final ContentTypes types = new BuiltinContentTypes().getAll();
        assertEquals( 22, types.getSize() );

        assertType( types.get( 0 ), "base:unstructured", true );
        assertType( types.get( 1 ), "base:structured", true );
        assertType( types.get( 2 ), "base:folder", true );
        assertType( types.get( 3 ), "base:shortcut", true );
        assertType( types.get( 4 ), "base:media", true );
        assertType( types.get( 5 ), "media:text", true );
        assertType( types.get( 6 ), "media:data", true );
        assertType( types.get( 7 ), "media:audio", true );
        assertType( types.get( 8 ), "media:video", true );
        assertType( types.get( 9 ), "media:image", true );
        assertType( types.get( 10 ), "media:vector", true );
        assertType( types.get( 11 ), "media:archive", true );
        assertType( types.get( 12 ), "media:document", true );
        assertType( types.get( 13 ), "media:spreadsheet", true );
        assertType( types.get( 14 ), "media:presentation", true );
        assertType( types.get( 15 ), "media:code", true );
        assertType( types.get( 16 ), "media:executable", true );
        assertType( types.get( 17 ), "media:unknown", false );
        assertType( types.get( 18 ), "portal:site", true );
        assertType( types.get( 19 ), "portal:template-folder", true );
        assertType( types.get( 20 ), "portal:page-template", true );
        assertType( types.get( 21 ), "portal:fragment", true );
    }

    private void assertType( final ContentType type, final String name, final boolean hasIcon )
    {
        assertEquals( name, type.getName().toString() );
        assertEquals( hasIcon, type.getIcon() != null );
    }
}
