package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.ContentId;

public class PublishContentResultJson
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

    public void addSuccess( final ContentId contentId, final String displayName )
    {
        successes.add( new Success( contentId, displayName ) );
    }

    public void addFailure( final ContentId contentId, final String reason )
    {
        failures.add( new Failure( contentId, reason ) );
    }

    public class Success
    {

        private String id;
        private String name;

        public Success( final ContentId contentId, final String displayName )
        {
            this.id = contentId.toString();
            this.name = displayName;
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
    {

        private String id;
        private String reason;

        public Failure( final ContentId contentId, final String reason )
        {
            this.id = contentId.toString();
            this.reason = reason;
        }

        public String getId()
        {
            return id;
        }

        public String getReason()
        {
            return reason;
        }
    }

}
