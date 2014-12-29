package com.enonic.wem.core.schema.content;

import org.junit.Test;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class BuiltinContentTypeProviderTest
{
    @Test
    public void testBuiltin()
    {
        final ContentTypes types = new BuiltinContentTypeProvider().get();
        assertEquals( 21, types.getSize() );

        assertType( types.get( 0 ), "system:unstructured", true );
        assertType( types.get( 1 ), "system:structured", true );
        assertType( types.get( 2 ), "system:folder", true );
        assertType( types.get( 3 ), "system:shortcut", true );
        assertType( types.get( 4 ), "system:media", true );
        assertType( types.get( 5 ), "system:text", true );
        assertType( types.get( 6 ), "system:data", true );
        assertType( types.get( 7 ), "system:audio", true );
        assertType( types.get( 8 ), "system:video", true );
        assertType( types.get( 9 ), "system:image", true );
        assertType( types.get( 10 ), "system:vector", true );
        assertType( types.get( 11 ), "system:archive", true );
        assertType( types.get( 12 ), "system:document", true );
        assertType( types.get( 13 ), "system:spreadsheet", true );
        assertType( types.get( 14 ), "system:presentation", true );
        assertType( types.get( 15 ), "system:code", true );
        assertType( types.get( 16 ), "system:executable", true );
        assertType( types.get( 17 ), "system:unknown", false );
        assertType( types.get( 18 ), "system:site", true );
        assertType( types.get( 19 ), "system:template-folder", true );
        assertType( types.get( 20 ), "system:page-template", true );
    }

    private void assertType( final ContentType type, final String name, final boolean hasIcon )
    {
        assertEquals( name, type.getName().toString() );
        assertEquals( hasIcon, type.getIcon() != null );
    }
}
