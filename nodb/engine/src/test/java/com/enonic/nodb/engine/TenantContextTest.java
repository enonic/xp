package com.enonic.nodb.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Gate 2 test #5: TenantContext rejects invalid ids before any identifier ever reaches
 * SQL/DDL (DESIGN.md §7.2, §5).
 */
class TenantContextTest
{
    @Test
    void acceptsValidIds()
    {
        assertDoesNotThrow( () -> new TenantContext( "acme" ) );
        assertDoesNotThrow( () -> new TenantContext( "fisk" ) );
        assertDoesNotThrow( () -> new TenantContext( "a12" ) ); // minimum length (3)
        assertDoesNotThrow( () -> new TenantContext( "a".repeat( 31 ) ) ); // maximum length (31)
    }

    @Test
    void rejectsUppercase()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "Public" ) );
    }

    @Test
    void rejectsUnderscore()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "pg_x" ) );
    }

    @Test
    void rejectsDash()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "a-b" ) );
    }

    @Test
    void rejectsReservedNodbSystem()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "nodb_system" ) );
    }

    @Test
    void rejectsOtherReservedNames()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "public" ) );
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "pg_catalog" ) );
    }

    @Test
    void rejectsOneChar()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "a" ) );
    }

    @Test
    void rejectsThirtyTwoChars()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( "a".repeat( 32 ) ) );
    }

    @Test
    void rejectsNull()
    {
        assertThrows( IllegalArgumentException.class, () -> new TenantContext( null ) );
    }
}
