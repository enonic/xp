package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.lib.schema.mapper.DescriptorConverter;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.CreateOrUpdateComponentParams;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateOrUpdateComponentHandler
    implements ScriptBean
{
    private String key;

    private String type;

    private String resource;

    private ByteSource iconData;

    private String iconMimeType;

    private boolean removeIcon;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setResource( final String resource )
    {
        this.resource = resource;
    }

    public void setIconData( final ByteSource iconData )
    {
        this.iconData = iconData;
    }

    public void setIconMimeType( final String iconMimeType )
    {
        this.iconMimeType = iconMimeType;
    }

    public void setRemoveIcon( final boolean removeIcon )
    {
        this.removeIcon = removeIcon;
    }

    public Object execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final CreateOrUpdateComponentParams params = CreateOrUpdateComponentParams.create()
            .key( DescriptorKey.from( key ) )
            .resource( resource )
            .icon( IconUpdates.from( iconData, iconMimeType, removeIcon ) )
            .build();

        final DynamicSchemaResult<? extends ComponentDescriptor> result = switch ( type )
        {
            case "PART" -> service.createOrUpdatePart( params );
            case "LAYOUT" -> service.createOrUpdateLayout( params );
            case "PAGE" -> service.createOrUpdatePage( params );
            default -> throw new IllegalArgumentException( "illegal component type: " + type );
        };

        return DescriptorConverter.convert( result );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
