package com.enonic.xp.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;

final class SiteConfiguratorType
    extends InputTypeBase
{
    public static final SiteConfiguratorType INSTANCE = new SiteConfiguratorType();

    private SiteConfiguratorType()
    {
        super( InputTypeName.SITE_CONFIGURATOR );
    }

    @Override
    public Value createValue( final Value value, final GenericValue config )
    {
        return ValueFactory.newPropertySet( value.asData() );
    }

    @Override
    public void validate( final Property property, final GenericValue config )
    {
    }
}
