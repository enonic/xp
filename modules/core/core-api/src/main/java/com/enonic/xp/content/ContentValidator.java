package com.enonic.xp.content;

import com.enonic.xp.schema.content.ContentTypeName;

/**
 * A validator for Content, called when Content is about to be created or modified.
 */
public interface ContentValidator
{
    /**
     * Defines if this validator can validate the supplied content by content's type name.
     * Default implementation returns true.
     *
     * @param contentTypeName content type name
     * @return true if content's type name is supported, false otherwise
     */
    default boolean supports( ContentTypeName contentTypeName )
    {
        return true;
    }

    /**
     * Validate the supplied content by various parameters.
     *
     * @param params                  parameters containing information about the content to be validated
     * @param validationErrorsBuilder builder that accepts reported validation errors
     */
    void validate( ContentValidatorParams params, ValidationErrors.Builder validationErrorsBuilder );
}
