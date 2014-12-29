package com.enonic.wem.core.schema.metadata;

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
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.api.xml.mapper.XmlMetadataSchemaMapper;
import com.enonic.wem.api.xml.model.XmlMetadataSchema;
import com.enonic.wem.api.xml.serializer.XmlSerializers;
import com.enonic.wem.core.schema.IconLoader;

final class MetadataSchemaLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( MetadataSchemaLoader.class );

    private final static Pattern PATTERN = Pattern.compile( ".*/metadata/([^/]+)/metadata\\.xml" );

    private final static String FILE = "metadata.xml";

    private final static String DIRECTORY = "metadata";

    private final Bundle bundle;

    private final ModuleKey moduleKey;

    private final IconLoader iconLoader;

    public MetadataSchemaLoader( final Bundle bundle )
    {
        this.bundle = bundle;
        this.moduleKey = ModuleKey.from( this.bundle );
        this.iconLoader = new IconLoader( this.bundle );
    }

    public MetadataSchemas load()
    {
        if ( this.bundle.getEntry( DIRECTORY ) == null )
        {
            return null;
        }

        final List<MetadataSchemaName> names = findNames();
        final List<MetadataSchema> result = load( names );

        return MetadataSchemas.from( result );
    }

    private List<MetadataSchema> load( final List<MetadataSchemaName> names )
    {
        final List<MetadataSchema> result = Lists.newArrayList();
        for ( final MetadataSchemaName name : names )
        {
            final MetadataSchema value = load( name );
            if ( value != null )
            {
                result.add( value );
            }
        }

        return result;
    }

    private MetadataSchema load( final MetadataSchemaName name )
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
            LOG.warn( "Could not load metadata schema [" + name + "]", e );
            return null;
        }
    }

    private MetadataSchema doLoad( final MetadataSchemaName name, final URL url )
        throws Exception
    {
        final String str = Resources.toString( url, Charsets.UTF_8 );
        final MetadataSchema.Builder builder = parse( str );

        final Instant modifiedTime = Instant.ofEpochMilli( this.bundle.getLastModified() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( this.iconLoader.readIcon( DIRECTORY + "/" + name.getLocalName() ) );
        return builder.name( name ).build();
    }

    private List<MetadataSchemaName> findNames()
    {
        final Enumeration<URL> urls = this.bundle.findEntries( DIRECTORY, FILE, true );
        if ( urls == null )
        {
            return Lists.newArrayList();
        }

        final List<MetadataSchemaName> list = Lists.newArrayList();
        while ( urls.hasMoreElements() )
        {
            final URL url = urls.nextElement();
            final MetadataSchemaName name = getNameFromPath( url.getPath() );

            if ( name != null )
            {
                list.add( name );
            }
        }

        return list;
    }

    private MetadataSchemaName getNameFromPath( final String path )
    {
        final Matcher matcher = PATTERN.matcher( path );
        return matcher.matches() ? MetadataSchemaName.from( this.moduleKey, matcher.group( 1 ) ) : null;
    }

    private MetadataSchema.Builder parse( final String str )
    {
        final MetadataSchema.Builder builder = MetadataSchema.newMetadataSchema();
        final XmlMetadataSchema metadataSchemaXml = XmlSerializers.metadataSchema().parse( str );
        XmlMetadataSchemaMapper.fromXml( metadataSchemaXml, builder );
        return builder;
    }
}
