package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrorCode;
import com.enonic.xp.content.ValidationErrors;

import static com.google.common.base.Strings.nullToEmpty;

@Component
public final class ContentNameValidator
    implements ContentValidator
{
    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        if ( params.getName() == null || params.getName().isUnnamed() )
        {
            validationErrorsBuilder.add( ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.nameRequired" ) )
                                             .build() );
        }
        if ( nullToEmpty( params.getDisplayName() ).isBlank() )
        {
            validationErrorsBuilder.add(
                ValidationError.generalError( ValidationErrorCode.from( ApplicationKey.SYSTEM, "cms.validation.displaynameRequired" ) )
                    .build() );
        }
    }
}
