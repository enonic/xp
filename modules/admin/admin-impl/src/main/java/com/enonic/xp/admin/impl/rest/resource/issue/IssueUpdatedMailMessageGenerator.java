package com.enonic.xp.admin.impl.rest.resource.issue;

public class IssueUpdatedMailMessageGenerator
    extends IssueMailMessageGenerator<IssueUpdatedMailMessageParams>
{
    public IssueUpdatedMailMessageGenerator( final IssueUpdatedMailMessageParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return "Issue \"" + params.getIssue().getTitle() + "\" was updated by " + params.getModifier().getDisplayName();
    }

    @Override
    protected String getSender()
    {
        return params.getModifier().getEmail();
    }

    @Override
    protected String generateRecipients()
    {
        return super.getCreatorEmail();
    }

    @Override
    protected String getCopyRecepients()
    {
        return super.getApproverEmails();
    }

    @Override
    protected boolean isStatusShown()
    {
        return true;
    }
}
