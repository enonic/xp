package com.enonic.xp.admin.impl.rest.resource.content.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;

public class PublishContentResultJson
{
    private final List<Success> successes;

    private final List<Failure> failures;

    private final List<Deleted> deleted;

    @SuppressWarnings("unused")
    public List<Success> getSuccesses()
    {
        return successes;
    }

    @SuppressWarnings("unused")
    public List<Failure> getFailures()
    {
        return failures;
    }

    @SuppressWarnings("unused")
    public List<Deleted> getDeleted()
    {
        return deleted;
    }

    private PublishContentResultJson( final Builder builder )
    {
        this.successes = builder.successes;
        this.deleted = builder.deleted;
        this.failures = builder.failures;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final List<Success> successes = new ArrayList<>();

        private final List<Failure> failures = new ArrayList<>();

        private final List<Deleted> deleted = new ArrayList<>();

        public Builder success( final Contents contents )
        {
            for ( final Content content : contents )
            {
                this.successes.add( new Success( content.getId(), content.getDisplayName() ) );
            }

            return this;
        }

        public Builder deleted( final Contents contents )
        {
            for ( final Content content : contents )
            {
                this.deleted.add( new Deleted( content.getId(), content.getDisplayName() ) );
            }

            return this;
        }

        public Builder failures( final Contents contents )
        {
            for ( final Content content : contents )
            {
                this.failures.add( new Failure( content.getName().toString(), "" ) );
            }

            return this;
        }

        public PublishContentResultJson build()
        {
            return new PublishContentResultJson( this );
        }

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

    public static class Deleted
    {
        private final String id;


        private final String name;

        public Deleted( final ContentId contentId, final String displayName )
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
        private final String name;

        private final String reason;

        public Failure( final String name, final String reason )
        {
            this.name = name;
            this.reason = reason;
        }

        public String getName()
        {
            return name;
        }

        @SuppressWarnings("unused")
        public String getReason()
        {
            return reason;
        }
    }
}