package com.enonic.xp.repo.impl.vacuum.binary;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BinaryReferenceMatcher
{
    private static final Pattern pattern = Pattern.compile( "\"blobKey\":\"((\\w){40})\"" );

    public static Set<String> matches( final String value )
    {
        Set<String> matches = new HashSet<>();

        final Matcher matcher = pattern.matcher( value );

        while ( matcher.find() )
        {
            matches.add( matcher.group( 1 ) );
        }

        return matches;
    }
}
