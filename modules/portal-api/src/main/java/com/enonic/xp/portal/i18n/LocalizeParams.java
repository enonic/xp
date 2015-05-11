package com.enonic.xp.portal.i18n;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalContext;

public class LocalizeParams
{
    private String key;

    private Locale locale;

    private Object[] params;

    private final Pattern VALUES_PATTERN = Pattern.compile( "^\\{.*\\}$" );

    private final PortalContext context;

    public LocalizeParams( final PortalContext context )
    {
        this.context = context;
    }

    private void key( final String key )
    {
        this.key = key;
    }

    public ModuleKey getModuleKey()
    {
        return this.context.getModule();
    }

    private void locale( final String locale )
    {
        this.locale = Strings.isNullOrEmpty( locale ) ? null : Locale.forLanguageTag( locale );
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

    public LocalizeParams setAsMap( final Multimap<String, String> map )
    {
        key( singleValue( map, "_key" ) );
        locale( singleValue( map, "_locale" ) );
        values( multipleValues( map, "_values" ) );
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
        return this.locale != null ? this.locale : this.getContext().getSite().getLanguage();
    }

    private PortalContext getContext()
    {
        return context;
    }

    public Object[] getParams()
    {
        return params;
    }
}
