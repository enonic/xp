package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.CmsDescriptorMapper;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.CmsDescriptor;

public final class GetCmsHandler
    implements ScriptBean
{
    private String application;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public CmsDescriptorMapper execute()
    {
        final SchemaResult<CmsDescriptor> result =
            schemaServiceSupplier.get().getCmsDescriptor( ApplicationKey.from( this.application ) );
        return result != null ? new CmsDescriptorMapper( result.getSchema(), result.getResource() ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
