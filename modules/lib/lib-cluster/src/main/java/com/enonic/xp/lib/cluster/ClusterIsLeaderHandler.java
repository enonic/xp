package com.enonic.xp.lib.cluster;

import java.util.function.Supplier;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ClusterIsLeaderHandler
    implements ScriptBean
{
    private Supplier<IndexService> indexService;

    public boolean isLeader()
    {
        return this.indexService.get().isLeader();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.indexService = context.getService( IndexService.class );
    }
}
