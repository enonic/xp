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

        assertEquals( "system:media", ContentTypeName.media().getContentTypeName() );
        assertEquals( "system:folder", ContentTypeName.folder().getContentTypeName() );
        assertEquals( "system:page-template", ContentTypeName.pageTemplate().getContentTypeName() );
        assertEquals( "system:shortcut", ContentTypeName.shortcut().getContentTypeName() );
        assertEquals( "system:structured", ContentTypeName.structured().getContentTypeName() );
        assertEquals( "system:unstructured", ContentTypeName.unstructured().getContentTypeName() );

        assertEquals( "system:text", ContentTypeName.textMedia().getContentTypeName() );
        assertEquals( "system:data", ContentTypeName.dataMedia().getContentTypeName() );
        assertEquals( "system:audio", ContentTypeName.audioMedia().getContentTypeName() );
        assertEquals( "system:video", ContentTypeName.videoMedia().getContentTypeName() );
        assertEquals( "system:image", ContentTypeName.imageMedia().getContentTypeName() );
        assertEquals( "system:vector", ContentTypeName.vectorMedia().getContentTypeName() );
        assertEquals( "system:archive", ContentTypeName.archiveMedia().getContentTypeName() );
        assertEquals( "system:document", ContentTypeName.documentMedia().getContentTypeName() );
        assertEquals( "system:spreadsheet", ContentTypeName.spreadsheetMedia().getContentTypeName() );
        assertEquals( "system:presentation", ContentTypeName.presentationMedia().getContentTypeName() );
        assertEquals( "system:code", ContentTypeName.codeMedia().getContentTypeName() );
        assertEquals( "system:executable", ContentTypeName.executableMedia().getContentTypeName() );
    }
}
