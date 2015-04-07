package com.enonic.xp.form;


import java.util.Objects;

import org.apache.commons.lang.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.inputtype.InputType;
import com.enonic.xp.form.inputtype.InputTypeConfig;

import static com.enonic.xp.form.Occurrences.newOccurrences;

@Beta
public final class Input
    extends FormItem
{
    private final String name;

    private final InputType type;

    private final String label;

    private final boolean immutable;

    private final Occurrences occurrences;

    private final boolean indexed;

    private final String customText;

    private final ValidationRegex validationRegexp;

    private final String helpText;

    private final InputTypeConfig inputTypeConfig;

    private Input( Builder builder )
    {
        super( );

        Preconditions.checkNotNull( builder.name, "a name is required for a Input" );
        Preconditions.checkArgument( StringUtils.isNotBlank( builder.name ), "a name is required for a Input" );
        Preconditions.checkArgument( !builder.name.contains( "." ), "name cannot contain punctations: " + builder.name );
        Preconditions.checkNotNull( builder.inputType, "inputType cannot be null" );

        if ( builder.inputType.requiresConfig() )
        {
            Preconditions.checkArgument( builder.inputTypeConfig != null,
                                         "Input [name='%s', type=%s] is missing required InputTypeConfig: %s", builder.name,
                                         builder.inputType.getName(), builder.inputType.requiredConfigClass().getName() );

            //noinspection ConstantConditions
            Preconditions.checkArgument( builder.inputType.requiredConfigClass().isInstance( builder.inputTypeConfig ),
                                         "Input [name='%s', type=%s] expects InputTypeConfig of type [%s] but was: %s", builder.name,
                                         builder.inputType.getName(), builder.inputType.requiredConfigClass().getName(),
                                         builder.inputTypeConfig.getClass().getName() );
        }

        this.name = builder.name;
        this.type = builder.inputType;
        this.label = builder.label;
        this.immutable = builder.immutable;
        this.occurrences = builder.occurrences;
        this.indexed = builder.indexed;
        this.customText = builder.customText;
        this.validationRegexp = builder.validationRegexp;
        this.helpText = builder.helpText;
        this.inputTypeConfig = builder.inputTypeConfig;

        this.type.validateOccurrences( this.occurrences );
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

    public InputType getInputType()
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

    public boolean isIndexed()
    {
        return indexed;
    }

    public String getCustomText()
    {
        return customText;
    }

    public ValidationRegex getValidationRegexp()
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

    public void checkValidityAccordingToInputTypeConfig( final Property property )
    {
        if ( inputTypeConfig != null )
        {
            inputTypeConfig.checkValidity( property );
        }
    }

    public void checkValidationRegexp( final Property property )
        throws InvalidDataException
    {
        try
        {
            validationRegexp.checkValidity( property );
        }
        catch ( BreaksRegexValidationException e )
        {
            throw new InvalidDataException( property, e );
        }
    }

    public void checkValidity( final Property property )
        throws InvalidDataException
    {
        try
        {
            if ( property == null )
            {
                return;
            }

            checkValidityAccordingToInputTypeConfig( property );
        }
        catch ( Exception e )
        {
            throw new InvalidDataException( property, e );
        }
    }

    @Override
    public Input copy()
    {
        return newInput( this ).build();
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
            Objects.equals( this.label, that.label ) &&
            Objects.equals( this.immutable, that.immutable ) &&
            Objects.equals( this.occurrences, that.occurrences ) &&
            Objects.equals( this.indexed, that.indexed ) &&
            Objects.equals( this.customText, that.customText ) &&
            Objects.equals( this.helpText, that.helpText ) &&
            Objects.equals( this.validationRegexp, that.validationRegexp ) &&
            Objects.equals( this.inputTypeConfig, that.inputTypeConfig );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), this.type, this.label, this.immutable, this.occurrences, this.indexed, this.customText,
                             this.helpText, this.validationRegexp, this.inputTypeConfig );
    }

    public static Builder newInput()
    {
        return new Builder();
    }

    public static Builder newInput( final Input input )
    {
        return new Builder( input );
    }

    public static class Builder
    {
        private String name;

        private InputType inputType;

        private String label;

        private boolean immutable = false;

        private Occurrences occurrences = newOccurrences().minimum( 0 ).maximum( 1 ).build();

        private boolean indexed = false;

        private String customText;

        private ValidationRegex validationRegexp;

        private String helpText;

        private InputTypeConfig inputTypeConfig;

        public Builder()
        {
            // default
        }

        public Builder( final Input source )
        {
            this.name = source.name;
            this.inputType = source.type;
            this.label = source.label;
            this.occurrences = source.occurrences;
            this.indexed = source.indexed;
            this.customText = source.customText;
            this.validationRegexp = source.validationRegexp;
            this.helpText = source.helpText;
            this.inputTypeConfig = source.inputTypeConfig;
        }

        public Builder name( String value )
        {
            name = value;
            return this;
        }

        public Builder inputType( InputType value )
        {
            inputType = value;
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
            occurrences = newOccurrences().minimum( value.getMinimum() ).maximum( value.getMaximum() ).build();
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
            validationRegexp = value != null ? new ValidationRegex( value ) : null;
            return this;
        }

        public Builder helpText( String value )
        {
            helpText = value;
            return this;
        }

        public Builder inputTypeConfig( InputTypeConfig value )
        {
            inputTypeConfig = value;
            return this;
        }

        public Input build()
        {
            return new Input( this );
        }
    }
}
