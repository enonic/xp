package com.enonic.wem.api.content.schema.content;


import java.util.ArrayList;
import java.util.List;

public class ContentTypeDeletionResult
{
    private List<QualifiedContentTypeName> successes = new ArrayList<QualifiedContentTypeName>();

    private List<Failure> failures = new ArrayList<Failure>();

    public void success( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        successes.add( qualifiedContentTypeName );
    }

    public void failure( final QualifiedContentTypeName qualifiedContentTypeName, final Exception e )
    {
        failures.add( new Failure( qualifiedContentTypeName, e, e.getMessage() ) );
    }

    public Iterable<QualifiedContentTypeName> successes()
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
        public final QualifiedContentTypeName qualifiedContentTypeName;

        public final Exception exception;

        public final String reason;

        public Failure( final QualifiedContentTypeName qualifiedContentTypeName, final Exception exception, final String reason )
        {
            this.qualifiedContentTypeName = qualifiedContentTypeName;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
