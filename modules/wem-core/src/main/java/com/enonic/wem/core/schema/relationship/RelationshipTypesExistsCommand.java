package com.enonic.wem.core.schema.relationship;

import com.enonic.wem.api.command.schema.relationship.RelationshipTypesExistsResult;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;


final class RelationshipTypesExistsCommand
{
    private RelationshipTypeDao relationshipTypeDao;

    private RelationshipTypeNames relationshipTypeNames;

    RelationshipTypesExistsResult execute()
    {
        final RelationshipTypeNames existing = relationshipTypeDao.exists( relationshipTypeNames );

        return RelationshipTypesExistsResult.from( existing );
    }

    RelationshipTypesExistsCommand relationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
        return this;
    }

    RelationshipTypesExistsCommand names( final RelationshipTypeNames names )
    {
        this.relationshipTypeNames = names;
        return this;
    }
}
