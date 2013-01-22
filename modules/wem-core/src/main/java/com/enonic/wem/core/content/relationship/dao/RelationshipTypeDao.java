package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelector;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrConstants;

public interface RelationshipTypeDao
{
    public static final String RELATIONSHIP_TYPES_NODE = "relationshipTypes";

    public static final String RELATIONSHIP_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + RELATIONSHIP_TYPES_NODE + "/";

    public void createRelationshipType( RelationshipType relationshipType, Session session );

    public void updateRelationshipType( RelationshipType relationshipType, Session session );

    public void deleteRelationshipType( QualifiedRelationshipTypeName relationshipTypeName, Session session );

    public RelationshipTypes retrieveAllRelationshipTypes( Session session );

    public RelationshipTypes retrieveRelationshipTypes( RelationshipTypeSelector selector, Session session );

    public QualifiedRelationshipTypeNames exists( RelationshipTypeSelector selector, Session session );
}
