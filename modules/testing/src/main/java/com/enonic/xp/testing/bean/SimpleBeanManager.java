package com.enonic.xp.testing.bean;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.bean.BeanManager;
import com.enonic.xp.module.ModuleKey;

public final class SimpleBeanManager
    implements BeanManager
{
    private final Map<String, Object> beans;

    public SimpleBeanManager()
    {
        this.beans = Maps.newHashMap();
    }

    private String createKey( final ModuleKey module, final String name )
    {
        return module.toString() + ":" + name;
    }

    public void register( final ModuleKey module, final String name, final Object bean )
    {
        this.beans.put( createKey( module, name ), bean );
    }

    @Override
    public Object getBean( final ModuleKey module, final String name )
    {
        final Object value = this.beans.get( createKey( module, name ) );
        if ( value != null )
        {
            return value;
        }

        throw new IllegalArgumentException( String.format( "Could not find bean [%s] in [%s]", name, module.toString() ) );
    }
}
