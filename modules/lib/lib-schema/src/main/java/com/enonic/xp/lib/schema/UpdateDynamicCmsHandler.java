package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.CmsDescriptorMapper;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.SchemaService;
import com.enonic.xp.resource.UpdateDynamicCmsParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.CmsDescriptor;

public final class UpdateDynamicCmsHandler
    implements ScriptBean
{
    private String application;

    private String resource;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public Object execute()
    {
        final UpdateDynamicCmsParams params =
            UpdateDynamicCmsParams.create().key( ApplicationKey.from( application ) ).resource( resource ).build();

        final DynamicSchemaResult<CmsDescriptor> result = schemaServiceSupplier.get().updateCms( params );

        return new CmsDescriptorMapper( result.getSchema(), result.getResource() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
