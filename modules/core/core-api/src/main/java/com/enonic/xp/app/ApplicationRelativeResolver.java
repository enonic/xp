package com.enonic.xp.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

@Beta
public final class ApplicationRelativeResolver
{
    private final ApplicationKey current;

    public ApplicationRelativeResolver( final ApplicationKey current )
    {
        this.current = current;
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
