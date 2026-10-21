package com.enonic.xp.core.internal.form;

/**
 * A form cannot have its form fragments inlined: one of them is missing, or they reference each other in a cycle. Unlike a
 * descriptor that fails to parse, which is logged and left out, this is reported to the caller reading the schema.
 * <p>
 * Internal: the type exists so the loaders' catch-all can let it through. Callers of the API see it as an
 * {@link IllegalArgumentException}, as before the fragments were inlined into the cached value.
 */
public final class FormFragmentException
    extends IllegalArgumentException
{
    public FormFragmentException( final String message )
    {
        super( message );
    }
}
