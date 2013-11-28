package com.enonic.wem.api.schema.content;


import com.enonic.wem.api.schema.SchemaName;

public final class ContentTypeName
    extends SchemaName
{
    private static final ContentTypeName UNSTRUCTURED = new ContentTypeName( "unstructured" );

    private static final ContentTypeName STRUCTURED = new ContentTypeName( "structured" );

    private static final ContentTypeName FOLDER = new ContentTypeName( "folder" );

    private static final ContentTypeName PAGE = new ContentTypeName( "page" );

    private static final ContentTypeName SHORTCUT = new ContentTypeName( "shortcut" );

    private static final ContentTypeName MEDIA = new ContentTypeName( "media" );

    private static final ContentTypeName MEDIA_TEXT = new ContentTypeName( "text" );

    private static final ContentTypeName MEDIA_DATA = new ContentTypeName( "data" );

    private static final ContentTypeName MEDIA_AUDIO = new ContentTypeName( "audio" );

    private static final ContentTypeName MEDIA_VIDEO = new ContentTypeName( "video" );

    private static final ContentTypeName MEDIA_IMAGE = new ContentTypeName( "image" );

    private static final ContentTypeName MEDIA_VECTOR = new ContentTypeName( "vector" );

    private static final ContentTypeName MEDIA_ARCHIVE = new ContentTypeName( "archive" );

    private static final ContentTypeName MEDIA_DOCUMENT = new ContentTypeName( "document" );

    private static final ContentTypeName MEDIA_SPREADSHEET = new ContentTypeName( "spreadsheet" );

    private static final ContentTypeName MEDIA_PRESENTATION = new ContentTypeName( "presentation" );

    private static final ContentTypeName MEDIA_CODE = new ContentTypeName( "code" );

    private static final ContentTypeName MEDIA_EXECUTABLE = new ContentTypeName( "executable" );

    private ContentTypeName( final String name )
    {
        super( name );
    }

    public String getContentTypeName()
    {
        return toString();
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

    public static ContentTypeName structured()
    {
        return STRUCTURED;
    }

    public static ContentTypeName unstructured()
    {
        return UNSTRUCTURED;
    }

    public static ContentTypeName folder()
    {
        return FOLDER;
    }

    public static ContentTypeName page()
    {
        return PAGE;
    }

    public static ContentTypeName shortcut()
    {
        return SHORTCUT;
    }

    public static ContentTypeName media()
    {
        return MEDIA;
    }

    public static ContentTypeName textMedia()
    {
        return MEDIA_TEXT;
    }

    public static ContentTypeName dataMedia()
    {
        return MEDIA_DATA;
    }

    public static ContentTypeName audioMedia()
    {
        return MEDIA_AUDIO;
    }

    public static ContentTypeName videoMedia()
    {
        return MEDIA_VIDEO;
    }

    public static ContentTypeName imageMedia()
    {
        return MEDIA_IMAGE;
    }

    public static ContentTypeName vectorMedia()
    {
        return MEDIA_VECTOR;
    }

    public static ContentTypeName archiveMedia()
    {
        return MEDIA_ARCHIVE;
    }

    public static ContentTypeName documentMedia()
    {
        return MEDIA_DOCUMENT;
    }

    public static ContentTypeName spreadsheetMedia()
    {
        return MEDIA_SPREADSHEET;
    }

    public static ContentTypeName presentationMedia()
    {
        return MEDIA_PRESENTATION;
    }

    public static ContentTypeName codeMedia()
    {
        return MEDIA_CODE;
    }

    public static ContentTypeName executableMedia()
    {
        return MEDIA_EXECUTABLE;
    }

    public static ContentTypeName from( String contentTypeName )
    {
        return new ContentTypeName( contentTypeName );
    }

}
