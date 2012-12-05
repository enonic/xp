package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.SubType;

public abstract class SubTypeEditors
{
    public static SubTypeEditor composite( final SubTypeEditor... editors )
    {
        return new CompositeSubTypeEditor( editors );
    }

    public static SubTypeEditor setSubType( final SubType subType )
    {
        return new SetSubTypeEditor( subType );
    }
}
