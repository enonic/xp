package com.enonic.xp.lib.grid;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

public class MemoryGridHandler
    implements ScriptBean
{
    private Supplier<SharedMapService> sharedMapService;

    @Override
    public void initialize( final BeanContext context )
    {
        this.sharedMapService = context.getService( SharedMapService.class );
    }

    public <K, V> SharedMap<K, V> getMap( String mapId )
    {
        return sharedMapService.get().getSharedMap( mapId );
    }
}
