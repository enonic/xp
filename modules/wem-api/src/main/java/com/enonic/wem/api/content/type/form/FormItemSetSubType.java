package com.enonic.wem.api.content.type.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.module.Module;

public class FormItemSetSubType
    extends SubType
{
    private FormItemSet formItemSet = new FormItemSet();

    FormItemSetSubType( final Module module )
    {
        super( module );
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

    public HierarchicalFormItem create( final SubTypeReference subTypeReference )
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

    public static class Builder
    {
        private Module module;

        private FormItemSet formItemSet;

        public Builder module( Module value )
        {
            this.module = value;
            return this;
        }

        public Builder formItemSet( FormItemSet value )
        {
            this.formItemSet = value;
            return this;
        }

        public FormItemSetSubType build()
        {
            FormItemSetSubType subType = new FormItemSetSubType( module );
            subType.formItemSet = formItemSet;
            return subType;
        }
    }
}
