package com.enonic.wem.admin.rest.resource.schema.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Name;

public class SchemaDeleteJson
{
    private List<SuccessJson> successes = Lists.newArrayList();

    private List<FailureJson> failures = Lists.newArrayList();

    public void success( final Name qualifiedName )
    {
        successes.add( new SuccessJson( qualifiedName ) );
    }

    public void failure( final Name name, final String reason )
    {
        failures.add( new FailureJson( name, reason ) );
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
        private final Name name;

        public SuccessJson( final Name name )
        {
            this.name = name;
        }

        public String getName()
        {
            return name.toString();
        }
    }

    public class FailureJson
    {
        private final Name name;

        private final String reason;

        public FailureJson( final Name name, final String reason )
        {
            this.name = name;
            this.reason = reason;
        }

        public String getName()
        {
            return name.toString();
        }

        public String getReason()
        {
            return reason;
        }
    }
}
