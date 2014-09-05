package com.enonic.wem.admin.rest.resource.schema.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.SchemaName;

public class SchemaDeleteJson
{
    private List<SuccessJson> successes = Lists.newArrayList();

    private List<FailureJson> failures = Lists.newArrayList();

    public void success( final SchemaName name )
    {
        successes.add( new SuccessJson( name ) );
    }

    public void failure( final SchemaName name, final String reason )
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
        private final SchemaName name;

        public SuccessJson( final SchemaName name )
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
        private final SchemaName name;

        private final String reason;

        public FailureJson( final SchemaName name, final String reason )
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
