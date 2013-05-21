package com.enonic.wem.web.rest.rpc.schema.relationship;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

final class RelationshipTypeDeletionResult
{
    private List<QualifiedRelationshipTypeName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final QualifiedRelationshipTypeName qualifiedName )
    {
        successes.add( qualifiedName );
    }

    public void failure( final QualifiedRelationshipTypeName qualifiedName, final String reason )
    {
        failures.add( new Failure( qualifiedName, reason ) );
    }

    public Iterable<QualifiedRelationshipTypeName> successes()
    {
        return successes;
    }

    public boolean hasFailures()
    {
        return !failures.isEmpty();
    }

    public Iterable<Failure> failures()
    {
        return failures;
    }

    public class Failure
    {
        public final QualifiedRelationshipTypeName relationshipTypeName;

        public final String reason;

        public Failure( final QualifiedRelationshipTypeName qualifiedName, final String reason )
        {
            this.relationshipTypeName = qualifiedName;
            this.reason = reason;
        }
    }
}
