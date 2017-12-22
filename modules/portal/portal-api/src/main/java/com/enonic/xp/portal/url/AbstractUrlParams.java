package com.enonic.xp.portal.url;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequest;


@Beta
public abstract class AbstractUrlParams<T extends AbstractUrlParams>
{
    private PortalRequest portalRequest;

    private String type = UrlTypeConstants.SERVER_RELATIVE;

    private Boolean includeContentPath;

    private final Multimap<String, String> params;

    public AbstractUrlParams()
    {
        this.params = HashMultimap.create();
    }

    public String getType()
    {
        return type;
    }

    public boolean mustIncludeContentPath()
    {
        return includeContentPath == null ? getDefaultContentPathInclusion() : includeContentPath.booleanValue();
    }

    public final Multimap<String, String> getParams()
    {
        return this.params;
    }

    public final PortalRequest getPortalRequest()
    {
        return this.portalRequest;
    }

    public final T type( final String value )
    {
        if ( Strings.isNullOrEmpty( value ) )
        {
            this.type = UrlTypeConstants.SERVER_RELATIVE;
        }
        else
        {
            this.type = value;
        }
        return typecastThis();
    }

    public final T includeContentPath( final Boolean value )
    {
        this.includeContentPath = value;
        return typecastThis();
    }

    protected boolean getDefaultContentPathInclusion()
    {
        return true;
    }

    public final T param( final String name, final Object value )
    {
        final String strValue = value != null ? value.toString() : null;
        this.params.put( name, strValue );
        return typecastThis();
    }

    public final T portalRequest( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
        return typecastThis();
    }

    public T setAsMap( Multimap<String, String> map )
    {
        type( singleValue( map, "_type" ) );
        final String contentPath = singleValue( map, "_contentPath" );
        includeContentPath( contentPath == null ? null : Boolean.valueOf( contentPath ) );
        return typecastThis();
    }

    protected static String singleValue( final Multimap<String, String> map, final String name )
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

    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        helper.omitNullValues();
        helper.add( "type", this.type );
        helper.add( "params", this.params );
    }

    @SuppressWarnings("unchecked")
    private T typecastThis()
    {
        return (T) this;
    }

    @Override
    public final String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        buildToString( helper );
        return helper.toString();
    }
}
