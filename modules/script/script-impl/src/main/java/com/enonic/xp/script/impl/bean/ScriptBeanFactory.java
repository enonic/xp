package com.enonic.xp.script.impl.bean;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ScriptBeanFactory
{
    private final ClassLoader classLoader;

    private final BeanContext beanContext;

    public ScriptBeanFactory( final ClassLoader classLoader, final BeanContext beanContext )
    {
        this.classLoader = classLoader;
        this.beanContext = beanContext;
    }

    public Object newBean( final String type )
        throws Exception
    {
        final Class<?> clz = Class.forName( type, true, this.classLoader );
        final Object instance = clz.getDeclaredConstructor().newInstance();

        if ( instance instanceof ScriptBean )
        {
            ( (ScriptBean) instance ).initialize( this.beanContext );
        }
        return instance;
    }
}
