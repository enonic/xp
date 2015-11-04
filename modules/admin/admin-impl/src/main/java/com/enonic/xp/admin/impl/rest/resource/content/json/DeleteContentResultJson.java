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

    public void addSuccess( final String id, final String contentName, final String type )
    {
        successes.add( new Success( id, contentName, type ) );
    }

    public void addPending( final String id, final String contentName )
    {
        pendings.add( new Pending( id, contentName ) );
    }

    public void addFailure( final String id, final String contentName, final String type, final String reason )
    {
        failures.add( new Failure( id, contentName, type, reason ) );
    }

    public class Success
    {

        private String id;

        private String name;

        private String type;

        public Success( final String id, final String contentName, final String type )
        {
            this.id = id;
            this.name = contentName;
            this.type = type;
        }

        public String getId()
        {
            return id;
        }

        public String getName()
        {
            return name;
        }

        public String getType()
        {
            return type;
        }
    }

    public class Pending
    {
        private String id;

        private String name;

        public Pending( final String id, final String contentName )
        {
            this.id = id;
            this.name = contentName;
        }

        public String getId()
        {
            return id;
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

        public Failure( final String id, final String contentName, final String type, final String reason )
        {
            super( id, contentName, type );
            this.reason = reason;
        }

        public String getReason()
        {
            return reason;
        }
    }

}
