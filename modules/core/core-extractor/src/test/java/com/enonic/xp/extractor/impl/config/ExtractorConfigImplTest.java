package com.enonic.xp.extractor.impl.config;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static com.enonic.xp.extractor.impl.config.ExtractorConfigImpl.BODY_SIZE_LIMIT_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtractorConfigImplTest
{

    private ExtractorConfigImpl instance;

    @BeforeEach
    public void setup()
    {
        this.instance = new ExtractorConfigImpl();
    }

    @Test
    public void testNoConfig()
    {
        assertFalse( instance.isEnabled() );
        assertEquals( BODY_SIZE_LIMIT_DEFAULT, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Empty()
        throws Exception
    {
        loadConfig( "empty" );

        assertFalse( instance.isEnabled() );
        assertEquals( BODY_SIZE_LIMIT_DEFAULT, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Complete()
        throws Exception
    {
        loadConfig( "complete" );

        assertTrue( instance.isEnabled() );
        assertEquals( 200_000, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Disabled()
        throws Exception
    {
        loadConfig( "disabled" );

        assertFalse( instance.isEnabled() );
        assertEquals( BODY_SIZE_LIMIT_DEFAULT, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Invalid()
        throws Exception
    {
        loadConfig( "invalid" );

        assertTrue( instance.isEnabled() );
        assertEquals( BODY_SIZE_LIMIT_DEFAULT, instance.getBodySizeLimit() );
    }

    private void loadConfig( final String name )
        throws Exception
    {
        try (InputStream in = getClass().getResourceAsStream( "extractor-" + name + ".properties" ))
        {
            Properties props = new Properties();
            props.load( in );

            Map<String, String> map = Maps.fromProperties( props );
            this.instance.configure( map );
        }
    }

}
