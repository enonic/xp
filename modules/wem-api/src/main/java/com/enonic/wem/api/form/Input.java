package com.enonic.wem.api.form;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.type.InvalidPropertyTypeException;
import com.enonic.wem.api.data.type.ValueOfUnexpectedClassException;
import com.enonic.wem.api.form.inputtype.InputType;
import com.enonic.wem.api.form.inputtype.InputTypeConfig;

import static com.enonic.wem.api.form.Occurrences.newOccurrences;

public final class Input
    extends FormItem
{
    private final InputType type;

    private final String label;

    private final Boolean immutable;

    private final Occurrences occurrences;

    private final Boolean indexed;

    private final String customText;

    private final ValidationRegex validationRegexp;

    private final String helpText;

    private final InputTypeConfig inputTypeConfig;

    private Input( Builder builder )
    {
        super( builder.name );

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

        this.type = builder.inputType;
        this.label = builder.label;
        this.immutable = builder.immutable;
        this.occurrences = builder.occurrences;
        this.indexed = builder.indexed;
        this.customText = builder.customText;
        this.validationRegexp = builder.validationRegexp;
        this.helpText = builder.helpText;
        this.inputTypeConfig = builder.inputTypeConfig;
    }

    public InputType getInputType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public Boolean isRequired()
    {
        return occurrences.impliesRequired();
    }

    public Boolean isImmutable()
    {
        return immutable;
    }

    public Boolean isMultiple()
    {
        return occurrences.isMultiple();
    }

    public Occurrences getOccurrences()
    {
        return occurrences;
    }

    public Boolean isIndexed()
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
        throws InvalidValueException
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
        catch ( InvalidValueException e )
        {
            throw new InvalidDataException( property, e );
        }
        catch ( InvalidPropertyTypeException e )
        {
            throw new InvalidDataException( property, e );
        }
        catch ( ValueOfUnexpectedClassException e )
        {
            throw new InvalidDataException( property, e );
        }
    }


    @Override
    public Input copy()
    {
        return newInput( this ).build();
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

        private Boolean immutable = false;

        private Occurrences occurrences = newOccurrences().minimum( 0 ).maximum( 1 ).build();

        private Boolean indexed = false;

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
            this.name = source.getName();
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

        public Builder immutable( Boolean value )
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

        public Builder required( Boolean value )
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

        public Builder multiple( Boolean value )
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

        public Builder indexed( Boolean value )
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
