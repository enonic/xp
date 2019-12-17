package com.enonic.xp.extractor.impl.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;

import static com.enonic.xp.extractor.impl.config.ExtractorConfigMap.BODY_SIZE_LIMIT_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        instance.configure( new HashMap<>(  ) );

        assertEquals( BODY_SIZE_LIMIT_DEFAULT, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Empty()
        throws Exception
    {
        loadConfig( "empty" );

        assertEquals( BODY_SIZE_LIMIT_DEFAULT, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Complete()
        throws Exception
    {
        loadConfig( "complete" );

        assertEquals( 200_000, instance.getBodySizeLimit() );
    }

    @Test
    public void testLoadConfig_Invalid()
        throws Exception
    {
        loadConfig( "invalid" );

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
