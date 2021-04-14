package com.enonic.xp.admin.impl.json.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.content.page.PageDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.LayoutDescriptorJson;
import com.enonic.xp.admin.impl.json.content.page.region.PartDescriptorJson;
import com.enonic.xp.admin.impl.json.schema.content.ContentTypeSummaryJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.ContentTypeIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;

@Component
public class JsonObjectsFactory
{
    private ContentTypeIconUrlResolver contentTypeIconUrlResolver;

    private LocaleService localeService;

    private MixinService mixinService;

    public PartDescriptorJson createPartDescriptorJson( final PartDescriptor descriptor )
    {
        return new PartDescriptorJson( descriptor, new LocaleMessageResolver( this.localeService, descriptor.getApplicationKey() ),
                                       new InlineMixinResolver( mixinService ) );
    }

    public PageDescriptorJson createPageDescriptorJson( final PageDescriptor descriptor )
    {
        return new PageDescriptorJson( descriptor, new LocaleMessageResolver( this.localeService, descriptor.getApplicationKey() ),
                                       new InlineMixinResolver( mixinService ) );
    }

    public LayoutDescriptorJson createLayoutDescriptorJson( final LayoutDescriptor descriptor )
    {
        return new LayoutDescriptorJson( descriptor, new LocaleMessageResolver( this.localeService, descriptor.getApplicationKey() ),
                                         new InlineMixinResolver( mixinService ) );
    }

    public ContentTypeSummaryJson createContentTypeSummaryJson( final ContentType contentType )
    {
        return new ContentTypeSummaryJson( contentType, this.contentTypeIconUrlResolver,
                                           new LocaleMessageResolver( localeService, contentType.getName().getApplicationKey() ) );
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeIconUrlResolver = new ContentTypeIconUrlResolver( new ContentTypeIconResolver( contentTypeService ) );
    }
}
