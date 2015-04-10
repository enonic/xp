package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.ContentId;

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

    public void addSuccess( final ContentId contentId )
    {
        successes.add( new Success( contentId ) );
    }

    public void addFailure( final ContentId contentId, final String reason )
    {
        failures.add( new Failure( contentId, reason ) );
    }

    public class Success
    {

        private String contentId;

        public Success( final ContentId contentId )
        {
            this.contentId = contentId.toString();
        }

        public String getContentId()
        {
            return contentId;
        }
    }

    public class Failure
        extends Success
    {

        private String reason;

        public Failure( final ContentId contentId, final String reason )
        {
            super( contentId );
            this.reason = reason;
        }

        public String getReason()
        {
            return reason;
        }
    }
}
