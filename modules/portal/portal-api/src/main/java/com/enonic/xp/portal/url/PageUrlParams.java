package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

/**
 * Parameters of a page URL: the content it addresses, by {@link #id(String) id} or
 * {@link #path(String) path}. The project and branch are those of the request or the context.
 *
 * @see PortalUrlService#pageUrl(PageUrlParams)
 * @see PageUrlPartsParams
 */
@NullMarked
public final class PageUrlParams
    extends AbstractUrlParams<PageUrlParams>
{
    private @Nullable String id;

    private @Nullable String path;

    private @Nullable String projectName;

    private @Nullable String branch;

    /**
     * @return id of the content the URL addresses, or {@code null} when it is named by path
     */
    public @Nullable String getId()
    {
        return this.id;
    }

    /**
     * @return path of the content the URL addresses, or {@code null} when it is named by id
     */
    public @Nullable String getPath()
    {
        return this.path;
    }

    /**
     * @return project of the content, or {@code null} to take it from the context
     */
    public @Nullable String getProjectName()
    {
        return projectName;
    }

    /**
     * @return branch of the content, or {@code null} to take it from the context
     */
    public @Nullable String getBranch()
    {
        return branch;
    }

    /**
     * Names the content the URL addresses by its id.
     *
     * @param value content id; {@code null} or empty clears it
     * @return these params
     */
    public PageUrlParams id( final @Nullable String value )
    {
        this.id = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Names the content the URL addresses by its path within the project.
     *
     * @param value content path, such as {@code /my-site/posts/first-post}; {@code null} or empty clears it
     * @return these params
     */
    public PageUrlParams path( final @Nullable String value )
    {
        this.path = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Sets the project of the content. The URL is then resolved from configuration in place of the request: the
     * Base URL configured for the site of the content, or the site engine address of that site.
     *
     * @param value project name; {@code null} or empty takes it from the context
     * @return these params
     * @deprecated use {@link PortalUrlService#pageUrlParts(PageUrlPartsParams)}, with a {@link UrlBase} resolved for the project
     */
    @Deprecated
    public PageUrlParams projectName( final @Nullable String value )
    {
        this.projectName = Strings.emptyToNull( value );
        return this;
    }

    /**
     * Sets the branch of the content. The URL is then resolved from configuration in place of the request: the
     * Base URL configured for the site of the content, or the site engine address of that site.
     *
     * @param value branch name; {@code null} or empty takes it from the context
     * @return these params
     * @deprecated use {@link PortalUrlService#pageUrlParts(PageUrlPartsParams)}, with a {@link UrlBase} resolved for the branch
     */
    @Deprecated
    public PageUrlParams branch( final @Nullable String value )
    {
        this.branch = Strings.emptyToNull( value );
        return this;
    }

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
        return helper.toString();
    }
}
