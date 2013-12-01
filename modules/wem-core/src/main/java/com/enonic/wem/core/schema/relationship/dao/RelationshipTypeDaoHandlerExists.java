package com.enonic.wem.core.schema.relationship.dao;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

final class RelationshipTypeDaoHandlerExists
    extends AbstractRelationshipTypeDaoHandler<RelationshipTypeNames>
{
    private RelationshipTypeNames relationshipTypeNames;

    RelationshipTypeDaoHandlerExists( final Session session )
    {
        super( session );
    }


    RelationshipTypeDaoHandlerExists selectors( final RelationshipTypeNames names )
    {
        this.relationshipTypeNames = names;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        setResult( handle( relationshipTypeNames ) );
    }

    private RelationshipTypeNames handle( final RelationshipTypeNames names )
        throws RepositoryException
    {
        final List<RelationshipTypeName> existing = Lists.newArrayList();
        for ( RelationshipTypeName name : names )
        {
            if ( nodeExists( name ) )
            {
                existing.add( name );
            }

        }
        return RelationshipTypeNames.from( existing );
    }
}
