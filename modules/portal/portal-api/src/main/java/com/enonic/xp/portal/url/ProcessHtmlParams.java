package com.enonic.xp.portal.url;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.style.StyleDescriptors;

@PublicApi
public final class ProcessHtmlParams
    extends AbstractUrlParams<ProcessHtmlParams>
{
    private String value;

    private List<Integer> imageWidths;

    private String imageSizes;

    private Function<HtmlProcessorParams, String> customHtmlProcessor;

    private Supplier<StyleDescriptors> customStyleDescriptorsCallback;

    private boolean processMacros = true;

    private String baseUrl;

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

    public Supplier<StyleDescriptors> getCustomStyleDescriptorsCallback()
    {
        return customStyleDescriptorsCallback;
    }

    public ProcessHtmlParams imageWidths( final List<Integer> imageWidths )
    {
        this.imageWidths = imageWidths;
        return this;
    }

    public ProcessHtmlParams customStyleDescriptorsCallback( final Supplier<StyleDescriptors> customStyleDescriptorsCallback )
    {
        this.customStyleDescriptorsCallback = customStyleDescriptorsCallback;
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

    public Function<HtmlProcessorParams, String> getCustomHtmlProcessor()
    {
        return customHtmlProcessor;
    }

    public ProcessHtmlParams customHtmlProcessor( final Function<HtmlProcessorParams, String> customHtmlProcessor )
    {
        this.customHtmlProcessor = customHtmlProcessor;
        return this;
    }

    public boolean isProcessMacros()
    {
        return processMacros;
    }

    public ProcessHtmlParams processMacros( final boolean processMacros )
    {
        this.processMacros = processMacros;
        return this;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public ProcessHtmlParams baseUrl( final String baseUrl )
    {
        this.baseUrl = baseUrl;
        return this;
    }

    @Override
    public ProcessHtmlParams setAsMap( final Multimap<String, String> map )
    {
        super.setAsMap( map );
        value( singleValue( map, "_value" ) );
        imageWidths( Objects.requireNonNullElse( map.removeAll( "_imageWidths" ), List.<String>of() )
                         .stream()
                         .map( Integer::parseInt )
                         .collect( Collectors.toUnmodifiableList() ) );
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
