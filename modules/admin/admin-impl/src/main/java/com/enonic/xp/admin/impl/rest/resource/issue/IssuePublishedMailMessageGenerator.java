package com.enonic.xp.admin.impl.rest.resource.issue;

public class IssuePublishedMailMessageGenerator
    extends IssueMailMessageGenerator<IssuePublishedNotificationParams>
{
    public IssuePublishedMailMessageGenerator( final IssuePublishedNotificationParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return String.format( "Re: %s (#%d)", params.getIssue().getTitle(), params.getIssue().getIndex() );
    }

    @Override
    protected boolean shouldShowComments()
    {
        return false;
    }

    @Override
    protected String generateMessageTitle()
    {
        final String message =
            params.getLocaleMessageResolver().localizeMessage( "issue.email.published", "%s published and closed the %" );
        return String.format( message, params.getPublisher().getDisplayName(), getIssueTypeText() );
    }

    @Override
    protected String getSender()
    {
        return params.getPublisher().getEmail();
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
