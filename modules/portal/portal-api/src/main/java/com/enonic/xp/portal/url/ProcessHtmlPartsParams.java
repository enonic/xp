package com.enonic.xp.portal.url;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.Strings;

import com.enonic.xp.style.StyleDescriptors;

/**
 * Parameters of {@link PortalUrlService#processHtmlParts(ProcessHtmlPartsParams)}: the HTML fragment, the site or
 * project it belongs to, and how its images and macros are processed.
 */
@NullMarked
public final class ProcessHtmlPartsParams
{
    private final @Nullable String value;

    private final @Nullable UrlBase base;

    private final @Nullable List<Integer> imageWidths;

    private final @Nullable String imageSizes;

    private final @Nullable Function<HtmlProcessorParams, String> customHtmlProcessor;

    private final @Nullable Supplier<StyleDescriptors> customStyleDescriptorsCallback;

    private final boolean processMacros;

    private ProcessHtmlPartsParams( final Builder builder )
    {
        this.value = builder.value;
        this.base = builder.base;
        this.imageWidths = builder.imageWidths == null ? null : List.copyOf( builder.imageWidths );
        this.imageSizes = builder.imageSizes;
        this.customHtmlProcessor = builder.customHtmlProcessor;
        this.customStyleDescriptorsCallback = builder.customStyleDescriptorsCallback;
        this.processMacros = builder.processMacros;
    }

    /**
     * @return the HTML to process, or {@code null} when there is none
     */
    public @Nullable String getValue()
    {
        return value;
    }

    /**
     * @return the site or project the HTML belongs to, or {@code null} for the project of the current context
     * @see Builder#base(UrlBase)
     */
    public @Nullable UrlBase getBase()
    {
        return base;
    }

    /**
     * @return widths of the {@code srcset} of images, or {@code null} for no {@code srcset}
     */
    public @Nullable List<Integer> getImageWidths()
    {
        return imageWidths;
    }

    /**
     * @return the {@code sizes} attribute of images, or {@code null} for none
     */
    public @Nullable String getImageSizes()
    {
        return imageSizes;
    }

    /**
     * @return the custom HTML processor, or {@code null} when the default processing applies
     */
    public @Nullable Function<HtmlProcessorParams, String> getCustomHtmlProcessor()
    {
        return customHtmlProcessor;
    }

    /**
     * @return supplier of the style descriptors image styles are looked up in, or {@code null} to use those of the
     * system application and of the applications of the site or project the HTML belongs to
     */
    public @Nullable Supplier<StyleDescriptors> getCustomStyleDescriptorsCallback()
    {
        return customStyleDescriptorsCallback;
    }

    /**
     * @return whether macros are processed
     */
    public boolean isProcessMacros()
    {
        return processMacros;
    }

    /**
     * @return a new builder
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder of {@link ProcessHtmlPartsParams}.
     */
    public static final class Builder
    {
        private @Nullable String value;

        private @Nullable UrlBase base;

        private @Nullable List<Integer> imageWidths;

        private @Nullable String imageSizes;

        private @Nullable Function<HtmlProcessorParams, String> customHtmlProcessor;

        private @Nullable Supplier<StyleDescriptors> customStyleDescriptorsCallback;

        private boolean processMacros = true;

        private Builder()
        {
        }

        /**
         * @param value the HTML to process; {@code null} or empty processes to an empty string
         * @return this builder
         */
        public Builder value( final @Nullable String value )
        {
            this.value = Strings.emptyToNull( value );
            return this;
        }

        /**
         * Sets the site - or the project - the HTML belongs to. Its configuration decides the Base URL, what content
         * links are relative to, and the applications image styles come from; contents are looked up in its project
         * and branch.
         *
         * @param base the base, resolved by {@link PortalUrlService#urlBase(UrlBaseParams)}; {@code null} for the
         *             project of the current context
         * @return this builder
         * @see PageUrlPartsParams.Builder#setBase(UrlBase)
         */
        public Builder base( final @Nullable UrlBase base )
        {
            this.base = base;
            return this;
        }

        /**
         * Adds a {@code srcset} to every image the image API scales, with an entry for the image scaled to each of the
         * widths. The other images, served as stored, keep their {@code src} alone.
         *
         * @param imageWidths widths in pixels; {@code null} for no {@code srcset}
         * @return this builder
         */
        public Builder imageWidths( final @Nullable List<Integer> imageWidths )
        {
            this.imageWidths = imageWidths;
            return this;
        }

        /**
         * Sets the {@code sizes} attribute of every image that gets a {@code srcset} from {@link #imageWidths(List)}.
         *
         * @param imageSizes value of the attribute; {@code null} or blank for none
         * @return this builder
         */
        public Builder imageSizes( final @Nullable String imageSizes )
        {
            this.imageSizes = imageSizes;
            return this;
        }

        /**
         * Replaces the default processing. The function receives the parsed document along with the default
         * processors, which it may apply to all elements or to single ones, and returns the resulting HTML.
         *
         * @param customHtmlProcessor the processor; {@code null} for the default processing
         * @return this builder
         */
        public Builder customHtmlProcessor( final @Nullable Function<HtmlProcessorParams, String> customHtmlProcessor )
        {
            this.customHtmlProcessor = customHtmlProcessor;
            return this;
        }

        /**
         * Sets where the image styles referenced by images are looked up.
         *
         * @param customStyleDescriptorsCallback supplier of the style descriptors; {@code null} to use those of the
         *                                       system application and of the applications of the site or project
         * @return this builder
         */
        public Builder customStyleDescriptorsCallback( final @Nullable Supplier<StyleDescriptors> customStyleDescriptorsCallback )
        {
            this.customStyleDescriptorsCallback = customStyleDescriptorsCallback;
            return this;
        }

        /**
         * Sets whether macros are processed, in the processed HTML: the default processing's, or the HTML a
         * {@link #customHtmlProcessor(Function) custom HTML processor} returns. Each macro an application of the base
         * provides is replaced by a placeholder and gets an entry in {@link ProcessedHtml#macros()}; other macros stay
         * as written. Without processing, every macro stays as written. Defaults to {@code true}.
         *
         * @param processMacros whether to process macros
         * @return this builder
         */
        public Builder processMacros( final boolean processMacros )
        {
            this.processMacros = processMacros;
            return this;
        }

        /**
         * @return the params
         */
        public ProcessHtmlPartsParams build()
        {
            return new ProcessHtmlPartsParams( this );
        }
    }
}
