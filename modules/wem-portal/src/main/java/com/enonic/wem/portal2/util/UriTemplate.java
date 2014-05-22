package com.enonic.wem.portal2.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public final class UriTemplate
{
    private final String template;

    private final List<String> variableNames;

    private final Pattern matchPattern;

    public UriTemplate( final String template )
    {
        this.template = template;

        final UriTemplateParser parser = new UriTemplateParser( this.template );
        this.variableNames = ImmutableList.copyOf( parser.getVariables() );
        this.matchPattern = parser.getPattern();
    }

    public List<String> getVariableNames()
    {
        return this.variableNames;
    }

    public boolean matches( final String uri )
    {
        if ( uri == null )
        {
            return false;
        }

        final Matcher matcher = this.matchPattern.matcher( uri );
        return matcher.matches();
    }

    public final Map<String, String> match( final String uri )
    {
        if ( uri == null )
        {
            return Collections.emptyMap();
        }

        final Map<String, String> map = Maps.newHashMap();
        final Matcher matcher = this.matchPattern.matcher( uri );
        if ( matcher.find() )
        {
            for ( int i = 1; i <= matcher.groupCount(); i++ )
            {
                final String name = this.variableNames.get( i - 1 );
                final String value = matcher.group( i );
                map.put( name, value );
            }
        }

        return map;
    }

    @Override
    public String toString()
    {
        return this.template;
    }
}
