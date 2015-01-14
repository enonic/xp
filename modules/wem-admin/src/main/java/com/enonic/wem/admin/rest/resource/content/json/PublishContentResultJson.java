package com.enonic.wem.admin.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.PushContentsResult;

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

    public PublishContentResultJson()
    {
    }

    public static PublishContentResultJson from( final PushContentsResult pushContentsResult )
    {
        final PublishContentResultJson json = new PublishContentResultJson();

        for ( final Content content : pushContentsResult.getSuccessfull() )
        {
            json.successes.add( new Success( content.getId(), content.getDisplayName() ) );
        }

        for ( final PushContentsResult.Failed failed : pushContentsResult.getFailed() )
        {
            json.failures.add( new Failure( failed.getContent().getId(), failed.getReason().getMessage() ) );
        }

        return json;
    }

    public static class Success
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

    public static class Failure
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
