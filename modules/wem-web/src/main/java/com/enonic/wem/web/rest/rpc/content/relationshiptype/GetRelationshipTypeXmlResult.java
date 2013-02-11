package com.enonic.wem.web.rest.rpc.content.relationshiptype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.content.relationshiptype.RelationshipTypeJsonSerializer;
import com.enonic.wem.core.content.relationshiptype.RelationshipTypeXmlSerializer;
import com.enonic.wem.core.content.type.ContentTypeXmlSerializer;
import com.enonic.wem.web.json.JsonResult;

class GetRelationshipTypeXmlResult
    extends JsonResult
{
    private final RelationshipTypeXmlSerializer relationshipTypeXmlSerializer;

    private RelationshipType relationshipType;

    GetRelationshipTypeXmlResult( final RelationshipType relationshipType )
    {
        this.relationshipType = relationshipType;
        this.relationshipTypeXmlSerializer = new RelationshipTypeXmlSerializer( );
    }

    @Override
    public void serialize( final ObjectNode json )
    {
        final String contentTypeXml = relationshipTypeXmlSerializer.toString( relationshipType );
        json.put( "relationshipTypeXml", contentTypeXml );
    }
}
