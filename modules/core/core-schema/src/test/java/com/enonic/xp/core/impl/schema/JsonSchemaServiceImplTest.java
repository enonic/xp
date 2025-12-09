package com.enonic.xp.core.impl.schema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JsonSchemaServiceImplTest
{

    private JsonSchemaServiceImpl service;

    @BeforeEach
    void setUp()
    {
        BundleContext bundleContext = mock( BundleContext.class );
        service = new JsonSchemaServiceImpl( bundleContext );
    }

    @Test
    void activateRegistersListenerAndSchemas()
        throws Exception
    {
        BundleContext mockContext = mock( BundleContext.class );
        Bundle mockBundle = mock( Bundle.class );
        Bundle extBundle = mock( Bundle.class );
        when( mockContext.getBundle() ).thenReturn( mockBundle );
        when( mockContext.getBundles() ).thenReturn( new Bundle[]{mockBundle, extBundle} );

        when( extBundle.getBundleContext() ).thenReturn( mock( BundleContext.class ) );
        when( extBundle.getBundleId() ).thenReturn( 2L );
        when( extBundle.findEntries( anyString(), anyString(), anyBoolean() ) ).thenReturn(
            Collections.enumeration( List.of( new URI( "file:/tmp/schema.json" ).toURL() ) ) );

        JsonSchemaServiceImpl service = spy( new JsonSchemaServiceImpl( mockContext ) );
        doReturn( false ).when( service ).loadJsonSchema( any() );
        doNothing().when( service ).refreshSchemaRegistry();

        service.activate();

        verify( mockContext ).addBundleListener( service );
        verify( service ).loadJsonSchema( mockBundle );
        verify( service ).refreshSchemaRegistry();
    }

    @Test
    void deactivateRemovesBundleListener()
    {
        BundleContext mockContext = mock( BundleContext.class );
        JsonSchemaServiceImpl service = new JsonSchemaServiceImpl( mockContext );
        service.deactivate();
        verify( mockContext ).removeBundleListener( service );
    }

    @Test
    void registerSchemaNewSchemaReturnsTrue()
    {
        String schema = "{\"$id\":\"test1\",\"type\":\"object\"}";
        assertTrue( service.registerSchema( schema ) );
    }

    @Test
    void registerSchemaExistingSchemaReturnsFalse()
    {
        String schema = "{\"$id\":\"test2\",\"type\":\"object\"}";
        assertTrue( service.registerSchema( schema ) );
        assertFalse( service.registerSchema( schema ) );
    }

    @Test
    void registerSchemaInvalidJsonThrowsUncheckedIOException()
    {
        assertThrows( UncheckedIOException.class, () -> service.registerSchema( "{invalid json" ) );
    }

    @Test
    void registerSchemaMissingIdThrowsNullPointerException()
    {
        String schema = "{\"type\":\"object\"}";
        Exception ex = assertThrows( NullPointerException.class, () -> service.registerSchema( schema ) );
        assertTrue( ex.getMessage().contains( "$id must be set" ) );
    }

    @Test
    void loadSchemasNullListReturnsFalse()
    {
        assertFalse( service.loadSchemas( null ) );
    }

    @Test
    void loadSchemasEmptyListReturnsFalse()
    {
        assertFalse( service.loadSchemas( Collections.emptyList() ) );
    }

    @Test
    void loadSchemasValidListAllNewSchemasReturnsTrue()
        throws Exception
    {
        String schema1 = "{\"$id\":\"test-schema-1\",\"type\":\"object\"}";
        String schema2 = "{\"$id\":\"test-schema-2\",\"type\":\"object\"}";
        URL url1 = mock( URL.class );
        URL url2 = mock( URL.class );
        when( url1.openStream() ).thenReturn( new ByteArrayInputStream( schema1.getBytes( StandardCharsets.UTF_8 ) ) );
        when( url2.openStream() ).thenReturn( new ByteArrayInputStream( schema2.getBytes( StandardCharsets.UTF_8 ) ) );
        assertTrue( service.loadSchemas( List.of( url1, url2 ) ) );
    }

    @Test
    void loadSchemasValidListExistingSchemaReturnsFalse()
        throws Exception
    {
        String schema = "{\"$id\":\"test-schema-3\",\"type\":\"object\"}";
        URL url = mock( URL.class );
        when( url.openStream() ).thenAnswer( i -> new ByteArrayInputStream( schema.getBytes( StandardCharsets.UTF_8 ) ) );
        // Register first
        assertTrue( service.loadSchemas( List.of( url ) ) );
        // Register again, should return false
        assertFalse( service.loadSchemas( List.of( url ) ) );
    }

    @Test
    void loadSchemasValidListMixedNewAndExistingSchemasReturnsTrue()
        throws Exception
    {
        String schema1 = "{\"$id\":\"test-schema-4\",\"type\":\"object\"}";
        String schema2 = "{\"$id\":\"test-schema-5\",\"type\":\"object\"}";
        URL url1 = mock( URL.class );
        URL url2 = mock( URL.class );
        when( url1.openStream() ).thenAnswer( i -> new ByteArrayInputStream( schema1.getBytes( StandardCharsets.UTF_8 ) ) );
        // Register schema1 first
        assertTrue( service.loadSchemas( List.of( url1 ) ) );
        // Now schema1 is existing, schema2 is new
        when( url2.openStream() ).thenReturn( new ByteArrayInputStream( schema2.getBytes( StandardCharsets.UTF_8 ) ) );
        assertTrue( service.loadSchemas( List.of( url1, url2 ) ) );
    }

    @Test
    void loadSchemasIOExceptionThrowsRuntimeException()
        throws Exception
    {
        URL url = mock( URL.class );
        when( url.openStream() ).thenThrow( new IOException( "fail" ) );
        RuntimeException ex = assertThrows( RuntimeException.class, () -> service.loadSchemas( List.of( url ) ) );
        assertTrue( ex.getCause() instanceof IOException );
    }

    @Test
    void validateValidNoErrors()
    {
        String schema = "{\"$id\":\"https://schemas.example.com/myschema.json\",\"type\":\"object\"}";
        service.registerSchema( schema );
        service.refreshSchemaRegistry();
        assertDoesNotThrow( () -> service.validate( "https://schemas.example.com/myschema.json", "foo: bar" ) );
    }

    @Test
    void refreshSchemaRegistryThreadSafety()
    {
        String schema = "{\"$id\":\"test4\",\"type\":\"object\"}";
        service.registerSchema( schema );
        assertDoesNotThrow( service::refreshSchemaRegistry );
    }

    @Test
    void bundleChangedInstalledOrUpdatedAddsBundle()
    {
        BundleEvent event = mock( BundleEvent.class );
        Bundle bundle = mock( Bundle.class );
        when( event.getType() ).thenReturn( BundleEvent.INSTALLED );
        when( event.getBundle() ).thenReturn( bundle );

        JsonSchemaServiceImpl spyService = spy( service );
        doNothing().when( spyService ).addBundle( bundle );

        spyService.bundleChanged( event );
        verify( spyService, times( 1 ) ).addBundle( bundle );

        when( event.getType() ).thenReturn( BundleEvent.UPDATED );
        spyService.bundleChanged( event );
        verify( spyService, times( 2 ) ).addBundle( bundle );
    }

    @Test
    void bundleChangedOtherTypeDoesNothing()
    {
        BundleEvent event = mock( BundleEvent.class );
        when( event.getType() ).thenReturn( BundleEvent.RESOLVED );
        JsonSchemaServiceImpl spyService = spy( service );
        spyService.bundleChanged( event );
        verify( spyService, never() ).addBundle( any() );
    }

    @Test
    void addBundleLoadJsomSchemasTrueRefreshesRegistry()
    {
        Bundle bundle = mock( Bundle.class );
        JsonSchemaServiceImpl spyService = spy( service );
        doReturn( true ).when( spyService ).loadJsonSchema( bundle );
        doNothing().when( spyService ).refreshSchemaRegistry();
        spyService.addBundle( bundle );
        verify( spyService ).refreshSchemaRegistry();
    }

    @Test
    void addBundleLoadJsomSchemasFalseDoesNotRefreshRegistry()
    {
        Bundle bundle = mock( Bundle.class );
        JsonSchemaServiceImpl spyService = spy( service );
        doReturn( false ).when( spyService ).loadJsonSchema( bundle );
        spyService.addBundle( bundle );
        verify( spyService, never() ).refreshSchemaRegistry();
    }

    @Test
    void loadJsomSchemasDelegatesToLoadSchemas()
    {
        Bundle bundle = mock( Bundle.class );
        JsonSchemaServiceImpl spyService = spy( service );
        doReturn( Collections.emptyList() ).when( spyService ).loadJsonSchemasFromBundle( bundle );
        doReturn( true ).when( spyService ).loadSchemas( anyList() );
        assertTrue( spyService.loadJsonSchema( bundle ) );
    }

    @Test
    void loadJsonSchemasFromBundleReturnsList()
    {
        Bundle bundle = mock( Bundle.class );
        URL url = mock( URL.class );
        Vector<URL> urls = new Vector<>();
        urls.add( url );
        when( bundle.findEntries( "/META-INF/schemas", "*.json", true ) ).thenReturn( urls.elements() );
        List<URL> result = service.loadJsonSchemasFromBundle( bundle );
        assertEquals( 1, result.size() );
        assertEquals( url, result.get( 0 ) );
    }

    @Test
    void loadJsonSchemasFromBundleNullEnumerationReturnsEmptyList()
    {
        Bundle bundle = mock( Bundle.class );
        when( bundle.findEntries( "/META-INF/schemas", "*.json", true ) ).thenReturn( null );
        List<URL> result = service.loadJsonSchemasFromBundle( bundle );
        assertTrue( result.isEmpty() );
    }
}
