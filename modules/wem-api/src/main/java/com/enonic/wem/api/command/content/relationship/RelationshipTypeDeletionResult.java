package com.enonic.wem.api.command.content.relationship;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;

public final class RelationshipTypeDeletionResult
{
    private List<QualifiedRelationshipTypeName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        successes.add( relationshipTypeName );
    }

    public void failure( final QualifiedRelationshipTypeName relationshipTypeName, final Exception e )
    {
        failures.add( new Failure( relationshipTypeName, e, e.getMessage() ) );
    }

    public Iterable<QualifiedRelationshipTypeName> successes()
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
        public final QualifiedRelationshipTypeName relationshipTypeName;

        public final Exception exception;

        public final String reason;

        public Failure( final QualifiedRelationshipTypeName relationshipTypeName, final Exception exception, final String reason )
        {
            this.relationshipTypeName = relationshipTypeName;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
