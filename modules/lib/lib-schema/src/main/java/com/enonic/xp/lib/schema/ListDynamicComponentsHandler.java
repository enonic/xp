package com.enonic.xp.lib.schema;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.lib.schema.mapper.DescriptorMapper;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class ListDynamicComponentsHandler
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

    public List<DescriptorMapper> execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final ApplicationKey applicationKey = ApplicationKey.from( application );

        final List<? extends DynamicSchemaResult<? extends ComponentDescriptor>> results = switch ( type )
        {
            case "PART" -> service.listParts( applicationKey );
            case "LAYOUT" -> service.listLayouts( applicationKey );
            case "PAGE" -> service.listPages( applicationKey );
            default -> throw new IllegalArgumentException( "illegal component type: " + type );
        };

        return results.stream().map( DescriptorConverter::convert ).collect( Collectors.toList() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
