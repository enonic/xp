package com.enonic.wem.api.content.page;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;

public final class PageDescriptorKey
    extends DescriptorKey
{
    private PageDescriptorKey( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        super( moduleKey, descriptorName, DescriptorType.PAGE );
    }

    public static PageDescriptorKey from( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        return new PageDescriptorKey( moduleKey, descriptorName );
    }

    public static PageDescriptorKey from( final ModuleKey moduleKey, final String descriptorName )
    {
        return new PageDescriptorKey( moduleKey, new ComponentDescriptorName( descriptorName ) );
    }

    public static PageDescriptorKey from( final String pageDescriptorKey )
    {
        final String moduleKey = StringUtils.substringBefore( pageDescriptorKey, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( pageDescriptorKey, SEPARATOR );
        return new PageDescriptorKey( ModuleKey.from( moduleKey ), new ComponentDescriptorName( descriptorName ) );
    }

    @Override
    public ResourceKey toResourceKey()
    {
        return ResourceKey.from( getModuleKey(), "page/" + getName().toString() + "/page.xml" );
    }
}
