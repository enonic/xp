package com.enonic.xp.admin.impl.rest.resource.issue;

public class IssueCreatedMailMessageGenerator
    extends IssueMailMessageGenerator<IssueNotificationParams>
{
    public IssueCreatedMailMessageGenerator( final IssueNotificationParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return String.format( "%s (#%d)", params.getIssue().getTitle(), params.getIssue().getIndex() );
    }

    @Override
    protected String generateMessageTitle()
    {
        return String.format( "%s assigned you a new issue", params.getCreator().getDisplayName() );
    }

    @Override
    protected String getSender()
    {
        return super.getCreatorEmail();
    }

    @Override
    protected String generateRecipients()
    {
        return super.getApproverEmails();
    }

    @Override
    protected String getCopyRecepients()
    {
        return "";
    }

    @Override
    protected boolean shouldShowComments()
    {
        return params.getComments().size() > 0;
    }
}
