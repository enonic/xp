package com.enonic.wem.core.elasticsearch;


/**
 * This is a proof of concept, stolen from http://jasonfager.com/770-lexi-sortable-number-strings/
 * This must be rewritten to our own code as soon as possible
 */
public class LexiSortable
{

    // Lookup table to find hex digits from bytes.
    private final static char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    // Lookup table to find bytes from hex digits.
    private final static byte[] BYTE_LOOKUP = new byte['F' + 1];

    static
    {
        for ( int i = 0; i < HEX_DIGITS.length; i++ )
        {
            BYTE_LOOKUP[HEX_DIGITS[i]] = (byte) i;
        }
    }

    // 16 chars to represent a long in hex, plus a type token.
    private static final int LEXI_STRING_LEN = 17;

    // Utility method converts a long to a hex string and prepends
    // a type token
    private static String toHexString( char type, long i )
    {
        final char[] buf = new char[LEXI_STRING_LEN];
        int charPos = LEXI_STRING_LEN;
        do
        {
            // read bottom 4 bits to lookup hex char for
            // current position
            buf[--charPos] = HEX_DIGITS[(int) ( i & 0xf )];
            // shift so we can do it again for the next.
            i >>>= 4;
        }
        while ( charPos > 0 );
        buf[0] = type;
        return new String( buf );
    }

    // Utility method converts a hex string to a long.
    // It ignores the leading type token; verification
    // needs to be handled by the calling function.
    private static long fromHexString( final String s )
    {
        final byte[] bytes = s.getBytes();
        long out = 0L;
        for ( int i = 1; i < LEXI_STRING_LEN; i++ )
        {
            // first shift is wasted, but after that,
            // move previously xor'd bits out of the
            // way so they don't get clobbered by
            // subsequent chars.
            out <<= 4;
            // Note that we shifted 4 bits b/c we're
            // using hex, but we have to XOR a byte
            // at a time. This is fine, b/c the high
            // bits of the bytes stored in the lookup
            // table are zeroed out.
            out ^= BYTE_LOOKUP[bytes[i]];
        }
        return out;
    }

    // All zeroes except the sign bit.
    private static final long SIGN_MASK = 0x8000000000000000L;

    /**
     * Returns a string s for long l such that for any long l'
     * where l < l', s.compareTo(toLexiSortable(l')) < 0.
     *
     * @param l The long to represent as a properly sortable
     *          String.
     * @return A properly sortable String representation of
     * the input value.
     */
    public static String toLexiSortable( final long l )
    {
        // Toggle the sign bit with an XOR and dump out
        // as a hex string.
        return toHexString( 'l', l ^ SIGN_MASK );
    }

    /**
     * Returns a long l such that
     * longFromLexiSortable(toLexiSortable(l)) == l.
     *
     * @param s The String to convert back to a source long.
     * @return The source long for the input String s.
     */
    public static long longFromLexiSortable( final String s )
    {
        if ( !s.startsWith( "l" ) )
        {
            throw new IllegalArgumentException( s + " does not represent a long" );
        }
        // Get an intermediate long representation
        // from the hex string.
        long tmp = fromHexString( s );
        // Toggle the sign bit with an XOR to get original
        // long back.
        return tmp ^ SIGN_MASK;
    }

    public static String toLexiSortable( final double d )
    {
        long tmp = Double.doubleToRawLongBits( d );
        return toHexString( 'd', ( tmp < 0 ) ? ~tmp : ( tmp ^ SIGN_MASK ) );
    }

    public static double doubleFromLexiSortable( final String s )
    {
        if ( !s.startsWith( "d" ) )
        {
            throw new IllegalArgumentException( s + " does not represent a double" );
        }
        long tmp = fromHexString( s );
        tmp = ( tmp < 0 ) ? ( tmp ^ SIGN_MASK ) : ~tmp;
        return Double.longBitsToDouble( tmp );
    }
}
