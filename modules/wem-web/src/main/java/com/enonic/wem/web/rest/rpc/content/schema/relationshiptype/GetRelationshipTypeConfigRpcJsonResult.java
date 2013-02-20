package com.enonic.wem.web.rest.rpc.content.schema.relationshiptype;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.relationshiptype.RelationshipType;
import com.enonic.wem.core.content.schema.relationshiptype.RelationshipTypeXmlSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.schema.SchemaImageUriResolver;

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
