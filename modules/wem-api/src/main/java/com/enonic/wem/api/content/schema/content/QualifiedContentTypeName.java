package com.enonic.wem.api.content.schema.content;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;

public final class QualifiedContentTypeName
    extends ModuleBasedQualifiedName
{
    private static final QualifiedContentTypeName UNSTRUCTURED = new QualifiedContentTypeName( Module.SYSTEM.getName(), "unstructured" );

    private static final QualifiedContentTypeName SPACE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "space" );

    private static final QualifiedContentTypeName STRUCTURED = new QualifiedContentTypeName( Module.SYSTEM.getName(), "structured" );

    private static final QualifiedContentTypeName FOLDER = new QualifiedContentTypeName( Module.SYSTEM.getName(), "folder" );

    private static final QualifiedContentTypeName PAGE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "page" );

    private static final QualifiedContentTypeName SHORTCUT = new QualifiedContentTypeName( Module.SYSTEM.getName(), "shortcut" );

    private static final QualifiedContentTypeName FILE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "file" );

    private static final QualifiedContentTypeName FILE_TEXT = new QualifiedContentTypeName( Module.SYSTEM.getName(), "text" );

    private static final QualifiedContentTypeName FILE_DATA = new QualifiedContentTypeName( Module.SYSTEM.getName(), "data" );

    private static final QualifiedContentTypeName FILE_AUDIO = new QualifiedContentTypeName( Module.SYSTEM.getName(), "audio" );

    private static final QualifiedContentTypeName FILE_VIDEO = new QualifiedContentTypeName( Module.SYSTEM.getName(), "video" );

    private static final QualifiedContentTypeName FILE_IMAGE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "image" );

    private static final QualifiedContentTypeName FILE_VECTOR = new QualifiedContentTypeName( Module.SYSTEM.getName(), "vector" );

    private static final QualifiedContentTypeName FILE_ARCHIVE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "archive" );

    private static final QualifiedContentTypeName FILE_DOCUMENT = new QualifiedContentTypeName( Module.SYSTEM.getName(), "document" );

    private static final QualifiedContentTypeName FILE_SPREADSHEET = new QualifiedContentTypeName( Module.SYSTEM.getName(), "spreadsheet" );

    private static final QualifiedContentTypeName FILE_PRESENTATION =
        new QualifiedContentTypeName( Module.SYSTEM.getName(), "presentation" );

    private static final QualifiedContentTypeName FILE_CODE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "code" );

    private static final QualifiedContentTypeName FILE_EXECUTABLE = new QualifiedContentTypeName( Module.SYSTEM.getName(), "executable" );

    public QualifiedContentTypeName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public QualifiedContentTypeName( final ModuleName moduleName, final String contentTypeName )
    {
        super( moduleName, contentTypeName );
    }

    public String getContentTypeName()
    {
        return getLocalName();
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

    public boolean isFile()
    {
        return FILE.equals( this );
    }

    public boolean isTextFile()
    {
        return FILE_TEXT.equals( this );
    }

    public boolean isDataFile()
    {
        return FILE_DATA.equals( this );
    }

    public boolean isAudioFile()
    {
        return FILE_AUDIO.equals( this );
    }

    public boolean isVideoFile()
    {
        return FILE_VIDEO.equals( this );
    }

    public boolean isImageFile()
    {
        return FILE_IMAGE.equals( this );
    }

    public boolean isVectorFile()
    {
        return FILE_VECTOR.equals( this );
    }

    public boolean isArchiveFile()
    {
        return FILE_ARCHIVE.equals( this );
    }

    public boolean isDocumentFile()
    {
        return FILE_DOCUMENT.equals( this );
    }

    public boolean isSpreadsheetFile()
    {
        return FILE_SPREADSHEET.equals( this );
    }

    public boolean isPresentationFile()
    {
        return FILE_PRESENTATION.equals( this );
    }

    public boolean isCodeFile()
    {
        return FILE_CODE.equals( this );
    }

    public boolean isExecutableFile()
    {
        return FILE_EXECUTABLE.equals( this );
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

    public static QualifiedContentTypeName file()
    {
        return FILE;
    }

    public static QualifiedContentTypeName textFile()
    {
        return FILE_TEXT;
    }

    public static QualifiedContentTypeName dataFile()
    {
        return FILE_DATA;
    }

    public static QualifiedContentTypeName audioFile()
    {
        return FILE_AUDIO;
    }

    public static QualifiedContentTypeName videoFile()
    {
        return FILE_VIDEO;
    }

    public static QualifiedContentTypeName imageFile()
    {
        return FILE_IMAGE;
    }

    public static QualifiedContentTypeName vectorFile()
    {
        return FILE_VECTOR;
    }

    public static QualifiedContentTypeName archiveFile()
    {
        return FILE_ARCHIVE;
    }

    public static QualifiedContentTypeName documentFile()
    {
        return FILE_DOCUMENT;
    }

    public static QualifiedContentTypeName spreadsheetFile()
    {
        return FILE_SPREADSHEET;
    }

    public static QualifiedContentTypeName presentationFile()
    {
        return FILE_PRESENTATION;
    }

    public static QualifiedContentTypeName codeFile()
    {
        return FILE_CODE;
    }

    public static QualifiedContentTypeName executableFile()
    {
        return FILE_EXECUTABLE;
    }

    public static QualifiedContentTypeName from( String qualifiedContentTypeName )
    {
        return new QualifiedContentTypeName( qualifiedContentTypeName );
    }

    public static QualifiedContentTypeName from( ModuleName module, final String name )
    {
        return new QualifiedContentTypeName( module, name );
    }

}
