package com.enonic.xp.lib.content;

import java.util.Map;

import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.lib.content.deserializer.ContentDataDeserializer;
import com.enonic.xp.lib.content.deserializer.PageDeserializer;
import com.enonic.xp.lib.content.deserializer.PropertyTreeTranslator;
import com.enonic.xp.lib.content.deserializer.SiteConfigDeserializer;
import com.enonic.xp.lib.content.deserializer.WorkflowDeserializer;
import com.enonic.xp.page.Page;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.site.SiteService;

public abstract class BaseContentHandler
    extends BaseContextHandler
{
    private ContentDataDeserializer contentDataDeserializer;

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );

        final ContentTypeService contentTypeService = context.getService( ContentTypeService.class ).get();
        final XDataService xDataService = context.getService( XDataService.class ).get();
        final SiteService siteService = context.getService( SiteService.class ).get();
        final PropertyTreeMarshallerService propertyTreeMarshallerService = context.getService( PropertyTreeMarshallerService.class ).get();

        final PropertyTreeTranslator translator =
            new PropertyTreeTranslator( contentTypeService, xDataService, siteService, propertyTreeMarshallerService,
                                        this::strictContentValidation );

        this.contentDataDeserializer =
            new ContentDataDeserializer( translator, new SiteConfigDeserializer( translator ), new WorkflowDeserializer(),
                                         new PageDeserializer( translator ) );
    }

    protected PropertyTree createPropertyTree( final Map<String, Object> data, final ContentTypeName contentTypeName )
    {
        return contentDataDeserializer.toPropertyTree( data, contentTypeName );
    }

    protected ExtraDatas createExtraDatas( final Map<String, Object> xData, final ContentTypeName contentTypeName )
    {
        return contentDataDeserializer.toExtraDatas( xData, contentTypeName );
    }

    protected Page createPage( final Map<String, Object> pageMap )
    {
        return contentDataDeserializer.toPage( pageMap );
    }

    protected WorkflowInfo createWorkflowInfo( final Map<String, Object> workflowMap )
    {
        return contentDataDeserializer.toWorkflowInfo( workflowMap );
    }

    protected boolean strictContentValidation( final ContentTypeName contentTypeName )
    {
        return !contentTypeName.isUnstructured() && strictDataValidation();
    }

    protected boolean strictDataValidation()
    {
        return true;
    }
}
