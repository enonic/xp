package com.enonic.wem.core.schema.relationship.dao;

import javax.jcr.Session;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrConstants;

public interface RelationshipTypeDao
{
    public static final String RELATIONSHIP_TYPES_NODE = "relationshipTypes";

    public static final String RELATIONSHIP_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + RELATIONSHIP_TYPES_NODE + "/";

    public void create( final RelationshipType relationshipType, final Session session );

    public void update( final RelationshipType relationshipType, final Session session );

    public void delete( final RelationshipTypeName relationshipTypeName, final Session session );

    public RelationshipTypeNames exists( final RelationshipTypeNames relationshipTypeNames, final Session session );

    public RelationshipTypes selectAll( Session session );

    public RelationshipTypes select( RelationshipTypeNames relationshipTypeNames, final Session session );

    public RelationshipType select( RelationshipTypeName relationshipTypeName, final Session session );
}
