package com.enonic.xp.admin.impl.rest.resource.content.json;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.PushContentsResult;

public class PublishContentResultJson
{
    private final List<Success> successes = new ArrayList<>();

    private final List<Failure> failures = new ArrayList<>();

    private final List<Success> deleted = new ArrayList<>();

    public List<Success> getSuccesses()
    {
        return successes;
    }

    public List<Failure> getFailures()
    {
        return failures;
    }

    public List<Success> getDeleted()
    {
        return deleted;
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
            json.failures.add( new Failure( failed.getContent().getPath(), failed.getFailedReason().getMessage() ) );
        }

        for ( final Content content : pushContentsResult.getDeleted() )
        {
            json.deleted.add( new Success( content.getId(), content.getDisplayName() ) );
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
        private final String path;

        private final String reason;

        public Failure( final ContentPath contentPath, final String reason )
        {
            this.path = contentPath.toString();
            this.reason = reason;
        }

        public String getPath()
        {
            return path;
        }

        public String getReason()
        {
            return reason;
        }
    }

}
