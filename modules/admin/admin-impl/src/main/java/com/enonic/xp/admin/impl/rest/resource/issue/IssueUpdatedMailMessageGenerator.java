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
        return String.format( "Issue \"%s\" (#%d) was updated by %s", params.getIssue().getTitle(), params.getIssue().getIndex(),
                              params.getModifier().getDisplayName() );
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
