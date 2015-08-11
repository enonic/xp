package com.enonic.xp.form.inputtype;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;

final class ContentSelectorType
    extends InputType
{
    public final static ContentSelectorType INSTANCE = new ContentSelectorType();

    private ContentSelectorType()
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
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();
        jsonConfig.put( "relationshipType", config.getValue( "relationshipType", String.class, "system:reference" ) );

        final ArrayNode contentTypesArray = jsonConfig.putArray( "allowedContentTypes" );
        config.getValues( "allowedContentTypes" ).forEach( contentTypesArray::add );

        return jsonConfig;
    }
}
