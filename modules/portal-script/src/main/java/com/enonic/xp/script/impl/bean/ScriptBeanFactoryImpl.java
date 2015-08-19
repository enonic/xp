package com.enonic.xp.script.impl.bean;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ScriptBeanFactoryImpl
    implements ScriptBeanFactory
{
    private final ClassLoader classLoader;

    private final BeanContext beanContext;

    public ScriptBeanFactoryImpl( final ClassLoader classLoader, final BeanContext beanContext )
    {
        this.classLoader = classLoader;
        this.beanContext = beanContext;
    }

    @Override
    public Object newBean( final String type )
        throws Exception
    {
        final Class<?> clz = Class.forName( type, true, this.classLoader );
        final Object instance = clz.newInstance();

        injectBean( instance );
        return instance;
    }

    private void injectBean( final Object instance )
    {
        if ( instance instanceof ScriptBean )
        {
            ( (ScriptBean) instance ).initialize( this.beanContext );
        }
    }
}
