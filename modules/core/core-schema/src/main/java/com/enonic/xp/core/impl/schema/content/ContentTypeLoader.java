package com.enonic.xp.core.impl.schema.content;

import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.xml.parser.XmlContentTypeParser;

final class ContentTypeLoader
{
    private final static String PATH = "/site/content-types";

    private final static Pattern PATTERN = Pattern.compile( PATH + "/([^/]+)/([^/]+)\\.xml" );

    private final ResourceService resourceService;

    public ContentTypeLoader( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public ContentType load( final ContentTypeName name )
    {
        final ResourceKey resourceKey = toResourceKey( name, "xml" );
        final Resource resource = this.resourceService.getResource( resourceKey );

        if ( !resource.exists() )
        {
            return null;
        }

        final ContentType.Builder builder = ContentType.create();
        parseXml( resource, builder );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    public List<ContentTypeName> findNames( final ApplicationKey key )
    {
        final List<ContentTypeName> keys = Lists.newArrayList();
        for ( final ResourceKey resource : this.resourceService.findFiles( key, PATH, "xml", true ) )
        {
            final Matcher matcher = PATTERN.matcher( resource.getPath() );
            if ( matcher.matches() )
            {
                final String name = matcher.group( 2 );
                if ( name.equals( matcher.group( 1 ) ) )
                {
                    keys.add( ContentTypeName.from( key, name ) );
                }
            }
        }

        return keys;
    }

    private ResourceKey toResourceKey( final ContentTypeName name, final String ext )
    {
        final ApplicationKey appKey = name.getApplicationKey();
        final String localName = name.getLocalName();
        return ResourceKey.from( appKey, PATH + "/" + localName + "/" + localName + "." + ext );
    }

    private void parseXml( final Resource resource, final ContentType.Builder builder )
    {
        final XmlContentTypeParser parser = new XmlContentTypeParser();
        parser.currentApplication( resource.getKey().getApplicationKey() );
        parser.source( resource.readString() );
        parser.builder( builder );
        parser.parse();
    }

    private Icon loadIcon( final ContentTypeName name )
    {
        final ResourceKey resourceKey = toResourceKey( name, "png" );
        final Resource resource = this.resourceService.getResource( resourceKey );
        return SchemaHelper.loadIcon( resource );
    }
}
