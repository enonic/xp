package com.enonic.xp.core.impl.idprovider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.ApplicationTestSupport;
import com.enonic.xp.core.impl.schema.JsonSchemaService;
import com.enonic.xp.core.impl.schema.JsonSchemaServiceImpl;
import com.enonic.xp.core.impl.schema.JsonSchemaValidationException;
import com.enonic.xp.idprovider.IdProviderDescriptor;
import com.enonic.xp.idprovider.IdProviderDescriptorMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IdProviderDescriptorServiceTest
    extends ApplicationTestSupport
{

    protected IdProviderDescriptorServiceImpl service;

    private JsonSchemaService jsonSchemaService;

    @Override
    protected void initialize()
    {
        addApplication( "myapp1", "/apps/myapp1" );
        addApplication( "myapp2", "/apps/myapp2" );
        addApplication( "myapp3", "/apps/myapp3" );
        addApplication( "myapp4", "/apps/myapp4" );
        this.jsonSchemaService = mock( JsonSchemaService.class );
        this.service = new IdProviderDescriptorServiceImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setJsonSchemaService( this.jsonSchemaService );
    }

    @Test
    void testGetDescriptor()
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "myapp1" ) );

        assertNotNull( idProviderDescriptor );
        assertEquals( ApplicationKey.from( "myapp1" ), idProviderDescriptor.getKey() );
        assertEquals( IdProviderDescriptorMode.MIXED, idProviderDescriptor.getMode() );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "title" ) );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "defaultPrincipals" ) );
    }

    @Test
    void testGetDescriptorLocalMode()
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "myapp2" ) );

        assertNotNull( idProviderDescriptor );
        assertEquals( ApplicationKey.from( "myapp2" ), idProviderDescriptor.getKey() );
        assertEquals( IdProviderDescriptorMode.LOCAL, idProviderDescriptor.getMode() );
        assertNotNull( idProviderDescriptor.getConfig().getInput( "domain" ) );
    }

    @Test
    void testGetDescriptorNotFound()
    {
        final IdProviderDescriptor idProviderDescriptor = this.service.getDescriptor( ApplicationKey.from( "nonexistent" ) );

        assertNull( idProviderDescriptor );
    }

    @Test
    void testValidationCalledOnGetDescriptor()
    {
        this.service.getDescriptor( ApplicationKey.from( "myapp1" ) );

        verify( this.jsonSchemaService ).validate(
            eq( "https://json-schema.enonic.com/8.0.0/idprovider.schema.json" ), anyString() );
    }

    @Test
    void testValidationFailure()
    {
        doThrow( new RuntimeException( "Validation failed" ) ).when( this.jsonSchemaService ).validate( anyString(), anyString() );

        assertThrows( RuntimeException.class, () -> this.service.getDescriptor( ApplicationKey.from( "myapp1" ) ) );
    }

    @Test
    void testGetDescriptorPassesRealSchemaValidation()
    {
        final IdProviderDescriptorServiceImpl serviceWithRealSchema = new IdProviderDescriptorServiceImpl();
        serviceWithRealSchema.setResourceService( this.resourceService );
        serviceWithRealSchema.setJsonSchemaService( createRealJsonSchemaService() );

        final IdProviderDescriptor descriptor = serviceWithRealSchema.getDescriptor( ApplicationKey.from( "myapp1" ) );

        assertNotNull( descriptor );
        assertEquals( IdProviderDescriptorMode.MIXED, descriptor.getMode() );
    }

    @Test
    void testGetDescriptorInvalidYmlFailsRealSchemaValidation()
    {
        final IdProviderDescriptorServiceImpl serviceWithRealSchema = new IdProviderDescriptorServiceImpl();
        serviceWithRealSchema.setResourceService( this.resourceService );
        serviceWithRealSchema.setJsonSchemaService( createRealJsonSchemaService() );

        assertThrows( JsonSchemaValidationException.class,
                      () -> serviceWithRealSchema.getDescriptor( ApplicationKey.from( "myapp3" ) ) );
    }

    @Test
    void testGetDescriptorWithUnsupportedFormItemYmlFailsRealSchemaValidation()
    {
        final IdProviderDescriptorServiceImpl serviceWithRealSchema = new IdProviderDescriptorServiceImpl();
        serviceWithRealSchema.setResourceService( this.resourceService );
        serviceWithRealSchema.setJsonSchemaService( createRealJsonSchemaService() );

        assertThrows( JsonSchemaValidationException.class, () -> serviceWithRealSchema.getDescriptor( ApplicationKey.from( "myapp4" ) ) );
    }

    private static JsonSchemaServiceImpl createRealJsonSchemaService()
    {
        final JsonSchemaServiceImpl schemaService = new JsonSchemaServiceImpl( mock( BundleContext.class ) );
        try
        {
            final Enumeration<URL> resources =
                IdProviderDescriptorServiceTest.class.getClassLoader().getResources( "META-INF/schemas/8.0.0" );
            while ( resources.hasMoreElements() )
            {
                final URI uri = resources.nextElement().toURI();
                if ( "jar".equals( uri.getScheme() ) )
                {
                    final String uriStr = uri.toString();
                    final Path jarPath = Path.of( URI.create( uriStr.substring( "jar:".length(), uriStr.indexOf( '!' ) ) ) );
                    try ( FileSystem fs = FileSystems.newFileSystem( jarPath, Map.of() ) )
                    {
                        walkAndRegister( schemaService, fs.getPath( "/META-INF/schemas/8.0.0" ) );
                    }
                }
                else
                {
                    walkAndRegister( schemaService, Path.of( uri ) );
                }
            }
        }
        catch ( IOException | URISyntaxException e )
        {
            throw new RuntimeException( e );
        }
        schemaService.refreshSchemaRegistry();
        return schemaService;
    }

    private static void walkAndRegister( final JsonSchemaServiceImpl schemaService, final Path dir )
        throws IOException
    {
        try ( var stream = Files.walk( dir ) )
        {
            stream.filter( p -> p.toString().endsWith( ".json" ) ).forEach( p -> {
                try
                {
                    schemaService.registerSchema( Files.readString( p ) );
                }
                catch ( IOException e )
                {
                    throw new UncheckedIOException( e );
                }
            } );
        }
    }
}