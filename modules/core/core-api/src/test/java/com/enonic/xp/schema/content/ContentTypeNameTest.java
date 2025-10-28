package com.enonic.xp.schema.content;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentTypeNameTest
{
    @Test
    void equalsContract()
    {
        EqualsVerifier.forClass( ContentTypeName.class ).usingGetClass().withNonnullFields( "applicationKey", "localName" ).verify();
    }

    @Test
    void testPredefinedTypes()
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

        assertTrue( ContentTypeName.textMedia().isTextualMedia() );
        assertTrue( ContentTypeName.codeMedia().isTextualMedia() );
        assertTrue( ContentTypeName.dataMedia().isTextualMedia() );
        assertTrue( ContentTypeName.spreadsheetMedia().isTextualMedia() );
        assertTrue( ContentTypeName.presentationMedia().isTextualMedia() );
        assertTrue( ContentTypeName.documentMedia().isTextualMedia() );

        assertEquals( "base:media", ContentTypeName.media().toString() );
        assertEquals( "base:folder", ContentTypeName.folder().toString() );
        assertEquals( "portal:page-template", ContentTypeName.pageTemplate().toString() );
        assertEquals( "base:shortcut", ContentTypeName.shortcut().toString() );
        assertEquals( "base:structured", ContentTypeName.structured().toString() );
        assertEquals( "base:unstructured", ContentTypeName.unstructured().toString() );

        assertEquals( "media:text", ContentTypeName.textMedia().toString() );
        assertEquals( "media:data", ContentTypeName.dataMedia().toString() );
        assertEquals( "media:audio", ContentTypeName.audioMedia().toString() );
        assertEquals( "media:video", ContentTypeName.videoMedia().toString() );
        assertEquals( "media:image", ContentTypeName.imageMedia().toString() );
        assertEquals( "media:vector", ContentTypeName.vectorMedia().toString() );
        assertEquals( "media:archive", ContentTypeName.archiveMedia().toString() );
        assertEquals( "media:document", ContentTypeName.documentMedia().toString() );
        assertEquals( "media:spreadsheet", ContentTypeName.spreadsheetMedia().toString() );
        assertEquals( "media:presentation", ContentTypeName.presentationMedia().toString() );
        assertEquals( "media:code", ContentTypeName.codeMedia().toString() );
        assertEquals( "media:executable", ContentTypeName.executableMedia().toString() );
    }
}
