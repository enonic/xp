package com.enonic.xp.issue;

public final class UpdateIssueParams
{
    private final IssueId id;

    private final IssueEditor editor;

    private UpdateIssueParams( final Builder builder )
    {
        this.id = builder.id;
        this.editor = builder.editor;
    }

    public IssueId getId()
    {
        return id;
    }

    public IssueEditor getEditor()
    {
        return editor;
    }

    public static Builder create()
    {
        return new UpdateIssueParams.Builder();
    }

    public static final class Builder
    {
        private IssueId id;

        private IssueEditor editor;

        private Builder()
        {
        }

        public Builder id( final IssueId id )
        {
            this.id = id;
            return this;
        }

        public Builder editor( final IssueEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public UpdateIssueParams build()
        {
            return new UpdateIssueParams( this );
        }
    }
}
