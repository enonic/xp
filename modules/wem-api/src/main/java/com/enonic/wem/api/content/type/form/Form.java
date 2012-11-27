package com.enonic.wem.api.content.type.form;


public class Form
{
    private final FormItems formItems = new FormItems();

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public Iterable<FormItem> formItemIterable()
    {
        return formItems;
    }

    public HierarchicalFormItem getFormItem( final String path )
    {
        return formItems.getFormItem( new FormItemPath( path ) );
    }

    public HierarchicalFormItem getFormItem( final FormItemPath path )
    {
        return formItems.getFormItem( path );
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    public Input getInput( final FormItemPath path )
    {
        return formItems.getInput( path );
    }

    public Input getInput( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path ) ? formItems.getInput( path ) : formItems.getInput( new FormItemPath( path ) );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return formItems.getFormItemSet( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getFormItemSet( path )
            : formItems.getFormItemSet( new FormItemPath( path ) );
    }

    public SubTypeReference getSubTypeReference( final FormItemPath path )
    {
        return formItems.getSubTypeReference( path );
    }

    public SubTypeReference getSubTypeReference( final String path )
    {
        return FormItemPath.hasNotPathElementDivider( path )
            ? formItems.getSubTypeReference( path )
            : formItems.getSubTypeReference( new FormItemPath( path ) );
    }

    public void subTypeReferencesToFormItems( final SubTypeFetcher subTypeFetcher )
    {
        formItems.subTypeReferencesToFormItems( subTypeFetcher );
    }
}
