package com.enonic.wem.api.content.page;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ResourcePath;

public final class PageDescriptorKey
    extends DescriptorKey
{
    private PageDescriptorKey( final ModuleKey moduleKey, final ResourcePath path )
    {
        super( moduleKey, path );
    }

    public static PageDescriptorKey from( final ModuleKey moduleKey, final ResourcePath path )
    {
        return new PageDescriptorKey( moduleKey, path );
    }

    public static PageDescriptorKey from( final String pageDescriptorKey )
    {
        final String moduleKey = StringUtils.substringBefore( pageDescriptorKey, SEPARATOR );
        final String resourcePath = StringUtils.substringAfter( pageDescriptorKey, SEPARATOR );
        return new PageDescriptorKey( ModuleKey.from( moduleKey ), ResourcePath.from( resourcePath ) );
    }
}
