package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.lib.schema.mapper.MacroDescriptorMapper;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.CreateOrUpdateMacroParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class CreateOrUpdateMacroHandler
    implements ScriptBean
{
    private String key;

    private String resource;

    private ByteSource iconData;

    private String iconMimeType;

    private boolean removeIcon;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setKey( final String key )
    {
        this.key = key;
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

    public MacroDescriptorMapper execute()
    {
        final CreateOrUpdateMacroParams params = CreateOrUpdateMacroParams.create()
            .key( MacroKey.from( key ) )
            .resource( resource )
            .icon( IconUpdates.from( iconData, iconMimeType, removeIcon ) )
            .build();
        return new MacroDescriptorMapper( dynamicSchemaServiceSupplier.get().createOrUpdateMacro( params ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
