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
 * Keys of the returned map are paths relative to the application root ({@code enonic.yaml}, {@code cms/cms.yaml}, ...),
 * values are the resource contents.
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

                if ( matcher.group( SchemaResourcePaths.APP_DESCRIPTOR_GROUP ) != null )
                {
                    putDescriptor( resources, SchemaResourcePaths.APP_DESCRIPTOR_NAME,
                                   matcher.group( SchemaResourcePaths.APP_DESCRIPTOR_EXTENSION_GROUP ), content );
                    continue;
                }

                final String appIconPath = matcher.group( SchemaResourcePaths.APP_ICON_PATH_GROUP );
                if ( appIconPath != null )
                {
                    resources.put( appIconPath, content );
                    continue;
                }

                final String verbatimPath = firstNonNull( matcher.group( SchemaResourcePaths.PHRASES_PATH_GROUP ),
                                                          matcher.group( SchemaResourcePaths.ICON_PATH_GROUP ) );
                if ( verbatimPath != null )
                {
                    resources.put( cmsPath( verbatimPath ), content );
                }
                else
                {
                    putDescriptor( resources, cmsPath( matcher.group( SchemaResourcePaths.DESCRIPTOR_PATH_GROUP ) + ".yaml" ),
                                   matcher.group( SchemaResourcePaths.EXTENSION_GROUP ), content );
                }
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
        return resources;
    }

    /**
     * Both .yaml and .yml descriptors normalize to the same ".yaml" key.
     * If a JAR contains both variants, .yaml wins regardless of zip entry order:
     * put() lets .yaml overwrite, putIfAbsent() keeps .yml from replacing it.
     */
    private static void putDescriptor( final Map<String, ByteSource> resources, final String path, final String extension,
                                       final ByteSource content )
    {
        if ( "yaml".equals( extension ) )
        {
            resources.put( path, content );
        }
        else
        {
            resources.putIfAbsent( path, content );
        }
    }

    private static String cmsPath( final String cmsRelativePath )
    {
        return VirtualAppConstants.CMS_ROOT_NAME + "/" + cmsRelativePath;
    }

    private static String firstNonNull( final String first, final String second )
    {
        return first != null ? first : second;
    }
}