package com.enonic.wem.core.content.relationship.dao;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationship.RelationshipType;
import com.enonic.wem.api.content.relationship.RelationshipTypeSelectors;
import com.enonic.wem.api.content.relationship.RelationshipTypes;
import com.enonic.wem.core.jcr.JcrConstants;
import com.enonic.wem.core.support.dao.CrudDao;

public interface RelationshipTypeDao
    extends
    CrudDao<RelationshipType, RelationshipTypes, QualifiedRelationshipTypeName, QualifiedRelationshipTypeNames, RelationshipTypeSelectors>
{
    public static final String RELATIONSHIP_TYPES_NODE = "relationshipTypes";

    public static final String RELATIONSHIP_TYPES_PATH = JcrConstants.ROOT_NODE + "/" + RELATIONSHIP_TYPES_NODE + "/";

}
