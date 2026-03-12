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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.enonic.xp.server.VersionInfo;

@Component(immediate = true)
public class JsonSchemaServiceImpl
    implements JsonSchemaService, SynchronousBundleListener
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaServiceImpl.class );

    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private final ConcurrentMap<String, String> schemasMap = new ConcurrentHashMap<>();

    private final BundleContext bundleContext;

    private volatile SchemaRegistry schemaRegistry;

    @Activate
    public JsonSchemaServiceImpl( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Activate
    public void activate()
    {
        bundleContext.addBundleListener( this );

        final Bundle currentBundle = bundleContext.getBundle();
        loadJsonSchema( currentBundle );

        for ( Bundle bundle : bundleContext.getBundles() )
        {
            if ( bundle.getBundleId() != currentBundle.getBundleId() )
            {
                loadJsonSchema( bundle );
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
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        return shouldBeUpdated;
    }

    @Override
    public void validate( final String schemaId, final String yml )
    {
        final Schema schema = schemaRegistry.getSchema( SchemaLocation.of( schemaId ) );

        final List<Error> errors = schema.validate( yml, InputFormat.YAML );

        if ( !errors.isEmpty() )
        {
            throw new JsonSchemaValidationException( schemaId, errors );
        }
    }

    public void refreshSchemaRegistry()
    {
        this.schemaRegistry = SchemaRegistry.withDialect( Dialects.getDraft202012(), builder -> builder.schemas( getAllSchema() ) );
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        if ( event.getType() == BundleEvent.INSTALLED || event.getType() == BundleEvent.UPDATED )
        {
            addBundle( event.getBundle() );
        }
    }

    private boolean register( final String schemaDefinition )
    {
        try
        {
            final JsonNode schemaNode = MAPPER.readTree( schemaDefinition );
            final String schemaId = Objects.requireNonNull( schemaNode.get( "$id" ), "$id must be set" ).asText();

            final boolean[] changed = {false};
            schemasMap.compute( schemaId, ( key, existing ) -> {
                if ( existing == null || !existing.equals( schemaDefinition ) )
                {
                    changed[0] = true;
                    LOG.debug( "JSON Schema Definition: {} is registered", schemaId );
                    return schemaDefinition;
                }
                return existing;
            } );

            return changed[0];
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private Map<String, String> getAllSchema()
    {
        return Map.copyOf( schemasMap );
    }

    protected void addBundle( Bundle bundle )
    {
        if ( loadJsonSchema( bundle ) )
        {
            refreshSchemaRegistry();
        }
    }

    protected boolean loadJsonSchema( final Bundle bundle )
    {
        return loadSchemas( loadJsonSchemasFromBundle( bundle ) );
    }

    protected List<URL> loadJsonSchemasFromBundle( final Bundle bundle )
    {
        final String xpVersion = VersionInfo.get().toString().split( "-" )[0];
        final Enumeration<URL> schemaURLs = bundle.findEntries( "/META-INF/schemas/" + xpVersion, "*.json", true );
        return schemaURLs != null ? Collections.list( schemaURLs ) : Collections.emptyList();
    }
}
