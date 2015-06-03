package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

public class DeleteContentResultJson
{
    private List<Success> successes = new ArrayList<>();

    private List<Pending> pendings = new ArrayList<>();

    private List<Failure> failures = new ArrayList<>();

    public List<Success> getSuccesses()
    {
        return successes;
    }

    public List<Pending> getPendings()
    {
        return pendings;
    }

    public List<Failure> getFailures()
    {
        return failures;
    }

    public void addSuccess( final String contentName )
    {
        successes.add( new Success( contentName ) );
    }

    public void addPending( final String contentName )
    {
        pendings.add( new Pending( contentName ) );
    }

    public void addFailure( final String contentName, final String reason )
    {
        failures.add( new Failure( contentName, reason ) );
    }

    public class Success
    {

        private String name;

        public Success( final String contentName )
        {
            this.name = contentName;
        }

        public String getName()
        {
            return name;
        }
    }

    public class Pending
    {

        private String name;

        public Pending( final String contentName )
        {
            this.name = contentName;
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

        public Failure( final String contentName, final String reason )
        {
            super( contentName );
            this.reason = reason;
        }

        public String getReason()
        {
            return reason;
        }
    }

}
