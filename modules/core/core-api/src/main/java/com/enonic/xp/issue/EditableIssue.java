package com.enonic.xp.issue;

import java.time.Instant;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class EditableIssue
{

    public final Issue source;

    public IssueId id;

    public String title;

    public IssueName name;

    public IssuePath issuePath;

    public String description;

    public Instant modifiedTime;

    public PrincipalKey modifier;

    public IssueStatus issueStatus;

    private PrincipalKey creator;

    private Instant createdTime;

    public PrincipalKeys approverIds;

    public ContentIds itemIds;

    public EditableIssue( final Issue source )
    {
        this.source = source;
        this.id = source.getId();
        this.title = source.getTitle();
        this.name = source.getName();
        this.issuePath = source.getPath();
        this.description = source.getDescription();
        this.modifiedTime = source.getModifiedTime();
        this.modifier = source.getModifier();
        this.issueStatus = source.getStatus();
        this.approverIds = source.getApproverIds();
        this.itemIds = source.getItemIds();
        this.creator = source.getCreator();
        this.createdTime = source.getCreatedTime();
    }

    public Issue build()
    {
        return Issue.create().
            id( id ).
            name( name ).
            title( title ).
            description( description ).
            status( issueStatus ).
            modifiedTime( modifiedTime ).
            modifier( modifier ).
            createdTime( createdTime ).
            creator( creator ).
            addApproverIds( approverIds ).
            addItemIds( itemIds ).
            build();
    }
}
