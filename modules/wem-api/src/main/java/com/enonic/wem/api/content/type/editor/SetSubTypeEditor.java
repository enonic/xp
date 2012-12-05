package com.enonic.wem.api.content.type.editor;

import com.enonic.wem.api.content.type.form.FormItemSetSubType;
import com.enonic.wem.api.content.type.form.InputSubType;
import com.enonic.wem.api.content.type.form.SubType;

final class SetSubTypeEditor
    implements SubTypeEditor
{
    protected final SubType source;

    public SetSubTypeEditor( final SubType source )
    {
        this.source = source;
    }

    @Override
    public SubType edit( final SubType subType )
        throws Exception
    {
        if ( subType instanceof InputSubType )
        {
            final InputSubType inputSubType = (InputSubType) subType;
            return InputSubType.newInputSubType( inputSubType ).build();
        }
        else if ( subType instanceof FormItemSetSubType )
        {
            FormItemSetSubType formItemSetSubType = (FormItemSetSubType) subType;
            return FormItemSetSubType.newFormItemSetSubType( formItemSetSubType ).build();
        }
        else
        {
            throw new IllegalArgumentException( "Type of subType not supported: " + subType.getClass().getSimpleName() );
        }
    }
}
