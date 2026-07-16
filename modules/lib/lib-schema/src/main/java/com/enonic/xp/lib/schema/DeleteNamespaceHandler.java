package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteNamespaceHandler
    implements ScriptBean
{
    private String key;

    private Supplier<ApplicationService> applicationServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public Boolean execute()
    {
        return applicationServiceSupplier.get().deleteNamespace( ApplicationKey.from( key ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
    }
}