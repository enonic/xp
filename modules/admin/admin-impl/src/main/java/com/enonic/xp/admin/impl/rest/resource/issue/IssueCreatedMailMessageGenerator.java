package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Set;

import com.enonic.xp.issue.Issue;

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
        final Issue issue = params.getIssue();
        final boolean isNew = issue.getModifiedTime() == null || issue.getModifiedTime().equals( issue.getCreatedTime() );

        final Set<String> emails = getApproverEmails();
        final String creatorEmail = this.getCreatorEmail();
        if ( isNew && !creatorEmail.isBlank() )
        {
            filterEmail( emails, creatorEmail );
        }
        return String.join( ",", emails );
    }
}
