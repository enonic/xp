package com.enonic.xp.script.impl.bean;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

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
