package com.enonic.wem.admin.rpc.schema.relationship;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.core.schema.relationship.RelationshipTypeXmlSerializer;

class GetRelationshipTypeConfigRpcJsonResult
    extends JsonResult
{
    private final RelationshipTypeXmlSerializer relationshipTypeXmlSerializer;

    private RelationshipType relationshipType;

    GetRelationshipTypeConfigRpcJsonResult( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
        this.relationshipTypeXmlSerializer = new RelationshipTypeXmlSerializer();
    }

    @Override
    public void serialize( final ObjectNode json )
    {
        final String contentTypeXml = relationshipTypeXmlSerializer.toString( relationshipType );
        json.put( "relationshipTypeXml", contentTypeXml );
        json.put( "iconUrl", SchemaImageUriResolver.resolve( relationshipType.getSchemaKey() ) );
    }
}
