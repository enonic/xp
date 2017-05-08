package com.enonic.xp.admin.impl.rest.resource.issue;

public class IssueCreatedMailMessageGenerator
    extends IssueMailMessageGenerator<IssueMailMessageParams>
{
    public IssueCreatedMailMessageGenerator( final IssueMailMessageParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return "You were assigned to a new issue \"" + params.getIssue().getTitle() + "\"";
    }
}
