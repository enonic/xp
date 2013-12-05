package com.enonic.wem.admin.rest.resource;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.inmemory.InMemoryTestContainerFactory;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public abstract class AbstractResourceTest
    extends JerseyTest
{
    public AbstractResourceTest()
    {
        super( new InMemoryTestContainerFactory() );
    }

    @Override
    protected AppDescriptor configure()
    {
        final DefaultResourceConfig config = new DefaultResourceConfig();
        configure( config );

        return new LowLevelAppDescriptor.Builder( config ).build();
    }

    protected void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    private void configure( final DefaultResourceConfig config )
    {
        config.getClasses().add( JsonObjectProvider.class );
        config.getClasses().add( JsonSerializableProvider.class );
        config.getSingletons().add( getResourceInstance() );
    }

    protected abstract Object getResourceInstance();

    protected final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );
        final JsonNode actualNode = parseJson( actualJson );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualNode );

        assertEquals( expectedStr, actualStr );
    }

    protected JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.readTree( json );
    }

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }
}
