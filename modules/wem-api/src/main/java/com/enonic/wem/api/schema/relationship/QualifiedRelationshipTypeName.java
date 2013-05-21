package com.enonic.wem.api.schema.relationship;


import com.enonic.wem.api.Name;
import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public final class QualifiedRelationshipTypeName
    extends ModuleBasedQualifiedName
{
    public static final QualifiedRelationshipTypeName DEFAULT = new QualifiedRelationshipTypeName( ModuleName.SYSTEM, "default" );

    public static final QualifiedRelationshipTypeName PARENT = new QualifiedRelationshipTypeName( ModuleName.SYSTEM, "parent" );

    public static final QualifiedRelationshipTypeName LINK = new QualifiedRelationshipTypeName( ModuleName.SYSTEM, "link" );

    public static final QualifiedRelationshipTypeName LIKE = new QualifiedRelationshipTypeName( ModuleName.SYSTEM, "like" );

    public QualifiedRelationshipTypeName( final ModuleName moduleName, final String name )
    {
        super( moduleName, name );
    }

    public QualifiedRelationshipTypeName( final ModuleName moduleName, final Name name )
    {
        super( moduleName, name );
    }

    private QualifiedRelationshipTypeName( final String qualifiedName )
    {
        super( qualifiedName );
    }

    public static QualifiedRelationshipTypeName from( String qualifiedRelationTypeName )
    {
        return new QualifiedRelationshipTypeName( qualifiedRelationTypeName );
    }
}
