package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.CmsDescriptorMapper;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.CmsDescriptor;

public final class GetDynamicSiteHandler
    implements ScriptBean
{
    private String application;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public CmsDescriptorMapper execute()
    {
        final DynamicSchemaResult<CmsDescriptor> result =
            dynamicSchemaServiceSupplier.get().getCmsDescriptor( ApplicationKey.from( this.application ) );
        return result != null ? new CmsDescriptorMapper( result.getSchema(), result.getResource() ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
