package com.enonic.wem.core.content.relationship.dao;

import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelector;

final class RelationshipTypesExistsDaoHandler
    extends AbstractRelationshipTypeDaoHandler
{
    RelationshipTypesExistsDaoHandler( final Session session )
    {
        super( session );
    }

    QualifiedRelationshipTypeNames handle( final RelationshipTypeSelector selector )
        throws RepositoryException
    {
        if ( selector instanceof QualifiedRelationshipTypeNames )
        {
            return handle( (QualifiedRelationshipTypeNames) selector );
        }
        else
        {
            throw new UnsupportedOperationException( "selector [" + selector.getClass().getSimpleName() + " ] not supported" );
        }
    }

    QualifiedRelationshipTypeNames handle( final QualifiedRelationshipTypeNames selector )
        throws RepositoryException
    {
        final List<QualifiedRelationshipTypeName> existing = Lists.newArrayList();
        for ( QualifiedRelationshipTypeName qName : selector )
        {
            if ( nodeExists( qName ) )
            {
                existing.add( qName );
            }

        }
        return QualifiedRelationshipTypeNames.from( existing );
    }
}
