package com.enonic.wem.api.command.content.relationship;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.relationship.RelationshipId;

public final class UpdateRelationshipsResult
{
    private final ImmutableSet<RelationshipId> successes;

    private final ImmutableMap<RelationshipId, Failure> failures;

    private UpdateRelationshipsResult( final Builder builder )
    {
        this.successes = builder.successes.build();
        this.failures = builder.failures.build();
    }

    public boolean isSuccess( final RelationshipId relationshipId )
    {
        return successes.contains( relationshipId );
    }

    public Iterable<RelationshipId> successes()
    {
        return successes;
    }

    public boolean hasFailures()
    {
        return failures.size() > 0;
    }

    public boolean isFailure( final RelationshipId relationshipId )
    {
        return failures.containsKey( relationshipId );
    }

    public Failure getFailure( final RelationshipId relationshipId )
    {
        return failures.get( relationshipId );
    }

    public Iterable<Failure> failures()
    {
        return failures.values();
    }

    public static class Failure
    {
        public final RelationshipId relationshipId;

        public final String reason;

        public final Exception exception;

        private Failure( final RelationshipId relationshipId, final String reason, final Exception exception )
        {
            this.relationshipId = relationshipId;
            this.reason = reason;
            this.exception = exception;
        }
    }

    public static Builder newUpdateRelationshipsResult()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableSet.Builder<RelationshipId> successes = ImmutableSet.builder();

        private ImmutableMap.Builder<RelationshipId, Failure> failures = ImmutableMap.builder();

        public Builder success( final RelationshipId id )
        {
            successes.add( id );
            return this;
        }

        public Builder failure( final RelationshipId id, final Exception e )
        {
            failures.put( id, new Failure( id, e.getMessage(), e ) );
            return this;
        }

        public UpdateRelationshipsResult build()
        {
            return new UpdateRelationshipsResult( this );
        }
    }
}
