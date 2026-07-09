package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.StyleDescriptorMapper;
import com.enonic.xp.schema.CreateStylesParams;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.style.StyleDescriptor;

public final class CreateStylesHandler
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
        final CreateStylesParams params =
            CreateStylesParams.create().key( ApplicationKey.from( application ) ).resource( resource ).build();

        final SchemaResult<StyleDescriptor> result = schemaServiceSupplier.get().createStyles( params );

        return new StyleDescriptorMapper( result.getSchema(), result.getResource() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
