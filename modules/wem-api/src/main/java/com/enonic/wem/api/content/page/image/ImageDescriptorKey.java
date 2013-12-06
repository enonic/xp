package com.enonic.wem.api.content.page.image;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ResourcePath;

public final class ImageDescriptorKey
    extends DescriptorKey
{
    private ImageDescriptorKey( final ModuleKey moduleKey, final ResourcePath path )
    {
        super( moduleKey, path );
    }

    public static ImageDescriptorKey from( final ModuleKey moduleKey, final ResourcePath path )
    {
        return new ImageDescriptorKey( moduleKey, path );
    }

    public static ImageDescriptorKey from( final String imageDescriptorKey )
    {
        final String moduleKey = StringUtils.substringBefore( imageDescriptorKey, SEPARATOR );
        final String resourcePath = StringUtils.substringAfter( imageDescriptorKey, SEPARATOR );
        return new ImageDescriptorKey( ModuleKey.from( moduleKey ), ResourcePath.from( resourcePath ) );
    }
}
