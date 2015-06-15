package com.enonic.xp.bean;

import com.enonic.xp.module.ModuleKey;

public interface BeanManager
{
    Object getBean( ModuleKey module, String name );
}
