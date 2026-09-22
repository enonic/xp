package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DeleteDynamicComponentParams;
import com.enonic.xp.resource.DeleteDynamicContentSchemaParams;
import com.enonic.xp.resource.DynamicSchemaService;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class DeleteDynamicIconHandler
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

    public boolean execute()
    {
        final DynamicSchemaService service = dynamicSchemaServiceSupplier.get();
        final DynamicIconType iconType = DynamicIconType.from( type );

        return switch ( iconType )
        {
            case CONTENT_TYPE -> service.deleteContentSchemaIcon( contentSchemaParams( iconType, ContentTypeName.from( name ) ) );
            case FORM_FRAGMENT -> service.deleteContentSchemaIcon( contentSchemaParams( iconType, FormFragmentName.from( name ) ) );
            case MIXIN -> service.deleteContentSchemaIcon( contentSchemaParams( iconType, MixinName.from( name ) ) );
            case PART -> service.deleteComponentIcon( DeleteDynamicComponentParams.create()
                                                          .descriptorKey( DescriptorKey.from( name ) )
                                                          .type( iconType.componentType() )
                                                          .build() );
            case MACRO -> service.deleteMacroIcon( MacroKey.from( name ) );
        };
    }

    private static DeleteDynamicContentSchemaParams contentSchemaParams( final DynamicIconType iconType, final BaseSchemaName schemaName )
    {
        return DeleteDynamicContentSchemaParams.create().name( schemaName ).type( iconType.contentSchemaType() ).build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
