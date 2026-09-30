package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.lib.schema.mapper.SchemaMapper;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListDynamicSchemasHandler
    implements ScriptBean
{
    private String application;

    private String type;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setApplication( final String application )
    {
        this.application = application;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public List<SchemaMapper> execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final ApplicationKey applicationKey = ApplicationKey.from( application );

        final List<? extends DynamicSchemaResult<? extends BaseSchema<?>>> results = switch ( type )
        {
            case "CONTENT_TYPE" -> service.listContentTypes( applicationKey );
            case "FORM_FRAGMENT" -> service.listFormFragments( applicationKey );
            case "MIXIN" -> service.listMixins( applicationKey );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return results.stream().map( SchemaConverter::convert ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
