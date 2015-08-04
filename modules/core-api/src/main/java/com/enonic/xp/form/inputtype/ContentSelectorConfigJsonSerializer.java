package com.enonic.xp.form.inputtype;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.schema.content.ContentTypeName;

@Beta
public class ContentSelectorConfigJsonSerializer
    implements InputTypeConfigJsonSerializer<ContentSelectorConfig>
{
    public static final ContentSelectorConfigJsonSerializer DEFAULT = new ContentSelectorConfigJsonSerializer();

    private static final String RELATIONSHIP_TYPE_KEY = "relationshipType";

    private static final String ALLOWED_CONTENT_TYPE_KEY = "allowedContentTypes";

    @Override
    public JsonNode serializeConfig( final ContentSelectorConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        if ( config.getRelationshipType() != null )
        {
            jsonConfig.put( RELATIONSHIP_TYPE_KEY, config.getRelationshipType().toString() );
        }
        else
        {
            jsonConfig.putNull( RELATIONSHIP_TYPE_KEY );
        }

        if ( config.getAllowedContentTypes().isNotEmpty() )
        {
            final ArrayNode contentTypesArray = jsonConfig.putArray( ALLOWED_CONTENT_TYPE_KEY );
            for ( ContentTypeName allowedContentTypeName : config.getAllowedContentTypes() )
            {
                contentTypesArray.add( allowedContentTypeName.toString() );
            }
        }

        return jsonConfig;
    }
}
