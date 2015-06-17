package com.enonic.xp.portal.impl.script;

import com.enonic.xp.bean.BeanManager;
import com.enonic.xp.portal.impl.script.invoker.CommandInvoker;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;

public interface ScriptExecutor
{
    Object executeRequire( ResourceKey script );

    ScriptValue newScriptValue( Object value );

    CommandInvoker getInvoker();

    BeanManager getBeanManager();
}
