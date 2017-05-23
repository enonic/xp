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
        return "Issue \"" + params.getIssue().getTitle() + "\" was published by " +
            params.getPublisher().getDisplayName();
    }

    @Override
    protected String getSender() {
        return params.getPublisher().getEmail();
    }

    @Override
    protected String generateRecipients()
    {
        return super.getCreatorEmail();
    }

    @Override
    protected String getCopyRecepients() {
        return super.getApproverEmails();
    }

    @Override
    protected boolean isStatusShown()
    {
        return true;
    }
}
