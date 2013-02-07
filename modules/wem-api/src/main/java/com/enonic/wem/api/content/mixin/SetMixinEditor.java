package com.enonic.wem.api.content.mixin;

import com.enonic.wem.api.Icon;

final class SetMixinEditor
    implements MixinEditor
{
    private final Mixin source;

    SetMixinEditor( final Mixin source )
    {
        this.source = source;
    }

    @Override
    public Mixin edit( final Mixin mixin )
        throws Exception
    {
        final Icon iconToSet = ( source.getIcon() == null ) ? null : Icon.copyOf( source.getIcon() );

        final Mixin.Builder builder = Mixin.newMixin( mixin );
        builder.displayName( source.getDisplayName() );
        builder.formItem( source.getFormItem() );
        builder.module( source.getModuleName() );
        builder.createdTime( source.getCreatedTime() );
        builder.modifiedTime( source.getModifiedTime() );
        builder.icon( iconToSet );

        return builder.build();
    }
}
