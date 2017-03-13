package com.enonic.xp.core.impl.issue;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueService;
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
