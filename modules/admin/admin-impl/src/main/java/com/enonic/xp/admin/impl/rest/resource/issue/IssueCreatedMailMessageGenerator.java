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
        final String message = params.getLocaleMessageResolver().localizeMessage( "issue.email.created", "%s assigned you a new %s" );
        return String.format( message, params.getCreator().getDisplayName(), getIssueTypeText() );
    }

    @Override
    protected String getSender()
    {
        return super.getCreatorEmail();
    }


    @Override
    protected String generateRecipients()
    {
        return getApproverEmails( params.getCreator().getEmail() );
    }
}
