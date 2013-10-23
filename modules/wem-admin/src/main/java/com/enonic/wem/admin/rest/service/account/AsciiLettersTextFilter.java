package com.enonic.wem.admin.rest.service.account;

import java.util.HashMap;
import java.util.Map;

final class AsciiLettersTextFilter
{
    private static final char CAPITAL_LETTER_A_GRAVE = '\u00C0';

    private static final char CAPITAL_LETTER_A_ACUTE = '\u00C1';

    private static final char CAPITAL_LETTER_A_RING = '\u00C5';

    private static final char CAPITAL_LETTER_A_E = '\u00C6';

    private static final char CAPITAL_LETTER_C_CEDILLA = '\u00C7';

    private static final char CAPITAL_LETTER_E_GRAVE = '\u00C8';

    private static final char CAPITAL_LETTER_E_ACUTE = '\u00C9';

    private static final char CAPITAL_LETTER_I_GRAVE = '\u00CC';

    private static final char CAPITAL_LETTER_I_ACUTE = '\u00CD';

    private static final char CAPITAL_LETTER_N_TILDE = '\u00D1';

    private static final char CAPITAL_LETTER_O_GRAVE = '\u00D2';

    private static final char CAPITAL_LETTER_O_ACUTE = '\u00D3';

    private static final char CAPITAL_LETTER_O_SLASH = '\u00D8';

    private static final char CAPITAL_LETTER_U_GRAVE = '\u00D9';

    private static final char CAPITAL_LETTER_U_ACUTE = '\u00DA';

    private static final char SMALL_LETTER_A_GRAVE = '\u00E0';

    private static final char SMALL_LETTER_A_ACUTE = '\u00E1';

    private static final char SMALL_LETTER_A_RING = '\u00E5';

    private static final char SMALL_LETTER_A_E = '\u00E6';

    private static final char SMALL_LETTER_C_CEDILLA = '\u00E7';

    private static final char SMALL_LETTER_E_GRAVE = '\u00E8';

    private static final char SMALL_LETTER_E_ACUTE = '\u00E9';

    private static final char SMALL_LETTER_I_GRAVE = '\u00EC';

    private static final char SMALL_LETTER_I_ACUTE = '\u00ED';

    private static final char SMALL_LETTER_N_TILDE = '\u00F1';

    private static final char SMALL_LETTER_O_GRAVE = '\u00F2';

    private static final char SMALL_LETTER_O_ACUTE = '\u00F3';

    private static final char SMALL_LETTER_O_SLASH = '\u00F8';

    private static final char SMALL_LETTER_U_GRAVE = '\u00F9';

    private static final char SMALL_LETTER_U_ACUTE = '\u00FA';

    private static final Map<Character, Character> latinOneCharsTable;

    static
    {
        latinOneCharsTable = new HashMap<Character, Character>();
        latinOneCharsTable.put( CAPITAL_LETTER_A_E, 'A' );
        latinOneCharsTable.put( SMALL_LETTER_A_E, 'a' );
        latinOneCharsTable.put( CAPITAL_LETTER_O_SLASH, 'O' );
        latinOneCharsTable.put( SMALL_LETTER_O_SLASH, 'o' );
        latinOneCharsTable.put( CAPITAL_LETTER_A_RING, 'A' );
        latinOneCharsTable.put( SMALL_LETTER_A_RING, 'a' );

        latinOneCharsTable.put( CAPITAL_LETTER_N_TILDE, 'N' );
        latinOneCharsTable.put( SMALL_LETTER_N_TILDE, 'n' );
        latinOneCharsTable.put( CAPITAL_LETTER_C_CEDILLA, 'C' );
        latinOneCharsTable.put( SMALL_LETTER_C_CEDILLA, 'c' );

        latinOneCharsTable.put( CAPITAL_LETTER_A_GRAVE, 'A' );
        latinOneCharsTable.put( SMALL_LETTER_A_GRAVE, 'a' );
        latinOneCharsTable.put( CAPITAL_LETTER_A_ACUTE, 'A' );
        latinOneCharsTable.put( SMALL_LETTER_A_ACUTE, 'a' );

        latinOneCharsTable.put( CAPITAL_LETTER_E_GRAVE, 'E' );
        latinOneCharsTable.put( SMALL_LETTER_E_GRAVE, 'e' );
        latinOneCharsTable.put( CAPITAL_LETTER_E_ACUTE, 'E' );
        latinOneCharsTable.put( SMALL_LETTER_E_ACUTE, 'e' );

        latinOneCharsTable.put( CAPITAL_LETTER_I_GRAVE, 'I' );
        latinOneCharsTable.put( SMALL_LETTER_I_GRAVE, 'i' );
        latinOneCharsTable.put( CAPITAL_LETTER_I_ACUTE, 'I' );
        latinOneCharsTable.put( SMALL_LETTER_I_ACUTE, 'i' );

        latinOneCharsTable.put( CAPITAL_LETTER_O_GRAVE, 'O' );
        latinOneCharsTable.put( SMALL_LETTER_O_GRAVE, 'o' );
        latinOneCharsTable.put( CAPITAL_LETTER_O_ACUTE, 'O' );
        latinOneCharsTable.put( SMALL_LETTER_O_ACUTE, 'o' );

        latinOneCharsTable.put( CAPITAL_LETTER_U_GRAVE, 'U' );
        latinOneCharsTable.put( SMALL_LETTER_U_GRAVE, 'u' );
        latinOneCharsTable.put( CAPITAL_LETTER_U_ACUTE, 'U' );
        latinOneCharsTable.put( SMALL_LETTER_U_ACUTE, 'u' );
    }

    public AsciiLettersTextFilter()
    {
    }

    public String convertUnicodeToAsciiLetters( final String text )
    {
        final StringBuilder output = new StringBuilder( text.length() );

        for ( int i = 0; i < text.length(); i++ )
        {
            final Character inputChar = text.charAt( i );

            if ( ( inputChar >= 'a' ) && ( inputChar <= 'z' ) || ( inputChar >= 'A' ) && ( inputChar <= 'Z' ) )
            {
                output.append( inputChar );
            }
            else if ( latinOneCharsTable.containsKey( inputChar ) )
            {
                output.append( latinOneCharsTable.get( inputChar ) );
            }
        }
        return output.toString();
    }
}
