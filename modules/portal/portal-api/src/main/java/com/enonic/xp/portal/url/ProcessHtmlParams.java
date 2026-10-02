package com.enonic.xp.portal.url;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import com.enonic.xp.style.StyleDescriptors;

/**
 * Parameters of {@link PortalUrlService#processHtml(ProcessHtmlParams)}: the HTML fragment, and how
 * its internal links - {@code content://}, {@code image://} and {@code media://} - and macros are
 * processed.
 */
@NullMarked
public final class ProcessHtmlParams
    extends AbstractUrlParams<ProcessHtmlParams>
{
    private @Nullable String value;

    private @Nullable List<Integer> imageWidths;

    private @Nullable String imageSizes;

    private @Nullable Function<HtmlProcessorParams, String> customHtmlProcessor;

    private @Nullable Supplier<StyleDescriptors> customStyleDescriptorsCallback;

    private boolean processMacros = true;

    private @Nullable String baseUrl;

    /**
     * @return the HTML to process, or {@code null} when there is none
     */
    public @Nullable String getValue()
    {
        return this.value;
    }

    /**
     * @param value the HTML to process; {@code null} or empty processes to an empty string
     * @return these params
     */
    public ProcessHtmlParams value( final @Nullable String value )
    {
        this.value = Strings.emptyToNull( value );
        return this;
    }

    /**
     * @return widths of the {@code srcset} of images, or {@code null} for no {@code srcset}
     */
    public @Nullable List<Integer> getImageWidths()
    {
        return imageWidths;
    }

    /**
     * @return supplier of the style descriptors image styles are looked up in, or {@code null} to use those of the
     * system application and of the site of the current request
     */
    public @Nullable Supplier<StyleDescriptors> getCustomStyleDescriptorsCallback()
    {
        return customStyleDescriptorsCallback;
    }

    /**
     * Adds a {@code srcset} to every image the image API scales, with a URL of the image scaled to each of the widths.
     * The other images, served as stored, keep their {@code src} alone.
     *
     * @param imageWidths widths in pixels; {@code null} for no {@code srcset}
     * @return these params
     */
    public ProcessHtmlParams imageWidths( final @Nullable List<Integer> imageWidths )
    {
        this.imageWidths = imageWidths;
        return this;
    }

    /**
     * Sets where the image styles referenced by images are looked up.
     *
     * @param customStyleDescriptorsCallback supplier of the style descriptors; {@code null} to use those of the
     *                                       system application and of the site of the current request
     * @return these params
     */
    public ProcessHtmlParams customStyleDescriptorsCallback( final @Nullable Supplier<StyleDescriptors> customStyleDescriptorsCallback )
    {
        this.customStyleDescriptorsCallback = customStyleDescriptorsCallback;
        return this;
    }

    /**
     * @return the {@code sizes} attribute of images, or {@code null} for none
     */
    public @Nullable String getImageSizes()
    {
        return imageSizes;
    }

    /**
     * Sets the {@code sizes} attribute of every image, which goes with {@link #imageWidths(List)}.
     *
     * @param imageSizes value of the attribute; {@code null} or blank for none
     * @return these params
     */
    public ProcessHtmlParams imageSizes( final @Nullable String imageSizes )
    {
        this.imageSizes = imageSizes;
        return this;
    }

    /**
     * @return the custom HTML processor, or {@code null} when the default processing applies
     */
    public @Nullable Function<HtmlProcessorParams, String> getCustomHtmlProcessor()
    {
        return customHtmlProcessor;
    }

    /**
     * Replaces the default processing. The function receives the parsed document along with the default processors,
     * which it may apply to all elements or to single ones, and returns the resulting HTML.
     *
     * @param customHtmlProcessor the processor; {@code null} for the default processing
     * @return these params
     */
    public ProcessHtmlParams customHtmlProcessor( final @Nullable Function<HtmlProcessorParams, String> customHtmlProcessor )
    {
        this.customHtmlProcessor = customHtmlProcessor;
        return this;
    }

    /**
     * @return whether macros are processed
     */
    public boolean isProcessMacros()
    {
        return processMacros;
    }

    /**
     * Sets whether macros are processed. With a {@link #customHtmlProcessor(Function) custom HTML processor}, macros
     * are processed in the parsed document after it, and with {@code false} the HTML the processor returns is the
     * result. Defaults to {@code true}.
     *
     * @param processMacros whether to process macros
     * @return these params
     */
    public ProcessHtmlParams processMacros( final boolean processMacros )
    {
        this.processMacros = processMacros;
        return this;
    }

    /**
     * @return the mount base URL of media URLs, or {@code null} when not set
     * @see #baseUrl(String)
     */
    public @Nullable String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * Base URL of a mount where media URLs generated for the processed HTML live under
     * the "_" endpoint segment: {@code <baseUrl>/_/media:image/...}. Despite its generic
     * name it applies to media URLs only.
     * Trailing slash is appended if missing. Empty value is treated as unspecified.
     *
     * @deprecated configure where the media APIs are served with a virtual host mapping, or
     * {@code media.defaultBaseUrl} in {@code com.enonic.xp.portal.cfg}; use
     * {@link PortalUrlService#processHtmlParts(ProcessHtmlPartsParams)} to resolve every link from configuration.
     */
    @Deprecated
    public ProcessHtmlParams baseUrl( final @Nullable String baseUrl )
    {
        this.baseUrl = Strings.emptyToNull( baseUrl );
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
