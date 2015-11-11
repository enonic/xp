package com.enonic.xp.testing.script;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.bean.BeanContext;

public abstract class ScriptBeanTestSupport
    extends AbstractScriptTest
{
    protected final BeanContext newBeanContext( final ResourceKey resourceKey )
    {
        return new BeanContext()
        {
            @Override
            public ApplicationKey getApplicationKey()
            {
                return resourceKey.getApplicationKey();
            }

            @Override
            public ResourceKey getResourceKey()
            {
                return resourceKey;
            }

            @Override
            public <T> Supplier<T> getBinding( final Class<T> type )
            {
                return () -> null;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> Supplier<T> getService( final Class<T> type )
            {
                return () -> (T) services.get( type );
            }
        };
    }
}
