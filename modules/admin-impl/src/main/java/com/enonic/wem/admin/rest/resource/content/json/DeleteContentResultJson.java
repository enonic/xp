package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.ContentPath;

public class DeleteContentResultJson
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

    public void addSuccess( final ContentPath contentPath )
    {
        successes.add( new Success( contentPath ) );
    }

    public void addFailure( final ContentPath contentPath, final String reason )
    {
        failures.add( new Failure( contentPath, reason ) );
    }

    public class Success
    {

        private String path;

        public Success( final ContentPath contentPath )
        {
            this.path = contentPath.toString();
        }

        public String getPath()
        {
            return path;
        }
    }

    public class Failure
        extends Success
    {

        private String reason;

        public Failure( final ContentPath contentPath, final String reason )
        {
            super( contentPath );
            this.reason = reason;
        }

        public String getReason()
        {
            return reason;
        }
    }

}
