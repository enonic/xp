package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;

final class SiteConfiguratorType
    extends InputTypeBase
{
    public final static SiteConfiguratorType INSTANCE = new SiteConfiguratorType();

    private SiteConfiguratorType()
    {
        super( InputTypeName.SITE_CONFIGURATOR );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newPropertySet( value.asData() );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
    }
}
