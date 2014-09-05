package com.enonic.wem.core.content;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.core.content.attachment.AttachmentServiceImpl;
import com.enonic.wem.core.content.page.PageModule;
import com.enonic.wem.core.content.site.SiteModule;
import com.enonic.wem.core.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.core.index.query.QueryService;

public final class ContentModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( AttachmentService.class ).to( AttachmentServiceImpl.class ).in( Singleton.class );
        bind( ContentService.class ).to( ContentServiceImpl.class ).in( Singleton.class );
        bind( QueryService.class ).to( ElasticsearchQueryService.class ).in( Singleton.class );

        install( new SiteModule() );
        install( new PageModule() );
    }
}
