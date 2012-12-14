package com.enonic.wem.api.content;


import java.util.ArrayList;
import java.util.List;

public class ContentDeletionResult
{
    private List<ContentSelector> successes = new ArrayList<ContentSelector>();

    private List<Failure> failures = new ArrayList<Failure>();

    public void success( final ContentSelector contentSelector )
    {
        successes.add( contentSelector );
    }

    public void failure( final ContentSelector contentSelector, final Exception e )
    {
        failures.add( new Failure( contentSelector, e, e.getMessage() ) );
    }

    public Iterable<ContentSelector> successes()
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
        public final ContentSelector contentSelector;

        public final Exception exception;

        public final String reason;

        public Failure( final ContentSelector contentSelector, final Exception exception, final String reason )
        {
            this.contentSelector = contentSelector;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
