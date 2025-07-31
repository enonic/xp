package com.enonic.xp.lib.content.deserializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.ValidationErrors;
import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.Page;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.xdata.XDataName;

public final class ContentDataDeserializer
{
    private final PropertyTreeTranslator propertyTreeTranslator;

    private final SiteConfigDeserializer siteConfigDeserializer;

    private final WorkflowDeserializer workflowDeserializer;

    private final PageDeserializer pageDeserializer;

    private final ValidationErrorsDeserializer validationErrorsDeserializer;

    public ContentDataDeserializer( final PropertyTreeTranslator propertyTreeTranslator,
                                    final SiteConfigDeserializer siteConfigDeserializer, final WorkflowDeserializer workflowDeserializer,
                                    final PageDeserializer pageDeserializer,
                                    final ValidationErrorsDeserializer validationErrorsDeserializer )
    {
        this.propertyTreeTranslator = propertyTreeTranslator;
        this.siteConfigDeserializer = siteConfigDeserializer;
        this.workflowDeserializer = workflowDeserializer;
        this.pageDeserializer = pageDeserializer;
        this.validationErrorsDeserializer = validationErrorsDeserializer;
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

    public WorkflowInfo toWorkflowInfo( Map<String, Object> workflowMap )
    {
        return workflowDeserializer.deserialize( workflowMap );
    }

    public ValidationErrors toValidationErrors( List<Object> validationErrors )
    {
        return validationErrorsDeserializer.deserialize( validationErrors );
    }
}

