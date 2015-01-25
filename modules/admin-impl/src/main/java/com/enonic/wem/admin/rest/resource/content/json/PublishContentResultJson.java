package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.PushContentsResult;

public class PublishContentResultJson
{
    private final List<Success> successes = new ArrayList<>();

    private final List<Failure> failures = new ArrayList<>();

    public List<Success> getSuccesses()
    {
        return successes;
    }

    public List<Failure> getFailures()
    {
        return failures;
    }

    private PublishContentResultJson()
    {
    }

    public static PublishContentResultJson from( final PushContentsResult pushContentsResult )
    {
        final PublishContentResultJson json = new PublishContentResultJson();

        for ( final Content content : pushContentsResult.getPushedContent() )
        {
            json.successes.add( new Success( content.getId(), content.getDisplayName() ) );
        }

        for ( final PushContentsResult.Failed failed : pushContentsResult.getFailed() )
        {
            json.failures.add( new Failure( failed.getContent().getId(), failed.getFailedReason().getMessage() ) );
        }

        return json;
    }

    public static class Success
    {
        private final String id;

        private final String name;

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

    public static class Failure
    {

        private final String id;

        private final String reason;

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
