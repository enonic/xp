package com.enonic.wem.api.content.page.image;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;

public final class ImageDescriptorKey
    extends DescriptorKey
{
    private ImageDescriptorKey( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        super( moduleKey, descriptorName, DescriptorType.IMAGE );
    }

    public static ImageDescriptorKey from( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        return new ImageDescriptorKey( moduleKey, descriptorName );
    }

    public static ImageDescriptorKey from( final String imageDescriptorKey )
    {
        Preconditions.checkNotNull( imageDescriptorKey, "imageDescriptorKey cannot be null" );
        final String moduleKey = StringUtils.substringBefore( imageDescriptorKey, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( imageDescriptorKey, SEPARATOR );
        return new ImageDescriptorKey( ModuleKey.from( moduleKey ), new ComponentDescriptorName( descriptorName ) );
    }
}
