package com.enonic.wem.api.command.content.schema.relationshiptype;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.schema.relationshiptype.QualifiedRelationshipTypeName;

public final class RelationshipTypeDeletionResult
{
    private List<QualifiedRelationshipTypeName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final QualifiedRelationshipTypeName qualifiedName )
    {
        successes.add( qualifiedName );
    }

    public void failure( final QualifiedRelationshipTypeName qualifiedName, final Exception e )
    {
        failures.add( new Failure( qualifiedName, e, e.getMessage() ) );
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

        public Failure( final QualifiedRelationshipTypeName qualifiedName, final Exception exception, final String reason )
        {
            this.relationshipTypeName = qualifiedName;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
