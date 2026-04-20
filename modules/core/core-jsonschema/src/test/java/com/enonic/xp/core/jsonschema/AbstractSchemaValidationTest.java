package com.enonic.xp.core.jsonschema;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.dialect.Dialects;

import org.junit.jupiter.api.BeforeAll;

import static java.util.Objects.requireNonNull;

abstract class AbstractSchemaValidationTest
{
    private static Map<String, Schema> schemasByFileName;

    @BeforeAll
    static void initSchemas()
        throws IOException, URISyntaxException
    {
        if ( schemasByFileName != null )
        {
            return;
        }

        final Map<String, String> schemas = loadAllSchemas();
        final SchemaRegistry registry =
            SchemaRegistry.withDialect( Dialects.getDraft202012(), builder -> builder.schemas( schemas ) );

        schemasByFileName = new HashMap<>();
        for ( final String id : schemas.keySet() )
        {
            final String fileName = id.substring( id.lastIndexOf( '/' ) + 1 );
            schemasByFileName.put( fileName, registry.getSchema( SchemaLocation.of( id ) ) );
        }
    }

    protected static Schema schemaFor( final String schemaFileName )
    {
        return requireNonNull( schemasByFileName.get( schemaFileName ),
                                       () -> schemaFileName + " not found in loaded schemas" );
    }

    protected static Collection<Error> validateYaml( final Schema schema, final String resourcePath )
    {
        final InputStream is =
            AbstractSchemaValidationTest.class.getClassLoader().getResourceAsStream( resourcePath );
        requireNonNull( is, () -> "Resource not found: " + resourcePath );
        try ( is )
        {
            return schema.validate( new String( is.readAllBytes(), StandardCharsets.UTF_8 ), InputFormat.YAML );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static Map<String, String> loadAllSchemas()
        throws IOException, URISyntaxException
    {
        final Map<String, String> schemas = new HashMap<>();
        final ObjectMapper mapper = new ObjectMapper();

        final URL resource = AbstractSchemaValidationTest.class.getClassLoader().getResource( "META-INF/schemas" );
        if ( resource == null )
        {
            return schemas;
        }

        final URI uri = resource.toURI();
        if ( "jar".equals( uri.getScheme() ) )
        {
            final String uriStr = uri.toString();
            final Path jarPath = Path.of( URI.create( uriStr.substring( "jar:".length(), uriStr.indexOf( '!' ) ) ) );
            try ( FileSystem fs = FileSystems.newFileSystem( jarPath, Map.of() ) )
            {
                walkAndCollect( fs.getPath( "/META-INF/schemas" ), schemas, mapper );
            }
        }
        else
        {
            walkAndCollect( Path.of( uri ), schemas, mapper );
        }

        return schemas;
    }

    private static void walkAndCollect( final Path dir, final Map<String, String> schemas, final ObjectMapper mapper )
        throws IOException
    {
        try ( var stream = Files.walk( dir ) )
        {
            stream.filter( p -> p.toString().endsWith( ".json" ) ).forEach( p -> {
                try
                {
                    final String content = Files.readString( p, StandardCharsets.UTF_8 );
                    final String id = mapper.readTree( content ).path( "$id" ).asText( null );
                    if ( id != null )
                    {
                        schemas.put( id, content );
                    }
                }
                catch ( IOException e )
                {
                    throw new UncheckedIOException( e );
                }
            } );
        }
    }
}
