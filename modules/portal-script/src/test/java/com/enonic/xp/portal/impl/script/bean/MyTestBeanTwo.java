package com.enonic.xp.portal.impl.script.bean;

import com.enonic.xp.portal.bean.BeanContext;
import com.enonic.xp.portal.bean.ScriptBean;

public class MyTestBeanTwo
    implements ScriptBean
{
    private BeanContext context;

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
    }

    public String getStatus()
    {
        return getClass().getSimpleName() + ", " + this.context.getResource();
    }
}
