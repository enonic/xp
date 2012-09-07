package com.enonic.wem.core.content.type.configitem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;


public class VisualFieldSet
    extends FormItem
    implements Iterable<FormItem>
{
    private String label;

    private FormItems formItems = new FormItems();

    protected VisualFieldSet()
    {
        super( FormItemType.VISUAL_FIELD_SET );
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    @Override
    public VisualFieldSet copy()
    {
        final VisualFieldSet copy = (VisualFieldSet) super.copy();
        copy.label = label;
        copy.formItems = formItems.copy();
        return copy;
    }

    public static Builder newVisualFieldSet()
    {
        return new Builder();
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.addFormItem( formItem );
    }

    FormItems getFormItems()
    {
        return formItems;
    }

    public FormItem getFormItem( final String name )
    {
        return formItems.getFormItem( name );
    }

    void forwardSetPath( FormItemPath path )
    {
        formItems.setPath( path );
    }

    public Iterable<FormItem> getFormItemsIterable()
    {
        return formItems.iterable();
    }

    public static class Builder
    {
        private String label;

        private String name;

        private List<FormItem> formItems = new ArrayList<FormItem>();

        public Builder label( String value )
        {
            this.label = value;
            return this;
        }

        public Builder name( String value )
        {
            this.name = value;
            return this;
        }

        public Builder add( FormItem formItem )
        {
            this.formItems.add( formItem );
            return this;
        }

        public VisualFieldSet build()
        {
            Preconditions.checkNotNull( this.label, "label is required" );
            Preconditions.checkNotNull( this.name, "name is required" );

            VisualFieldSet visualFieldSet = new VisualFieldSet();
            visualFieldSet.label = this.label;
            visualFieldSet.setName( this.name );
            for ( FormItem formItem : formItems )
            {
                visualFieldSet.addFormItem( formItem );
            }
            return visualFieldSet;
        }
    }
}
