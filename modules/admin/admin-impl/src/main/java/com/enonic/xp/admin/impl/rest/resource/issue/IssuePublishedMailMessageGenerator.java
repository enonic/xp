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
        return String.format( "Issue \"%s\" (#%d) was published by %s", params.getIssue().getTitle(), params.getIssue().getIndex(),
                              params.getPublisher().getDisplayName() );
    }

    @Override
    protected String getSender() {
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
