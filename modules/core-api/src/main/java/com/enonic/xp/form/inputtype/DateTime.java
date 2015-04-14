package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;

@Beta
public class DateTime
    extends InputType
{

    DateTime()
    {
        super( DateTimeConfig.class, false );
    }

    @Override
    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return DateTimeConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return DateTimeConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {
        final String stringValue = property.getString();
        if ( StringUtils.isBlank( stringValue ) )
        {
            throw new BreaksRequiredContractException( property, this );
        }
    }

    @Override
    public Value newValue( final String value )
    {
        return Value.newInstant( ValueTypes.DATE_TIME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return DateTimeConfig.create().
            withTimezone( false ).
            build();
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        if ( config != null && !( config instanceof DateTimeConfig ) )
        {
            throw new IllegalArgumentException(
                "Expected config of type " + DateTimeConfig.class.getName() + ", got " + config.getClass() );
        }

        DateTimeConfig dateTimeConfig = config == null ? (DateTimeConfig) getDefaultConfig() : (DateTimeConfig) config;

        if ( dateTimeConfig.isWithTimezone() )
        {
            return Value.newInstant( ValueTypes.DATE_TIME.convert( value ) );
        }
        else
        {
            return Value.newLocalDateTime( ValueTypes.LOCAL_DATE_TIME.convert( value ) );
        }
    }
}