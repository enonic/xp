package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteSource;

class AppSchemaResolver
{
    private AppSchemaResolver()
    {
    }

    static Map<String, String> resolve( final ByteSource byteSource )
    {
        final Map<String, String> resources = new LinkedHashMap<>();
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

                final String content = new String( zip.readAllBytes(), StandardCharsets.UTF_8 );

                final String phrasesPath = matcher.group( SchemaResourcePaths.PHRASES_PATH_GROUP );
                if ( phrasesPath != null )
                {
                    resources.put( phrasesPath, content );
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
}
