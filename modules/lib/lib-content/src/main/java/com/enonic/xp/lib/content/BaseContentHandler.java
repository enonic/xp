package com.enonic.xp.lib.content;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.lib.content.deserializer.AttachmentsDeserializer;
import com.enonic.xp.lib.content.deserializer.ContentDataDeserializer;
import com.enonic.xp.lib.content.deserializer.PageDeserializer;
import com.enonic.xp.lib.content.deserializer.PropertyTreeTranslator;
import com.enonic.xp.lib.content.deserializer.SiteConfigDeserializer;
import com.enonic.xp.lib.content.deserializer.ValidationErrorsDeserializer;
import com.enonic.xp.lib.content.deserializer.WorkflowDeserializer;
import com.enonic.xp.page.Page;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.util.BinaryReferences;

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
                                         new PageDeserializer( translator ), new ValidationErrorsDeserializer(),
                                         new AttachmentsDeserializer() );
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

    protected Regions createRegions( final Map<String, Object> regionsMap )
    {
        return contentDataDeserializer.toRegions( regionsMap );
    }

    protected Component createComponent( final Map<String, Object> componentMap )
    {
        return contentDataDeserializer.toComponent( componentMap );
    }

    protected ValidationErrors createValidationErrors( final List<Object> validationErrors )
    {
        return contentDataDeserializer.toValidationErrors( validationErrors );
    }

    protected WorkflowInfo createWorkflowInfo( final Map<String, Object> workflowMap )
    {
        return contentDataDeserializer.toWorkflowInfo( workflowMap );
    }

    protected Attachments createAttachments( final List<Map<String, Object>> attachmentsMap, final Attachments originalAttachments )
    {
        return contentDataDeserializer.toAttachments( attachmentsMap, originalAttachments );
    }

    protected CreateAttachments createAddAttachments( final List<Map<String, Object>> attachmentsMap )
    {
        return contentDataDeserializer.toAddAttachments( attachmentsMap );
    }

    protected BinaryReferences createRemoveAttachments( final List<String> references )
    {
        return contentDataDeserializer.toRemoveAttachments( references );
    }

    protected boolean strictContentValidation( final ContentTypeName contentTypeName )
    {
        return !contentTypeName.isUnstructured() && strictDataValidation();
    }

    protected boolean strictDataValidation()
    {
        return true;
    }

    protected <T> void edit( Map<String, ?> map, String key, Class<T> type, Consumer<Optional<T>> fieldEditor )
    {
        if ( map.containsKey( key ) )
        {
            fieldEditor.accept( Optional.ofNullable( map.get( key ) ).map( type::cast ) );
        }
    }
}
