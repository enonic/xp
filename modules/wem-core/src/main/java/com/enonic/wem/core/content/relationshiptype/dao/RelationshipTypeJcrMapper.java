package com.enonic.wem.core.content.relationshiptype.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.core.content.relationshiptype.RelationshipTypeJsonSerializer;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.api.content.relationshiptype.RelationshipType.newRelationshipType;

class RelationshipTypeJcrMapper
{
    private static final String RELATIONSHIP_TYPE = "relationshipType";

    private final RelationshipTypeJsonSerializer jsonSerializer = new RelationshipTypeJsonSerializer();

    private final IconJcrMapper iconJcrMapper = new IconJcrMapper();

    void toJcr( final RelationshipType relationshipType, final Node relationshipTypeNode )
        throws RepositoryException
    {
        final String relationshipTypeJson = jsonSerializer.toString( relationshipType );
        relationshipTypeNode.setProperty( RELATIONSHIP_TYPE, relationshipTypeJson );
        iconJcrMapper.toJcr( relationshipType.getIcon(), relationshipTypeNode );
    }

    RelationshipType toRelationshipType( final Node relationshipTypeNode )
        throws RepositoryException
    {
        final String relationshipTypeJson = relationshipTypeNode.getProperty( RELATIONSHIP_TYPE ).getString();
        final RelationshipType relationshipType = jsonSerializer.toObject( relationshipTypeJson );
        final Icon icon = iconJcrMapper.toIcon( relationshipTypeNode );
        return icon == null ? relationshipType : newRelationshipType( relationshipType ).icon( icon ).build();
    }

}
