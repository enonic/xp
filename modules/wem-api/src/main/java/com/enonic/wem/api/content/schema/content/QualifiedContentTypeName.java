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
}
