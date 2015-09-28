package com.enonic.xp.inputtype;

import org.junit.Test;

import com.enonic.xp.data.Value;
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
        final Value value = this.type.createValue( "test", config );

        assertNotNull( value );
        assertSame( ValueTypes.STRING, value.getType() );
    }

    @Test
    public void testValidate()
    {
        final InputTypeConfig config = InputTypeConfig.create().build();
        this.type.validate( stringProperty( "test" ), config );
    }
}
