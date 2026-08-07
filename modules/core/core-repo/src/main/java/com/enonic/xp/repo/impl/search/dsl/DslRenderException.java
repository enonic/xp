package com.enonic.xp.repo.impl.search.dsl;

/**
 * Phase 4 Gate F (nodb/BUILD-PHASE-4.md): extends {@link IllegalArgumentException}, not bare
 * {@code RuntimeException}. A malformed DSL query -- no root expression, two root expressions -- IS
 * an illegal argument, and {@code IllegalArgumentException} is what XP's public contract has always
 * thrown for it: the Elasticsearch DSL builders raise it, and {@code ContentServiceImplTest_find}
 * asserts it. Gate B's renderer runs only in nodb mode (the DSL supplier is lazy), so a bare
 * {@code RuntimeException} here changed the exception an application sees purely because of the
 * backend. Narrowing the supertype is enough -- the message and the throw sites stay as they are.
 */
public class DslRenderException
    extends IllegalArgumentException
{
    public DslRenderException( final String message )
    {
        super( message );
    }
}
