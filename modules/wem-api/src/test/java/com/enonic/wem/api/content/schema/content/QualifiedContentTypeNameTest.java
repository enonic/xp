package com.enonic.wem.api.content.schema.content;


import org.junit.Test;

import com.enonic.wem.api.content.AbstractEqualsTest;

import static org.junit.Assert.*;

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

        assertTrue( QualifiedContentTypeName.textFile().isTextFile() );
        assertTrue( QualifiedContentTypeName.dataFile().isDataFile() );
        assertTrue( QualifiedContentTypeName.audioFile().isAudioFile() );
        assertTrue( QualifiedContentTypeName.videoFile().isVideoFile() );
        assertTrue( QualifiedContentTypeName.imageFile().isImageFile() );
        assertTrue( QualifiedContentTypeName.vectorFile().isVectorFile() );
        assertTrue( QualifiedContentTypeName.archiveFile().isArchiveFile() );
        assertTrue( QualifiedContentTypeName.documentFile().isDocumentFile() );
        assertTrue( QualifiedContentTypeName.spreadsheetFile().isSpreadsheetFile() );
        assertTrue( QualifiedContentTypeName.presentationFile().isPresentationFile() );
        assertTrue( QualifiedContentTypeName.codeFile().isCodeFile() );
        assertTrue( QualifiedContentTypeName.executableFile().isExecutableFile() );

        assertEquals( "file", QualifiedContentTypeName.file().getContentTypeName() );
        assertEquals( "folder", QualifiedContentTypeName.folder().getContentTypeName() );
        assertEquals( "page", QualifiedContentTypeName.page().getContentTypeName() );
        assertEquals( "shortcut", QualifiedContentTypeName.shortcut().getContentTypeName() );
        assertEquals( "space", QualifiedContentTypeName.space().getContentTypeName() );
        assertEquals( "structured", QualifiedContentTypeName.structured().getContentTypeName() );
        assertEquals( "unstructured", QualifiedContentTypeName.unstructured().getContentTypeName() );

        assertEquals( "text", QualifiedContentTypeName.textFile().getContentTypeName() );
        assertEquals( "data", QualifiedContentTypeName.dataFile().getContentTypeName() );
        assertEquals( "audio", QualifiedContentTypeName.audioFile().getContentTypeName() );
        assertEquals( "video", QualifiedContentTypeName.videoFile().getContentTypeName() );
        assertEquals( "image", QualifiedContentTypeName.imageFile().getContentTypeName() );
        assertEquals( "vector", QualifiedContentTypeName.vectorFile().getContentTypeName() );
        assertEquals( "archive", QualifiedContentTypeName.archiveFile().getContentTypeName() );
        assertEquals( "document", QualifiedContentTypeName.documentFile().getContentTypeName() );
        assertEquals( "spreadsheet", QualifiedContentTypeName.spreadsheetFile().getContentTypeName() );
        assertEquals( "presentation", QualifiedContentTypeName.presentationFile().getContentTypeName() );
        assertEquals( "code", QualifiedContentTypeName.codeFile().getContentTypeName() );
        assertEquals( "executable", QualifiedContentTypeName.executableFile().getContentTypeName() );
    }
}
