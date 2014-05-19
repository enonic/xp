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
    public ModuleKeyResolver forContent( final Content content )
    {
        final SiteTemplate siteTemplate = findSiteTemplate( content );
        if ( siteTemplate == null )
        {
            return ModuleKeyResolver.empty();
        }

        final ModuleKeys siteModules = siteTemplate.getModules();
        return ModuleKeyResolver.from( siteModules );
    }

    @Override
    public ModuleKeyResolver forContent( final ContentPath contentPath )
    {
        final Content content = getContent( contentPath );
        if ( content == null )
        {
            return ModuleKeyResolver.empty();
        }

        return forContent( content );
    }

    private SiteTemplate findSiteTemplate( final Content content )
    {
        final Site site = resolveSite( content.getId() );
        if ( site == null )
        {
            return null;
        }
        return getSiteTemplate( site.getTemplate() );
    }

    private Site resolveSite( final ContentId contentId )
    {
        final Content siteContent = this.siteService.getNearestSite( contentId );
        return siteContent != null ? siteContent.getSite() : null;
    }

    private Content getContent( final ContentPath contentPath )
    {
        return contentService.getByPath( contentPath );
    }

    private SiteTemplate getSiteTemplate( final SiteTemplateKey siteTemplateKey )
    {
        return this.siteTemplateService.getSiteTemplate( siteTemplateKey );
    }
}
