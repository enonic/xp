package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.InvalidTypeException;
import com.enonic.xp.xml.DomHelper;

@Beta
final class DateTime
    extends InputType
{
    public DateTime()
    {
        super( InputTypeName.DATE_TIME );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public void checkTypeValidity( final Property property )
    {
        if ( ( ValueTypes.DATE_TIME != property.getType() ) && ( ValueTypes.LOCAL_DATE_TIME != property.getType() ) )
        {
            throw new InvalidTypeException( property, ValueTypes.DATE_TIME );
        }
    }

    @Override
    public DateTimeConfig getDefaultConfig()
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.create();
        builder.withTimezone( false );
        return builder.build();
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        if ( config != null && !( config instanceof DateTimeConfig ) )
        {
            throw new IllegalArgumentException(
                "Expected config of type " + DateTimeConfig.class.getName() + ", got " + config.getClass() );
        }

        final DateTimeConfig dateTimeConfig = config == null ? getDefaultConfig() : (DateTimeConfig) config;
        if ( dateTimeConfig.isWithTimezone() )
        {
            return Value.newInstant( ValueTypes.DATE_TIME.convert( value ) );
        }
        else
        {
            return Value.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
    }

    @Override
    public ObjectNode serializeConfig( final InputTypeConfig config )
    {
        if ( !( config instanceof DateTimeConfig ) )
        {
            return null;
        }

        final DateTimeConfig typedConfig = (DateTimeConfig) config;
        final ObjectNode jsonConfig = JsonNodeFactory.instance.objectNode();
        jsonConfig.put( "withTimezone", typedConfig.isWithTimezone() );
        return jsonConfig;
    }

    @Override
    public InputTypeConfig parseConfig( final ApplicationKey app, final Element elem )
    {
        final DateTimeConfig.Builder builder = DateTimeConfig.create();
        parseTimezone( elem, builder );
        return builder.build();
    }

    private void parseTimezone( final Element elem, DateTimeConfig.Builder builder )
    {
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "with-timezone" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.withTimezone( Boolean.valueOf( text ) );
        }
    }
}
