package com.enonic.xp.toolbox.app;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class GradlePropertiesProcessor
{
    private static final String GROUP_PROPERTY_KEY = "group";

    private static final String VERSION_PROPERTY_KEY = "version";

    private static final String PROJECT_NAME_PROPERTY_KEY = "projectName";

    private static final String APP_NAME_PROPERTY_KEY = "appName";

    private static final String DISPLAY_NAME_PROPERTY_KEY = "displayName";

    private static final Pattern PROPERTY_PATTERN = Pattern.compile(
        "^(\\s*(" + GROUP_PROPERTY_KEY + "|" + VERSION_PROPERTY_KEY + "|" + PROJECT_NAME_PROPERTY_KEY + "|" + APP_NAME_PROPERTY_KEY + "|" +
            DISPLAY_NAME_PROPERTY_KEY + ")\\s*=\\s*)" );

    private String groupPropertyValue;

    private String versionPropertyValue;

    private String projectNamePropertyValue;

    private String appNamePropertyValue;

    private String displayNamePropertyValue;

    public GradlePropertiesProcessor( final String applicationName, final String version )
    {
        final int lastIndexOfPoint = applicationName.lastIndexOf( '.' );

        this.groupPropertyValue = lastIndexOfPoint == -1 ? "" : applicationName.substring( 0, lastIndexOfPoint );
        this.versionPropertyValue = version;
        this.projectNamePropertyValue = lastIndexOfPoint == -1 ? applicationName : applicationName.substring( lastIndexOfPoint + 1 );
        this.appNamePropertyValue = applicationName;
        this.displayNamePropertyValue = this.projectNamePropertyValue.isEmpty()
            ? ""
            : Character.toUpperCase( this.projectNamePropertyValue.charAt( 0 ) ) + this.projectNamePropertyValue.substring( 1 ) + " App";
    }

    public List<String> process( final List<String> lines )
    {
        return lines.stream().
            map( line -> process( line ) ).
            collect( Collectors.toList() );
    }

    private String process( String line )
    {
        final Matcher matcher = PROPERTY_PATTERN.matcher( line );
        if ( matcher.find() )
        {
            final String propertyValuePrefix = matcher.group( 1 );
            final String propertyKey = matcher.group( 2 );
            final String newPropertyValue = getNewPropertyValue( propertyKey );

            return propertyValuePrefix + newPropertyValue;
        }
        return line;
    }

    private String getNewPropertyValue( final String propertyKey )
    {
        switch ( propertyKey )
        {
            case GROUP_PROPERTY_KEY:
                return groupPropertyValue;
            case VERSION_PROPERTY_KEY:
                return versionPropertyValue;
            case PROJECT_NAME_PROPERTY_KEY:
                return projectNamePropertyValue;
            case APP_NAME_PROPERTY_KEY:
                return appNamePropertyValue;
            case DISPLAY_NAME_PROPERTY_KEY:
                return displayNamePropertyValue;
        }
        return "";
    }
}
