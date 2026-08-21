package com.enonic.xp.repo.impl.elasticsearch.query.translator.factory;

import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

/**
 * ASCII-folds the fuzzy (Levenshtein) terms of a <code>simple_query_string</code> query.
 * <p>
 * Regular terms, phrases and prefix terms of a <code>simple_query_string</code> are run through the analyzer of the query, hence they are
 * ASCII-folded the very same way the indexed values are. Fuzzy terms are not analyzed at all, so a term like <code>Grønnsak~2</code> is
 * matched against index terms which are already folded to <code>gronnsaker</code>, and the diacritics eat up the edit distance budget.
 * Folding the fuzzy terms up-front brings query and index terms back into the same shape.
 * <p>
 * Only the term part of a fuzzy expression is folded. Operators, fuzziness, phrases, prefixes and escaping are left untouched, and a term
 * is left alone if folding it would introduce a character which the query parser treats as an operator.
 */
public final class SimpleQueryStringAsciiFolder
{
    private SimpleQueryStringAsciiFolder()
    {
    }

    public static String foldFuzzyTerms( final String queryString )
    {
        if ( queryString == null || queryString.chars().allMatch( c -> c < 128 ) )
        {
            return queryString;
        }

        final char[] data = queryString.toCharArray();
        final StringBuilder result = new StringBuilder( data.length );

        int index = 0;
        while ( index < data.length )
        {
            final char c = data[index];

            if ( c == '"' )
            {
                index = copyPhrase( data, index, result );
            }
            else if ( isTermFinished( c ) || c == '-' )
            {
                result.append( c );
                index++;
            }
            else
            {
                index = copyTerm( data, index, result );
            }
        }

        return result.toString();
    }

    /**
     * Copies a quoted phrase, including the terminating quote. Phrases are analyzed, hence copied as they are.
     */
    private static int copyPhrase( final char[] data, final int start, final StringBuilder result )
    {
        result.append( data[start] );

        int index = start + 1;
        while ( index < data.length )
        {
            final char c = data[index];
            result.append( c );
            index++;

            if ( c == '\\' && index < data.length )
            {
                result.append( data[index] );
                index++;
            }
            else if ( c == '"' )
            {
                break;
            }
        }

        return index;
    }

    /**
     * Copies a single term, ASCII-folding it if it turns out to be a fuzzy one.
     */
    private static int copyTerm( final char[] data, final int start, final StringBuilder result )
    {
        int index = start;
        int termLength = 0;
        boolean escaped = false;
        boolean fuzzy = false;

        while ( index < data.length )
        {
            if ( !escaped )
            {
                if ( data[index] == '\\' )
                {
                    escaped = true;
                    index++;
                    continue;
                }
                else if ( isTermFinished( data[index] ) )
                {
                    break;
                }
                else if ( termLength > 0 && data[index] == '~' )
                {
                    fuzzy = true;
                    break;
                }
            }

            escaped = false;
            termLength++;
            index++;
        }

        final String term = new String( data, start, index - start );

        if ( !fuzzy )
        {
            result.append( term );
            return index;
        }

        result.append( fold( term ) );

        return copyFuzziness( data, index, result );
    }

    /**
     * Copies the <code>~</code> character and the fuzziness following it.
     */
    private static int copyFuzziness( final char[] data, final int start, final StringBuilder result )
    {
        result.append( data[start] );

        int index = start + 1;
        while ( index < data.length && !isTermFinished( data[index] ) )
        {
            result.append( data[index] );
            index++;
        }

        return index;
    }

    private static String fold( final String term )
    {
        final StringBuilder folded = new StringBuilder( term.length() );
        final char[] output = new char[4];

        for ( int i = 0; i < term.length(); i++ )
        {
            final char c = term.charAt( i );

            if ( c < 128 )
            {
                folded.append( c );
                continue;
            }

            final String replacement = new String( output, 0, ASCIIFoldingFilter.foldToASCII( new char[]{c}, 0, output, 0, 1 ) );

            // folding may produce a character which the query parser treats as an operator, such a character is left as it is
            if ( replacement.chars().anyMatch( r -> isReserved( (char) r ) ) )
            {
                folded.append( c );
            }
            else
            {
                folded.append( replacement );
            }
        }

        return folded.toString();
    }

    private static boolean isReserved( final char c )
    {
        return isTermFinished( c ) || c == '-' || c == '~' || c == '\\';
    }

    private static boolean isTermFinished( final char c )
    {
        return c == '"' || c == '|' || c == '+' || c == '(' || c == ')' || c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
}
