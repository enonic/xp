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
 * <p>
 * Schemas are returned in the flat structure the persisted schema uses: {@code cms/<kind>/<name>.yaml} and its icon
 * {@code cms/<kind>/<name>.svg|png}. Schemas of the legacy folder structure ({@code cms/<kind>/<name>/<name>.yaml}) are moved to
 * the flat structure; {@code cms/cms.yaml} and {@code cms/style/style.yaml} keep their paths. The legacy application descriptor and icon
 * ({@code application.yaml|yml}, {@code application.svg}) are returned as {@code enonic.yaml} and {@code enonic.svg}.
 * Descriptors are normalized to {@code .yaml}.
 * When a jar holds several variants of a resource, the flat structure (and {@code enonic.*}) wins over the legacy one, then {@code .yaml}
 * over {@code .yml}, regardless of the zip entry order.
 */
final class AppSchemaResolver
{
    private AppSchemaResolver()
    {
    }

    static Map<String, ByteSource> resolve( final ByteSource byteSource )
    {
        final Map<String, Candidate> resources = new LinkedHashMap<>();
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

                // enonic.yaml|yml or the legacy application.yaml|yml, persisted as enonic.yaml
                final String appDescriptor = matcher.group( SchemaResourcePaths.APP_DESCRIPTOR_GROUP );
                if ( appDescriptor != null )
                {
                    put( resources, SchemaResourcePaths.APP_DESCRIPTOR_NAME,
                         rank( isLegacyAppResource( appDescriptor ), matcher.group( SchemaResourcePaths.APP_DESCRIPTOR_EXTENSION_GROUP ) ),
                         content );
                    continue;
                }

                // enonic.svg or the legacy application.svg, persisted as enonic.svg
                final String appIcon = matcher.group( SchemaResourcePaths.APP_ICON_GROUP );
                if ( appIcon != null )
                {
                    put( resources, SchemaResourcePaths.APP_ICON_NAME, isLegacyAppResource( appIcon ) ? 1 : 0, content );
                    continue;
                }

                final String phrasesPath = matcher.group( SchemaResourcePaths.PHRASES_PATH_GROUP );
                if ( phrasesPath != null )
                {
                    put( resources, cmsPath( phrasesPath ), 0, content );
                    continue;
                }

                final String kind = matcher.group( SchemaResourcePaths.KIND_GROUP );
                if ( kind != null )
                {
                    final String legacyName = matcher.group( SchemaResourcePaths.NAME_GROUP );
                    final String name = legacyName != null ? legacyName : matcher.group( SchemaResourcePaths.FLAT_NAME_GROUP );
                    put( resources, cmsPath( kind + "/" + name + ".yaml" ),
                         rank( legacyName != null, matcher.group( SchemaResourcePaths.EXTENSION_GROUP ) ), content );
                    continue;
                }

                final String iconKind = matcher.group( SchemaResourcePaths.ICON_KIND_GROUP );
                if ( iconKind != null )
                {
                    final String legacyName = matcher.group( SchemaResourcePaths.ICON_NAME_GROUP );
                    final String name = legacyName != null ? legacyName : matcher.group( SchemaResourcePaths.FLAT_ICON_NAME_GROUP );
                    put( resources, cmsPath( iconKind + "/" + name + "." + matcher.group( SchemaResourcePaths.ICON_EXTENSION_GROUP ) ),
                         legacyName != null ? 1 : 0, content );
                    continue;
                }

                // cms/cms.yaml or cms/style/style.yaml
                put( resources, cmsPath( matcher.group( SchemaResourcePaths.CMS_DESCRIPTOR_GROUP ) + ".yaml" ),
                     rank( false, matcher.group( SchemaResourcePaths.CMS_DESCRIPTOR_EXTENSION_GROUP ) ), content );
            }
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        final Map<String, ByteSource> result = new LinkedHashMap<>();
        resources.forEach( ( path, candidate ) -> result.put( path, candidate.content() ) );
        return result;
    }

    private static boolean isLegacyAppResource( final String name )
    {
        return SchemaResourcePaths.LEGACY_APP_RESOURCE_NAME.equals( name );
    }

    // lower wins: flat (or enonic) before legacy (or application), then yaml before yml
    private static int rank( final boolean legacy, final String extension )
    {
        return ( legacy ? 2 : 0 ) + ( "yaml".equals( extension ) ? 0 : 1 );
    }

    private static void put( final Map<String, Candidate> resources, final String path, final int rank, final ByteSource content )
    {
        resources.merge( path, new Candidate( rank, content ), ( existing, candidate ) -> candidate.rank() < existing.rank()
            ? candidate
            : existing );
    }

    private static String cmsPath( final String cmsRelativePath )
    {
        return SchemaResourceNames.CMS_ROOT_NAME + "/" + cmsRelativePath;
    }

    private record Candidate(int rank, ByteSource content)
    {
    }
}
