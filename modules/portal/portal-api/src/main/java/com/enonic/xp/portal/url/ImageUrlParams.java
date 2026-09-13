package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Mutable parameters for {@link PortalUrlService#imageUrl(ImageUrlParams)}.
 * The image may be identified by content ID or path. Scale is required, and a selected
 * style supplies filter, quality and background settings. Except for parsing a textual
 * quality value, validation occurs when the URL service consumes these parameters.
 */
@NullMarked
public final class ImageUrlParams
    extends AbstractUrlParams<ImageUrlParams>
{
    private @Nullable String id;

    private @Nullable String path;

    private @Nullable String background;

    private @Nullable Integer quality;

    private @Nullable String filter;

    private @Nullable String format;

    private @Nullable String scale;

    private @Nullable String style;

    private @Nullable String projectName;

    private @Nullable String branch;

    private @Nullable String baseUrl;

    private @Nullable String mediaBaseUrl;

    /**
     * Returns the selected image content identifier.
     *
     * @return the content identifier
     */
    public @Nullable String getId()
    {
        return this.id;
    }

    /**
     * Returns the selected image content path.
     *
     * @return the content path
     */
    public @Nullable String getPath()
    {
        return this.path;
    }

    /**
     * Returns the explicit background color override.
     *
     * @return the hexadecimal RGB color
     */
    public @Nullable String getBackground()
    {
        return this.background;
    }

    /**
     * Returns the explicit encoder quality override.
     *
     * @return the encoder quality
     */
    public @Nullable Integer getQuality()
    {
        return this.quality;
    }

    /**
     * Returns the explicit filter override.
     *
     * @return the filter specification
     */
    public @Nullable String getFilter()
    {
        return this.filter;
    }

    /**
     * Returns the explicit output format.
     *
     * @return the requested filename extension
     */
    public @Nullable String getFormat()
    {
        return this.format;
    }

    /**
     * Returns the predefined image style selected for this request.
     *
     * @return the fully qualified {@code application:name} key
     */
    public @Nullable String getStyle()
    {
        return style;
    }

    /**
     * Selects a predefined image style. A styled request cannot also set quality, filter
     * or background overrides. Scale and format remain separate request parameters.
     *
     * @param value the fully qualified {@code application:name} key
     * @return this parameter object
     */
    public ImageUrlParams style( final @Nullable String value )
    {
        this.style = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Returns the requested scaling operation.
     *
     * @return the scaling expression
     */
    public @Nullable String getScale()
    {
        return this.scale;
    }

    /**
     * Returns the explicitly selected source project.
     *
     * @return the project name
     */
    public @Nullable String getProjectName()
    {
        return projectName;
    }

    /**
     * Returns the explicitly selected source branch.
     *
     * @return the branch name
     */
    public @Nullable String getBranch()
    {
        return branch;
    }

    /**
     * Returns the explicit mount base URL.
     *
     * @return the mount base URL
     */
    public @Nullable String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * Selects the image by content identifier.
     *
     * @param value the source content identifier
     * @return this parameter object
     */
    public ImageUrlParams id( final @Nullable String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Selects the image by content path.
     *
     * @param value the source content path
     * @return this parameter object
     */
    public ImageUrlParams path( final @Nullable String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Sets the encoder quality for an unstyled request.
     *
     * @param value a value from 0 through 100; the default is 85
     * @return this parameter object
     */
    public ImageUrlParams quality( final @Nullable Integer value )
    {
        this.quality = value;
        return this;
    }

    /**
     * Parses a decimal encoder quality for an unstyled request.
     * Empty input leaves the current quality unchanged. Range validation is deferred until URL generation.
     *
     * @param value the quality expressed as a decimal integer from 0 through 100
     * @return this parameter object
     * @throws NumberFormatException if a non-empty value is not a valid decimal integer
     */
    public ImageUrlParams quality( final @Nullable String value )
    {
        return isNullOrEmpty( value ) ? this : quality( Integer.valueOf( value ) );
    }

    /**
     * Selects the output format independently of the style. WebP and AVIF require a style.
     * When no format is selected, the source format is retained.
     *
     * @param value an output extension such as {@code jpeg}, {@code png}, {@code gif}, {@code webp} or {@code avif}
     * @return this parameter object
     */
    public ImageUrlParams format( final @Nullable String value )
    {
        this.format = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Sets the background for an unstyled image when flattening transparency.
     *
     * @param value one to six hexadecimal RGB digits, optionally prefixed by {@code 0x}; the default is white
     * @return this parameter object
     */
    public ImageUrlParams background( final @Nullable String value )
    {
        this.background = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Sets the filter specification for an unstyled image.
     *
     * @param value the filter specification
     * @return this parameter object
     */
    public ImageUrlParams filter( final @Nullable String value )
    {
        this.filter = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Sets the required scaling operation independently of the style.
     * Examples include {@code width(640)} and {@code block(640,480)}.
     *
     * @param value the scaling expression
     * @return this parameter object
     */
    public ImageUrlParams scale( final @Nullable String value )
    {
        this.scale = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Selects the source project explicitly instead of resolving it from the request or current context.
     *
     * @param projectName the project name
     * @return this parameter object
     */
    public ImageUrlParams projectName( final @Nullable String projectName )
    {
        this.projectName = projectName;
        return this;
    }

    /**
     * Selects the source branch explicitly instead of resolving it from the request or current context.
     *
     * @param branch the branch name
     * @return this parameter object
     */
    public ImageUrlParams branch( final @Nullable String branch )
    {
        this.branch = branch;
        return this;
    }

    /**
     * Sets a mount base URL, producing URLs in the form {@code <baseUrl>/_/media:image/...}.
     *
     * @param baseUrl the mount base URL
     * @return this parameter object
     * @deprecated configure {@code media.defaultBaseUrl} in {@code com.enonic.xp.portal.cfg}
     *     or a Base URL on the site instead. Use {@link #mediaBaseUrl(String)} to address the API root directly.
     */
    @Deprecated
    public ImageUrlParams baseUrl( final @Nullable String baseUrl )
    {
        this.baseUrl = Strings.emptyToNull( baseUrl );
        return this;
    }

    /**
     * Returns the explicit media API root.
     *
     * @return the media API root
     */
    public @Nullable String getMediaBaseUrl()
    {
        return mediaBaseUrl;
    }

    /**
     * Sets the API root used verbatim: {@code <mediaBaseUrl>/media:image/...}.
     * This takes precedence over {@link #baseUrl(String)}.
     *
     * @param mediaBaseUrl the media API root
     * @return this parameter object
     */
    public ImageUrlParams mediaBaseUrl( final @Nullable String mediaBaseUrl )
    {
        this.mediaBaseUrl = Strings.emptyToNull( mediaBaseUrl );
        return this;
    }

    /**
     * Returns a diagnostic representation of the configured URL parameters.
     *
     * @return the parameter representation
     */
    @Override
    public String toString()
    {
        final MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper( this );
        helper.omitNullValues();
        helper.add( "type", this.getType() );
        helper.add( "params", this.getParams() );
        helper.add( "id", this.id );
        helper.add( "path", this.path );
        helper.add( "project", this.projectName );
        helper.add( "branch", this.branch );
        helper.add( "baseUrl", this.baseUrl );
        helper.add( "format", this.format );
        helper.add( "quality", this.quality );
        helper.add( "filter", this.filter );
        helper.add( "background", this.background );
        helper.add( "scale", this.scale );
        helper.add( "style", this.style );
        return helper.toString();
    }
}
