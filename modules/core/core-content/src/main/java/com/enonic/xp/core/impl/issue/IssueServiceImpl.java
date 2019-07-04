package com.enonic.xp.core.impl.issue;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.DeleteIssueCommentParams;
import com.enonic.xp.issue.DeleteIssueCommentResult;
import com.enonic.xp.issue.FindIssueCommentsResult;
import com.enonic.xp.issue.FindIssuesResult;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.node.NodeService;

@Component(immediate = true)
public class IssueServiceImpl
    implements IssueService
{
    private NodeService nodeService;

    private IndexService indexService;

    @SuppressWarnings("unused")
    // Just needed for now to ensure that the content-service is initialized first, since we need the content-repo initialized
    private ContentService contentService;

    @SuppressWarnings("unused")
    @Activate
    public void initialize()
    {
        IssueInitializer.create().
            setIndexService( indexService ).
            setNodeService( nodeService ).
            build().
            initialize();
    }

    @Override
    public Issue create( CreateIssueParams params )
    {
        return CreateIssueCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    @Override
    public Issue update( final UpdateIssueParams params )
    {
        return UpdateIssueCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    @Override
    public Issue getIssue( final IssueId id )
    {
        return GetIssueByIdCommand.create().
            issueId( id ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    @Override
    public FindIssuesResult findIssues( final IssueQuery query )
    {
        return FindIssuesCommand.create().
            query( query ).
            nodeService( nodeService ).
            build().
            execute();
    }

    public IssueComment createComment( final CreateIssueCommentParams params )
    {
        return CreateIssueCommentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    public FindIssueCommentsResult findComments( final IssueCommentQuery query )
    {
        return FindIssueCommentsCommand.create().
            query( query ).
            nodeService( nodeService ).
            build().
            execute();
    }

    public DeleteIssueCommentResult deleteComment( DeleteIssueCommentParams params )
    {
        return DeleteIssueCommentCommand.create().
            params( params ).
            nodeService( nodeService ).
            build().
            execute();
    }

    public IssueComment updateComment( final UpdateIssueCommentParams params )
    {
        return UpdateIssueCommentCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }

    @SuppressWarnings("unused")
    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
