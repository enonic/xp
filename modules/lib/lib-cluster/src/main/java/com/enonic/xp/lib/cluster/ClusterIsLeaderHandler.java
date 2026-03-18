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

    private String applicationKey;

    public void setApplicationKey( final String applicationKey )
    {
        this.applicationKey = applicationKey;
    }

    public boolean isLeader()
    {
        final ClusterService service = this.clusterService.get();
        if ( service == null )
        {
            return true;
        }
        if ( this.applicationKey == null )
        {
            return service.isLeader();
        }
        return service.isLeader( ApplicationKey.from( this.applicationKey ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.clusterService = context.getService( ClusterService.class );
    }
}
