package com.enonic.xp.core.impl.schema.mixin;

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

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.IconLoader;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.Mixins;

final class MixinLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( MixinLoader.class );

    private final static Pattern MIXIN_PATTERN = Pattern.compile( ".*/site/mixins/([^/]+)/([^/]+)\\.xml" );

    private final static String MIXIN_FILES = "*.xml";

    private final static String EXTENSION = ".xml";

    private final static String MIXIN_DIRECTORY = "site/mixins";

    private final Bundle bundle;

    private final ApplicationKey applicationKey;

    private final IconLoader iconLoader;

    public MixinLoader( final Bundle bundle )
    {
        this.bundle = bundle;
        this.applicationKey = ApplicationKey.from( this.bundle );
        this.iconLoader = new IconLoader( this.bundle );
    }

    public Mixins loadMixins()
    {
        if ( this.bundle.getEntry( MIXIN_DIRECTORY ) == null )
        {
            return null;
        }

        final List<MixinName> names = findMixinNames();
        final List<Mixin> result = loadMixins( names );

        return Mixins.from( result );
    }

    private List<Mixin> loadMixins( final List<MixinName> names )
    {
        final List<Mixin> result = Lists.newArrayList();
        for ( final MixinName name : names )
        {
            final Mixin mixin = loadMixin( name );
            if ( mixin != null )
            {
                result.add( mixin );
            }
        }

        return result;
    }

    private Mixin loadMixin( final MixinName name )
    {
        final String localName = name.getLocalName();
        final String basePath = MIXIN_DIRECTORY + "/" + localName;
        final URL url = this.bundle.getEntry( basePath + "/" + localName + EXTENSION );

        if ( url == null )
        {
            return null;
        }

        try
        {
            return doLoadMixin( name, url );
        }
        catch ( final Exception e )
        {
            LOG.warn( "Could not load mixin [" + name + "]", e );
            return null;
        }
    }

    private Mixin doLoadMixin( final MixinName name, final URL url )
        throws Exception
    {
        final String str = Resources.toString( url, Charsets.UTF_8 );
        final Mixin.Builder mixin = parseMixinXml( str );

        final Instant modifiedTime = Instant.ofEpochMilli( this.bundle.getLastModified() );
        mixin.modifiedTime( modifiedTime );
        mixin.createdTime( modifiedTime );

        mixin.icon( this.iconLoader.readIcon( MIXIN_DIRECTORY + "/" + name.getLocalName() ) );
        return mixin.name( name ).build();
    }

    private List<MixinName> findMixinNames()
    {
        final Enumeration<URL> urls = this.bundle.findEntries( MIXIN_DIRECTORY, MIXIN_FILES, true );
        if ( urls == null )
        {
            return Lists.newArrayList();
        }

        final List<MixinName> list = Lists.newArrayList();
        while ( urls.hasMoreElements() )
        {
            final URL url = urls.nextElement();
            final MixinName name = getMixinNameFromPath( url.getPath() );

            if ( name != null )
            {
                list.add( name );
            }
        }

        return list;
    }

    private MixinName getMixinNameFromPath( final String path )
    {
        final Matcher matcher = MIXIN_PATTERN.matcher( path );
        return matcher.matches() ? MixinName.from( this.applicationKey, matcher.group( 1 ) ) : null;
    }

    private Mixin.Builder parseMixinXml( final String serializedMixin )
    {
        final Mixin.Builder builder = Mixin.create();

        final XmlMixinParser parser = new XmlMixinParser();
        parser.builder( builder );
        parser.source( serializedMixin );
        parser.currentModule( this.applicationKey );
        parser.parse();

        return builder;
    }
}
