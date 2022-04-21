package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.SiteDescriptorMapper;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.SiteDescriptor;

public final class GetDynamicSiteHandler
    implements ScriptBean
{
    private String key;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public SiteDescriptorMapper execute()
    {
        final DynamicSchemaResult<SiteDescriptor> result = dynamicSchemaServiceSupplier.get().getSite( ApplicationKey.from( this.key ) );
        return result != null ? new SiteDescriptorMapper( result.getSchema(), result.getResource().getKey().getApplicationKey(),
                                                          result.getResource().readString() ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
