package com.enonic.wem.api.content.page.layout;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ResourcePath;

public final class LayoutDescriptorKey
    extends DescriptorKey
{
    private LayoutDescriptorKey( final ModuleKey moduleKey, final ResourcePath path )
    {
        super( moduleKey, path );
    }

    public static LayoutDescriptorKey from( final ModuleKey moduleKey, final ResourcePath path )
    {
        return new LayoutDescriptorKey( moduleKey, path );
    }

    public static LayoutDescriptorKey from( final String layoutDescriptorKey )
    {
        final String moduleKey = StringUtils.substringBefore( layoutDescriptorKey, SEPARATOR );
        final String resourcePath = StringUtils.substringAfter( layoutDescriptorKey, SEPARATOR );
        return new LayoutDescriptorKey( ModuleKey.from( moduleKey ), ResourcePath.from( resourcePath ) );
    }
}
