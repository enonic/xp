package com.enonic.wem.jsapi.internal;

import java.net.URL;
import java.util.Map;

import org.junit.Before;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import junit.framework.Assert;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandHandler;
import com.enonic.wem.script.internal.invoker.CommandRequestImpl;
import com.enonic.wem.script.internal.serializer.HashMapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public abstract class AbstractHandlerTest
{
    private final ObjectMapper mapper;

    private CommandHandler handler;

    public AbstractHandlerTest()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    @Before
    public final void setup()
        throws Exception
    {
        this.handler = createHandler();
    }

    protected abstract CommandHandler createHandler()
        throws Exception;

    protected final Object execute( final Map<String, Object> params )
    {
        final CommandRequestImpl request = new CommandRequestImpl();
        request.setScript( ResourceKey.from( "mymodule:/test.js" ) );
        request.setName( this.handler.getName() );
        request.setParamsMap( params );

        return this.handler.execute( request );
    }

    protected final void assertJson( final String name, final Object actual )
        throws Exception
    {
        if ( actual instanceof MapSerializable )
        {
            assertJson( name, (MapSerializable) actual );
            return;
        }

        Assert.fail( "Expected object should be of type [" + MapSerializable.class.getName() + "] but was [" +
                         ( actual != null ? actual.getClass().getName() : "null" ) + "]" );
    }

    private void assertJson( final String name, final MapSerializable actual )
        throws Exception
    {
        final HashMapGenerator generator = new HashMapGenerator();
        actual.serialize( generator );

        final String actualJson = this.mapper.writeValueAsString( generator.getRoot() );

        final String expectedFile = getClass().getSimpleName() + ( name != null ? ( "-" + name ) : "" ) + ".json";
        final URL url = getClass().getResource( expectedFile );
        Assert.assertNotNull( "Could not find resource [" + expectedFile + "]", url );
        final JsonNode expectedNode = this.mapper.readTree( url );
        final String expectedJson = this.mapper.writeValueAsString( expectedNode );

        Assert.assertEquals( expectedJson, actualJson );
    }
}
