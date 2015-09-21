package com.enonic.xp.toolbox.app;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.LineProcessor;

/**
 * Created by gri on 21/09/15.
 */
public class GradlePropertiesProcessor
    implements LineProcessor<String>
{
    private static Pattern PROPERTY_PATTERN = generatePropertyPattern();

    private StringBuilder content = new StringBuilder();

    private String[] propertyValues = new String[GradlePropertyKey.values().length];

    private boolean[] propertyFlags = new boolean[GradlePropertyKey.values().length];

    public GradlePropertiesProcessor( final String applicationName, final String version )
    {
        propertyValues[GradlePropertyKey.APP_NAME_PROPERTY_KEY.ordinal()] = applicationName;
        propertyValues[GradlePropertyKey.VERSION_PROPERTY_KEY.ordinal()] = version;

        final int lastIndexOfPoint = applicationName.lastIndexOf( '.' );
        String group = lastIndexOfPoint == -1 ? "" : applicationName.substring( 0, lastIndexOfPoint );
        String projectName = lastIndexOfPoint == -1 ? applicationName : applicationName.substring( lastIndexOfPoint + 1 );
        String displayName =
            projectName.isEmpty() ? "" : Character.toUpperCase( projectName.charAt( 0 ) ) + projectName.substring( 1 ) + " App";
        propertyValues[GradlePropertyKey.GROUP_PROPERTY_KEY.ordinal()] = group;
        propertyValues[GradlePropertyKey.PROJECT_NAME_PROPERTY_KEY.ordinal()] = projectName;
        propertyValues[GradlePropertyKey.DISPLAY_NAME_PROPERTY_KEY.ordinal()] = displayName;
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        final Matcher matcher = PROPERTY_PATTERN.matcher( line );
        if ( matcher.find() )
        {
            final String propertyKeyValue = matcher.group( 2 );
            final GradlePropertyKey gradlePropertyKey = GradlePropertyKey.fromValue( propertyKeyValue );

            content.append( matcher.group( 1 ) ).
                append( propertyValues[gradlePropertyKey.ordinal()] ).
                append( "\n" );

            propertyFlags[gradlePropertyKey.ordinal()] = true;
        }
        else
        {
            content.append( line ).append( "\n" );
        }
        return true;
    }

    @Override
    public String getResult()
    {
        for ( GradlePropertyKey gradlePropertyKey : GradlePropertyKey.values() )
        {
            if ( !propertyFlags[gradlePropertyKey.ordinal()] )
            {
                content.append( '\n' ).append( gradlePropertyKey.getValue() ).append( " = " ).append(
                    propertyValues[gradlePropertyKey.ordinal()] );
            }
        }

        return content.toString();
    }

    private static Pattern generatePropertyPattern()
    {
        StringBuilder propertyPatternBuilder = new StringBuilder();
        propertyPatternBuilder.append( "^(\\s*(" );
        final GradlePropertyKey[] gradlePropertyKeys = GradlePropertyKey.values();
        for ( int i = 0; i < gradlePropertyKeys.length; i++ )
        {
            if ( i > 0 )
            {
                propertyPatternBuilder.append( '|' );
            }
            propertyPatternBuilder.append( gradlePropertyKeys[i].getValue() );

        }
        propertyPatternBuilder.append( ")\\s*=\\s*)" );

        return Pattern.compile( propertyPatternBuilder.toString() );
    }

    private enum GradlePropertyKey

    {
        GROUP_PROPERTY_KEY( "group" ),
        VERSION_PROPERTY_KEY( "version" ),
        PROJECT_NAME_PROPERTY_KEY( "projectName" ),
        APP_NAME_PROPERTY_KEY( "appName" ),
        DISPLAY_NAME_PROPERTY_KEY( "displayName" );

        private String value;

        GradlePropertyKey( final String value )
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public static GradlePropertyKey fromValue( final String value )
        {
            for ( GradlePropertyKey gradlePropertyKey : values() )
            {
                if ( gradlePropertyKey.getValue().equals( value ) )
                {
                    return gradlePropertyKey;
                }
            }
            return null;
        }
    }
}
