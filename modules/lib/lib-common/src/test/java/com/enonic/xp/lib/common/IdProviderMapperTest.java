package com.enonic.xp.lib.common;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.testing.helper.JsonAssert;

class IdProviderMapperTest
{
    @Test
    void testIdProviderSerialized()
        throws Exception
    {
        final IdProvider idProvider = IdProvider.create()
            .key( IdProviderKey.from( "idProviderTestKey" ) )
            .displayName( "Id Provider test" )
            .description( "Id Provider used for testing" )
            .build();

        JsonAssert.assertJson( getClass(), "minimal", new IdProviderMapper( idProvider ) );
    }

    @Test
    void testIdProviderWithConfigSerialized()
        throws Exception
    {
        final PropertyTree config = new PropertyTree();
        final PropertySet nested = config.newSet();
        nested.setString( "subString", "subStringValue" );
        nested.setLong( "subLong", 123L );
        config.setSet( "set", nested );
        config.setString( "string", "stringValue" );

        final IdProvider idProvider = IdProvider.create()
            .key( IdProviderKey.from( "idProviderTestKey" ) )
            .displayName( "Id Provider test" )
            .description( "Id Provider used for testing" )
            .idProviderConfig(
                IdProviderConfig.create().applicationKey( ApplicationKey.from( "com.enonic.app.test" ) ).config( config ).build() )
            .build();

        JsonAssert.assertJson( getClass(), "with-config", new IdProviderMapper( idProvider ) );
    }
}
