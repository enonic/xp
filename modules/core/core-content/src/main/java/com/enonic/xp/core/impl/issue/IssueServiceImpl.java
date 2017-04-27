package com.enonic.xp.core.impl.issue;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueService;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.node.NodeService;

@Component(immediate = true)
public class IssueServiceImpl
    implements IssueService
{

    private NodeService nodeService;

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
    public List<Issue> findIssues( final IssueQuery query )
    {
        return FindIssuesCommand.create().
            query( query ).
            nodeService( nodeService ).
            build().
            execute();
    }

    @Override
    public Long countIssues( final IssueQuery query )
    {
        return CountIssuesCommand.create().
            nodeService( nodeService ).
            query( query ).
            build().
            execute();
    }

    @SuppressWarnings("unused")
    @Activate
    public void initialize()
    {
        new IssueInitializer( this.nodeService ).initialize();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
