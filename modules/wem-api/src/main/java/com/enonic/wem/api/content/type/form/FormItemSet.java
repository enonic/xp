package com.enonic.wem.api.content.type.form;


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
    }

    @Override
    void setPath( final FormItemPath formItemPath )
    {
        super.setPath( formItemPath );
        formItems.setPath( formItemPath );
    }

    public void add( final FormItem formItem )
    {
        if ( formItem instanceof HierarchicalFormItem )
        {
            Preconditions.checkState( getPath() != null, "Cannot add HierarchicalFormItem before this FormItemSet have a path" );
            final HierarchicalFormItem hierarchicalFormItem = (HierarchicalFormItem) formItem;
            hierarchicalFormItem.setPath( new FormItemPath( getPath(), formItem.getName() ) );
        }

        this.formItems.add( formItem );
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
        for ( HierarchicalFormItem formItem : formItems.iterableForHierarchicalFormItems() )
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
        copy.occurrences.setMinOccurrences( occurrences.getMinimum() );
        copy.occurrences.setMaxOccurrences( occurrences.getMaximum() );
        copy.customText = customText;
        copy.helpText = helpText;
        copy.formItems = formItems.copy();
        return copy;
    }

    public static Builder newFormItemSet()
    {
        return new Builder();
    }

    HierarchicalFormItem getHierarchicalFormItem( final FormItemPath formItemPath )
    {
        return formItems.getHierarchicalFormItem( formItemPath );
    }

    public Input getInput( final String name )
    {
        return formItems.getInput( name );
    }

    public Input getInput( final FormItemPath formItemPath )
    {
        return formItems.getInput( formItemPath );
    }

    public FormItemSet getFormItemSet( final String name )
    {
        return formItems.getFormItemSet( name );
    }

    public FormItemSet getFormItemSet( final FormItemPath formItemPath )
    {
        return formItems.getFormItemSet( formItemPath );
    }

    public SubTypeReference getSubTypeReference( final FormItemPath formItemPath )
    {
        return formItems.getSubTypeReference( formItemPath );
    }

    public SubTypeReference getSubTypeReference( final String name )
    {
        return formItems.getSubTypeReference( name );
    }

    public Layout getLayout( final String name )
    {
        return formItems.getLayout( name );
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
                occurrences.setMinOccurrences( 1 );
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences.setMinOccurrences( 0 );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences.setMaxOccurrences( 0 );
            }
            else
            {
                occurrences.setMaxOccurrences( 1 );
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
            Preconditions.checkNotNull( name, "a name for the FormItemSet is required" );

            FormItemSet formItemSet = new FormItemSet();
            formItemSet.setName( name );
            formItemSet.label = label;
            formItemSet.immutable = immutable;
            formItemSet.occurrences.setMinOccurrences( occurrences.getMinimum() );
            formItemSet.occurrences.setMaxOccurrences( occurrences.getMaximum() );
            formItemSet.customText = customText;
            formItemSet.helpText = helpText;

            formItemSet.setPath( new FormItemPath( formItemSet.getName() ) );

            for ( FormItem formItem : formItems )
            {
                formItemSet.add( formItem );
            }
            return formItemSet;
        }
    }
}
