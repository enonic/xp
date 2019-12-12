package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Set;

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
            params.getLocaleMessageResolver().localizeMessage( "issue.email.published", "%s published and closed the %s" );
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
        final String creatorEmail = super.getCreatorEmail();
        final String publisherEmail = params.getPublisher().getEmail();
        final Set<String> emails = getApproverEmails();
        emails.add( creatorEmail );
        filterEmail( emails, publisherEmail );

        return String.join( ",", emails );
    }
}
