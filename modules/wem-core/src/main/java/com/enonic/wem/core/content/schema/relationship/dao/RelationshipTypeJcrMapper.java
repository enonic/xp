package com.enonic.wem.core.content.schema.relationship.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.core.content.schema.relationship.RelationshipTypeJsonSerializer;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.api.content.schema.relationship.RelationshipType.newRelationshipType;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyDateTime;
import static com.enonic.wem.core.jcr.JcrHelper.setPropertyDateTime;

class RelationshipTypeJcrMapper
{
    private static final String RELATIONSHIP_TYPE = "relationshipType";

    private static final String CREATED_TIME = "createdTime";

    private static final String MODIFIED_TIME = "modifiedTime";

    private final RelationshipTypeJsonSerializer jsonSerializer = new RelationshipTypeJsonSerializer();

    private final IconJcrMapper iconJcrMapper = new IconJcrMapper();

    void toJcr( final RelationshipType relationshipType, final Node relationshipTypeNode )
        throws RepositoryException
    {
        final String relationshipTypeJson = jsonSerializer.toString( relationshipType );
        relationshipTypeNode.setProperty( RELATIONSHIP_TYPE, relationshipTypeJson );
        setPropertyDateTime( relationshipTypeNode, CREATED_TIME, relationshipType.getCreatedTime() );
        setPropertyDateTime( relationshipTypeNode, MODIFIED_TIME, relationshipType.getModifiedTime() );
        iconJcrMapper.toJcr( relationshipType.getIcon(), relationshipTypeNode );
    }

    RelationshipType toRelationshipType( final Node relationshipTypeNode )
        throws RepositoryException
    {
        final String relationshipTypeJson = relationshipTypeNode.getProperty( RELATIONSHIP_TYPE ).getString();
        return newRelationshipType( jsonSerializer.toObject( relationshipTypeJson ) ).
            createdTime( getPropertyDateTime( relationshipTypeNode, CREATED_TIME ) ).
            modifiedTime( getPropertyDateTime( relationshipTypeNode, MODIFIED_TIME ) ).
            icon( iconJcrMapper.toIcon( relationshipTypeNode ) ).
            build();
    }

}
