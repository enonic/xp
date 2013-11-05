package com.enonic.wem.admin.rpc.schema.relationship;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

final class RelationshipTypeDeletionResult
{
    private List<RelationshipTypeName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final RelationshipTypeName qualifiedName )
    {
        successes.add( qualifiedName );
    }

    public void failure( final RelationshipTypeName qualifiedName, final String reason )
    {
        failures.add( new Failure( qualifiedName, reason ) );
    }

    public Iterable<RelationshipTypeName> successes()
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
        public final RelationshipTypeName relationshipTypeName;

        public final String reason;

        public Failure( final RelationshipTypeName qualifiedName, final String reason )
        {
            this.relationshipTypeName = qualifiedName;
            this.reason = reason;
        }
    }
}
