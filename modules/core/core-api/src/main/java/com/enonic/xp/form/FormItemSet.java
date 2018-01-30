package com.enonic.xp.form;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public class FormItemSet
    extends FormItem
    implements Iterable<FormItem>
{
    private final String name;

    private final String label;

    private final String labelI18nKey;

    private final FormItems formItems;

    private final boolean immutable;

    private final Occurrences occurrences;

    private final String customText;

    private final String helpText;

    private final String helpTextI18nKey;

    private FormItemSet( Builder builder )
    {
        super();

        Preconditions.checkNotNull( builder.name, "a name is required for a FormItemSet" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a FormItemSet" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctations: " + builder.name );

        this.name = builder.name;
        this.label = builder.label;
        this.immutable = builder.immutable;
        this.occurrences = builder.occurrences;
        this.customText = builder.customText;
        this.helpText = builder.helpText;
        this.formItems = new FormItems( this );
        this.labelI18nKey = builder.labelI18nKey;
        this.helpTextI18nKey = builder.helpTextI18nKey;
        for ( final FormItem formItem : builder.formItems )
        {
            this.formItems.add( formItem );
        }

    }

    @Override
    public String getName()
    {
        return name;
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

    public String getLabelI18nKey()
    {
        return labelI18nKey;
    }

    public String getHelpTextI18nKey()
    {
        return helpTextI18nKey;
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
            Objects.equals( this.label, that.label ) &&
            Objects.equals( this.immutable, that.immutable ) &&
            Objects.equals( this.customText, that.customText ) &&
            Objects.equals( this.helpText, that.helpText ) &&
            Objects.equals( this.occurrences, that.occurrences ) && Objects.equals( this.labelI18nKey, that.labelI18nKey ) &&
            Objects.equals( this.helpTextI18nKey, that.helpTextI18nKey ) &&
            Objects.equals( this.formItems, that.formItems );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.label, this.immutable, this.customText, this.helpText, this.occurrences,
                             this.helpTextI18nKey, this.labelI18nKey,
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
    {
        private String name;

        private String label;

        private String labelI18nKey;

        private boolean immutable;

        private Occurrences occurrences = Occurrences.create( 0, 1 );

        private String customText;

        private String helpText;

        private String helpTextI18nKey;

        private List<FormItem> formItems = new ArrayList<FormItem>();

        public Builder()
        {
            // default
        }

        public Builder( final FormItemSet source )
        {
            this.name = source.name;
            this.label = source.label;
            this.immutable = source.immutable;
            this.occurrences = source.occurrences;
            this.customText = source.customText;
            this.helpText = source.helpText;
            this.labelI18nKey = source.labelI18nKey;
            this.helpTextI18nKey = source.helpTextI18nKey;

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

        public Builder labelI18nKey( String value )
        {
            labelI18nKey = value;
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

        public Builder helpTextI18nKey( String value )
        {
            helpTextI18nKey = value;
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
