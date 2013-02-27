package com.enonic.wem.api.command.content.relationship;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.relationship.RelationshipKey;

public final class DeleteRelationshipsResult
{
    private List<RelationshipKey> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final RelationshipKey relationshipKey )
    {
        successes.add( relationshipKey );
    }

    public void failure( final RelationshipKey relationshipKey, final Exception e )
    {
        failures.add( new Failure( relationshipKey, e, e.getMessage() ) );
    }

    public Iterable<RelationshipKey> successes()
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
        public final RelationshipKey relationshipKey;

        public final Exception exception;

        public final String reason;

        public Failure( final RelationshipKey relationshipKey, final Exception exception, final String reason )
        {
            this.relationshipKey = relationshipKey;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
