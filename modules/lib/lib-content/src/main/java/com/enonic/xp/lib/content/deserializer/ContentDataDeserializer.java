package com.enonic.xp.lib.content.deserializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.attachment.Attachments;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Regions;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.util.BinaryReferences;

public final class ContentDataDeserializer
{
    private final PropertyTreeTranslator propertyTreeTranslator;

    private final SiteConfigDeserializer siteConfigDeserializer;

    private final WorkflowDeserializer workflowDeserializer;

    private final PageDeserializer pageDeserializer;

    private final ValidationErrorsDeserializer validationErrorsDeserializer;

    private final AttachmentsDeserializer attachmentsDeserializer;

    public ContentDataDeserializer( final PropertyTreeTranslator propertyTreeTranslator,
                                    final SiteConfigDeserializer siteConfigDeserializer, final WorkflowDeserializer workflowDeserializer,
                                    final PageDeserializer pageDeserializer,
                                    final ValidationErrorsDeserializer validationErrorsDeserializer,
                                    final AttachmentsDeserializer attachmentsDeserializer )
    {
        this.propertyTreeTranslator = propertyTreeTranslator;
        this.siteConfigDeserializer = siteConfigDeserializer;
        this.workflowDeserializer = workflowDeserializer;
        this.pageDeserializer = pageDeserializer;
        this.validationErrorsDeserializer = validationErrorsDeserializer;
        this.attachmentsDeserializer = attachmentsDeserializer;
    }

    public PropertyTree toPropertyTree( Map<String, Object> source, ContentTypeName contentTypeName )
    {
        if ( source == null )
        {
            return null;
        }

        final Object siteConfig = source.get( ContentPropertyNames.SITECONFIG );
        final Map<String, Object> filtered = new LinkedHashMap<>( source );
        filtered.remove( ContentPropertyNames.SITECONFIG );

        final PropertyTree tree = propertyTreeTranslator.translate( filtered, contentTypeName );

        if ( siteConfig != null )
        {
            siteConfigDeserializer.deserialize( siteConfig, contentTypeName, tree.getRoot() );
        }

        return tree;
    }

    public ExtraDatas toExtraDatas( Map<String, Object> source, ContentTypeName contentTypeName )
    {
        if ( source == null )
        {
            return null;
        }

        final ExtraDatas.Builder builder = ExtraDatas.create();
        source.forEach( ( key, data ) -> {
            ApplicationKey appKey = ExtraData.fromApplicationPrefix( key );
            if ( data instanceof Map )
            {
                ( (Map<String, Object>) data ).forEach( ( xName, values ) -> {
                    if ( values instanceof Map )
                    {
                        final ExtraData extra = new ExtraData( XDataName.from( appKey, xName ),
                                                               propertyTreeTranslator.translate( (Map<String, Object>) values,
                                                                                                 XDataName.from( appKey, xName ),
                                                                                                 contentTypeName ) );
                        builder.add( extra );
                    }
                } );
            }
        } );

        return builder.build();
    }

    public Page toPage( Map<String, Object> pageMap )
    {
        return pageDeserializer.deserialize( pageMap );
    }

    public Regions toRegions( Map<String, Object> regionsMap )
    {
        return pageDeserializer.deserializeRegions( regionsMap );
    }

    public Component toComponent( Map<String, Object> componentMap )
    {
        return pageDeserializer.deserializeComponent( componentMap );
    }

    public Attachments toAttachments( List<Map<String, Object>> attachmentsMap, Attachments originalAttachments )
    {
        return attachmentsDeserializer.deserializeAttachments( attachmentsMap, originalAttachments );
    }

    public CreateAttachments toAddAttachments( List<Map<String, Object>> attachmentsMap )
    {
        return attachmentsDeserializer.deserializeCreateAttachments( attachmentsMap );
    }

    public BinaryReferences toRemoveAttachments( List<String> references )
    {
        return attachmentsDeserializer.deserializeBinaryReferences( references );
    }

    public WorkflowInfo toWorkflowInfo( Map<String, Object> workflowMap )
    {
        return workflowDeserializer.deserialize( workflowMap );
    }

    public ValidationErrors toValidationErrors( List<Object> validationErrors )
    {
        return validationErrorsDeserializer.deserialize( validationErrors );
    }
}

