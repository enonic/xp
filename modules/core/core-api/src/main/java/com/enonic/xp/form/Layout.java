package com.enonic.xp.form;


import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import static com.google.common.base.Strings.nullToEmpty;

@Beta
public abstract class Layout
    extends FormItem
{
    private final String name;

    Layout( final String name )
    {
        Preconditions.checkNotNull( name, "a name is required for a Layout" );
        Preconditions.checkArgument( !nullToEmpty( name ).isBlank(), "a name is required for a Layout" );
        Preconditions.checkArgument( !name.contains( "." ), "name cannot contain punctuations: " + name );
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
