package com.enonic.wem.api.relationship;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class Relationships
    extends AbstractImmutableEntityList<Relationship>
{
    private Relationships( final ImmutableList<Relationship> list )
    {
        super( list );
    }

    public static Relationships empty()
    {
        final ImmutableList<Relationship> list = ImmutableList.of();
        return new Relationships( list );
    }

    public static Relationships from( final Relationship... relationshipTypes )
    {
        return new Relationships( ImmutableList.copyOf( relationshipTypes ) );
    }

    public static Relationships from( final Iterable<? extends Relationship> relationshipTypes )
    {
        return new Relationships( ImmutableList.copyOf( relationshipTypes ) );
    }

    public static Relationships from( final Collection<? extends Relationship> relationshipTypes )
    {
        return new Relationships( ImmutableList.copyOf( relationshipTypes ) );
    }

    public static Builder newRelationships()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<Relationship> relationships = new ArrayList<>();

        public Builder add( Relationship value )
        {
            relationships.add( value );
            return this;
        }

        public Relationships build()
        {
            return new Relationships( ImmutableList.copyOf( relationships ) );
        }
    }
}
