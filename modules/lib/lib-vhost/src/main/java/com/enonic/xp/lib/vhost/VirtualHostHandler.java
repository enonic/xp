package com.enonic.xp.lib.vhost;

import java.util.function.Supplier;

import com.enonic.xp.lib.vhost.mapper.VirtualHostsMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.web.vhost.VirtualHostService;

public class VirtualHostHandler
    implements ScriptBean
{

    private Supplier<VirtualHostService> virtualHostServiceSupplier;

    @Override
    public void initialize( final BeanContext context )
    {
        this.virtualHostServiceSupplier = context.getService( VirtualHostService.class );
    }

    public boolean isEnabled()
    {
        return virtualHostServiceSupplier.get().isEnabled();
    }

    public Object getVirtualHosts()
    {
        return new VirtualHostsMapper( virtualHostServiceSupplier.get().getVirtualHosts() );
    }

}
