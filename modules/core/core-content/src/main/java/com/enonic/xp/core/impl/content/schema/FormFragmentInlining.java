package com.enonic.xp.core.impl.content.schema;

import com.enonic.xp.form.Form;
import com.enonic.xp.schema.content.CmsFormFragmentService;

/**
 * Inlines the form fragments of a schema, naming the schema in the error when that fails: the failure itself (a fragment missing,
 * a cycle, two fragments contributing an item of the same name) says nothing about whose form was being expanded.
 */
public final class FormFragmentInlining
{
    private FormFragmentInlining()
    {
    }

    /**
     * @param owner what the form belongs to, as the error names it: {@code content type [myapp:article]}, {@code part [myapp:hero]}
     */
    public static Form inline( final CmsFormFragmentService formFragmentService, final Form form, final String owner )
    {
        try
        {
            return formFragmentService.inlineFormItems( form );
        }
        catch ( final RuntimeException e )
        {
            throw new IllegalArgumentException( "Form fragments of " + owner + " cannot be inlined: " + e.getMessage(), e );
        }
    }
}
