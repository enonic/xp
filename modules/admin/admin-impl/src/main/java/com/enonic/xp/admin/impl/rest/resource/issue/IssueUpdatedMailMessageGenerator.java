package com.enonic.xp.admin.impl.rest.resource.issue;

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
        String key, defaultValue;
        if ( isIssueOpen() )
        {
            key = "issue.email.reopened";
            defaultValue = "%s reopened the %s";
        }
        else
        {
            key = "issue.email.closed";
            defaultValue = "%s closed the %s";
        }
        final String message = params.getLocaleMessageResolver().localizeMessage( key, defaultValue );
        return String.format( message, params.getModifier().getDisplayName(), this.getIssueTypeText() );
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
