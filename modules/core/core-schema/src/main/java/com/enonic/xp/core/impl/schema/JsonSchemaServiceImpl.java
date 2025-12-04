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
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

@Component(immediate = true)
public class JsonSchemaServiceImpl
    implements JsonSchemaService
{
    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaServiceImpl.class );

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final ConcurrentMap<String, JsonSchemaDefinitionWrapper> schemasMap = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    private final BundleContext bundleContext;

    private volatile JsonSchemaFactory schemaFactory;

    @Activate
    public JsonSchemaServiceImpl( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Activate
    @Modified
    public void activate()
    {
        schemasMap.clear();

        final Enumeration<URL> predefinedSchemas = bundleContext.getBundle().findEntries( "/META-INF/schemas", "*.json", true );
        registerSchemas( Collections.list( predefinedSchemas ) );

        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator = new FormItemsJsonSchemaGenerator( Set.of() );

        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
        if ( register( formItemsSchema ) )
        {
            refreshSchemaRegistry();
        }
    }

    @Override
    public void validate( final String schemaId, final String yml )
    {
        final JsonSchemaFactory factory = this.schemaFactory;

        if ( factory == null )
        {
            throw new IllegalArgumentException( "Schema factory is not initialized" );
        }

        final JsonSchema schema = factory.getSchema( SchemaLocation.of( schemaId ) );
        schema.initializeValidators();

        Set<ValidationMessage> errors =
            schema.validate( yml, InputFormat.YAML, ctx -> ctx.getExecutionConfig().setFormatAssertionsEnabled( true ) );

        final boolean valid = errors.isEmpty();

        if ( !valid )
        {
            final StringBuilder builder = new StringBuilder( "Validation errors:" );
            errors.forEach( err -> builder.append( "\n" ).append( "- " ).append( err.getMessage() ) );
            final String message = builder.toString();
            LOG.info( message );

            throw new IllegalArgumentException( message );
        }
    }

    private void registerSchemas( final List<URL> schemaURLs )
    {
        if ( schemaURLs == null )
        {
            return;
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
                LOG.debug( "JSON Schema Definition: {} is registered", schemaURL );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }

        if ( shouldBeUpdated )
        {
            refreshSchemaRegistry();
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
            }

            return changed;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private void refreshSchemaRegistry()
    {
        synchronized ( lock )
        {
            this.schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V202012, builder -> builder.schemaLoaders(
                loader -> loader.schemas( getAllSchemas() ) ) );
        }
    }

    private Map<String, String> getAllSchemas()
    {
        return schemasMap.entrySet()
            .stream()
            .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey, entry -> entry.getValue().schema() ) );
    }

    @Reference(service = JsonSchemaContributor.class, cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addContributor( final JsonSchemaContributor contributor )
    {
        registerSchemas( contributor.getSchemaURLs() );
    }

    public void removeContributor( final JsonSchemaContributor contributor )
    {
        // TODO remove contributor's schemas
    }
}

//package com.enonic.xp.core.impl.schema;
//
//import java.util.List;
//import java.util.Set;
//
//import org.osgi.service.component.annotations.Component;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.networknt.schema.Error;
//import com.networknt.schema.InputFormat;
//import com.networknt.schema.Schema;
//import com.networknt.schema.SchemaLocation;
//import com.networknt.schema.SchemaRegistry;
//import com.networknt.schema.dialect.Dialects;
//
//@Component(immediate = true)
//public class JsonSchemaServiceImpl
//    implements JsonSchemaService
//{
//    private static final Logger LOG = LoggerFactory.getLogger( JsonSchemaServiceImpl.class );
//
//    private final JsonSchemaRegistry jsonSchemaRegistry;
//
//    private SchemaRegistry schemaRegistry;
//
//    public JsonSchemaServiceImpl( final JsonSchemaRegistry jsonSchemaRegistry )
//    {
//        this.jsonSchemaRegistry = jsonSchemaRegistry;
//    }
//
//    public void activate()
//    {
//        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator = new FormItemsJsonSchemaGenerator( Set.of() );
//
//        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
//        jsonSchemaRegistry.register( formItemsSchema );
//
//        this.schemaRegistry =
//            SchemaRegistry.withDialect( Dialects.getDraft202012(), builder -> builder.schemas( jsonSchemaRegistry.getAllSchemas() ) );
//    }
//
//    @Override
//    public void validate( final String schemaId, final String yml )
//    {
//        final Schema schema = schemaRegistry.getSchema( SchemaLocation.of( schemaId ) );
//
//        final List<Error> errors = schema.validate( yml, InputFormat.YAML );
//
//        final boolean valid = errors.isEmpty();
//
//        if ( !valid )
//        {
//            final StringBuilder builder = new StringBuilder( "Validation errors:" );
//            errors.forEach( err -> builder.append( "\n" ).append( "- " ).append( err.getMessage() ) );
//            final String message = builder.toString();
//            LOG.info( message );
//
//            throw new IllegalArgumentException( message );
//        }
//    }
//}
