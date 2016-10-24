package com.enonic.xp.form;


import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeDefault;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.inputtype.InputTypeProperty;

@Beta
public final class Input
    extends GenericFormItem
{
    private final InputTypeName type;

    private final InputTypeDefault defaultValue;

    private final boolean immutable;

    private final boolean indexed;

    private final String customText;

    private final String validationRegexp;

    private final InputTypeConfig inputTypeConfig;

    private final boolean maximizeUIInputWidth;

    private Input( Builder builder )
    {
        super( builder );

        Preconditions.checkNotNull( builder.inputType, "inputType cannot be null" );

        Preconditions.checkNotNull( label, "a label is required for a Input" );
        Preconditions.checkArgument( StringUtils.isNotBlank( label ), "a label is required for a Input" );

        this.type = builder.inputType;
        this.defaultValue = builder.defaultValue;
        this.immutable = builder.immutable;
        this.indexed = builder.indexed;
        this.customText = builder.customText;
        this.validationRegexp = builder.validationRegexp;
        this.inputTypeConfig = builder.inputTypeConfig.build();
        this.maximizeUIInputWidth = builder.maximizeUIInputWidth;
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

    public InputTypeDefault getDefaultValue()
    {
        return defaultValue;
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

    public boolean isMaximizeUIInputWidth()
    {
        return maximizeUIInputWidth;
    }

    public boolean isIndexed()
    {
        return indexed;
    }

    public String getCustomText()
    {
        return customText;
    }

    public String getValidationRegexp()
    {
        return validationRegexp;
    }

    public String getHelpText()
    {
        return helpText;
    }

    public InputTypeConfig getInputTypeConfig()
    {
        return inputTypeConfig;
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
        return super.equals( o ) &&
            Objects.equals( this.type, that.type ) &&
            Objects.equals( this.defaultValue, that.defaultValue ) &&
            Objects.equals( this.immutable, that.immutable ) &&
            Objects.equals( this.indexed, that.indexed ) &&
            Objects.equals( this.maximizeUIInputWidth, that.maximizeUIInputWidth ) &&
            Objects.equals( this.customText, that.customText ) &&
            Objects.equals( this.validationRegexp, that.validationRegexp ) &&
            Objects.equals( this.inputTypeConfig, that.inputTypeConfig );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.type, this.label, this.defaultValue, this.immutable, this.occurrences, this.indexed,
                             this.customText, this.helpText, this.validationRegexp, this.inputTypeConfig, this.maximizeUIInputWidth );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Input input )
    {
        return new Builder( input );
    }

    public static class Builder
        extends GenericFormItem.Builder
    {

        private InputTypeName inputType;

        private InputTypeDefault defaultValue;

        private boolean immutable = false;

        private boolean indexed = false;

        private String customText;

        private String validationRegexp;

        private final InputTypeConfig.Builder inputTypeConfig = InputTypeConfig.create();

        private boolean maximizeUIInputWidth = true;

        public Builder()
        {
            // default
        }

        public Builder( final Input source )
        {
            super( source );

            this.inputType = source.type;
            this.defaultValue = source.defaultValue;
            this.indexed = source.indexed;
            this.customText = source.customText;
            this.validationRegexp = source.validationRegexp;
            this.maximizeUIInputWidth = source.maximizeUIInputWidth;

            if ( source.inputTypeConfig != null )
            {
                this.inputTypeConfig.config( source.inputTypeConfig );
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

        public Builder defaultValue( InputTypeDefault value )
        {
            defaultValue = value;
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

        public Builder maximizeUIInputWidth( boolean value )
        {
            this.maximizeUIInputWidth = value;
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

        public Builder indexed( boolean value )
        {
            indexed = value;
            return this;
        }

        public Builder customText( String value )
        {
            customText = value;
            return this;
        }

        public Builder validationRegexp( String value )
        {
            validationRegexp = value;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder inputTypeProperty( final InputTypeProperty property )
        {
            this.inputTypeConfig.property( property );
            return this;
        }

        public Builder inputTypeConfig( final InputTypeConfig config )
        {
            this.inputTypeConfig.config( config );
            return this;
        }

        public Input build()
        {
            return new Input( this );
        }
    }
}
