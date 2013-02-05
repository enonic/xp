package com.enonic.wem.api.content.type;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.mixin.QualifiedMixinName;

public class MixinDeletionResult
{
    private List<QualifiedMixinName> successes = new ArrayList<QualifiedMixinName>();

    private List<Failure> failures = new ArrayList<Failure>();

    public void success( final QualifiedMixinName qualifiedMixinName )
    {
        successes.add( qualifiedMixinName );
    }

    public void failure( final QualifiedMixinName qualifiedMixinName, final Exception e )
    {
        failures.add( new Failure( qualifiedMixinName, e, e.getMessage() ) );
    }

    public Iterable<QualifiedMixinName> successes()
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
        public final QualifiedMixinName qualifiedMixinName;

        public final Exception exception;

        public final String reason;

        public Failure( final QualifiedMixinName qualifiedMixinName, final Exception exception, final String reason )
        {
            this.qualifiedMixinName = qualifiedMixinName;
            this.exception = exception;
            this.reason = reason;
        }
    }
}
