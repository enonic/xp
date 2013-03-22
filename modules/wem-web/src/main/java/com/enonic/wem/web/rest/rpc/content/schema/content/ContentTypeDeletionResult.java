package com.enonic.wem.web.rest.rpc.content.schema.content;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;

final class ContentTypeDeletionResult
{
    private List<QualifiedContentTypeName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        successes.add( qualifiedContentTypeName );
    }

    public void failure( final QualifiedContentTypeName qualifiedContentTypeName, final String reason )
    {
        failures.add( new Failure( qualifiedContentTypeName, reason ) );
    }

    public Iterable<QualifiedContentTypeName> successes()
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
        public final QualifiedContentTypeName qualifiedContentTypeName;

        public final String reason;

        public Failure( final QualifiedContentTypeName qualifiedContentTypeName, final String reason )
        {
            this.qualifiedContentTypeName = qualifiedContentTypeName;
            this.reason = reason;
        }
    }
}
