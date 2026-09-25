package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.impl.schema.YmlParserBase;

/**
 * Validates the schema resources of an application (see {@link AppSchemaResolver}) before they are persisted: every descriptor must
 * declare the {@code kind} its path reserves (see {@link SchemaResourcePaths#expectedKind(String)}), e.g. {@code cms/style/style.yaml}
 * must be a {@code Style}. A single mismatch rejects the whole schema, so nothing of it is persisted.
 */
final class AppSchemaValidator
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    private AppSchemaValidator()
    {
    }

    static void validate( final Map<String, ByteSource> resources )
    {
        final List<String> errors = new ArrayList<>();
        resources.forEach( ( path, content ) -> {
            final String expectedKind = SchemaResourcePaths.expectedKind( path );
            if ( expectedKind == null )
            {
                return;
            }
            final String kind = readKind( path, content, errors );
            if ( kind != null && !expectedKind.equals( kind ) )
            {
                errors.add( String.format( "%s: invalid kind \"%s\", expected \"%s\"", path, kind, expectedKind ) );
            }
        } );

        if ( !errors.isEmpty() )
        {
            throw new ApplicationBundleException( "Invalid application schema: " + String.join( "; ", errors ) );
        }
    }

    private static String readKind( final String path, final ByteSource content, final List<String> errors )
    {
        try
        {
            final String kind = PARSER.kind( content.asCharSource( StandardCharsets.UTF_8 ).read() );
            if ( kind == null )
            {
                errors.add( String.format( "%s: missing kind", path ) );
            }
            return kind;
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        catch ( UncheckedIOException e )
        {
            errors.add( String.format( "%s: not a valid descriptor", path ) );
            return null;
        }
    }
}
