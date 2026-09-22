package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.lib.schema.mapper.IconMapper;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.resource.GetDynamicComponentParams;
import com.enonic.xp.resource.GetDynamicContentSchemaParams;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetDynamicIconHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private Supplier<DynamicSchemaService> dynamicSchemaServiceSupplier;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public IconMapper execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final DynamicIconType iconType = DynamicIconType.from( type );

        final Icon icon = switch ( iconType )
        {
            case CONTENT_TYPE -> service.getContentSchemaIcon( contentSchemaParams( iconType, ContentTypeName.from( name ) ) );
            case FORM_FRAGMENT -> service.getContentSchemaIcon( contentSchemaParams( iconType, FormFragmentName.from( name ) ) );
            case MIXIN -> service.getContentSchemaIcon( contentSchemaParams( iconType, MixinName.from( name ) ) );
            case PART -> service.getComponentIcon( GetDynamicComponentParams.create()
                                                       .descriptorKey( DescriptorKey.from( name ) )
                                                       .type( iconType.componentType() )
                                                       .build() );
            case MACRO -> service.getMacroIcon( MacroKey.from( name ) );
        };

        return icon != null ? new IconMapper( icon ) : null;
    }

    private static GetDynamicContentSchemaParams contentSchemaParams( final DynamicIconType iconType, final BaseSchemaName schemaName )
    {
        return GetDynamicContentSchemaParams.create().name( schemaName ).type( iconType.contentSchemaType() ).build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
