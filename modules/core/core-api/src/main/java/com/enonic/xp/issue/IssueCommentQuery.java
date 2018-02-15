package com.enonic.xp.issue;

import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.security.PrincipalKey;

public final class IssueCommentQuery
{
    private static final int DEFAULT_FETCH_SIZE = 10;

    private static final ChildOrder DEFAULT_ORDER = ChildOrder.defaultOrder();

    private final PrincipalKey creator;

    private final IssueId issue;

    private final int from;

    private final int size;

    private final boolean count;

    private final ChildOrder order;

    private IssueCommentQuery( final Builder builder )
    {
        this.creator = builder.creator;
        this.issue = builder.issue;
        this.from = builder.from;
        this.size = builder.size;
        this.count = builder.count;
        this.order = builder.order;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public IssueId getIssue()
    {
        return issue;
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

    public ChildOrder getOrder()
    {
        return order;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {

        private PrincipalKey creator;

        private IssueId issue;

        private int from = 0;

        private int size = DEFAULT_FETCH_SIZE;

        private boolean count = false;

        private ChildOrder order = DEFAULT_ORDER;

        public Builder creator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder issue( final IssueId issue )
        {
            this.issue = issue;
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

        public Builder order( final ChildOrder order )
        {
            this.order = order;
            return this;
        }

        public IssueCommentQuery build()
        {
            return new IssueCommentQuery( this );
        }
    }

}
