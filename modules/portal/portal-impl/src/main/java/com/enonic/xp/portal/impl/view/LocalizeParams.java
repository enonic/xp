package com.enonic.xp.portal.impl.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;

class LocalizeParams
{
    private String key;

    private Locale locale;

    private String application;

    private Object[] params;

    private final Pattern VALUES_PATTERN = Pattern.compile( "^\\{.*\\}$" );

    private final PortalRequest request;

    public LocalizeParams( final PortalRequest request )
    {
        this.request = request;
    }

    private void key( final String key )
    {
        this.key = key;
    }

    public ApplicationKey getApplicationKey()
    {
        if ( this.application != null )
        {
            return ApplicationKey.from( this.application );
        }
        if ( this.request == null )
        {
            throw new IllegalArgumentException( "Expected application parameter for localize function" );
        }
        return this.request.getApplicationKey();
    }

    private void locale( final String locale )
    {
        this.locale = Strings.isNullOrEmpty( locale ) ? resolveLocale() : Locale.forLanguageTag( locale );
    }

    private Locale resolveLocale()
    {
        if ( request.getSite().getLanguage() != null )
        {
            return request.getSite().getLanguage();
        }

        return null;
    }

    private void values( final Collection<String> values )
    {
        if ( values.size() == 1 && VALUES_PATTERN.matcher( values.iterator().next() ).find() )
        {
            parseValues( values.iterator().next() );
        }
        else
        {
            handleArray( values );
        }
    }

    private void handleArray( final Collection<String> values )
    {
        final List<Object> params = Lists.newArrayList();

        for ( final String value : values )
        {
            params.add( value );
        }

        this.params = params.toArray();
    }

    private void parseValues( final String valuesAsString )
    {
        if ( Strings.isNullOrEmpty( valuesAsString ) )
        {
            this.params = Lists.newArrayList().toArray();
            return;
        }

        if ( !VALUES_PATTERN.matcher( valuesAsString ).find() )
        {
            throw new IllegalArgumentException( "Wrong format on values-parameter: " + valuesAsString );
        }

        final String argumentList = valuesAsString.substring( 1, valuesAsString.length() - 1 );

        this.params = Arrays.asList( argumentList.split( "," ) ).toArray();
    }


    public LocalizeParams application( final String value )
    {
        this.application = Strings.emptyToNull( value );
        return this;
    }

    public LocalizeParams setAsMap( final Multimap<String, String> map )
    {
        key( singleValue( map, "_key" ) );
        locale( singleValue( map, "_locale" ) );
        values( multipleValues( map, "_values" ) );
        application( singleValue( map, "_application" ) );
        return this;
    }

    private static Collection<String> multipleValues( final Multimap<String, String> map, final String name )
    {
        return map.removeAll( name );
    }

    private static String singleValue( final Multimap<String, String> map, final String name )
    {
        final Collection<String> values = map.removeAll( name );
        if ( values == null )
        {
            return null;
        }

        if ( values.isEmpty() )
        {
            return null;
        }

        return values.iterator().next();
    }

    public String getKey()
    {
        return key;
    }


    public Locale getLocale()
    {
        return locale;
    }

    private PortalRequest getRequest()
    {
        return request;
    }

    public Object[] getParams()
    {
        return params;
    }
}
