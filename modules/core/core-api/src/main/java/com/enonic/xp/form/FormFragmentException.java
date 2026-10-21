package com.enonic.xp.form;

/**
 * A form cannot have its form fragments inlined: one of them is missing, or they reference each other in a cycle. Unlike a
 * descriptor that fails to parse, which is logged and left out, this is reported to the caller reading the schema.
 */
public final class FormFragmentException
    extends IllegalArgumentException
{
    public FormFragmentException( final String message )
    {
        super( message );
    }
}
