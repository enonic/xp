package com.enonic.wem.api.content.type.formitem;


import com.google.common.base.Preconditions;

public class FormItemSetSubType
    extends SubType
{
    private FormItemSet formItemSet = new FormItemSet();

    FormItemSetSubType()
    {
    }

    public String getName()
    {
        return formItemSet.getName();
    }

    void setFormItemSet( final FormItemSet formItemSet )
    {
        this.formItemSet = formItemSet;
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
            SubTypeReference subTypeReference = (SubTypeReference) formItem;
            Preconditions.checkArgument( subTypeReference.getSubTypeClass().equals( InputSubType.class ),
                                         "A SubType cannot reference other SubTypes unless it is of type %s: " +
                                             subTypeReference.getSubTypeClass().getSimpleName(), InputSubType.class.getSimpleName() );
        }
        formItemSet.addFormItem( formItem );
    }

    public HierarchicalFormItem create( final SubTypeReference subTypeReference )
    {
        final FormItemSet formItemSet = this.formItemSet.copy();
        formItemSet.setName( subTypeReference.getName() );
        formItemSet.setPath( subTypeReference.getPath() );
        return formItemSet;
    }
}
