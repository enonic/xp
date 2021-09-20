package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ValidationError;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.validate.ContentValidator;
import com.enonic.xp.content.validate.ContentValidatorParams;
import com.enonic.xp.schema.content.ContentTypeName;

import static com.google.common.base.Strings.nullToEmpty;

@Component
public class ContentNameValidator
    implements ContentValidator
{
    @Override
    public boolean supports( final ContentTypeName contentType )
    {
        return true;
    }

    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        if ( params.getName() == null || params.getName().isUnnamed() )
        {
            validationErrorsBuilder.add( new ValidationError( "NAME_REQUIRED", "name is required" ) );
        }
        if ( nullToEmpty( params.getDisplayName() ).isBlank() )
        {
            validationErrorsBuilder.add( new ValidationError( "DISPLAYNAME_REQUIRED", "displayName is required" ) );
        }
    }
}
