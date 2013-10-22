package com.enonic.wem.admin.rest.resource.schema.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.QualifiedName;

public class SchemaDeleteJson
{
    private List<SuccessJson> successes = Lists.newArrayList();

    private List<FailureJson> failures = Lists.newArrayList();

    public void success( final QualifiedName qualifiedName )
    {
        successes.add( new SuccessJson( qualifiedName ) );
    }

    public void failure( final QualifiedName qualifiedMixinName, final String reason )
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
        private final QualifiedName qualifiedName;

        public SuccessJson( final QualifiedName qualifiedName )
        {
            this.qualifiedName = qualifiedName;
        }

        public String getQualifiedName()
        {
            return qualifiedName.toString();
        }
    }

    public class FailureJson
    {
        private final QualifiedName qualifiedName;

        private final String reason;

        public FailureJson( final QualifiedName qualifiedName, final String reason )
        {
            this.qualifiedName = qualifiedName;
            this.reason = reason;
        }

        public String getQualifiedName()
        {
            return qualifiedName.toString();
        }

        public String getReason()
        {
            return reason;
        }
    }
}
