package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteSource;

/**
 * Extracts schema resources (see {@link SchemaResourcePaths}) from an application jar.
 * Keys of the returned map are paths relative to the {@code cms/} root, values are the resource contents.
 */
final class AppSchemaResolver
{
    private AppSchemaResolver()
    {
    }

    static Map<String, ByteSource> resolve( final ByteSource byteSource )
    {
        final Map<String, ByteSource> resources = new LinkedHashMap<>();
        try (ZipInputStream zip = new ZipInputStream( byteSource.openBufferedStream() ))
        {
            ZipEntry entry;
            while ( ( entry = zip.getNextEntry() ) != null )
            {
                if ( entry.isDirectory() )
                {
                    continue;
                }

                final Matcher matcher = SchemaResourcePaths.SCHEMA_RESOURCE_PATTERN.matcher( entry.getName() );
                if ( !matcher.matches() )
                {
                    continue;
                }

                final ByteSource content = ByteSource.wrap( zip.readAllBytes() );

                final String verbatimPath = firstNonNull( matcher.group( SchemaResourcePaths.PHRASES_PATH_GROUP ),
                                                          matcher.group( SchemaResourcePaths.ICON_PATH_GROUP ) );
                if ( verbatimPath != null )
                {
                    resources.put( verbatimPath, content );
                }
                else
                {
                    // Both .yaml and .yml descriptors normalize to the same ".yaml" key.
                    // If a JAR contains both variants, .yaml wins regardless of zip entry order:
                    // put() lets .yaml overwrite, putIfAbsent() keeps .yml from replacing it.
                    final String path = matcher.group( SchemaResourcePaths.DESCRIPTOR_PATH_GROUP ) + ".yaml";

                    if ( "yaml".equals( matcher.group( SchemaResourcePaths.EXTENSION_GROUP ) ) )
                    {
                        resources.put( path, content );
                    }
                    else
                    {
                        resources.putIfAbsent( path, content );
                    }
                }
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return resources;
    }

    private static String firstNonNull( final String first, final String second )
    {
        return first != null ? first : second;
    }
}