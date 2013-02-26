package com.enonic.wem.api.content.relationship;


import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.util.AbstractImmutableEntitySet;

public class RelationshipKeys
    extends AbstractImmutableEntitySet<RelationshipKey>
{
    private RelationshipKeys( final ImmutableSet set )
    {
        super( set );
    }

    public static RelationshipKeys from( final Iterable<RelationshipKey> keys )
    {
        return new RelationshipKeys( ImmutableSet.copyOf( keys ) );
    }

    public static RelationshipKeys from( final RelationshipKey... keys )
    {
        return new RelationshipKeys( ImmutableSet.copyOf( keys ) );
    }

    public static Builder newRelationshipKeys()
    {
        return new Builder();
    }

    public static class Builder
    {
        private LinkedHashSet<RelationshipKey> relationshipKeys = new LinkedHashSet<>();

        public Builder add( RelationshipKey value )
        {
            relationshipKeys.add( value );
            return this;
        }

        public RelationshipKeys build()
        {
            return new RelationshipKeys( ImmutableSet.copyOf( relationshipKeys ) );
        }
    }

}
