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
    private RelationshipTypeNames qualifiedNames;

    RelationshipTypeDaoHandlerExists( final Session session )
    {
        super( session );
    }


    RelationshipTypeDaoHandlerExists selectors( final RelationshipTypeNames qNames )
    {
        this.qualifiedNames = qNames;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        setResult( handle( qualifiedNames ) );
    }

    private RelationshipTypeNames handle( final RelationshipTypeNames qualifiedNames )
        throws RepositoryException
    {
        final List<RelationshipTypeName> existing = Lists.newArrayList();
        for ( RelationshipTypeName qName : qualifiedNames )
        {
            if ( nodeExists( qName ) )
            {
                existing.add( qName );
            }

        }
        return RelationshipTypeNames.from( existing );
    }
}
