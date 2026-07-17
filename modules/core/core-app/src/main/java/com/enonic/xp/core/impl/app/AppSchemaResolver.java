package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.common.io.ByteSource;

class AppSchemaResolver
{
    private static final Pattern SCHEMA_RESOURCE_PATTERN = Pattern.compile( "^" + VirtualAppConstants.CMS_ROOT_NAME + "/((?:" +
                                                                                String.join( "|",
                                                                                             VirtualAppConstants.CONTENT_TYPE_ROOT_NAME,
                                                                                             VirtualAppConstants.FORM_FRAGMENTS_ROOT_NAME,
                                                                                             VirtualAppConstants.MIXINS_ROOT_NAME,
                                                                                             VirtualAppConstants.PART_ROOT_NAME,
                                                                                             VirtualAppConstants.LAYOUT_ROOT_NAME,
                                                                                             VirtualAppConstants.PAGE_ROOT_NAME,
                                                                                             VirtualAppConstants.MACROS_ROOT_NAME ) +
                                                                                ")/([^/]+)/\\2|" + VirtualAppConstants.CMS_ROOT_NAME +
                                                                                "|" + VirtualAppConstants.STYLE_ROOT_NAME + "/" +
                                                                                VirtualAppConstants.STYLE_NAME + ")\\.(yaml|yml)$" );

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

                final Matcher matcher = SCHEMA_RESOURCE_PATTERN.matcher( entry.getName() );
                if ( !matcher.matches() )
                {
                    continue;
                }

                final String path = matcher.group( 1 ) + ".yaml";
                final String content = new String( zip.readAllBytes(), StandardCharsets.UTF_8 );

                if ( "yaml".equals( matcher.group( 3 ) ) )
                {
                    resources.put( path, content );
                }
                else
                {
                    resources.putIfAbsent( path, content );
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
