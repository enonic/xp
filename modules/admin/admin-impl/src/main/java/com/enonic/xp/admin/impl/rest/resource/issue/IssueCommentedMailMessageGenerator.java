package com.enonic.xp.admin.impl.rest.resource.issue;

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
        return String.format( "%s posted a new comment to \"%s\" (#%d)", params.getModifier().getDisplayName(),
                              params.getIssue().getTitle(), params.getIssue().getIndex() );
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
