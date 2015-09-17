package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.ArrayList;
import java.util.List;

public class MoveContentResultJson
{
    private List<Success> successes = new ArrayList<>();

    private List<Failure> failures = new ArrayList<>();

    public List<Success> getSuccesses()
    {
        return successes;
    }

    public List<Failure> getFailures()
    {
        return failures;
    }

    public void addSuccess( final String name )
    {
        successes.add( new Success( name ) );
    }

    public void addFailure( final String name, final String reason )
    {
        failures.add( new Failure( name, reason ) );
    }

    public class Success
    {

        private String name;

        public Success( final String name )
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    public class Failure
        extends Success
    {

        private String reason;

        public Failure( final String name, final String reason )
        {
            super( name );
            this.reason = reason;
        }

        public String getReason()
        {
            return reason;
        }
    }
}
