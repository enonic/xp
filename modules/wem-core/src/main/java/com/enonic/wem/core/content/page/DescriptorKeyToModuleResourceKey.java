package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public final class DescriptorKeyToModuleResourceKey
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private static final ResourcePath PAGE_FOLDER = ResourcePath.from( "page" );

    public static ModuleResourceKey translate( final DescriptorKey descriptorKey )
    {
        final ResourcePath parentFolder;
        final String descriptorType;
        switch ( descriptorKey.getDescriptorType() )
        {
            case IMAGE:
                descriptorType = "image";
                parentFolder = COMPONENT_FOLDER;
                break;
            case LAYOUT:
                descriptorType = "layout";
                parentFolder = COMPONENT_FOLDER;
                break;
            case PAGE:
                descriptorType = "page";
                parentFolder = PAGE_FOLDER;
                break;
            case PART:
                descriptorType = "part";
                parentFolder = COMPONENT_FOLDER;
                break;
            default:
                throw new IllegalArgumentException( "Unsupported DescriptorType: " + descriptorKey.getDescriptorType() );
        }
        final ResourcePath path = parentFolder.resolve( descriptorKey.getName().toString() ).resolve( descriptorType + ".xml" );
        return new ModuleResourceKey( descriptorKey.getModuleKey(), path );
    }
}
