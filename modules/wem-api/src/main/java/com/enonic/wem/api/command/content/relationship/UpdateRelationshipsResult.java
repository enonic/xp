package com.enonic.wem.api.command.content.relationship;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.relationship.RelationshipId;

public final class UpdateRelationshipsResult
{
    private List<RelationshipId> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final RelationshipId relationshipId )
    {
        successes.add( relationshipId );
    }

    public void failure( final RelationshipId relationshipId, final Exception e )
    {
        failures.add( new Failure( relationshipId, e, e.getMessage() ) );
    }

    public Iterable<RelationshipId> successes()
    {
        return successes;
    }

    public boolean hasFailures()
    {
        return failures.size() > 0;
    }

    public Iterable<Failure> failures()
    {
        return failures;
    }

    public class Failure
    {
        public final RelationshipId relationshipId;

        public final Exception exception;

        public final String reason;

        public Failure( final RelationshipId relationshipId, final Exception exception, final String reason )
        {
            this.relationshipId = relationshipId;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
