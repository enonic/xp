package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.SchemaConverter;
import com.enonic.xp.lib.schema.mapper.SchemaMapper;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListSchemasHandler
    implements ScriptBean
{
    private String application;

    private String type;

    private Supplier<SchemaService> schemaServiceSupplier;

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
        final ApplicationKey applicationKey = ApplicationKey.from( application );

        final List<? extends SchemaResult<? extends BaseSchema<?>>> results = switch ( type )
        {
            case "CONTENT_TYPE" -> schemaServiceSupplier.get().listContentTypes( applicationKey );
            case "FORM_FRAGMENT" -> schemaServiceSupplier.get().listFormFragments( applicationKey );
            case "MIXIN" -> schemaServiceSupplier.get().listMixins( applicationKey );
            default -> throw new IllegalArgumentException( "illegal schema type: " + type );
        };

        return results.stream().map( SchemaConverter::convert ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
