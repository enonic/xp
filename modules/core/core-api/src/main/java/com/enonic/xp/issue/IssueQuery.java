package com.enonic.xp.issue;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public final class IssueQuery
{
    private static final int DEFAULT_FETCH_SIZE = 10;

    private final PrincipalKey creator;

    private final PrincipalKeys approvers;

    private final IssueStatus status;

    private final ContentIds items;

    private final int from;

    private final int size;

    private final boolean count;

    private IssueQuery( final Builder builder )
    {
        this.creator = builder.creator;
        this.approvers = builder.approvers;
        this.items = builder.items;
        this.status = builder.status;
        this.from = builder.from;
        this.size = builder.size;
        this.count = builder.count;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKeys getApprovers()
    {
        return approvers;
    }

    public IssueStatus getStatus()
    {
        return status;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public boolean isCount()
    {
        return count;
    }

    public ContentIds getItems()
    {
        return items;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PrincipalKey creator;

        private PrincipalKeys approvers;

        private ContentIds items;

        private IssueStatus status;

        private int from = 0;

        private int size = DEFAULT_FETCH_SIZE;

        private boolean count = false;

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder approvers( final PrincipalKeys approvers )
        {
            this.approvers = approvers;
            return this;
        }

        public Builder status( final IssueStatus status )
        {
            this.status = status;
            return this;
        }

        public Builder from( final Integer from )
        {
            if ( from != null )
            {
                this.from = from;
            }
            return this;
        }

        public Builder size( final Integer size )
        {
            if ( size != null )
            {
                this.size = size;
            }
            return this;
        }

        public Builder count( final boolean value )
        {
            this.count = value;
            return this;
        }

        public Builder items( final ContentIds items )
        {
            this.items = items;
            return this;
        }

        public IssueQuery build()
        {
            return new IssueQuery( this );
        }
    }

}
