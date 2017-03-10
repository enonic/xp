package com.enonic.xp.core.impl.issue;

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

    private IssueNodeTranslator translator;

    @Override
    public Issue create( CreateIssueParams params )
    {
        return CreateIssueCommand.create().
            params( params ).
            nodeService( this.nodeService ).
            translator( this.translator ).
            build().
            execute();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setTranslator( final IssueNodeTranslator translator )
    {
        this.translator = translator;
    }
}
