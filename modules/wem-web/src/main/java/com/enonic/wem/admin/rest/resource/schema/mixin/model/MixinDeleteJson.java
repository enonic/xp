package com.enonic.wem.admin.rest.resource.schema.mixin.model;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public class MixinDeleteJson
{
    private List<SuccessJson> successes = Lists.newArrayList();

    private List<FailureJson> failures = Lists.newArrayList();

    public void success( final QualifiedMixinName qualifiedMixinName )
    {
        successes.add( new SuccessJson( qualifiedMixinName ) );
    }

    public void failure( final QualifiedMixinName qualifiedMixinName, final String reason )
    {
        failures.add( new FailureJson( qualifiedMixinName, reason ) );
    }

    public boolean isSuccess()
    {
        return failures.isEmpty();
    }

    public List<SuccessJson> getSuccesses()
    {
        return successes;
    }

    public List<FailureJson> getFailures()
    {
        return failures;
    }

    public class SuccessJson
    {
        private final QualifiedMixinName qualifiedMixinName;

        public SuccessJson( final QualifiedMixinName qualifiedMixinName )
        {
            this.qualifiedMixinName = qualifiedMixinName;
        }

        public String getQualifiedMixinName()
        {
            return qualifiedMixinName.toString();
        }
    }

    public class FailureJson
    {
        private final QualifiedMixinName qualifiedMixinName;

        private final String reason;

        public FailureJson( final QualifiedMixinName qualifiedMixinName, final String reason )
        {
            this.qualifiedMixinName = qualifiedMixinName;
            this.reason = reason;
        }

        public String getQualifiedMixinName()
        {
            return qualifiedMixinName.toString();
        }

        public String getReason()
        {
            return reason;
        }
    }
}
