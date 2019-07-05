package com.enonic.xp.issue;

import com.enonic.xp.security.PrincipalKeys;

public class EditableIssue
{
    public final Issue source;

    public String title;

    public IssueName name;

    public String description;

    public IssueStatus issueStatus;

    public PrincipalKeys approverIds;

    public PublishRequest publishRequest;

    public EditableIssue( final Issue source )
    {
        this.source = source;
        this.title = source.getTitle();
        this.name = source.getName();
        this.description = source.getDescription();
        this.issueStatus = source.getStatus();
        this.approverIds = source.getApproverIds();
        this.publishRequest = source.getPublishRequest();
    }

    public Issue.Builder builder()
    {
        return Issue.create().
            id( source.getId() ).
            index( source.getIndex() ).
            name( name ).
            title( title ).
            description( description ).
            status( issueStatus ).
            modifiedTime( source.getModifiedTime() ).
            modifier( source.getModifier() ).
            createdTime( source.getCreatedTime() ).
            creator( source.getCreator() ).
            addApproverIds( approverIds ).
            setPublishRequest( publishRequest );
    }
}
