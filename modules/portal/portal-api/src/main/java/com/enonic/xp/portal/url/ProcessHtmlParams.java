package com.enonic.xp.portal.url;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class ProcessHtmlParams
    extends AbstractUrlParams<ProcessHtmlParams>
{
    private String value;

    private List<Integer> imageWidths;

    private String imageSizes;

    private HtmlImageProcessor imageProcessor;

    private HtmlLinkProcessor linkProcessor;

    private MacrosProcessor macrosProcessor;

    private HtmlPostProcessor htmlPostProcessor;

    public String getValue()
    {
        return this.value;
    }

    public ProcessHtmlParams value( final String value )
    {
        this.value = Strings.emptyToNull( value );
        return this;
    }

    public List<Integer> getImageWidths()
    {
        return imageWidths;
    }

    public ProcessHtmlParams imageWidths( final List<Integer> imageWidths )
    {
        this.imageWidths = imageWidths;
        return this;
    }

    public String getImageSizes()
    {
        return imageSizes;
    }

    public ProcessHtmlParams imageSizes( final String imageSizes )
    {
        this.imageSizes = imageSizes;
        return this;
    }

    public HtmlImageProcessor getImageProcessor()
    {
        return imageProcessor;
    }

    public ProcessHtmlParams setImageProcessor( final HtmlImageProcessor imageProcessor )
    {
        this.imageProcessor = imageProcessor;
        return this;
    }

    public HtmlLinkProcessor getLinkProcessor()
    {
        return linkProcessor;
    }

    public ProcessHtmlParams setLinkProcessor( final HtmlLinkProcessor linkProcessor )
    {
        this.linkProcessor = linkProcessor;
        return this;
    }

    public MacrosProcessor getMacrosProcessor()
    {
        return macrosProcessor;
    }

    public ProcessHtmlParams setMacrosProcessor( final MacrosProcessor macrosProcessor )
    {
        this.macrosProcessor = macrosProcessor;
        return this;
    }

    public HtmlPostProcessor getHtmlPostProcessor()
    {
        return htmlPostProcessor;
    }

    public ProcessHtmlParams setHtmlPostProcessor( final HtmlPostProcessor htmlPostProcessor )
    {
        this.htmlPostProcessor = htmlPostProcessor;
        return this;
    }

    @Override
    public ProcessHtmlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        value( singleValue( map, "_value" ) );
        imageWidths( Objects.requireNonNullElse( map.removeAll( "_imageWidths" ), List.<String>of() ).
            stream().
            map( Integer::parseInt ).
            collect( Collectors.toUnmodifiableList() ) );
        imageSizes( singleValue( map, "_imageSizes" ) );
        getParams().putAll( map );
        return this;
    }

    @Override
    protected void buildToString( final MoreObjects.ToStringHelper helper )
    {
        super.buildToString( helper );
        helper.add( "value", this.value );
        helper.add( "imageWidths", this.imageWidths );
        helper.add( "imageSizes", this.imageSizes );
    }
}
