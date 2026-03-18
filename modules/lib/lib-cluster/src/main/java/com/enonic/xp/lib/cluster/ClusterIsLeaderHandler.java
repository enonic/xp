package com.enonic.xp.lib.cluster;

import java.util.function.Supplier;

import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ClusterIsLeaderHandler
    implements ScriptBean
{
    private Supplier<ClusterService> clusterService;

    public boolean isLeader()
    {
        return this.clusterService.get().isLeader();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.clusterService = context.getService( ClusterService.class );
    }
}
