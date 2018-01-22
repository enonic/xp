package com.enonic.xp.issue;

import com.enonic.xp.security.PrincipalKey;

public final class IssueCommentQuery
{
    private static final int DEFAULT_FETCH_SIZE = 10;

    private final PrincipalKey creator;

    private final IssueName issueName;

    private final int from;

    private final int size;

    private final boolean count;

    private IssueCommentQuery( final Builder builder )
    {
        this.creator = builder.creator;
        this.issueName = builder.issueName;
        this.from = builder.from;
        this.size = builder.size;
        this.count = builder.count;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public IssueName getIssueName()
    {
        return issueName;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PrincipalKey creator;

        private IssueName issueName;

        private int from = 0;

        private int size = DEFAULT_FETCH_SIZE;

        private boolean count = false;

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder issueName( final IssueName issueName )
        {
            this.issueName = issueName;
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder count( final boolean value )
        {
            this.count = value;
            return this;
        }


        public IssueCommentQuery build()
        {
            return new IssueCommentQuery( this );
        }
    }

}
