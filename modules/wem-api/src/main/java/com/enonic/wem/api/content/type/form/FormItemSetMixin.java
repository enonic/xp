package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public final class FormItemSetMixin
    extends Mixin
{
    private final FormItemSet formItemSet;

    FormItemSetMixin( final String displayName, final ModuleName moduleName, final FormItemSet formItemSet )
    {
        super( displayName, moduleName );
        Preconditions.checkNotNull( formItemSet, "formItemSet is required" );
        this.formItemSet = formItemSet;
    }

    public String getName()
    {
        return formItemSet.getName();
    }

    @Override
    public Class getType()
    {
        return this.getClass();
    }

    public FormItemSet getFormItemSet()
    {
        return formItemSet;
    }

    public void addFormItem( final HierarchicalFormItem formItem )
    {
        if ( formItem instanceof MixinReference )
        {
            final MixinReference mixinReference = (MixinReference) formItem;
            Preconditions.checkArgument( mixinReference.getMixinClass().equals( InputMixin.class ),
                                         "A Mixin cannot reference other Mixins unless it is of type %s: " +
                                             mixinReference.getMixinClass().getSimpleName(), InputMixin.class.getSimpleName() );
        }
        formItemSet.add( formItem );
    }

    public FormItem toFormItem( final MixinReference mixinReference )
    {
        final FormItemSet newSet = this.formItemSet.copy();
        newSet.setName( mixinReference.getName() );
        newSet.setPath( mixinReference.getPath() );
        return newSet;
    }

    public static Builder newFormItemSetMixin()
    {
        return new Builder();
    }

    public static Builder newFormItemSetMixin( final FormItemSetMixin source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private String displayName;

        private ModuleName moduleName;

        private FormItemSet formItemSet;

        public Builder()
        {
        }

        public Builder( final FormItemSetMixin source )
        {
            this.displayName = source.getDisplayName();
            this.moduleName = source.getModuleName();
            this.formItemSet = source.formItemSet;
        }

        public Builder displayName( String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder module( ModuleName value )
        {
            this.moduleName = value;
            return this;
        }

        public Builder formItemSet( FormItemSet value )
        {
            this.formItemSet = value;
            return this;
        }

        public FormItemSetMixin build()
        {
            Preconditions.checkNotNull( formItemSet, "formItemSet is required" );
            Preconditions.checkNotNull( moduleName, "moduleName is required" );

            return new FormItemSetMixin( displayName, moduleName, formItemSet );
        }
    }
}
