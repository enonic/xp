package com.enonic.xp.issue;

public class UpdateIssueParams
{
    private IssueId id;

    private IssueEditor editor;

    public UpdateIssueParams id( final IssueId id )
    {
        this.id = id;
        return this;
    }

    public UpdateIssueParams editor( final IssueEditor editor )
    {
        this.editor = editor;
        return this;
    }

    public IssueId getId()
    {
        return id;
    }

    public IssueEditor getEditor()
    {
        return editor;
    }
}
