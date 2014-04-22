package com.enonic.wem.api.content.page.part;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class PartDescriptorKey
    extends DescriptorKey
{
    private PartDescriptorKey( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        super( moduleKey, descriptorName, DescriptorType.PART );
    }

    public static PartDescriptorKey from( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        return new PartDescriptorKey( moduleKey, descriptorName );
    }

    public static PartDescriptorKey from( final String partDescriptorKey )
    {
        final String moduleKey = StringUtils.substringBefore( partDescriptorKey, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( partDescriptorKey, SEPARATOR );
        return new PartDescriptorKey( ModuleKey.from( moduleKey ), new ComponentDescriptorName( descriptorName ) );
    }

    @Override
    public ModuleResourceKey toResourceKey()
    {
        return ModuleResourceKey.from( getModuleKey(), "component/" + getName().toString() + "/part.xml" );
    }
}
