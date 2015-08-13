package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;

final class SiteConfiguratorType
    extends InputType
{
    public final static SiteConfiguratorType INSTANCE = new SiteConfiguratorType();

    private SiteConfiguratorType()
    {
        super( InputTypeName.SITE_CONFIGURATOR );
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
    {
        validateNotBlank( property );
    }

    @Override
    public Value createPropertyValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void checkValidity( final InputTypeConfig config, final Property property )
    {
    }
}
