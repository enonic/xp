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
                return ContentTypeName.from( "mymodule-1.0.0:mycontenttype" );
            }

            @Override
            public Object[] getObjectsThatNotEqualsX()
            {
                return new Object[]{ContentTypeName.from( "mymodule-1.0.0:myothercontenttype" )};
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame()
            {
                return ContentTypeName.from( "mymodule-1.0.0:mycontenttype" );
            }

            @Override
            public Object getObjectThatEqualsXButNotTheSame2()
            {
                return ContentTypeName.from( "mymodule-1.0.0:mycontenttype" );
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

        assertEquals( "system-0.0.0:media", ContentTypeName.media().getContentTypeName() );
        assertEquals( "system-0.0.0:folder", ContentTypeName.folder().getContentTypeName() );
        assertEquals( "system-0.0.0:page-template", ContentTypeName.pageTemplate().getContentTypeName() );
        assertEquals( "system-0.0.0:shortcut", ContentTypeName.shortcut().getContentTypeName() );
        assertEquals( "system-0.0.0:structured", ContentTypeName.structured().getContentTypeName() );
        assertEquals( "system-0.0.0:unstructured", ContentTypeName.unstructured().getContentTypeName() );

        assertEquals( "system-0.0.0:text", ContentTypeName.textMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:data", ContentTypeName.dataMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:audio", ContentTypeName.audioMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:video", ContentTypeName.videoMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:image", ContentTypeName.imageMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:vector", ContentTypeName.vectorMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:archive", ContentTypeName.archiveMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:document", ContentTypeName.documentMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:spreadsheet", ContentTypeName.spreadsheetMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:presentation", ContentTypeName.presentationMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:code", ContentTypeName.codeMedia().getContentTypeName() );
        assertEquals( "system-0.0.0:executable", ContentTypeName.executableMedia().getContentTypeName() );
    }
}
