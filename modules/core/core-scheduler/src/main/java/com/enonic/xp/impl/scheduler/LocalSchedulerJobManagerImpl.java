package com.enonic.xp.impl.scheduler;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.node.NodeService;

@Component(immediate = true, service = SchedulerJobManager.class)
@Local
public final class LocalSchedulerJobManagerImpl
    extends BaseSchedulerJobManager
{
    @Activate
    public LocalSchedulerJobManagerImpl( @Reference final NodeService nodeService )
    {
        super( nodeService );
    }
}
