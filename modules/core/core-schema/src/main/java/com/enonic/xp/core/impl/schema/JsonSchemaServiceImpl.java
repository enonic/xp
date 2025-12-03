package com.enonic.xp.core.impl.schema;

import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final JsonSchemaRegistry jsonSchemaRegistry;

    private JsonSchemaFactory schemaFactory;

    @Activate
    public JsonSchemaServiceImpl( final BundleContext bundleContext )
    {
        this.jsonSchemaRegistry = new JsonSchemaRegistry();
        this.jsonSchemaRegistry.activate( bundleContext );
    }

    @Activate
    @Modified
    public void activate()
    {
        final FormItemsJsonSchemaGenerator formItemsJsonSchemaGenerator = new FormItemsJsonSchemaGenerator( Set.of() );

        final String formItemsSchema = formItemsJsonSchemaGenerator.generate();
        jsonSchemaRegistry.register( formItemsSchema );

        this.schemaFactory = JsonSchemaFactory.getInstance( SpecVersion.VersionFlag.V202012, builder -> builder.schemaLoaders(
            loader -> loader.schemas( jsonSchemaRegistry.getAllSchemas() ) ) );
    }

    @Override
    public void validate( final String schemaId, final String yml )
    {
        final JsonSchema schema = schemaFactory.getSchema( SchemaLocation.of( schemaId ) );
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
