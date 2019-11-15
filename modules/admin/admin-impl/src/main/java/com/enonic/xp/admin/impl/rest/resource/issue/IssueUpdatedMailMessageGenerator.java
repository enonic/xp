package com.enonic.xp.admin.impl.rest.resource.issue;

import com.enonic.xp.issue.IssueStatus;

public class IssueUpdatedMailMessageGenerator
    extends IssueMailMessageGenerator<IssueUpdatedNotificationParams>
{
    public IssueUpdatedMailMessageGenerator( final IssueUpdatedNotificationParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return String.format( "Re: %s (#%d)", params.getIssue().getTitle(), params.getIssue().getIndex() );
    }

    @Override
    protected String generateMessageTitle()
    {
        return String.format( "%s %s the issue", params.getCreator().getDisplayName(),
                              params.getIssue().getStatus() == IssueStatus.CLOSED ? "closed" : "reopened" );
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

}
