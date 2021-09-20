package com.enonic.xp.content.validate;

import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.schema.content.ContentTypeName;

public interface ContentValidator
{
    boolean supports( ContentTypeName contentType );

    void validate( ContentValidatorParams params, ValidationErrors.Builder validationErrorsBuilder );
}
