package com.enonic.xp.admin.impl.rest.resource.issue;

public class IssueCreatedMailMessageGenerator
    extends IssueMailMessageGenerator<IssueMailMessageParams>
{
    public IssueCreatedMailMessageGenerator( final IssueMailMessageParams params )
    {
        super( params );
    }

    @Override
    protected String generateMessageSubject()
    {
        return String.format( "You were assigned a new issue \"%s\" (#%d) by %s", params.getIssue().getTitle(),
                              params.getIssue().getIndex(), params.getCreator().getDisplayName() );
    }

    @Override
    protected String getSender() {
        return super.getCreatorEmail();
    }

    @Override
    protected String generateRecipients()
    {
        return super.getApproverEmails();
    }

    @Override
    protected String getCopyRecepients() {
        return "";
    }
}
