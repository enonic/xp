package com.enonic.wem.api.command.content.relationship;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.relationship.RelationshipKey;

public final class UpdateRelationshipFailureException
    extends RuntimeException
{
    private final RelationshipKey relationshipKey;

    private final ImmutableList<Failure> failures;

    private UpdateRelationshipFailureException( final Builder builder )
    {
        super( buildMessage( builder ) );

        Preconditions.checkNotNull( builder.relationshipKey, "relationshipKey cannot be null" );

        this.relationshipKey = builder.relationshipKey;
        this.failures = ImmutableList.copyOf( builder.failures );
    }

    public RelationshipKey getRelationshipKey()
    {
        return relationshipKey;
    }

    public boolean hasFailures()
    {
        return failures.size() > 0;
    }

    public Failure firstFailure()
    {
        return failures.get( 0 );
    }

    public Iterable<Failure> failures()
    {
        return failures;
    }

    public static class Failure
    {
        public final String reason;

        public final Exception exception;

        private Failure( final String reason, final Exception exception )
        {
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
        private RelationshipKey relationshipKey;

        private List<Failure> failures = new ArrayList<>();

        public Builder relationshipKey( final RelationshipKey key )
        {
            relationshipKey = key;
            return this;
        }

        public Builder failure( final Exception e )
        {
            failures.add( new Failure( e.getMessage(), e ) );
            return this;
        }

        public UpdateRelationshipFailureException build()
        {
            return new UpdateRelationshipFailureException( this );
        }
    }

    private static String buildMessage( final Builder builder )
    {
        final StringBuilder s = new StringBuilder();
        s.append( "Failed to update Relationship [" ).append( builder.relationshipKey ).append( "]" );
        s.append( ":\n" );
        for ( int i = 0; i < builder.failures.size(); i++ )
        {
            final Failure failure = builder.failures.get( i );
            s.append( "Failure #" ).append( i + 1 ).append( ": " ).append( failure.reason ).append( "\n" );
        }
        return s.toString();
    }
}
