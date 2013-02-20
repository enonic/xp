package com.enonic.wem.api.content.schema.relationship;


import com.enonic.wem.api.content.ModuleBasedQualifiedName;
import com.enonic.wem.api.module.ModuleName;

public final class QualifiedRelationshipTypeName
    extends ModuleBasedQualifiedName
{
    public QualifiedRelationshipTypeName( final ModuleName moduleName, final String name )
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
