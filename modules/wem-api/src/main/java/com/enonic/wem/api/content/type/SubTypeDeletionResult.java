package com.enonic.wem.api.content.type;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;

public class SubTypeDeletionResult
{
    private List<QualifiedSubTypeName> successes = new ArrayList<QualifiedSubTypeName>();

    private List<Failure> failures = new ArrayList<Failure>();

    public void success( final QualifiedSubTypeName qualifiedSubTypeName )
    {
        successes.add( qualifiedSubTypeName );
    }

    public void failure( final QualifiedSubTypeName qualifiedSubTypeName, final Exception e )
    {
        failures.add( new Failure( qualifiedSubTypeName, e, e.getMessage() ) );
    }

    public Iterable<QualifiedSubTypeName> successes()
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
        public final QualifiedSubTypeName qualifiedSubTypeName;

        public final Exception exception;

        public final String reason;

        public Failure( final QualifiedSubTypeName qualifiedSubTypeName, final Exception exception, final String reason )
        {
            this.qualifiedSubTypeName = qualifiedSubTypeName;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
