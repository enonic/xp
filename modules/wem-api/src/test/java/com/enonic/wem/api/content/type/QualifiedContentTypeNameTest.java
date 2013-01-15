package com.enonic.wem.api.content.type;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QualifiedContentTypeNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{new QualifiedContentTypeName( "myModule:myOtherContentType" ),
                    new QualifiedContentTypeName( "myOtherModule:myContentType" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return new QualifiedContentTypeName( "myModule:myContentType" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testPredefinedTypes()
    {
        assertTrue( QualifiedContentTypeName.file().isFile() );
        assertTrue( QualifiedContentTypeName.folder().isFolder() );
        assertTrue( QualifiedContentTypeName.page().isPage() );
        assertTrue( QualifiedContentTypeName.shortcut().isShortcut() );
        assertTrue( QualifiedContentTypeName.space().isSpace() );
        assertTrue( QualifiedContentTypeName.structured().isStructured() );
        assertTrue( QualifiedContentTypeName.unstructured().isUnstructured() );

        assertEquals( "file", QualifiedContentTypeName.file().getContentTypeName() );
        assertEquals( "folder", QualifiedContentTypeName.folder().getContentTypeName() );
        assertEquals( "page", QualifiedContentTypeName.page().getContentTypeName() );
        assertEquals( "shortcut", QualifiedContentTypeName.shortcut().getContentTypeName() );
        assertEquals( "space", QualifiedContentTypeName.space().getContentTypeName() );
        assertEquals( "structured", QualifiedContentTypeName.structured().getContentTypeName() );
        assertEquals( "unstructured", QualifiedContentTypeName.unstructured().getContentTypeName() );
    }
}
