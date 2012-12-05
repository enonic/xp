package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public final class FormItemSetSubType
    extends SubType
{
    private final FormItemSet formItemSet;

    FormItemSetSubType( final ModuleName moduleName, final FormItemSet formItemSet )
    {
        super( moduleName );
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

    public void addFormItem( final HierarchicalFormItem formItem )
    {
        if ( formItem instanceof SubTypeReference )
        {
            final SubTypeReference subTypeReference = (SubTypeReference) formItem;
            Preconditions.checkArgument( subTypeReference.getSubTypeClass().equals( InputSubType.class ),
                                         "A SubType cannot reference other SubTypes unless it is of type %s: " +
                                             subTypeReference.getSubTypeClass().getSimpleName(), InputSubType.class.getSimpleName() );
        }
        formItemSet.add( formItem );
    }

    public FormItem create( final SubTypeReference subTypeReference )
    {
        final FormItemSet newSet = this.formItemSet.copy();
        newSet.setName( subTypeReference.getName() );
        newSet.setPath( subTypeReference.getPath() );
        return newSet;
    }

    public static Builder newFormItemSetSubType()
    {
        return new Builder();
    }

    public static Builder newFormItemSetSubType( final FormItemSetSubType source )
    {
        return new Builder( source );
    }

    public static class Builder
    {
        private ModuleName moduleName;

        private FormItemSet formItemSet;

        public Builder()
        {
        }

        public Builder( final FormItemSetSubType source )
        {
            this.moduleName = source.getModuleName();
            this.formItemSet = source.formItemSet;
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

        public FormItemSetSubType build()
        {
            Preconditions.checkNotNull( formItemSet, "formItemSet is required" );
            Preconditions.checkNotNull( moduleName, "moduleName is required" );

            return new FormItemSetSubType( moduleName, formItemSet );
        }
    }
}
