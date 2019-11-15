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
        return String.format( "Re: %s (#%d)", params.getIssue().getTitle(), params.getIssue().getIndex() );
    }

    @Override
    protected String generateMessageTitle()
    {
        return "A new comment is posted";
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

    @Override
    protected boolean shouldShowComments()
    {
        return params.getComments().size() > 0;
    }

}
