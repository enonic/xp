package com.enonic.xp.content.validate;

import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.schema.content.ContentType;

public interface ContentValidator
{
    boolean supports( ContentType contentType );

    ValidationErrors validate( ContentValidatorParams params );
}
