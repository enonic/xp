package com.enonic.xp.app;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;
import com.enonic.xp.schema.xdata.XDataName;

@Beta
public final class ApplicationRelativeResolver
{
    private final ApplicationKey current;

    private final ApplicationWildcardResolver applicationWildcardResolver;

    public ApplicationRelativeResolver( final ApplicationKey current )
    {
        this.current = current;
        this.applicationWildcardResolver = new ApplicationWildcardResolver();
    }

    public String toServiceUrl( final String name )
    {
        if ( name.contains( ":" ) )
        {
            if ( name.startsWith( "http" ) )
            {
                // points to external location
                return name;
            }
            else
            {
                // points to other app
                return name.replace( ":", "/" );
            }
        }

        if ( this.current == null )
        {
            throw new IllegalArgumentException( "Unable to resolve application for Service [" + name + "]" );
        }

        return Joiner.on( "/" ).join( current.toString(), name );
    }

    public ContentTypeName toContentTypeName( final String name )
    {

        if ( name.contains( ":" ) )
        {
            return ContentTypeName.from( name );
        }

        if ( this.current == null )
        {
            throw new IllegalArgumentException( "Unable to resolve application for ContentType [" + name + "]" );
        }

        return ContentTypeName.from( this.current, name );
    }

    public String toContentTypeNameRegexp( final String name )
    {

        if ( name.contains( ":" ) || this.applicationWildcardResolver.stringHasWildcard( name ) )
        {
            return name;
        }

        if ( this.current == null )
        {
            throw new IllegalArgumentException( "Unable to resolve application for ContentType [" + name + "]" );
        }

        return ContentTypeName.from( this.current, name ).toString();
    }

    public MixinName toMixinName( final String name )
    {
        if ( name.contains( ":" ) )
        {
            return MixinName.from( name );
        }

        if ( this.current == null )
        {
            throw new IllegalArgumentException( "Unable to resolve application for Mixin [" + name + "]" );
        }

        return MixinName.from( this.current, name );
    }

    public XDataName toXDataName( final String name )
    {
        if ( name.contains( ":" ) )
        {
            return XDataName.from( name );
        }

        if ( this.current == null )
        {
            throw new IllegalArgumentException( "Unable to resolve application for XData [" + name + "]" );
        }

        return XDataName.from( this.current, name );
    }

    public RelationshipTypeName toRelationshipTypeName( final String name )
    {
        if ( name.contains( ":" ) )
        {
            return RelationshipTypeName.from( name );
        }

        if ( this.current == null )
        {
            throw new IllegalArgumentException( "Unable to resolve application for RelationshipType [" + name + "]" );
        }

        return RelationshipTypeName.from( this.current, name );
    }
}
