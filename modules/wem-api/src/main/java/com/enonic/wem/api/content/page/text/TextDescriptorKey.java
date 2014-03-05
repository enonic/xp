package com.enonic.wem.api.content.page.text;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleKey;

public final class TextDescriptorKey
    extends DescriptorKey
{
    private TextDescriptorKey( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        super( moduleKey, descriptorName, DescriptorType.TEXT );
    }

    public static TextDescriptorKey from( final ModuleKey moduleKey, final ComponentDescriptorName descriptorName )
    {
        return new TextDescriptorKey( moduleKey, descriptorName );
    }

    public static TextDescriptorKey from( final String imageDescriptorKey )
    {
        Preconditions.checkNotNull( imageDescriptorKey, "textDescriptorKey cannot be null" );
        final String moduleKey = StringUtils.substringBefore( imageDescriptorKey, SEPARATOR );
        final String descriptorName = StringUtils.substringAfter( imageDescriptorKey, SEPARATOR );
        return new TextDescriptorKey( ModuleKey.from( moduleKey ), new ComponentDescriptorName( descriptorName ) );
    }
}
