package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

import static org.junit.Assert.*;

public class SiteConfiguratorTypeTest
    extends BaseInputTypeTest
{
    public SiteConfiguratorTypeTest()
    {
        super( SiteConfiguratorType.INSTANCE );
    }

    @Test
    public void testName()
    {
        assertEquals( "SiteConfigurator", this.type.getName().toString() );
    }

    @Test
    public void testToString()
    {
        assertEquals( "SiteConfigurator", this.type.toString() );
    }

    @Test
    public void testCreateProperty()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        final PropertySet siteConfig = new PropertySet();
        siteConfig.setString( "applicationKey", "com.enonic.app.myapp" );
        final PropertySet appConfig = new PropertySet();
        appConfig.setString( "param", "value" );
        siteConfig.setSet( "config", appConfig );
        final Value value = this.type.createValue( ValueFactory.newPropertySet( siteConfig ), config );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }
}
