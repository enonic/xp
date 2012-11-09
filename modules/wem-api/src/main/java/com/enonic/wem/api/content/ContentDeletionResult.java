package com.enonic.wem.api.content;


import java.util.ArrayList;
import java.util.List;

public class ContentDeletionResult
{
    private List<ContentPath> successes = new ArrayList<ContentPath>();

    private List<Failure> failures = new ArrayList<Failure>();

    public void success( final ContentPath contentPath )
    {
        successes.add( contentPath );
    }

    public void failure( final ContentPath contentPath, final Exception e )
    {
        failures.add( new Failure( contentPath, e, e.getMessage() ) );
    }

    public Iterable<ContentPath> successes()
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
        public final ContentPath contentPath;

        public final Exception exception;

        public final String reason;

        public Failure( final ContentPath contentPath, final Exception exception, final String reason )
        {
            this.contentPath = contentPath;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
