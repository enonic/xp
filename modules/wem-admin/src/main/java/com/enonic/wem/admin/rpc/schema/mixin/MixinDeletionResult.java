package com.enonic.wem.admin.rpc.schema.mixin;


import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.mixin.MixinName;

final class MixinDeletionResult
{
    private List<MixinName> successes = Lists.newArrayList();

    private List<Failure> failures = Lists.newArrayList();

    public void success( final MixinName mixinName )
    {
        successes.add( mixinName );
    }

    public void failure( final MixinName mixinName, final String reason )
    {
        failures.add( new Failure( mixinName, reason ) );
    }

    public Iterable<MixinName> successes()
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
        public final MixinName mixinName;

        public final String reason;

        public Failure( final MixinName mixinName, final String reason )
        {
            this.mixinName = mixinName;
            this.reason = reason;
        }
    }
}
