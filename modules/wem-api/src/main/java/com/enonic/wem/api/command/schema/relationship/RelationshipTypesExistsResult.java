package com.enonic.wem.api.command.schema.relationship;


import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

public class RelationshipTypesExistsResult
{
    // DO NOT COMMIT ?
    private final RelationshipTypeNames qualifiedNames;

    private RelationshipTypesExistsResult( final RelationshipTypeNames qualifiedNames )
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

    public boolean exists( RelationshipTypeName qualifiedName )
    {
        return qualifiedNames.contains( qualifiedName );
    }

    public RelationshipTypeNames getQualifiedNames()
    {
        return qualifiedNames;
    }

    public static RelationshipTypesExistsResult empty()
    {
        return new RelationshipTypesExistsResult( RelationshipTypeNames.empty() );
    }

    public static RelationshipTypesExistsResult from( final RelationshipTypeNames existing )
    {
        return new RelationshipTypesExistsResult( existing );
    }
}
