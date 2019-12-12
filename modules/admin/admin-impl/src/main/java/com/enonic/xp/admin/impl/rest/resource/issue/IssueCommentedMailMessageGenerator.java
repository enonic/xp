package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.Set;

public class IssueCommentedMailMessageGenerator
    extends IssueMailMessageGenerator<IssueCommentedNotificationParams>
{
    public IssueCommentedMailMessageGenerator( final IssueCommentedNotificationParams params )
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
        return params.getLocaleMessageResolver().localizeMessage( "issue.email.commented", "A new comment is posted" );
    }

    @Override
    protected String getSender()
    {
        return params.getModifier().getEmail();
    }

    @Override
    protected String generateRecipients()
    {
        final String creatorEmail = super.getCreatorEmail();
        final String modifierEmail = params.getModifier().getEmail();
        final Set<String> emails = getApproverEmails();
        emails.add( creatorEmail );
        filterEmail( emails, modifierEmail );

        return String.join( ",", emails );
    }
}
