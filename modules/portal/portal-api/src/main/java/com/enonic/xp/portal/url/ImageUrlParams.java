package com.enonic.xp.portal.url;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class ImageUrlParams
    extends AbstractUrlParams<ImageUrlParams>
{
    private String id;

    private String path;

    private String background;

    private Integer quality;

    private String filter;

    private String format;

    private String scale;

    public String getId()
    {
        return this.id;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getBackground()
    {
        return this.background;
    }

    public Integer getQuality()
    {
        return this.quality;
    }

    public String getFilter()
    {
        return this.filter;
    }

    public String getFormat()
    {
        return this.format;
    }

    public String getScale()
    {
        return this.scale;
    }

    public ImageUrlParams id( final String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    public ImageUrlParams path( final String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    public ImageUrlParams quality( final Integer value )
    {
        this.quality = value;
        return this;
    }

    public ImageUrlParams quality( final String value )
    {
        return isNullOrEmpty( value ) ? this : quality( Integer.valueOf( value ) );
    }

    public ImageUrlParams format( final String value )
    {
        this.format = Strings.emptyToNull( value );
        return this;
    }

    public ImageUrlParams background( final String value )
    {
        this.background = Strings.emptyToNull( value );
        return this;
    }

    public ImageUrlParams filter( final String value )
    {
        this.filter = Strings.emptyToNull( value );
        return this;
    }

    public ImageUrlParams scale( final String value )
    {
        this.scale = Strings.emptyToNull( value );
        return this;
    }

    @Override
    public ImageUrlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        id( singleValue( map, "_id" ) );
        path( singleValue( map, "_path" ) );
        format( singleValue( map, "_format" ) );
        quality( singleValue( map, "_quality" ) );
        filter( singleValue( map, "_filter" ) );
        background( singleValue( map, "_background" ) );
        scale( singleValue( map, "_scale" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "format", this.format );
        helper.add( "quality", this.quality );
        helper.add( "filter", this.filter );
        helper.add( "background", this.background );
        helper.add( "scale", this.scale );
    }
}
