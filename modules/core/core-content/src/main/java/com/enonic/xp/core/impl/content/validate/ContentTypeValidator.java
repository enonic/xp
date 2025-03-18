package com.enonic.xp.core.impl.content.validate;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.ContentValidator;
import com.enonic.xp.content.ContentValidatorParams;
import com.enonic.xp.content.ValidationErrors;

@Component
public class ContentTypeValidator
    implements ContentValidator
{
    @Override
    public void validate( final ContentValidatorParams params, final ValidationErrors.Builder validationErrorsBuilder )
    {
        if ( params.getContentType() == null )
        {
            throw new IllegalArgumentException( "ContentType is required" );
        }
    }
}
