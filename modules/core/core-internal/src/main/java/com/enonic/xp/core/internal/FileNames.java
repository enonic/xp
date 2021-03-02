package com.enonic.xp.core.internal;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Collection of utility methods for file names
 */
public class FileNames
{
    // From Windows File Naming Conventions: reserved characters <>:"/\|?*
    // Sorted for binary search
    private static final int[] RESERVED_CHARACTERS = "<>:\"/\\|?*".chars().sorted().toArray();

    // From Windows File Naming Conventions, extended with COM0 and LPT0 (they are not acceptable either)
    public static final Set<String> RESERVED_NAME =
        Set.of( "con", "prn", "aux", "nul", "com0", "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9", "lpt0", "lpt1",
                "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9" );

    public static final Set<String> RESERVED_PREFIX = RESERVED_NAME.stream().map( reserved -> reserved + "." ).collect( Collectors.toUnmodifiableSet() );

    // All types of invisible characters, including surrogate to disallow non-BMP characters
    // Unassigned characters are can't be printed and also considered invisible
    // Sorted for binary search
    public static final int[] INVISIBLE_CHARACTER_TYPES =
        IntStream.of( Character.UNASSIGNED, Character.SPACE_SEPARATOR, Character.LINE_SEPARATOR, Character.PARAGRAPH_SEPARATOR,
                      Character.CONTROL, Character.FORMAT, Character.PRIVATE_USE, Character.SURROGATE ).sorted().toArray();

    private FileNames()
    {
    }

    /**
     * Checks if filename is safe for most supported platforms/configurations
     * - It is legal file name on most file systems
     * - It does not create severe vulnerabilities
     * - It does not create Unicode-equivalent names
     * - It's length is supported by most file systems
     *
     * @param fileName file name for a check
     * @return true if file name is safe, false otherwise
     */
    public static boolean isSafeFileName( final String fileName )
    {
        Objects.requireNonNull( fileName );

        final int length = fileName.length();
        if ( length == 0 || length > 255 )
        {
            return false;
        }

        // Single and double period - reserved names for current and parent folder
        if ( fileName.equals( "." ) || fileName.equals( ".." ) )
        {
            return false;
        }

        // Only accept normalized-composed to avoid Unicode interpretations
        if ( !Normalizer.isNormalized( fileName, Normalizer.Form.NFC ) )
        {
            return false;
        }

        final char firstChar = fileName.charAt( 0 );
        // Leading hyphen is problematic as it interferes with command line arguments
        // Leading spaces are legal but confusing
        if ( firstChar == '-' || firstChar == ' ' )
        {
            return false;
        }

        final char lastChar = fileName.charAt( length - 1 );
        // Windows File Naming Conventions: Do not end a file or directory name with a space or a period
        if ( lastChar == '.' || lastChar == ' ' )
        {
            return false;
        }

        if ( fileName.chars().
            anyMatch( cp -> Arrays.binarySearch( RESERVED_CHARACTERS, cp ) >= 0 || // Reserved Characters won't work on some filesystems
                ( cp != ' ' && charIsInvisible( cp ) ) // Allow normal space but no other invisible characters
            ) )
        {
            return false;
        }

        // Problematic names on Windows, at lest
        if ( RESERVED_NAME.stream().anyMatch( fileName::equalsIgnoreCase ) )
        {
            return false;
        }

        // Windows Explorer prohibits reserved names with any extension
        final String lowerCaseFileName = fileName.toLowerCase( Locale.ROOT );
        if ( RESERVED_PREFIX.stream().anyMatch( lowerCaseFileName::startsWith ) )
        {
            return false;
        }

        // Recommendation for file name encoding is UTF-8, and many filesystems can't accept more than 255 bytes
        return fileName.getBytes( StandardCharsets.UTF_8 ).length <= 255;
    }

    private static boolean charIsInvisible( final int c )
    {
        return Character.isWhitespace( c ) || Arrays.binarySearch( INVISIBLE_CHARACTER_TYPES, Character.getType( c ) ) >= 0;
    }
}
