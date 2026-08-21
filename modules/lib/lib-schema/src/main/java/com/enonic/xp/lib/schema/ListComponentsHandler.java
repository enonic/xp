package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.lib.schema.mapper.DescriptorMapper;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListComponentsHandler
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

    public List<DescriptorMapper> execute()
    {
        final ApplicationKey applicationKey = ApplicationKey.from( application );

        final List<? extends SchemaResult<? extends ComponentDescriptor>> results = switch ( type )
        {
            case "PART" -> schemaServiceSupplier.get().listParts( applicationKey );
            case "LAYOUT" -> schemaServiceSupplier.get().listLayouts( applicationKey );
            case "PAGE" -> schemaServiceSupplier.get().listPages( applicationKey );
            default -> throw new IllegalArgumentException( "unknown component type: " + type );
        };

        return results.stream().map( DescriptorConverter::convert ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
