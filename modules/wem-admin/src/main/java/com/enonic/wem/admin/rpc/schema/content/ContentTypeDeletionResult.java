package com.enonic.wem.admin.rpc.schema.content;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.content.ContentTypeName;

final class ContentTypeDeletionResult
{
    private List<ContentTypeName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final ContentTypeName qualifiedContentTypeName )
    {
        successes.add( qualifiedContentTypeName );
    }

    public void failure( final ContentTypeName qualifiedContentTypeName, final String reason )
    {
        failures.add( new Failure( qualifiedContentTypeName, reason ) );
    }

    public Iterable<ContentTypeName> successes()
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
        public final ContentTypeName qualifiedContentTypeName;

        public final String reason;

        public Failure( final ContentTypeName qualifiedContentTypeName, final String reason )
        {
            this.qualifiedContentTypeName = qualifiedContentTypeName;
            this.reason = reason;
        }
    }
}
