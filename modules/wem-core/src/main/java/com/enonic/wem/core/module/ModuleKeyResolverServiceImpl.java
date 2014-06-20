package com.enonic.wem.core.module;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteService;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleKeys;

@Singleton
final class ModuleKeyResolverServiceImpl
    implements ModuleKeyResolverService
{
    @Inject
    private ContentService contentService;

    @Inject
    protected SiteService siteService;

    @Inject
    protected SiteTemplateService siteTemplateService;

    @Override
    public ModuleKeyResolver forContent( final Content content, final Context context )
    {
        final SiteTemplate siteTemplate = findSiteTemplate( content, context );
        if ( siteTemplate == null )
        {
            return ModuleKeyResolver.empty();
        }

        final ModuleKeys siteModules = siteTemplate.getModules();
        return ModuleKeyResolver.from( siteModules );
    }

    @Override
    public ModuleKeyResolver forContent( final ContentPath contentPath, final Context context )
    {
        final Content content = getContent( contentPath, context );
        if ( content == null )
        {
            return ModuleKeyResolver.empty();
        }

        return forContent( content, context );
    }

    private SiteTemplate findSiteTemplate( final Content content, final Context context )
    {
        final Site site = resolveSite( content.getId(), context );
        if ( site == null )
        {
            return null;
        }
        return getSiteTemplate( site.getTemplate() );
    }

    private Site resolveSite( final ContentId contentId, final Context context )
    {
        final Content siteContent = this.siteService.getNearestSite( contentId, context );
        return siteContent != null ? siteContent.getSite() : null;
    }

    private Content getContent( final ContentPath contentPath, final Context context )
    {
        return contentService.getByPath( contentPath, context );
    }

    private SiteTemplate getSiteTemplate( final SiteTemplateKey siteTemplateKey )
    {
        return this.siteTemplateService.getSiteTemplate( siteTemplateKey );
    }
}
