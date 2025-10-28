package com.enonic.xp.core.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileNamesTest
{
    @Test
    void isSafeFileName_unsafe()
    {
        assertUnsafe( "a".repeat( 256 ) );
        assertUnsafe( "\u0101".repeat( 128 ) );
        assertUnsafe( "\u0800".repeat( 86 ) );
        assertUnsafe( "" );
        assertUnsafe( " " );
        assertUnsafe( "." );
        assertUnsafe( ".." );
        assertUnsafe( "file\u200B" );
        assertUnsafe( "\u200B.txt" );
        assertUnsafe( "file " );
        assertUnsafe( "path/to/file" );
        assertUnsafe( "\\path\\to\\file" );
        assertUnsafe( "C:" );
        assertUnsafe( "file\nname" );
        assertUnsafe( "txt." );
        assertUnsafe( " .txt" );
        assertUnsafe( "A\u0308\uFB03n" );
        assertUnsafe( "*.txt" );
        assertUnsafe( "AUX" );
        assertUnsafe( "COM0" );
        assertUnsafe( "lpt9" );
        assertUnsafe( "nuL.txt" );
        assertUnsafe( "-rm" );
        assertUnsafe( "\u0000.txt" );
        assertUnsafe( "\uD83D\uDE02.txt" );
        assertUnsafe( "\uFFFE.txt" );
    }


    @Test
    void isSafeFileName_safe()
    {
        assertSafe( "a".repeat( 255 ) );
        assertSafe( "\u0101".repeat( 127 ) );
        assertSafe( "\u0800".repeat( 85 ) );
        assertSafe( "a" );
        assertSafe( "\u00C4\uFB03n" );
        assertSafe( "b.txt" );
        assertSafe( "c.png.jpg" );
        assertSafe( "my file.jpg" );
        assertSafe( ".gitignore" );
    }

    void assertUnsafe( final String fileName )
    {
        assertFalse( FileNames.isSafeFileName( fileName ) );
    }

    void assertSafe( final String fileName )
    {
        assertTrue( FileNames.isSafeFileName( fileName ) );
    }
}
