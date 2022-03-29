package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.UpdateDynamicSiteParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class UpdateDynamicSiteHandler
    implements ScriptBean
{
    private String key;

    private String resource;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public Object execute()
    {
        final UpdateDynamicSiteParams params =
            UpdateDynamicSiteParams.create().key( ApplicationKey.from( key ) ).resource( resource ).build();

        return dynamicSchemaServiceSupplier.get().updateSite( params );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
