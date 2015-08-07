package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.xml.DomHelper;

final class Date
    extends InputType
{
    public Date()
    {
        super( InputTypeName.DATE );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        validateType( property, ValueTypes.LOCAL_DATE );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newLocalDate( ValueTypes.LOCAL_DATE.convert( value ) );
    }

    @Override
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        if ( !( config instanceof DateConfig ) )
        {
            return null;
        }

        final DateConfig typedConfig = (DateConfig) config;
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();
        jsonConfig.put( "withTimezone", typedConfig.isWithTimezone() );
        return jsonConfig;
    }

    @Override
    public InputTypeConfig parseConfig( final ApplicationKey app, final Element elem )
    {
        final DateConfig.Builder builder = DateConfig.create();
        parseTimezone( elem, builder );
        return builder.build();
    }

    private void parseTimezone( final Element elem, DateConfig.Builder builder )
    {
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }
    }
}
