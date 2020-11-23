package com.enonic.xp.core.impl.app.filter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;

public class AppFilter
{
    private final List<Rule> rules;

    public AppFilter( final String patterns )
    {
        rules = Arrays.stream( patterns.split( "," ) ).map( String::trim ).map( Rule::new ).collect( Collectors.toUnmodifiableList() );
    }

    public boolean accept( ApplicationKey key )
    {
        for ( Rule rule : rules )
        {
            if ( rule.pattern.matcher( key.getName() ).matches() )
            {
                return rule.positive;
            }
        }
        return false;
    }

    private static class Rule
    {
        final boolean positive;

        final Pattern pattern;

        public Rule( String ruleString )
        {
            if ( ruleString.startsWith( "!" ) )
            {
                this.positive = false;
                this.pattern = makePattern( ruleString.substring( 1 ) );
            }
            else
            {
                this.positive = true;
                this.pattern = makePattern( ruleString );
            }
        }

        Pattern makePattern( String pattern )
        {
            if ( pattern.endsWith( "*" ) )
            {
                return Pattern.compile( "^" + Pattern.quote( pattern.substring( 0, pattern.length() - 1 ) ) + ".*" );
            }
            else
            {
                return Pattern.compile( "^" + Pattern.quote( pattern ) + "$" );
            }
        }
    }
}
