package com.enonic.wem.api.schema.content;


import com.enonic.wem.api.content.QualifiedName;

public final class QualifiedContentTypeName
    extends QualifiedName
{
    private static final QualifiedContentTypeName UNSTRUCTURED = new QualifiedContentTypeName( "unstructured" );

    private static final QualifiedContentTypeName SPACE = new QualifiedContentTypeName( "space" );

    private static final QualifiedContentTypeName STRUCTURED = new QualifiedContentTypeName( "structured" );

    private static final QualifiedContentTypeName FOLDER = new QualifiedContentTypeName( "folder" );

    private static final QualifiedContentTypeName PAGE = new QualifiedContentTypeName( "page" );

    private static final QualifiedContentTypeName SHORTCUT = new QualifiedContentTypeName( "shortcut" );

    private static final QualifiedContentTypeName MEDIA = new QualifiedContentTypeName( "media" );

    private static final QualifiedContentTypeName MEDIA_TEXT = new QualifiedContentTypeName( "text" );

    private static final QualifiedContentTypeName MEDIA_DATA = new QualifiedContentTypeName( "data" );

    private static final QualifiedContentTypeName MEDIA_AUDIO = new QualifiedContentTypeName( "audio" );

    private static final QualifiedContentTypeName MEDIA_VIDEO = new QualifiedContentTypeName( "video" );

    private static final QualifiedContentTypeName MEDIA_IMAGE = new QualifiedContentTypeName( "image" );

    private static final QualifiedContentTypeName MEDIA_VECTOR = new QualifiedContentTypeName( "vector" );

    private static final QualifiedContentTypeName MEDIA_ARCHIVE = new QualifiedContentTypeName( "archive" );

    private static final QualifiedContentTypeName MEDIA_DOCUMENT = new QualifiedContentTypeName( "document" );

    private static final QualifiedContentTypeName MEDIA_SPREADSHEET = new QualifiedContentTypeName( "spreadsheet" );

    private static final QualifiedContentTypeName MEDIA_PRESENTATION = new QualifiedContentTypeName( "presentation" );

    private static final QualifiedContentTypeName MEDIA_CODE = new QualifiedContentTypeName( "code" );

    private static final QualifiedContentTypeName MEDIA_EXECUTABLE = new QualifiedContentTypeName( "executable" );

    private QualifiedContentTypeName( final String name )
    {
        super( name );
    }

    public String getContentTypeName()
    {
        return getName();
    }

    public boolean isSpace()
    {
        return SPACE.equals( this );
    }

    public boolean isUnstructured()
    {
        return UNSTRUCTURED.equals( this );
    }

    public boolean isStructured()
    {
        return STRUCTURED.equals( this );
    }

    public boolean isFolder()
    {
        return FOLDER.equals( this );
    }

    public boolean isPage()
    {
        return PAGE.equals( this );
    }

    public boolean isShortcut()
    {
        return SHORTCUT.equals( this );
    }

    public boolean isMedia()
    {
        return MEDIA.equals( this );
    }

    public boolean isTextMedia()
    {
        return MEDIA_TEXT.equals( this );
    }

    public boolean isDataMedia()
    {
        return MEDIA_DATA.equals( this );
    }

    public boolean isAudioMedia()
    {
        return MEDIA_AUDIO.equals( this );
    }

    public boolean isVideoMedia()
    {
        return MEDIA_VIDEO.equals( this );
    }

    public boolean isImageMedia()
    {
        return MEDIA_IMAGE.equals( this );
    }

    public boolean isVectorMedia()
    {
        return MEDIA_VECTOR.equals( this );
    }

    public boolean isArchiveMedia()
    {
        return MEDIA_ARCHIVE.equals( this );
    }

    public boolean isDocumentMedia()
    {
        return MEDIA_DOCUMENT.equals( this );
    }

    public boolean isSpreadsheetMedia()
    {
        return MEDIA_SPREADSHEET.equals( this );
    }

    public boolean isPresentationMedia()
    {
        return MEDIA_PRESENTATION.equals( this );
    }

    public boolean isCodeMedia()
    {
        return MEDIA_CODE.equals( this );
    }

    public boolean isExecutableMedia()
    {
        return MEDIA_EXECUTABLE.equals( this );
    }

    public static QualifiedContentTypeName space()
    {
        return SPACE;
    }

    public static QualifiedContentTypeName structured()
    {
        return STRUCTURED;
    }

    public static QualifiedContentTypeName unstructured()
    {
        return UNSTRUCTURED;
    }

    public static QualifiedContentTypeName folder()
    {
        return FOLDER;
    }

    public static QualifiedContentTypeName page()
    {
        return PAGE;
    }

    public static QualifiedContentTypeName shortcut()
    {
        return SHORTCUT;
    }

    public static QualifiedContentTypeName media()
    {
        return MEDIA;
    }

    public static QualifiedContentTypeName textMedia()
    {
        return MEDIA_TEXT;
    }

    public static QualifiedContentTypeName dataMedia()
    {
        return MEDIA_DATA;
    }

    public static QualifiedContentTypeName audioMedia()
    {
        return MEDIA_AUDIO;
    }

    public static QualifiedContentTypeName videoMedia()
    {
        return MEDIA_VIDEO;
    }

    public static QualifiedContentTypeName imageMedia()
    {
        return MEDIA_IMAGE;
    }

    public static QualifiedContentTypeName vectorMedia()
    {
        return MEDIA_VECTOR;
    }

    public static QualifiedContentTypeName archiveMedia()
    {
        return MEDIA_ARCHIVE;
    }

    public static QualifiedContentTypeName documentMedia()
    {
        return MEDIA_DOCUMENT;
    }

    public static QualifiedContentTypeName spreadsheetMedia()
    {
        return MEDIA_SPREADSHEET;
    }

    public static QualifiedContentTypeName presentationMedia()
    {
        return MEDIA_PRESENTATION;
    }

    public static QualifiedContentTypeName codeMedia()
    {
        return MEDIA_CODE;
    }

    public static QualifiedContentTypeName executableMedia()
    {
        return MEDIA_EXECUTABLE;
    }

    public static QualifiedContentTypeName from( String contentTypeName )
    {
        return new QualifiedContentTypeName( contentTypeName );
    }

}
