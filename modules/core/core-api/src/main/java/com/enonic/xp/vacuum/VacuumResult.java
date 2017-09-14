package com.enonic.xp.vacuum;

import java.util.List;

import com.google.common.collect.Lists;

public class VacuumResult
{
    private final List<VacuumTaskResult> results;

    private VacuumResult( final Builder builder )
    {
        results = builder.results;
    }

    public List<VacuumTaskResult> getResults()
    {
        return results;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<VacuumTaskResult> results = Lists.newLinkedList();

        private Builder()
        {
        }

        public Builder add( final VacuumTaskResult val )
        {
            results.add( val );
            return this;
        }

        public VacuumResult build()
        {
            return new VacuumResult( this );
        }
    }
}
