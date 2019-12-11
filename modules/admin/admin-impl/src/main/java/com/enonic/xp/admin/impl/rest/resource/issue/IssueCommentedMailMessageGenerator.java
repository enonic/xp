package com.enonic.xp.admin.impl.rest.resource.issue;

import java.util.List;

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
        final List<String> emails = getApproverEmails();
        filterEmail( emails, modifierEmail );
        if ( !creatorEmail.equals( modifierEmail ) )
        {
            emails.add( creatorEmail );
        }

        return String.join( ",", emails );
    }
}
