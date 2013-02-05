package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrConstants;

public interface RelationshipTypeDao
{
    public static final String RELATIONSHIP_TYPES_NODE = "relationshipTypes";

    public static final String RELATIONSHIP_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + RELATIONSHIP_TYPES_NODE + "/";

    public void create( final RelationshipType relationshipType, final Session session );

    public void update( final RelationshipType relationshipType, final Session session );

    public void delete( final QualifiedRelationshipTypeName qualifiedName, final Session session );

    public QualifiedRelationshipTypeNames exists( final QualifiedRelationshipTypeNames qNames, final Session session );

    public RelationshipTypes selectAll( Session session );

    public RelationshipTypes select( RelationshipTypeSelectors selectors, final Session session );
}
