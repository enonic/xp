package com.enonic.wem.core.content.relation.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relation.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relation.RelationshipType;
import com.enonic.wem.api.content.relation.RelationshipTypes;

public interface RelationshipTypeDao
{
    public void createRelationshipType( RelationshipType relationshipType, Session session );

    public void updateRelationshipType( RelationshipType relationshipType, Session session );

    public void deleteRelationshipType( QualifiedRelationshipTypeName relationshipTypeName, Session session );

    public RelationshipTypes retrieveAllRelationshipTypes( Session session );

    public RelationshipTypes retrieveRelationshipTypes( QualifiedRelationshipTypeNames relationshipTypeNames, Session session );
}
