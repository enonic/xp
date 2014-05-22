package com.enonic.wem.portal2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

final class UriTemplateParser
{
    private static final Pattern NAMES_PATTERN = Pattern.compile( "\\{([^/]+?)\\}" );

    private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

    private final String template;

    private final StringBuilder pattern;

    private final List<String> variables;

    public UriTemplateParser( final String template )
    {
        Preconditions.checkNotNull( template, "template must not be null" );
        this.template = template;
        this.pattern = new StringBuilder();
        this.variables = new ArrayList<>();
        doParse();
    }

    private void doParse()
    {
        int end = 0;
        final Matcher matcher = NAMES_PATTERN.matcher( this.template );

        while ( matcher.find() )
        {
            this.pattern.append( quote( this.template, end, matcher.start() ) );

            final String match = matcher.group( 1 );
            addVariable( match );

            end = matcher.end();
        }

        this.pattern.append( quote( this.template, end, this.template.length() ) );

        final int lastIndex = this.pattern.length() - 1;
        if ( ( lastIndex >= 0 ) && ( this.pattern.charAt( lastIndex ) == '/' ) )
        {
            this.pattern.deleteCharAt( lastIndex );
        }
    }

    private void addVariable( final String match )
    {
        final int colonIndex = match.indexOf( ':' );

        if ( colonIndex == -1 )
        {
            this.pattern.append( DEFAULT_VARIABLE_PATTERN );
            this.variables.add( match );
            return;
        }

        try
        {
            final String variablePattern = match.substring( colonIndex + 1, match.length() );
            this.pattern.append( '(' );
            this.pattern.append( variablePattern );
            this.pattern.append( ')' );

            final String variableName = match.substring( 0, colonIndex );
            this.variables.add( variableName );
        }
        catch ( final IndexOutOfBoundsException e )
        {
            throw new IllegalArgumentException( String.format( "No custom regular expression specified after ':' in \"%s\"", match ) );
        }
    }

    public Pattern getPattern()
    {
        return Pattern.compile( this.pattern.toString() );
    }

    public List<String> getVariables()
    {
        return this.variables;
    }

    private String quote( final String str, final int start, final int end )
    {
        if ( start == end )
        {
            return "";
        }

        return Pattern.quote( str.substring( start, end ) );
    }
}
