package com.enonic.wem.admin.rest.resource.content.model;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.DeleteContentResult;

public class DeleteContentJson
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

    public void addResult( ContentPath contentPath, DeleteContentResult result )
    {
        if ( result == DeleteContentResult.SUCCESS )
        {
            successes.add( new Success( contentPath ) );
        }
        else
        {
            failures.add( new Failure( contentPath, result ) );
        }
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

        public Failure( final ContentPath contentPath, final DeleteContentResult result )
        {
            super( contentPath );
            this.reason = result.toString();
        }

        public String getReason()
        {
            return reason;
        }
    }

}
