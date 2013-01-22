package com.enonic.wem.api.command.content.relation;


import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;

public class RelationshipTypesExistsResult
{
    private final QualifiedRelationshipTypeNames qualifiedNames;

    private RelationshipTypesExistsResult( final QualifiedRelationshipTypeNames qualifiedNames )
    {
        this.qualifiedNames = qualifiedNames;
    }

    public boolean isEmpty()
    {
        return qualifiedNames.isEmpty();
    }

    public boolean isNotEmpty()
    {
        return qualifiedNames.isEmpty();
    }

    public boolean exists( QualifiedRelationshipTypeName qualifiedName )
    {
        return qualifiedNames.contains( qualifiedName );
    }

    public QualifiedRelationshipTypeNames getQualifiedNames()
    {
        return qualifiedNames;
    }

    public static RelationshipTypesExistsResult empty()
    {
        return new RelationshipTypesExistsResult( QualifiedRelationshipTypeNames.empty() );
    }

    public static RelationshipTypesExistsResult from( final QualifiedRelationshipTypeNames existing )
    {
        return new RelationshipTypesExistsResult( existing );
    }
}
