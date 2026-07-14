package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.lib.schema.mapper.NamespaceMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateNamespaceHandler
    implements ScriptBean
{
    private String key;

    private String description;

    private Supplier<ApplicationService> applicationServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public NamespaceMapper execute()
    {
        final CreateNamespaceParams params =
            CreateNamespaceParams.create().key( ApplicationKey.from( key ) ).description( description ).build();

        final Application application = applicationServiceSupplier.get().createNamespace( params );

        return new NamespaceMapper( application.getKey(), description );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        applicationServiceSupplier = context.getService( ApplicationService.class );
    }
}
