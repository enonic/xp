package com.enonic.wem.web.rest.rpc.content.relationshiptype;


import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.core.content.relationshiptype.RelationshipTypeXmlSerializer;
import com.enonic.wem.web.json.JsonResult;
import com.enonic.wem.web.rest.resource.content.BaseTypeImageUriResolver;

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
        json.put( "iconUrl", BaseTypeImageUriResolver.resolve( relationshipType.getBaseTypeKey() ) );
    }
}
