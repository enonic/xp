package com.enonic.xp.inputtype;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.util.GenericValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
        final PropertyTree tree = new PropertyTree();
        final PropertySet siteConfig = tree.newSet();
        siteConfig.setString( "applicationKey", "com.enonic.app.myapp" );
        final PropertySet appConfig = tree.newSet();
        appConfig.setString( "param", "value" );
        siteConfig.setSet( "config", appConfig );
        final Value value = this.type.createValue( ValueFactory.newPropertySet( siteConfig ), GenericValue.object().build() );

        assertNotNull( value );
        assertSame( ValueTypes.PROPERTY_SET, value.getType() );
    }

    @Test
    public void testValidate()
    {
        this.type.validate( stringProperty( "test" ), GenericValue.object().build() );
    }
}
