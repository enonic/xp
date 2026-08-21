package com.enonic.xp.lib.schema;

import java.util.function.Supplier;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.lib.schema.mapper.IconMapper;
import com.enonic.xp.macro.MacroKey;
import com.enonic.xp.schema.SchemaService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.formfragment.FormFragmentName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class GetIconHandler
    implements ScriptBean
{
    private String name;

    private String type;

    private Supplier<SchemaService> schemaServiceSupplier;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public Object execute()
    {
        final Icon icon = switch ( type )
        {
            case "CONTENT_TYPE" -> schemaServiceSupplier.get().getContentTypeIcon( ContentTypeName.from( name ) );
            case "FORM_FRAGMENT" -> schemaServiceSupplier.get().getFormFragmentIcon( FormFragmentName.from( name ) );
            case "MIXIN" -> schemaServiceSupplier.get().getMixinIcon( MixinName.from( name ) );
            case "PART" -> schemaServiceSupplier.get().getPartIcon( DescriptorKey.from( name ) );
            case "MACRO" -> schemaServiceSupplier.get().getMacroIcon( MacroKey.from( name ) );
            default -> throw new IllegalArgumentException( "icons are not supported for type: " + type );
        };

        return icon != null ? new IconMapper( icon ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        schemaServiceSupplier = context.getService( SchemaService.class );
    }
}
