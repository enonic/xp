package com.enonic.wem.api.schema.content.form;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.enonic.wem.api.schema.content.form.Occurrences.newOccurrences;

public class FormItemSet
    extends FormItem
    implements Iterable<FormItem>
{
    private final String label;

    private final FormItems formItems;

    private final boolean immutable;

    private final Occurrences occurrences;

    private final String customText;

    private final String helpText;

    private FormItemSet( Builder builder )
    {
        super( builder.name );

        this.label = builder.label;
        this.immutable = builder.immutable;
        this.occurrences = builder.occurrences;
        this.customText = builder.customText;
        this.helpText = builder.helpText;
        this.formItems = new FormItems( this );
        for ( final FormItem formItem : builder.formItems )
        {
            this.formItems.add( formItem );
        }
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

    @Override
    public Iterator<FormItem> iterator()
    {
        return formItems.iterator();
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
        return newFormItemSet( this ).build();
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

    public MixinReference getMixinReference( final String name )
    {
        return formItems.getMixinReference( FormItemPath.from( name ) );
    }

    public MixinReference getMixinReference( final FormItemPath formItemPath )
    {
        return formItems.getMixinReference( formItemPath );
    }

    public Layout getLayout( final String name )
    {
        return formItems.getLayout( FormItemPath.from( name ) );
    }

    public static Builder newFormItemSet()
    {
        return new Builder();
    }

    public static Builder newFormItemSet( final FormItemSet formItemSet )
    {
        return new Builder( formItemSet );
    }

    public static class Builder
    {
        private String name;

        private String label;

        private boolean immutable;

        private Occurrences occurrences = newOccurrences().minimum( 0 ).maximum( 1 ).build();

        private String customText;

        private String helpText;

        private List<FormItem> formItems = new ArrayList<FormItem>();

        public Builder()
        {
            // default
        }

        public Builder( final FormItemSet source )
        {
            this.name = source.getName();
            this.label = source.label;
            this.immutable = source.immutable;
            this.occurrences = source.occurrences;
            this.customText = source.customText;
            this.helpText = source.helpText;

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
            occurrences = newOccurrences().minimum( minOccurrences ).maximum( maxOccurrences ).build();
            return this;
        }

        public Builder minimumOccurrences( int value )
        {
            occurrences = newOccurrences( occurrences ).minimum( value ).build();
            return this;
        }

        public Builder maximumOccurrences( int value )
        {
            occurrences = newOccurrences( occurrences ).maximum( value ).build();
            return this;
        }

        public Builder required( boolean value )
        {
            if ( value && !occurrences.impliesRequired() )
            {
                occurrences = newOccurrences( occurrences ).minimum( 1 ).build();
            }
            else if ( !value && occurrences.impliesRequired() )
            {
                occurrences = newOccurrences( occurrences ).minimum( 0 ).build();
            }
            return this;
        }

        public Builder multiple( boolean value )
        {
            if ( value )
            {
                occurrences = newOccurrences( occurrences ).maximum( 0 ).build();
            }
            else
            {
                occurrences = newOccurrences( occurrences ).maximum( 1 ).build();
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
