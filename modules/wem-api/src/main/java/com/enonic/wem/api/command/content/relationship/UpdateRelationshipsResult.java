package com.enonic.wem.api.command.content.relationship;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.content.relationship.RelationshipKey;

public final class UpdateRelationshipsResult
{
    private final ImmutableSet<RelationshipKey> successes;

    private final ImmutableMap<RelationshipKey, Failure> failures;

    private UpdateRelationshipsResult( final Builder builder )
    {
        this.successes = builder.successes.build();
        this.failures = builder.failures.build();
    }

    public boolean isSuccess( final RelationshipKey relationshipKey )
    {
        return successes.contains( relationshipKey );
    }

    public Iterable<RelationshipKey> successes()
    {
        return successes;
    }

    public boolean hasFailures()
    {
        return failures.size() > 0;
    }

    public boolean isFailure( final RelationshipKey relationshipKey )
    {
        return failures.containsKey( relationshipKey );
    }

    public Failure getFailure( final RelationshipKey relationshipKey )
    {
        return failures.get( relationshipKey );
    }

    public Iterable<Failure> failures()
    {
        return failures.values();
    }

    public static class Failure
    {
        public final RelationshipKey relationshipKey;

        public final String reason;

        public final Exception exception;

        private Failure( final RelationshipKey relationshipKey, final String reason, final Exception exception )
        {
            this.relationshipKey = relationshipKey;
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
        private ImmutableSet.Builder<RelationshipKey> successes = ImmutableSet.builder();

        private ImmutableMap.Builder<RelationshipKey, Failure> failures = ImmutableMap.builder();

        public Builder success( final RelationshipKey key )
        {
            successes.add( key );
            return this;
        }

        public Builder failure( final RelationshipKey key, final Exception e )
        {
            failures.put( key, new Failure( key, e.getMessage(), e ) );
            return this;
        }

        public UpdateRelationshipsResult build()
        {
            return new UpdateRelationshipsResult( this );
        }
    }
}
