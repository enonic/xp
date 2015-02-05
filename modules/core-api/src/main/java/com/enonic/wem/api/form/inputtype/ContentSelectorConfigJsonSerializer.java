package com.enonic.wem.api.form.inputtype;


import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

import static com.enonic.wem.api.form.inputtype.ContentSelectorConfig.newRelationshipConfig;


public class ContentSelectorConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<ContentSelectorConfig>
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

    @Override
    public ContentSelectorConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final ContentSelectorConfig.Builder builder = newRelationshipConfig();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( RELATIONSHIP_TYPE_KEY );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( RelationshipTypeName.from( relationshipTypeNode.textValue() ) );
        }

        final JsonNode allowedNode = inputTypeConfigNode.get( ALLOWED_CONTENT_TYPE_KEY );
        final ArrayNode allowedContentTypesArray = allowedNode != null && allowedNode.isArray() ? (ArrayNode) allowedNode : null;
        if ( allowedContentTypesArray != null )
        {
            for ( JsonNode allowContentTypeNode : allowedContentTypesArray )
            {
                final String allowContentTypeText = allowContentTypeNode.asText();
                if ( StringUtils.isNotBlank( allowContentTypeText ) )
                {
                    builder.addAllowedContentType( ContentTypeName.from( allowContentTypeText ) );
                }
            }
        }

        return builder.build();
    }
}
