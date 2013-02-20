package com.enonic.wem.core.content.schema.relationshiptype.dao;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeNames;

final class RelationshipTypeDaoHandlerExists
    extends AbstractRelationshipTypeDaoHandler<QualifiedRelationshipTypeNames>
{
    private QualifiedRelationshipTypeNames qualifiedNames;

    RelationshipTypeDaoHandlerExists( final Session session )
    {
        super( session );
    }


    RelationshipTypeDaoHandlerExists selectors( final QualifiedRelationshipTypeNames qNames )
    {
        this.qualifiedNames = qNames;
        return this;
    }

    protected void doHandle()
        throws RepositoryException
    {
        setResult( handle( qualifiedNames ) );
    }

    private QualifiedRelationshipTypeNames handle( final QualifiedRelationshipTypeNames qualifiedNames )
        throws RepositoryException
    {
        final List<QualifiedRelationshipTypeName> existing = Lists.newArrayList();
        for ( QualifiedRelationshipTypeName qName : qualifiedNames )
        {
            if ( nodeExists( qName ) )
            {
                existing.add( qName );
            }

        }
        return QualifiedRelationshipTypeNames.from( existing );
    }
}
