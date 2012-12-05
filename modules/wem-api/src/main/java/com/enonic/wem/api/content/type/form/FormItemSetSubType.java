package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.ModuleName;

public final class FormItemSetSubType
    extends SubType
{
    private final FormItemSet formItemSet;

    FormItemSetSubType( final String displayName, final ModuleName moduleName, final FormItemSet formItemSet )
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
        if ( formItem instanceof SubTypeReference )
        {
            final SubTypeReference subTypeReference = (SubTypeReference) formItem;
            Preconditions.checkArgument( subTypeReference.getSubTypeClass().equals( InputSubType.class ),
                                         "A SubType cannot reference other SubTypes unless it is of type %s: " +
                                             subTypeReference.getSubTypeClass().getSimpleName(), InputSubType.class.getSimpleName() );
        }
        formItemSet.add( formItem );
    }

    public FormItem toFormItem( final SubTypeReference subTypeReference )
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
        private String displayName;

        private ModuleName moduleName;

        private FormItemSet formItemSet;

        public Builder()
        {
        }

        public Builder( final FormItemSetSubType source )
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

        public FormItemSetSubType build()
        {
            Preconditions.checkNotNull( formItemSet, "formItemSet is required" );
            Preconditions.checkNotNull( moduleName, "moduleName is required" );

            return new FormItemSetSubType( displayName, moduleName, formItemSet );
        }
    }
}
