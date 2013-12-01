package com.enonic.wem.api.command.schema.relationship;


import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

public class RelationshipTypesExistsResult
{
    // DO NOT COMMIT ?
    private final RelationshipTypeNames names;

    private RelationshipTypesExistsResult( final RelationshipTypeNames names )
    {
        this.names = names;
    }

    public boolean isEmpty()
    {
        return names.isEmpty();
    }

    public boolean isNotEmpty()
    {
        return names.isEmpty();
    }

    public boolean exists( RelationshipTypeName name )
    {
        return names.contains( name );
    }

    public RelationshipTypeNames getNames()
    {
        return names;
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
