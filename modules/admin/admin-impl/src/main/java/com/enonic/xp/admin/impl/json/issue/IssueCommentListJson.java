package com.enonic.xp.admin.impl.json.issue;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.issue.IssueListMetaData;
import com.enonic.xp.issue.IssueComment;

public class IssueCommentListJson
{
    private final List<IssueCommentJson> issueComments;

    private final IssueListMetaDataJson metadata;

    public IssueCommentListJson( final List<IssueComment> issueCommments, final IssueListMetaData metadata )
    {
        this.issueComments = issueCommments.stream().map( IssueCommentJson::new ).collect( Collectors.toList() );
        this.metadata = new IssueListMetaDataJson( metadata );
    }

    public List<IssueCommentJson> getIssueComments()
    {
        return issueComments;
    }

    public IssueListMetaDataJson getMetadata()
    {
        return metadata;
    }
}
