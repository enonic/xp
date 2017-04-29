package com.enonic.xp.issue;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class IssueQuery
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final PrincipalKey creator;

    private final PrincipalKeys approvers;

    private final IssueStatus status;

    private final int from;

    private final int size;

    private final boolean count;

    private IssueQuery( final Builder builder )
    {
        this.creator = builder.creator;
        this.approvers = builder.approvers;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PrincipalKey creator;

        private PrincipalKeys approvers;

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


        public IssueQuery build()
        {
            return new IssueQuery( this );
        }
    }

}
