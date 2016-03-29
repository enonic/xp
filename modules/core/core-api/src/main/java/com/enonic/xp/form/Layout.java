package com.enonic.xp.form;


import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public abstract class Layout
    extends FormItem
{
    private final String name;

    Layout( final String name )
    {
        Preconditions.checkNotNull( name, "a name is required for a Layout" );
        Preconditions.checkArgument( StringUtils.isNotBlank( name ), "a name is required for a Layout" );
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctations: " + name );
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.LAYOUT;
    }

    @Override
    FormItemPath resolvePath()
    {
        return resolveParentPath();
    }

    public abstract FormItem getFormItem( final String name );
}
