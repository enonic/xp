package com.enonic.xp.portal.impl.url;

import java.util.HashMap;
import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;
import com.enonic.xp.style.StyleDescriptors;

/**
 * The image styles a rich text image can name in its {@code style} parameter: {@code <application>:<name>} for the
 * style of that application, or a name alone for the first style of that name.
 */
final class ImageStyles
{
    private final Map<ApplicationKey, Map<String, Resolved>> byApplication = new HashMap<>();

    private final Map<String, Resolved> byName = new HashMap<>();

    ImageStyles( final StyleDescriptors styleDescriptors )
    {
        for ( final StyleDescriptor styleDescriptor : styleDescriptors )
        {
            styleDescriptor.getElements()
                .stream()
                .filter( ImageStyle.class::isInstance )
                .map( ImageStyle.class::cast )
                .map( style -> new Resolved( styleDescriptor.getApplicationKey(), style ) )
                .forEach( resolved -> {
                    byApplication.computeIfAbsent( resolved.application(), key -> new HashMap<>() )
                        .putIfAbsent( resolved.style().getName(), resolved );
                    byName.putIfAbsent( resolved.style().getName(), resolved );
                } );
        }
    }

    /**
     * @param reference {@code <application>:<name>}, or a name alone
     * @return the style with the application it belongs to, or {@code null} when there is none: a qualified reference
     * names the style of its application only, and is otherwise read as a name
     */
    Resolved get( final String reference )
    {
        final int separator = reference.indexOf( ':' );
        if ( separator > 0 )
        {
            final Map<String, Resolved> styles = byApplication.get( applicationKey( reference.substring( 0, separator ) ) );
            if ( styles != null )
            {
                return styles.get( reference.substring( separator + 1 ) );
            }
        }
        return byName.get( reference );
    }

    private static ApplicationKey applicationKey( final String value )
    {
        try
        {
            return ApplicationKey.from( value );
        }
        catch ( IllegalArgumentException e )
        {
            return null;
        }
    }

    /**
     * A style, with the application it belongs to.
     */
    record Resolved(ApplicationKey application, ImageStyle style)
    {
    }
}
