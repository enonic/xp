package com.enonic.xp.admin.impl.rest.resource.issue;

public class IssuePublishedMailMessageGenerator
    extends IssueMailMessageGenerator<IssuePublishedMailMessageParams>
{
    public IssuePublishedMailMessageGenerator( final IssuePublishedMailMessageParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return "Issue \"" + params.getIssue().getTitle() + "\" (#" + params.getIssue().getId() + ") was published by " +
            params.getPublisher().getDisplayName();
    }
}
