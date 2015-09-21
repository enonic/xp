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
        //Processes and stores the new values that will be inserted into the Gradle Properties file
        final int lastIndexOfPoint = applicationName.lastIndexOf( '.' );
        String group = lastIndexOfPoint == -1 ? "" : applicationName.substring( 0, lastIndexOfPoint );
        String projectName = lastIndexOfPoint == -1 ? applicationName : applicationName.substring( lastIndexOfPoint + 1 );
        String displayName =
            projectName.isEmpty() ? "" : Character.toUpperCase( projectName.charAt( 0 ) ) + projectName.substring( 1 ) + " App";
        propertyValues[GradlePropertyKey.APP_NAME_PROPERTY_KEY.ordinal()] = applicationName;
        propertyValues[GradlePropertyKey.VERSION_PROPERTY_KEY.ordinal()] = version;
        propertyValues[GradlePropertyKey.GROUP_PROPERTY_KEY.ordinal()] = group;
        propertyValues[GradlePropertyKey.PROJECT_NAME_PROPERTY_KEY.ordinal()] = projectName;
        propertyValues[GradlePropertyKey.DISPLAY_NAME_PROPERTY_KEY.ordinal()] = displayName;
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        //For each line, if this line is a property to handle
        final Matcher matcher = PROPERTY_PATTERN.matcher( line );
        if ( matcher.find() )
        {
            // Stores the property key and new value in the updated content
            final String propertyKeyValue = matcher.group( 2 );
            final GradlePropertyKey gradlePropertyKey = GradlePropertyKey.fromValue( propertyKeyValue );
            content.append( matcher.group( 1 ) ).
                append( propertyValues[gradlePropertyKey.ordinal()] ).
                append( "\n" );

            // Marks this property as already handled
            propertyFlags[gradlePropertyKey.ordinal()] = true;
        }
        else
        {
            // Else, stores the line as it is in the updated content
            content.append( line ).append( "\n" );
        }
        return true;
    }

    @Override
    public String getResult()
    {
        // For each property to handle
        for ( GradlePropertyKey gradlePropertyKey : GradlePropertyKey.values() )
        {
            // If this property has not bee updated
            if ( !propertyFlags[gradlePropertyKey.ordinal()] )
            {
                // Appends this property and its value to the end of the updated content
                content.append( '\n' ).append( gradlePropertyKey.getValue() ).append( " = " ).append(
                    propertyValues[gradlePropertyKey.ordinal()] );
            }
        }

        // Returns the updated content
        return content.toString();
    }

    private static Pattern generatePropertyPattern()
    {
        //Builds in this method the following regular expression: ^(\s*(group|version|projectName|appName|displayName)\s*=\s*)
        StringBuilder propertyPatternBuilder = new StringBuilder();

        //Handles whitespace characters at the beginning of the line
        propertyPatternBuilder.append( "^(\\s*(" );

        //For each property key that has to be handled
        final GradlePropertyKey[] gradlePropertyKeys = GradlePropertyKey.values();
        for ( int i = 0; i < gradlePropertyKeys.length; i++ )
        {
            //Add this property key value
            if ( i > 0 )
            {
                propertyPatternBuilder.append( '|' );
            }
            propertyPatternBuilder.append( gradlePropertyKeys[i].getValue() );

        }

        //Handles whitespace characters around the equality sign
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
