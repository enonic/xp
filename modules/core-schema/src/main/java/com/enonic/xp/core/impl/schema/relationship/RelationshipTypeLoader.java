package com.enonic.xp.core.impl.schema.relationship;

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

import com.enonic.xp.core.impl.schema.IconLoader;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.relationship.RelationshipTypes;

final class RelationshipTypeLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( RelationshipTypeLoader.class );

    private final static Pattern PATTERN = Pattern.compile( ".*/app/relationship-types/([^/]+)/([^/]+)\\.xml" );

    private final static String FILES = "*.xml";

    private final static String EXTENSION = ".xml";

    private final static String DIRECTORY = "relationship-types";

    private final Bundle bundle;

    private final ModuleKey moduleKey;

    private final IconLoader iconLoader;

    public RelationshipTypeLoader( final Bundle bundle )
    {
        this.bundle = bundle;
        this.moduleKey = ModuleKey.from( this.bundle );
        this.iconLoader = new IconLoader( this.bundle );
    }

    public RelationshipTypes load()
    {
        if ( this.bundle.getEntry( DIRECTORY ) == null )
        {
            return null;
        }

        final List<RelationshipTypeName> names = findNames();
        final List<RelationshipType> result = load( names );

        return RelationshipTypes.from( result );
    }

    private List<RelationshipType> load( final List<RelationshipTypeName> names )
    {
        final List<RelationshipType> result = Lists.newArrayList();
        for ( final RelationshipTypeName name : names )
        {
            final RelationshipType value = load( name );
            if ( value != null )
            {
                result.add( value );
            }
        }

        return result;
    }

    private RelationshipType load( final RelationshipTypeName name )
    {
        final String localName = name.getLocalName();
        final String basePath = DIRECTORY + "/" + localName;
        final URL url = this.bundle.getEntry( basePath + "/" + localName + EXTENSION );

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
            LOG.warn( "Could not load relationship-type [" + name + "]", e );
            return null;
        }
    }

    private RelationshipType doLoad( final RelationshipTypeName name, final URL url )
        throws Exception
    {
        final String str = Resources.toString( url, Charsets.UTF_8 );
        final RelationshipType.Builder builder = parse( str );

        final Instant modifiedTime = Instant.ofEpochMilli( this.bundle.getLastModified() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( this.iconLoader.readIcon( DIRECTORY + "/" + name.getLocalName() ) );
        return builder.name( name ).build();
    }

    private List<RelationshipTypeName> findNames()
    {
        final Enumeration<URL> urls = this.bundle.findEntries( DIRECTORY, FILES, true );
        if ( urls == null )
        {
            return Lists.newArrayList();
        }

        final List<RelationshipTypeName> list = Lists.newArrayList();
        while ( urls.hasMoreElements() )
        {
            final URL url = urls.nextElement();
            final RelationshipTypeName name = getNameFromPath( url.getPath() );

            if ( name != null )
            {
                list.add( name );
            }
        }

        return list;
    }

    private RelationshipTypeName getNameFromPath( final String path )
    {
        final Matcher matcher = PATTERN.matcher( path );
        return matcher.matches() ? RelationshipTypeName.from( this.moduleKey, matcher.group( 1 ) ) : null;
    }

    private RelationshipType.Builder parse( final String str )
    {
        final RelationshipType.Builder builder = RelationshipType.newRelationshipType();

        final XmlRelationshipTypeParser parser = new XmlRelationshipTypeParser();
        parser.builder( builder );
        parser.source( str );
        parser.currentModule( this.moduleKey );
        parser.parse();

        return builder;
    }
}
