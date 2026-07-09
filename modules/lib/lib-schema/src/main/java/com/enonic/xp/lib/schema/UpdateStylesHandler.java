package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.StyleDescriptorMapper;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.UpdateStylesParams;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.style.StyleDescriptor;

public final class UpdateStylesHandler
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
        final UpdateStylesParams params =
            UpdateStylesParams.create().key( ApplicationKey.from( application ) ).resource( resource ).build();

        final SchemaResult<StyleDescriptor> result = schemaServiceSupplier.get().updateStyles( params );

        return new StyleDescriptorMapper( result.getSchema(), result.getResource() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
