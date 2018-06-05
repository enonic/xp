package com.enonic.xp.lib.cluster;

import java.util.function.Supplier;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ClusterIsMasterHandler
    implements ScriptBean
{
    private Supplier<IndexService> indexService;

    public boolean isMaster()
    {
        return this.indexService.get().isMaster();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.indexService = context.getService( IndexService.class );
    }
}
