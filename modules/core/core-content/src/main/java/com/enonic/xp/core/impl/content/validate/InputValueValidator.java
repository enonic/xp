package com.enonic.xp.core.impl.content.validate;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.core.impl.content.InputVisitorHelper;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.inputtype.InputType;
import com.enonic.xp.inputtype.InputTypeValidationException;
import com.enonic.xp.inputtype.InputTypes;

@NullMarked
final class InputValueValidator
{
    private static final ValidationErrorCode VALUE_INVALID = ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.valueInvalid" );

    private static final String VALUE_INVALID_I18N = "system.cms.validation.valueInvalid";

    private InputValueValidator()
    {
    }

    static void validate( final Form form, final PropertyTree data, final OccurrenceValidator.ValidationErrorFactory errorFactory,
                          final ValidationErrors.Builder validationErrorsBuilder )
    {
        new InputVisitor()
        {
            @Override
            public void visit( final Input input )
            {
                InputVisitorHelper.visitProperties( input, data, property -> validateProperty( input, property ) );
            }

            private void validateProperty( final Input input, final Property property )
            {
                if ( property.hasNullValue() )
                {
                    return;
                }
                final InputType type = InputTypes.BUILTIN.resolve( input.getInputType() );
                try
                {
                    type.validate( property, input.getInputTypeConfig() );
                }
                catch ( InputTypeValidationException e )
                {
                    validationErrorsBuilder.add( errorFactory.create( VALUE_INVALID, e.getPropertyPath(), VALUE_INVALID_I18N )
                                                     .message( e.getMessage(), true )
                                                     .args( e.getMessage() )
                                                     .build() );
                }
            }
        }.traverse( form );
    }
}
