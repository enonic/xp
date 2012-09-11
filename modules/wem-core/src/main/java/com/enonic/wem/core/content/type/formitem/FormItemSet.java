package com.enonic.wem.core.content.type.formitem;


import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class FormItemSet
    extends HierarchicalFormItem
{
    private String label;

    private FormItems formItems = new FormItems();

    private boolean immutable;

    private final Occurrences occurrences = new Occurrences( 0, 1 );

    private String customText;

    private String helpText;

    protected FormItemSet()
    {
        super( FormItemType.FORM_ITEM_SET );
    }

    @Override
    void setPath( final FormItemPath formItemPath )
    {
        super.setPath( formItemPath );
        formItems.setPath( formItemPath );
    }

    public void addFormItem( final FormItem formItem )
    {
        this.formItems.addFormItem( formItem );
    }

    public void addItem( final Component component )
    {
        Preconditions.checkState( getPath() != null, "Cannot add Component before this FormItemSet is added" );

        component.setPath( new FormItemPath( getPath(), component.getName() ) );
        this.formItems.addFormItem( component );
    }

    public void addFormItemSet( final FormItemSet formItemSet )
    {
        Preconditions.checkState( getPath() != null, "Cannot add FormItemSet before this FormItemSet is added" );

        formItemSet.setPath( new FormItemPath( getPath(), formItemSet.getName() ) );
        this.formItems.addFormItem( formItemSet );
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public boolean isMultiple()
    {
        return occurrences.isMultiple();
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public String getCustomText()
    {
        return customText;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    @Override
    void setParentPath( final FormItemPath parentPath )
    {
        super.setParentPath( parentPath );
        for ( HierarchicalFormItem formItem : formItems.iterableForDirectAccessFormItems() )
        {
            formItem.setParentPath( this.getPath() );
        }
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        FormItemPath formItemPath = getPath();
        if ( formItemPath != null )
        {
            s.append( formItemPath.toString() );
        }
        else
        {
            s.append( getName() ).append( "?" );
        }
        if ( isMultiple() )
        {
            s.append( "[]" );
        }

        return s.toString();
    }

    @Override
    public FormItemSet copy()
    {
        FormItemSet copy = (FormItemSet) super.copy();
        copy.label = label;
        copy.immutable = immutable;
        copy.occurrences.setMinOccurences( occurrences.getMinimum() );
        copy.occurrences.setMaxOccurences( occurrences.getMaximum() );
        copy.customText = customText;
        copy.helpText = helpText;
        copy.formItems = formItems.copy();
        return copy;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static Builder newFormItemSet()
    {
        return new Builder();
    }

    public HierarchicalFormItem getFormItem( final FormItemPath formItemPath )
    {
        return formItems.getFormItem( formItemPath );
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean immutable;

        private Occurrences occurrences = new Occurrences( 0, 1 );

        private String customText;

        private String helpText;

        private List<FormItem> formItems = new ArrayList<FormItem>();

        private Builder()
        {

        }

        public Builder name( String value )
        {
            name = value;
            return this;
        }

        public Builder label( String value )
        {
            label = value;
            return this;
        }

        public Builder immutable( boolean value )
        {
            immutable = value;
            return this;
        }

        public Builder occurrences( Occurrences value )
        {
            occurrences = value;
            return this;
        }

        public Builder occurrences( int minOccurrences, int maxOccurrences )
        {
            occurrences = new Occurrences( minOccurrences, maxOccurrences );
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !occurrences.impliesRequired() )
            {
                occurrences.setMinOccurences( 1 );
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences.setMinOccurences( 0 );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences.setMaxOccurences( 0 );
            }
            else
            {
                occurrences.setMaxOccurences( 1 );
            }
            return this;
        }

        public Builder customText( String value )
        {
            customText = value;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder add( FormItem value )
        {
            formItems.add( value );
            return this;
        }

        public FormItemSet build()
        {
            FormItemSet formItemSet = new FormItemSet();
            formItemSet.setName( name );
            formItemSet.label = label;
            formItemSet.immutable = immutable;
            formItemSet.occurrences.setMinOccurences( occurrences.getMinimum() );
            formItemSet.occurrences.setMaxOccurences( occurrences.getMaximum() );
            formItemSet.customText = customText;
            formItemSet.helpText = helpText;
            for ( FormItem formItem : formItems )
            {
                formItemSet.addFormItem( formItem );
            }

            Preconditions.checkNotNull( formItemSet.getName(), "a name for the FormItemSet is required" );
            formItemSet.setPath( new FormItemPath( formItemSet.getName() ) );
            return formItemSet;
        }
    }
}
