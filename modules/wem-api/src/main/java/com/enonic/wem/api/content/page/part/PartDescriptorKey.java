package com.enonic.wem.api.content.page.part;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ResourcePath;

public final class PartDescriptorKey
    extends DescriptorKey
{
    private PartDescriptorKey( final ModuleKey moduleKey, final ResourcePath path )
    {
        super( moduleKey, path );
    }

    public static PartDescriptorKey from( final ModuleKey moduleKey, final ResourcePath path )
    {
        return new PartDescriptorKey( moduleKey, path );
    }

    public static PartDescriptorKey from( final String partDescriptorKey )
    {
        final String moduleKey = StringUtils.substringBefore( partDescriptorKey, SEPARATOR );
        final String resourcePath = StringUtils.substringAfter( partDescriptorKey, SEPARATOR );
        return new PartDescriptorKey( ModuleKey.from( moduleKey ), ResourcePath.from( resourcePath ) );
    }
}
