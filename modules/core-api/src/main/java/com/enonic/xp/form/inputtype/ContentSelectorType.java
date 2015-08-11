package com.enonic.xp.form.inputtype;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.xml.DomHelper;

final class ContentSelectorType
    extends InputType
{
    public ContentSelectorType()
    {
        super( InputTypeName.CONTENT_SELECTOR );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.REFERENCE );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newReference( ValueTypes.REFERENCE.convert( value ) );
    }

    @Override
    public InputTypeConfig parseConfig( final ApplicationKey app, final Element elem )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( app );

        final ContentSelectorTypeConfig.Builder builder = ContentSelectorTypeConfig.create();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );
        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( resolver.toRelationshipTypeName( text ) );
        }

        final List<Element> allowContentTypeEls = DomHelper.getChildElementsByTagName( elem, "allow-content-type" );
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
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        if ( !( config instanceof ContentSelectorTypeConfig ) )
        {
            return null;
        }

        final ContentSelectorTypeConfig typedConfig = (ContentSelectorTypeConfig) config;
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();
        if ( typedConfig.getRelationshipType() != null )
        {
            jsonConfig.put( "relationshipType", typedConfig.getRelationshipType().toString() );
        }
        else
        {
            jsonConfig.putNull( "relationshipType" );
        }

        if ( typedConfig.getAllowedContentTypes().isNotEmpty() )
        {
            final ArrayNode contentTypesArray = jsonConfig.putArray( "allowedContentTypes" );
            for ( ContentTypeName allowedContentTypeName : typedConfig.getAllowedContentTypes() )
            {
                contentTypesArray.add( allowedContentTypeName.toString() );
            }
        }

        return jsonConfig;
    }
}
