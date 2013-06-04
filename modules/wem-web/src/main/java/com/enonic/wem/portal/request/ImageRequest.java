package com.enonic.wem.portal.request;

import com.google.common.base.Objects;

public class ImageRequest
{
    private String key;

    private String filter;

    private String format;

    private String quality;

    private String background;

    private String label;

    public String getLabel()
    {
        return label;
    }

    public void setLabel( final String label )
    {
        this.label = label;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter( final String filter )
    {
        this.filter = filter;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat( final String format )
    {
        this.format = format;
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality( final String quality )
    {
        this.quality = quality;
    }

    public String getBackground()
    {
        return background;
    }

    public void setBackground( final String background )
    {
        this.background = background;
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper( this ).
            add( "key", key ).
            add( "label", label ).
            add( "filter", filter ).
            add( "format", format ).
            add( "quality", quality ).
            add( "background", background ).
            omitNullValues().
            toString();
    }

}
