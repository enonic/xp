package com.enonic.wem.web.rest.rpc.content.schema.mixin;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;

final class MixinDeletionResult
{
    private List<QualifiedMixinName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final QualifiedMixinName qualifiedMixinName )
    {
        successes.add( qualifiedMixinName );
    }

    public void failure( final QualifiedMixinName qualifiedMixinName, final String reason )
    {
        failures.add( new Failure( qualifiedMixinName, reason ) );
    }

    public Iterable<QualifiedMixinName> successes()
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
        public final QualifiedMixinName qualifiedMixinName;

        public final String reason;

        public Failure( final QualifiedMixinName qualifiedMixinName, final String reason )
        {
            this.qualifiedMixinName = qualifiedMixinName;
            this.reason = reason;
        }
    }
}
