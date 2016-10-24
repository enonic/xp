package com.enonic.xp.form;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.annotations.Beta;

@Beta
public class FormItemSet
    extends GenericFormItem
    implements Iterable<FormItem>
{
    private final FormItems formItems;

    private final boolean immutable;

    private final String customText;

    private FormItemSet( final Builder builder )
    {
        super( builder );

        this.immutable = builder.immutable;
        this.customText = builder.customText;
        this.formItems = new FormItems( this );
        for ( final FormItem formItem : builder.formItems )
        {
            this.formItems.add( formItem );
        }
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.FORM_ITEM_SET;
    }

    public void add( final FormItem formItem )
    {
        this.formItems.add( formItem );
    }

    public boolean isImmutable()
    {
        return immutable;
    }

    public boolean isMultiple()
    {
        return occurrences.isMultiple();
    }

    public String getCustomText()
    {
        return customText;
    }

    public FormItems getFormItems()
    {
        return formItems;
    }

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final FormItemSet that = (FormItemSet) o;
        return super.equals( o ) &&
            Objects.equals( this.immutable, that.immutable ) &&
            Objects.equals( this.customText, that.customText ) &&
            Objects.equals( this.formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.label, this.immutable, this.customText, this.helpText, this.occurrences,
                             this.formItems );
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
        return create( this ).build();
    }

    public FormItem getFormItem( final String path )
    {
        return formItems.getFormItem( FormItemPath.from( path ) );
    }

    public FormItem getFormItem( final FormItemPath path )
    {
        return formItems.getFormItem( path );
    }

    public FormItemSet getFormItemSet( final String path )
    {
        return formItems.getFormItemSet( FormItemPath.from( path ) );
    }

    public FormItemSet getFormItemSet( final FormItemPath path )
    {
        return formItems.getFormItemSet( path );
    }

    public Input getInput( final String path )
    {
        return formItems.getInput( FormItemPath.from( path ) );
    }

    public Input getInput( final FormItemPath path )
    {
        return formItems.getInput( path );
    }

    public InlineMixin getInlineMixin( final String name )
    {
        return formItems.getInlineMixin( FormItemPath.from( name ) );
    }

    public InlineMixin getInlineMixin( final FormItemPath formItemPath )
    {
        return formItems.getInlineMixin( formItemPath );
    }

    public Layout getLayout( final String name )
    {
        return formItems.getLayout( FormItemPath.from( name ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final FormItemSet formItemSet )
    {
        return new Builder( formItemSet );
    }

    public static class Builder
        extends GenericFormItem.Builder
    {
        private boolean immutable;

        private String customText;

        private List<FormItem> formItems = new ArrayList<FormItem>();

        public Builder()
        {
            // default
        }

        public Builder( final FormItemSet source )
        {
            super( source );

            this.immutable = source.immutable;
            this.customText = source.customText;

            for ( final FormItem formItemSource : source.formItems )
            {
                formItems.add( formItemSource.copy() );
            }
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
            occurrences = Occurrences.create( minOccurrences, maxOccurrences );
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !occurrences.impliesRequired() )
            {
                occurrences = Occurrences.create( 1, occurrences.getMaximum() );
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences = Occurrences.create( 0, occurrences.getMaximum() );
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences = Occurrences.create( occurrences.getMinimum(), 0 );
            }
            else
            {
                occurrences = Occurrences.create( occurrences.getMinimum(), 1 );
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

        public Builder addFormItem( FormItem value )
        {
            formItems.add( value );
            return this;
        }

        public Builder addFormItems( Iterable<FormItem> iterable )
        {
            for ( FormItem formItem : iterable )
            {
                formItems.add( formItem );
            }
            return this;
        }

        public Builder clearFormItems()
        {
            formItems.clear();
            return this;
        }

        public FormItemSet build()
        {
            return new FormItemSet( this );
        }
    }
}
