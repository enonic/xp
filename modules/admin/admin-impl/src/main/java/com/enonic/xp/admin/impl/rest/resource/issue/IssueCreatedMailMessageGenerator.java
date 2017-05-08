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
        return "You were assigned a new issue \"" + params.getIssue().getTitle() + "\"";
    }

    @Override
    protected String getSender() {
        return super.getCreatorEmail();
    }

    @Override
    protected String generateRecipients()
    {
        return super.getApproverEmails();
    }

    @Override
    protected String getCopyRecepients() {
        return "";
    }
}
