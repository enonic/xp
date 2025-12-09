package com.enonic.xp.core.impl.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.dialect.Dialects;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;

@Component(immediate = true)
public class JsonSchemaServiceImpl
    implements JsonSchemaService, SynchronousBundleListener
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaServiceImpl.class );

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private final ConcurrentMap<String, JsonSchemaDefinitionWrapper> schemasMap = new ConcurrentHashMap<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final BundleContext bundleContext;

    private SchemaRegistry schemaRegistry;

    @Activate
    public JsonSchemaServiceImpl( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Activate
    public void activate()
    {
        bundleContext.addBundleListener( this );

        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator = new FormItemsJsonSchemaGenerator( Set.of() );
        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
        register( formItemsSchema );

        final Bundle currentBundle = bundleContext.getBundle();
        loadJsomSchemas( currentBundle );

        for ( Bundle bundle : bundleContext.getBundles() )
        {
            if ( bundle.getBundleId() != currentBundle.getBundleId() )
            {
                loadJsomSchemas( bundle );
            }
        }

        refreshSchemaRegistry();
    }

    @Deactivate
    public void deactivate()
    {
        bundleContext.removeBundleListener( this );
    }


    public boolean registerSchema( final String schema )
    {
        return register( schema );
    }

    public boolean loadSchemas( final List<URL> schemaURLs )
    {
        if ( schemaURLs == null )
        {
            return false;
        }

        boolean shouldBeUpdated = false;

        for ( URL schemaURL : schemaURLs )
        {
            try (InputStream inputStream = schemaURL.openStream())
            {
                final String schemaDefinition = new String( inputStream.readAllBytes(), StandardCharsets.UTF_8 );
                if ( register( schemaDefinition ) )
                {
                    shouldBeUpdated = true;
                }
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }

        return shouldBeUpdated;
    }

    @Override
    public void validate( final String schemaId, final String yml )
    {
        lock.lock();
        Schema schema;
        try
        {
            schema = schemaRegistry.getSchema( SchemaLocation.of( schemaId ) );
        }
        finally
        {
            lock.unlock();
        }

        final List<Error> errors = schema.validate( yml, InputFormat.YAML );

        if ( !errors.isEmpty() )
        {
            final StringBuilder builder = new StringBuilder( "Validation errors for schema \"" );
            builder.append( schemaId );
            builder.append( "\":" );
            errors.forEach( err -> builder.append( "\n - " ).append( err.getMessage() ) );
            throw new IllegalArgumentException( builder.toString() );
        }
    }

    public void refreshSchemaRegistry()
    {
        lock.lock();
        try
        {
            this.schemaRegistry = SchemaRegistry.withDialect( Dialects.getDraft202012(), builder -> builder.schemas( getAllSchemas() ) );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        switch ( event.getType() )
        {
            case BundleEvent.INSTALLED:
            case BundleEvent.UPDATED:
                addBundle( event.getBundle() );
                break;
            default:
                break;
        }
    }

    private boolean register( final String schemaDefinition )
    {
        try
        {
            final JsonNode schemaNode = MAPPER.readTree( schemaDefinition );
            final String schemaId = Objects.requireNonNull( schemaNode.get( "$id" ), "$id must be set" ).asText();

            final JsonSchemaDefinitionWrapper persistedSchema = schemasMap.get( schemaId );

            final boolean changed = persistedSchema == null || !persistedSchema.schema().equals( schemaDefinition );
            if ( changed )
            {
                schemasMap.put( schemaId, new JsonSchemaDefinitionWrapper( schemaDefinition ) );
                LOG.debug( "JSON Schema Definition: {} is registered", schemaId );
            }

            return changed;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Map<String, String> getAllSchemas()
    {
        return schemasMap.entrySet()
            .stream()
            .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, entry -> entry.getValue().schema() ) );
    }

    protected void addBundle( Bundle bundle )
    {
        if ( loadJsomSchemas( bundle ) )
        {
            refreshSchemaRegistry();
        }
    }

    protected boolean loadJsomSchemas( final Bundle bundle )
    {
        return loadSchemas( loadJsonSchemasFromBundle( bundle ) );
    }

    protected List<URL> loadJsonSchemasFromBundle( final Bundle bundle )
    {
        final Enumeration<URL> schemaURLs = bundle.findEntries( "/META-INF/schemas", "*.json", true );
        return schemaURLs != null ? Collections.list( schemaURLs ) : Collections.emptyList();
    }
}
