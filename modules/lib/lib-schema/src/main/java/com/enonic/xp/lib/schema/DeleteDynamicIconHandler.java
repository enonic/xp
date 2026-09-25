package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.resource.DynamicSchemaService;
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

        return switch ( DynamicIconType.from( type ) )
        {
            case CONTENT_TYPE -> service.deleteContentTypeIcon( ContentTypeName.from( name ) );
            case FORM_FRAGMENT -> service.deleteFormFragmentIcon( FormFragmentName.from( name ) );
            case MIXIN -> service.deleteMixinIcon( MixinName.from( name ) );
            case PART -> service.deletePartIcon( DescriptorKey.from( name ) );
            case MACRO -> service.deleteMacroIcon( MacroKey.from( name ) );
        };
    }

    @Override
    public void initialize( final BeanContext context )
    {
        dynamicSchemaServiceSupplier = context.getService( DynamicSchemaService.class );
    }
}
