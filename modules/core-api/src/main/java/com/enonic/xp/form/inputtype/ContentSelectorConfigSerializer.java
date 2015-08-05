package com.enonic.xp.form.inputtype;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.xml.DomHelper;

final class ContentSelectorConfigSerializer
    implements InputTypeConfigSerializer<ContentSelectorConfig>
{
    public static final ContentSelectorConfigSerializer INSTANCE = new ContentSelectorConfigSerializer();

    private static final String RELATIONSHIP_TYPE_ELEMENT = "relationship-type";

    private static final String ALLOWED_CONTENT_TYPE_ELEMENT = "allow-content-type";

    private static final String RELATIONSHIP_TYPE_KEY = "relationshipType";

    private static final String ALLOWED_CONTENT_TYPE_KEY = "allowedContentTypes";

    @Override
    public ContentSelectorConfig parseConfig( final ApplicationKey currentApplication, final Element elem )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( currentApplication );

        final ContentSelectorConfig.Builder builder = ContentSelectorConfig.create();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, RELATIONSHIP_TYPE_ELEMENT );
        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( resolver.toRelationshipTypeName( text ) );
        }

        final List<Element> allowContentTypeEls = DomHelper.getChildElementsByTagName( elem, ALLOWED_CONTENT_TYPE_ELEMENT );
        for ( Element allowContentTypeEl : allowContentTypeEls )
        {
            final String allowContentTypeText = DomHelper.getTextValue( allowContentTypeEl );
            if ( StringUtils.isNotBlank( allowContentTypeText ) )
            {
                builder.addAllowedContentType( resolver.toContentTypeName( allowContentTypeText ) );
            }
        }

        return builder.build();
    }

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
