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
                return ContentTypeName.from( "mycontenttype" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ContentTypeName.from( "myothercontenttype" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return ContentTypeName.from( "mycontenttype" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return ContentTypeName.from( "mycontenttype" );
            }
        };
        equalsTest.assertEqualsAndHashCodeContract();
    }

    @Test
    public void testPredefinedTypes()
    {
        assertTrue( ContentTypeName.media().isMedia() );
        assertTrue( ContentTypeName.folder().isFolder() );
        assertTrue( ContentTypeName.page().isPage() );
        assertTrue( ContentTypeName.shortcut().isShortcut() );
        assertTrue( ContentTypeName.space().isSpace() );
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

        assertEquals( "media", ContentTypeName.media().getContentTypeName() );
        assertEquals( "folder", ContentTypeName.folder().getContentTypeName() );
        assertEquals( "page", ContentTypeName.page().getContentTypeName() );
        assertEquals( "shortcut", ContentTypeName.shortcut().getContentTypeName() );
        assertEquals( "space", ContentTypeName.space().getContentTypeName() );
        assertEquals( "structured", ContentTypeName.structured().getContentTypeName() );
        assertEquals( "unstructured", ContentTypeName.unstructured().getContentTypeName() );

        assertEquals( "text", ContentTypeName.textMedia().getContentTypeName() );
        assertEquals( "data", ContentTypeName.dataMedia().getContentTypeName() );
        assertEquals( "audio", ContentTypeName.audioMedia().getContentTypeName() );
        assertEquals( "video", ContentTypeName.videoMedia().getContentTypeName() );
        assertEquals( "image", ContentTypeName.imageMedia().getContentTypeName() );
        assertEquals( "vector", ContentTypeName.vectorMedia().getContentTypeName() );
        assertEquals( "archive", ContentTypeName.archiveMedia().getContentTypeName() );
        assertEquals( "document", ContentTypeName.documentMedia().getContentTypeName() );
        assertEquals( "spreadsheet", ContentTypeName.spreadsheetMedia().getContentTypeName() );
        assertEquals( "presentation", ContentTypeName.presentationMedia().getContentTypeName() );
        assertEquals( "code", ContentTypeName.codeMedia().getContentTypeName() );
        assertEquals( "executable", ContentTypeName.executableMedia().getContentTypeName() );
    }
}
