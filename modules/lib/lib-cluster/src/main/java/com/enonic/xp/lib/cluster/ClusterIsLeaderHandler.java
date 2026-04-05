package com.enonic.xp.lib.cluster;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ClusterIsLeaderHandler
    implements ScriptBean
{
    private Supplier<ClusterService> clusterService;

    private ApplicationKey applicationKey;

    public boolean isLeader()
    {
        return this.clusterService.get().isLeader( this.applicationKey );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.clusterService = context.getService( ClusterService.class );
        this.applicationKey = context.getApplicationKey();
    }
}
