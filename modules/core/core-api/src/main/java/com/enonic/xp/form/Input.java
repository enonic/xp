package com.enonic.xp.form;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.util.GenericValue;
import com.enonic.xp.inputtype.InputTypeName;

import static com.google.common.base.Strings.nullToEmpty;

@PublicApi
public final class Input
    extends FormItem
{
    private final String name;

    private final InputTypeName type;

    private final String label;

    private final String labelI18nKey;

    private final Occurrences occurrences;

    private final String helpText;

    private final String helpTextI18nKey;

    private final GenericValue inputTypeConfig;

    private Input( Builder builder )
    {
        super();

        Preconditions.checkArgument( !nullToEmpty( builder.name ).isBlank(), "name is required for a Input" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctuations: %s", builder.name );
        Objects.requireNonNull( builder.inputType, "inputType is required for a Input" );
        Preconditions.checkArgument( !nullToEmpty( builder.label ).isBlank(), "label is required for a Input" );

        this.name = builder.name;
        this.type = builder.inputType;
        this.label = builder.label;
        this.labelI18nKey = builder.labelI18nKey;
        this.occurrences = builder.occurrences;
        this.helpText = builder.helpText;
        this.helpTextI18nKey = builder.helpTextI18nKey;
        this.inputTypeConfig = builder.inputTypeConfig.build();
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public FormItemType getType()
    {
        return FormItemType.INPUT;
    }

    public InputTypeName getInputType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public boolean isMultiple()
    {
        return occurrences.isMultiple();
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public GenericValue getInputTypeConfig()
    {
        return inputTypeConfig;
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
    public Input copy()
    {
        return create( this ).build();
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

        final Input that = (Input) o;
        return super.equals( o ) && Objects.equals( this.type, that.type ) && Objects.equals( this.label, that.label ) &&
            Objects.equals( this.occurrences, that.occurrences ) && Objects.equals( this.helpText, that.helpText ) &&
            Objects.equals( this.inputTypeConfig, that.inputTypeConfig ) && Objects.equals( this.helpTextI18nKey, that.helpTextI18nKey ) &&
            Objects.equals( this.labelI18nKey, that.labelI18nKey );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.type, this.label, this.occurrences, this.helpText, this.inputTypeConfig,
                             this.labelI18nKey, this.helpTextI18nKey );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Input input )
    {
        return new Builder( input );
    }

    public static final class Builder
    {
        private String name;

        private InputTypeName inputType;

        private String label;

        private String labelI18nKey;

        private Occurrences occurrences = Occurrences.create( 0, 1 );

        private String helpText;

        private String helpTextI18nKey;

        private final GenericValue.ObjectBuilder inputTypeConfig = GenericValue.newObject();

        private Builder()
        {
        }

        private Builder( final Input source )
        {
            this.name = source.name;
            this.inputType = source.type;
            this.label = source.label;
            this.occurrences = source.occurrences;
            this.helpText = source.helpText;
            this.labelI18nKey = source.labelI18nKey;
            this.helpTextI18nKey = source.helpTextI18nKey;

            if ( source.inputTypeConfig != null )
            {
                source.inputTypeConfig.properties().forEach( p -> this.inputTypeConfig.put( p.getKey(), p.getValue() ) );
            }
        }

        public Builder name( String value )
        {
            name = value;
            return this;
        }

        public Builder inputType( InputTypeName value )
        {
            inputType = value;
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

        public Builder minimumOccurrences( int value )
        {
            occurrences = Occurrences.create( value, occurrences.getMaximum() );
            return this;
        }

        public Builder maximumOccurrences( int value )
        {
            occurrences = Occurrences.create( occurrences.getMinimum(), value );
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

        public Builder inputTypeProperty( String name, String value )
        {
            this.inputTypeConfig.put( name, value );
            return this;
        }

        public Builder inputTypeProperty( String name, GenericValue value )
        {
            this.inputTypeConfig.put( name, value );
            return this;
        }

        public Builder inputTypeConfig( GenericValue config )
        {
            config.properties().forEach( p -> this.inputTypeConfig.put( p.getKey(), p.getValue() ) );
            return this;
        }

        public Input build()
        {
            return new Input( this );
        }
    }
}
