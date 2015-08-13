package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;

final class SiteConfiguratorType
    extends InputTypeBase
{
    public final static SiteConfiguratorType INSTANCE = new SiteConfiguratorType();

    private SiteConfiguratorType()
    {
        super( InputTypeName.SITE_CONFIGURATOR );
    }

    @Override
    public Value createValue( final String value, final InputTypeConfig config )
    {
        return Value.newString( value );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
    }
}
