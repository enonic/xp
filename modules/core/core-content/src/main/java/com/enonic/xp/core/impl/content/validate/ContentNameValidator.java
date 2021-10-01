package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.validate.ContentValidator;
import com.enonic.xp.content.validate.ContentValidatorParams;

import static com.google.common.base.Strings.nullToEmpty;

@Component
public class ContentNameValidator
    implements ContentValidator
{
    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        if ( params.getName() == null || params.getName().isUnnamed() )
        {
            validationErrorsBuilder.add( ValidationError.generalError( "com.enonic.cms.nameRequired" )
                                             .i18n( "system.cms.validation.nameRequired" )
                                             .build() );
        }
        if ( nullToEmpty( params.getDisplayName() ).isBlank() )
        {
            validationErrorsBuilder.add( ValidationError.generalError( "com.enonic.cms.displaynameRequired" )
                                             .i18n( "system.cms.validation.displayNameRequired" )
                                             .build() );
        }
    }
}
