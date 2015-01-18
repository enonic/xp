package com.enonic.wem.api.schema.content;


import org.junit.Test;

import com.enonic.wem.api.support.AbstractEqualsTest;

import static org.junit.Assert.*;

public class ContentTypeNameTest
{
    @Test
    public void equals()
    {
        AbstractEqualsTest equalsTest = new AbstractEqualsTest()
        {
            @Override
            public Object getObjectX()
            {
                return ContentTypeName.from( "mymodule:mycontenttype" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ContentTypeName.from( "mymodule:myothercontenttype" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return ContentTypeName.from( "mymodule:mycontenttype" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return ContentTypeName.from( "mymodule:mycontenttype" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testPredefinedTypes()
    {
        assertTrue( ContentTypeName.media().isMedia() );
        assertTrue( ContentTypeName.folder().isFolder() );
        assertTrue( ContentTypeName.pageTemplate().isPageTemplate() );
        assertTrue( ContentTypeName.shortcut().isShortcut() );
        assertTrue( ContentTypeName.structured().isStructured() );
        assertTrue( ContentTypeName.unstructured().isUnstructured() );

        assertTrue( ContentTypeName.textMedia().isTextMedia() );
        assertTrue( ContentTypeName.dataMedia().isDataMedia() );
        assertTrue( ContentTypeName.audioMedia().isAudioMedia() );
        assertTrue( ContentTypeName.videoMedia().isVideoMedia() );
        assertTrue( ContentTypeName.imageMedia().isImageMedia() );
        assertTrue( ContentTypeName.vectorMedia().isVectorMedia() );
        assertTrue( ContentTypeName.archiveMedia().isArchiveMedia() );
        assertTrue( ContentTypeName.documentMedia().isDocumentMedia() );
        assertTrue( ContentTypeName.spreadsheetMedia().isSpreadsheetMedia() );
        assertTrue( ContentTypeName.presentationMedia().isPresentationMedia() );
        assertTrue( ContentTypeName.codeMedia().isCodeMedia() );
        assertTrue( ContentTypeName.executableMedia().isExecutableMedia() );

        assertEquals( "system:media", ContentTypeName.media().toString() );
        assertEquals( "system:folder", ContentTypeName.folder().toString() );
        assertEquals( "system:page-template", ContentTypeName.pageTemplate().toString() );
        assertEquals( "system:shortcut", ContentTypeName.shortcut().toString() );
        assertEquals( "system:structured", ContentTypeName.structured().toString() );
        assertEquals( "system:unstructured", ContentTypeName.unstructured().toString() );

        assertEquals( "system:text", ContentTypeName.textMedia().toString() );
        assertEquals( "system:data", ContentTypeName.dataMedia().toString() );
        assertEquals( "system:audio", ContentTypeName.audioMedia().toString() );
        assertEquals( "system:video", ContentTypeName.videoMedia().toString() );
        assertEquals( "system:image", ContentTypeName.imageMedia().toString() );
        assertEquals( "system:vector", ContentTypeName.vectorMedia().toString() );
        assertEquals( "system:archive", ContentTypeName.archiveMedia().toString() );
        assertEquals( "system:document", ContentTypeName.documentMedia().toString() );
        assertEquals( "system:spreadsheet", ContentTypeName.spreadsheetMedia().toString() );
        assertEquals( "system:presentation", ContentTypeName.presentationMedia().toString() );
        assertEquals( "system:code", ContentTypeName.codeMedia().toString() );
        assertEquals( "system:executable", ContentTypeName.executableMedia().toString() );
    }
}
