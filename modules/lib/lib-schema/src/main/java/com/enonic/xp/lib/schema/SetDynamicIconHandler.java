package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.lib.schema.mapper.IconMapper;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.SetDynamicComponentIconParams;
import com.enonic.xp.resource.SetDynamicContentSchemaIconParams;
import com.enonic.xp.resource.SetDynamicMacroIconParams;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SetDynamicIconHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private ByteSource data;

    private String mimeType;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setData( final ByteSource data )
    {
        this.data = data;
    }

    public void setMimeType( final String mimeType )
    {
        this.mimeType = mimeType;
    }

    public IconMapper execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final DynamicIconType iconType = DynamicIconType.from( type );

        final Icon icon = switch ( iconType )
        {
            case CONTENT_TYPE -> service.setContentSchemaIcon( contentSchemaParams( iconType, ContentTypeName.from( name ) ) );
            case FORM_FRAGMENT -> service.setContentSchemaIcon( contentSchemaParams( iconType, FormFragmentName.from( name ) ) );
            case MIXIN -> service.setContentSchemaIcon( contentSchemaParams( iconType, MixinName.from( name ) ) );
            case PART -> service.setComponentIcon( SetDynamicComponentIconParams.create()
                                                       .descriptorKey( DescriptorKey.from( name ) )
                                                       .type( iconType.componentType() )
                                                       .data( data )
                                                       .mimeType( mimeType )
                                                       .build() );
            case MACRO -> service.setMacroIcon(
                SetDynamicMacroIconParams.create().key( MacroKey.from( name ) ).data( data ).mimeType( mimeType ).build() );
        };

        return new IconMapper( icon );
    }

    private SetDynamicContentSchemaIconParams contentSchemaParams( final DynamicIconType iconType, final BaseSchemaName schemaName )
    {
        return SetDynamicContentSchemaIconParams.create()
            .name( schemaName )
            .type( iconType.contentSchemaType() )
            .data( data )
            .mimeType( mimeType )
            .build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
