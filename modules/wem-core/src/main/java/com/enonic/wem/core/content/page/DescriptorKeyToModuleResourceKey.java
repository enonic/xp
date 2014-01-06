package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public final class DescriptorKeyToModuleResourceKey
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );


    public static ModuleResourceKey translate( final DescriptorKey descriptorKey )
    {
        final String descriptorType;
        switch ( descriptorKey.getDescriptorType() )
        {
            case IMAGE:
                descriptorType = "image";
                break;
            case LAYOUT:
                descriptorType = "layout";
                break;
            case PAGE:
                descriptorType = "page";
                break;
            case PART:
                descriptorType = "part";
                break;
            default:
                throw new IllegalArgumentException( "Unsupported DescriptorType: " + descriptorKey.getDescriptorType() );
        }
        final ResourcePath path = COMPONENT_FOLDER.resolve( descriptorKey.getName().toString() ).resolve( descriptorType + ".xml" );
        return new ModuleResourceKey( descriptorKey.getModuleKey(), path );
    }
}
