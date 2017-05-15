package com.enonic.xp.admin.impl.json.issue;

import java.time.Instant;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.json.PublishRequestJson;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.security.PrincipalKey;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class IssueJson
{
    private final Issue issue;

    public IssueJson( Issue issue )
    {
        this.issue = issue;
    }

    public String getId()
    {
        return issue.getId().toString();
    }

    public long getIndex()
    {
        return issue.getIndex();
    }

    public String getTitle()
    {
        return this.issue.getTitle();
    }

    public String getName()
    {
        return this.issue.getName().toString();
    }

    public String getDescription()
    {
        return this.issue.getDescription();
    }

    public Instant getCreatedTime()
    {
        return this.issue.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return this.issue.getModifiedTime();
    }

    public String getIssueStatus()
    {
        return this.issue.getStatus().toString();
    }

    public String getCreator()
    {
        return this.issue.getCreator() != null ? this.issue.getCreator().toString() : null;

    }

    public String getModifier()
    {
        return this.issue.getModifier() != null ? this.issue.getModifier().toString() : null;
    }

    public List<String> getApproverIds()
    {
        return this.issue.getApproverIds().stream().
            map( PrincipalKey::toString ).
            collect( toList() );
    }

    public PublishRequestJson getPublishRequest()
    {
        return this.issue.getPublishRequest() != null ? PublishRequestJson.from( this.issue.getPublishRequest() ) : null;
    }
}
