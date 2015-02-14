package com.enonic.xp.schema.impl.content;

import java.net.URL;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.xml.mapper.XmlContentTypeMapper;
import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.serializer.XmlSerializers;
import com.enonic.xp.schema.impl.IconLoader;

final class ContentTypeLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentTypeLoader.class );

    private final static Pattern PATTERN = Pattern.compile( ".*/cms/content-types/([^/]+)/content-type\\.xml" );

    private final static String FILE = "content-type.xml";

    private final static String DIRECTORY = "cms/content-types";

    private final Bundle bundle;

    private final ModuleKey moduleKey;

    private final IconLoader iconLoader;

    public ContentTypeLoader( final Bundle bundle )
    {
        this.bundle = bundle;
        this.moduleKey = ModuleKey.from( this.bundle );
        this.iconLoader = new IconLoader( this.bundle );
    }

    public ContentTypes load()
    {
        if ( this.bundle.getEntry( DIRECTORY ) == null )
        {
            return null;
        }

        final List<ContentTypeName> names = findNames();
        final List<ContentType> result = load( names );

        return ContentTypes.from( result );
    }

    private List<ContentType> load( final List<ContentTypeName> names )
    {
        final List<ContentType> result = Lists.newArrayList();
        for ( final ContentTypeName name : names )
        {
            final ContentType value = load( name );
            if ( value != null )
            {
                result.add( value );
            }
        }

        return result;
    }

    private ContentType load( final ContentTypeName name )
    {
        final String localName = name.getLocalName();
        final String basePath = DIRECTORY + "/" + localName;
        final URL url = this.bundle.getEntry( basePath + "/" + FILE );

        if ( url == null )
        {
            return null;
        }

        try
        {
            return doLoad( name, url );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Could not load content type [" + name + "]", e );
            return null;
        }
    }

    private ContentType doLoad( final ContentTypeName name, final URL url )
        throws Exception
    {
        final String str = Resources.toString( url, Charsets.UTF_8 );
        final ContentType.Builder builder = parse( str );

        final Instant modifiedTime = Instant.ofEpochMilli( this.bundle.getLastModified() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( this.iconLoader.readIcon( DIRECTORY + "/" + name.getLocalName() ) );
        return builder.name( name ).build();
    }

    private List<ContentTypeName> findNames()
    {
        final Enumeration<URL> urls = this.bundle.findEntries( DIRECTORY, FILE, true );
        if ( urls == null )
        {
            return Lists.newArrayList();
        }

        final List<ContentTypeName> list = Lists.newArrayList();
        while ( urls.hasMoreElements() )
        {
            final URL url = urls.nextElement();
            final ContentTypeName name = getNameFromPath( url.getPath() );

            if ( name != null )
            {
                list.add( name );
            }
        }

        return list;
    }

    private ContentTypeName getNameFromPath( final String path )
    {
        final Matcher matcher = PATTERN.matcher( path );
        return matcher.matches() ? ContentTypeName.from( this.moduleKey, matcher.group( 1 ) ) : null;
    }

    private ContentType.Builder parse( final String str )
    {
        final ContentType.Builder builder = ContentType.newContentType();
        final XmlContentType contentTypeXml = XmlSerializers.contentType().parse( str );
        new XmlContentTypeMapper( this.moduleKey ).fromXml( contentTypeXml, builder );
        return builder;
    }
}
