package com.enonic.xp.portal.url;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.style.StyleDescriptors;


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

    private String imageBaseUrl;

    private String attachmentBaseUrl;

    private String pageBaseUrl;

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

    /**
     * Base URL of a mount where media URLs generated for the processed HTML live under
     * the "_" endpoint segment: {@code <baseUrl>/_/media:image/...}. Despite its generic
     * name it only affects media URLs - content links are not affected.
     * Trailing slash is appended if missing. Empty value is treated as unspecified.
     *
     * @deprecated use {@link #imageBaseUrl(String)} and {@link #attachmentBaseUrl(String)}
     * for media URLs (append {@code /_} to the value to keep the mount form produced by
     * this method) and {@link #pageBaseUrl(String)} for content links.
     */
    @Deprecated
    public ProcessHtmlParams baseUrl( final String baseUrl )
    {
        this.baseUrl = Strings.emptyToNull( baseUrl );
        return this;
    }

    public String getImageBaseUrl()
    {
        return imageBaseUrl;
    }

    /**
     * Base URL used verbatim as the API root of image URLs generated for the processed HTML:
     * {@code <imageBaseUrl>/media:image/...} - no "_" endpoint segment is added.
     * Takes precedence over {@code baseUrl}, which points at a mount where APIs
     * live under the "_" endpoint segment: {@code <baseUrl>/_/media:image/...}.
     * <p>
     * Image and attachment bases are separate because the two media APIs can be
     * mounted (and therefore served) at different locations.
     */
    public ProcessHtmlParams imageBaseUrl( final String imageBaseUrl )
    {
        this.imageBaseUrl = Strings.emptyToNull( imageBaseUrl );
        return this;
    }

    public String getAttachmentBaseUrl()
    {
        return attachmentBaseUrl;
    }

    /**
     * Base URL used verbatim as the API root of attachment URLs generated for the processed
     * HTML: {@code <attachmentBaseUrl>/media:attachment/...} - no "_" endpoint segment is added.
     * Takes precedence over {@code baseUrl}.
     */
    public ProcessHtmlParams attachmentBaseUrl( final String attachmentBaseUrl )
    {
        this.attachmentBaseUrl = Strings.emptyToNull( attachmentBaseUrl );
        return this;
    }

    public String getPageBaseUrl()
    {
        return pageBaseUrl;
    }

    public ProcessHtmlParams pageBaseUrl( final String pageBaseUrl )
    {
        this.pageBaseUrl = Strings.emptyToNull( pageBaseUrl );
        return this;
    }

    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "value", this.value );
        helper.add( "imageWidths", this.imageWidths );
        helper.add( "imageSizes", this.imageSizes );
        return helper.toString();
    }
}
