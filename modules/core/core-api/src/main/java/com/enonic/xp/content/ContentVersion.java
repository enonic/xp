package com.enonic.xp.content;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.node.Attributes;

@PublicApi
public final class ContentVersion
{
    private final ContentVersionId id;

    private final ContentPath path;

    private final Instant timestamp;

    private final String change;

    private final List<String> changeFields;

    private final Instant changedTime;

    private final PrincipalKey changedBy;

    private final Instant publishedTime;

    private final PrincipalKey publishedBy;

    private final Instant publishedFrom;

    private final Instant publishedTo;

    private final Instant unpublishedTime;

    private final PrincipalKey unpublishedBy;

    private final String comment;

    private final Attributes attributes;

    private ContentVersion( Builder builder )
    {
        this.id = builder.id;
        this.path = builder.path;
        this.timestamp = builder.timestamp;
        this.change = builder.change;
        this.changeFields = builder.changeFields == null ? List.of() : List.copyOf( builder.changeFields );
        this.changedBy = builder.modifiedBy;
        this.changedTime = builder.modifiedTime;
        this.comment = builder.comment;
        this.publishedTime = builder.published;
        this.publishedBy = builder.publishedBy;
        this.publishedFrom = builder.publishedFrom;
        this.publishedTo = builder.publishedTo;
        this.unpublishedTime = builder.unpublished;
        this.unpublishedBy = builder.unpublishedBy;
        this.attributes = builder.attributes;
    }

    public PrincipalKey getChangedBy()
    {
        return changedBy;
    }

    public Instant getChangedTime()
    {
        return changedTime;
    }

    public String getComment()
    {
        return comment;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public ContentVersionId getId()
    {
        return id;
    }

    public ContentPath getPath()
    {
        return path;
    }

    public String getChange()
    {
        return change;
    }

    public List<String> getChangeFields()
    {
        return changeFields;
    }

    public Instant getPublishedTime()
    {
        return publishedTime;
    }

    public PrincipalKey getPublishedBy()
    {
        return publishedBy;
    }

    public Instant getPublishedFrom()
    {
        return publishedFrom;
    }

    public Instant getPublishedTo()
    {
        return publishedTo;
    }

    public Instant getUnpublishedTime()
    {
        return unpublishedTime;
    }

    public PrincipalKey getUnpublishedBy()
    {
        return unpublishedBy;
    }

    @Deprecated
    public Attributes getAttributes()
    {
        return attributes;
    }

    @Override
    public boolean equals( final Object o )
    {
        return o instanceof final ContentVersion that && Objects.equals( id, that.id ) && Objects.equals( path, that.path ) &&
            Objects.equals( timestamp, that.timestamp ) && Objects.equals( change, that.change ) &&
            Objects.equals( changeFields, that.changeFields ) && Objects.equals( changedTime, that.changedTime ) &&
            Objects.equals( changedBy, that.changedBy ) && Objects.equals( publishedTime, that.publishedTime ) &&
            Objects.equals( publishedBy, that.publishedBy ) && Objects.equals( publishedFrom, that.publishedFrom ) &&
            Objects.equals( publishedTo, that.publishedTo ) && Objects.equals( unpublishedTime, that.unpublishedTime ) &&
            Objects.equals( unpublishedBy, that.unpublishedBy ) && Objects.equals( comment, that.comment ) &&
            Objects.equals( attributes, that.attributes );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, path, timestamp, change, changeFields, changedTime, changedBy, publishedTime, publishedBy, publishedFrom,
                             publishedTo, unpublishedTime, unpublishedBy, comment, attributes );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private PrincipalKey modifiedBy;

        private ContentPath path;

        private String change;

        private List<String> changeFields;

        private Instant modifiedTime;

        private Instant timestamp;

        private String comment;

        private ContentVersionId id;

        private Instant published;

        private PrincipalKey publishedBy;

        private Instant publishedFrom;

        private Instant publishedTo;

        private Instant unpublished;

        private PrincipalKey unpublishedBy;

        private Attributes attributes;

        private Builder()
        {
        }

        public Builder id( final ContentVersionId id )
        {
            this.id = id;
            return this;
        }

        public Builder changedBy( final PrincipalKey modifier )
        {
            this.modifiedBy = modifier;
            return this;
        }

        public Builder path( final ContentPath path )
        {
            this.path = path;
            return this;
        }

        public Builder modified( final Instant modified )
        {
            this.modifiedTime = modified;
            return this;
        }

        public Builder timestamp( final Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public Builder comment( final String comment )
        {
            this.comment = comment;
            return this;
        }

        public Builder publishedFrom( final Instant publishedFrom )
        {
            this.publishedFrom = publishedFrom;
            return this;
        }

        public Builder publishedTo( final Instant publishedTo )
        {
            this.publishedTo = publishedTo;
            return this;
        }

        public Builder published( final Instant published )
        {
            this.published = published;
            return this;
        }

        public Builder publishedBy( final PrincipalKey publishedBy )
        {
            this.publishedBy = publishedBy;
            return this;
        }

        public Builder unpublished( final Instant unpublished )
        {
            this.unpublished = unpublished;
            return this;
        }

        public Builder unpublishedBy( final PrincipalKey unpublishedBy )
        {
            this.unpublishedBy = unpublishedBy;
            return this;
        }

        public Builder change( final String change) {
            this.change = change;
            return this;
        }

        public Builder changeFields( final List<String> changeFields) {
            this.changeFields = changeFields;
            return this;
        }

        @Deprecated
        public Builder attributes( final Attributes attributes )
        {
            this.attributes = attributes;
            return this;
        }

        public ContentVersion build()
        {
            return new ContentVersion( this );
        }
    }
}
