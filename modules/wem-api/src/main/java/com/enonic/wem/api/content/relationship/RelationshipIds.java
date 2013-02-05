package com.enonic.wem.api.content.relationship;


import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.util.AbstractImmutableEntitySet;

public class RelationshipIds
    extends AbstractImmutableEntitySet<RelationshipId>
{
    private RelationshipIds( final ImmutableSet set )
    {
        super( set );
    }

    public static RelationshipIds from( final Iterable<RelationshipId> ids )
    {
        return new RelationshipIds( ImmutableSet.copyOf( ids ) );
    }

    public static RelationshipIds from( final RelationshipId... ids )
    {
        return new RelationshipIds( ImmutableSet.copyOf( ids ) );
    }

    public static Builder newRelationshipIds()
    {
        return new Builder();
    }

    public static class Builder
    {
        private LinkedHashSet<RelationshipId> relationshipIds = new LinkedHashSet<>();

        public Builder add( RelationshipId value )
        {
            relationshipIds.add( value );
            return this;
        }

        public RelationshipIds build()
        {
            return new RelationshipIds( ImmutableSet.copyOf( relationshipIds ) );
        }
    }

}
