package com.enonic.xp.portal.impl.processor;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableSite;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.content.processor.ProcessCreateParams;
import com.enonic.xp.content.processor.ProcessCreateResult;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.FormItems;
import com.enonic.xp.portal.impl.url.HtmlLinkProcessor;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.schema.xdata.XDatas;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

@Component
public class HtmlAreaContentProcessor
    implements ContentProcessor
{
    private ContentTypeService contentTypeService;

    private XDataService xDataService;

    private SiteService siteService;

    @Override
    public boolean supports( final ContentType contentType )
    {
        return true;
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final ContentIds.Builder processedIds = ContentIds.create();

        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( createContentParams.getType() ) );

        processContentData( createContentParams.getData(), contentType, processedIds );
        processExtraData( createContentParams.getExtraDatas(), contentType, processedIds );

        return new ProcessCreateResult( CreateContentParams.
            create( createContentParams ).
            addProcessedIds( processedIds.build() ).
            build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final ContentEditor editor;

        editor = editable -> {
            final ContentIds.Builder processedIds = ContentIds.create();

            final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( editable.source.getType() ) );

            processContentData( editable.data, contentType, processedIds );
            processExtraData( editable.extraDatas, contentType, processedIds );
            if ( editable instanceof EditableSite )
            {
                processSiteConfigData( ( (EditableSite) editable ).siteConfigs, processedIds );
            }

            editable.processedReferences = processedIds;
        };

        return new ProcessUpdateResult( createAttachments, editor );
    }

    private void processSiteConfigData( final SiteConfigs siteConfigs, final ContentIds.Builder processedIds )
    {
        siteConfigs.forEach( siteConfig -> {

            final SiteDescriptor siteDescriptor = siteService.getDescriptor( siteConfig.getApplicationKey() );

            if ( siteDescriptor == null )
            {
                return;
            }

            final Collection<Property> properties = getProperties( siteConfig.getConfig(), siteDescriptor.getForm().getFormItems() );
            processDataTree( properties, processedIds );
        } );
    }

    private void processExtraData( final ExtraDatas extraDatas, final ContentType contentType, final ContentIds.Builder processedIds )
    {
        final XDatas xDatas = xDataService.getByNames( contentType.getXData() );

        if ( xDatas.getSize() > 0 )
        {
            xDatas.forEach( xData -> {
                if ( extraDatas == null )
                {
                    return;
                }

                final ExtraData extraData = extraDatas.getMetadata( xData.getName() );
                if ( extraData != null )
                {
                    final Collection<Property> properties = getProperties( extraData.getData(), xData.getForm().getFormItems() );
                    processDataTree( properties, processedIds );
                }
            } );
        }
    }

    private void processContentData( final PropertyTree contentData, final ContentType contentType, final ContentIds.Builder processedIds )
    {
        final Collection<Property> properties = getProperties( contentData, contentType.getForm().getFormItems() );
        processDataTree( properties, processedIds );
    }

    private Collection<Property> getProperties( final PropertyTree data, final FormItems formItems )
    {
        final Set<String> paths = getPaths( formItems );

        return paths.
            stream().
            map( data::getProperty ).
            filter( Objects::nonNull ).
            filter( Property::hasNotNullValue ).
            collect( Collectors.toList() );
    }

    private Set<String> getPaths( final FormItems formItems )
    {
        final HtmlAreaVisitor visitor = new HtmlAreaVisitor();
        visitor.traverse( formItems );

        return visitor.getPaths().stream().map( FormItemPath::toString ).collect( Collectors.toSet() );
    }

    private void processDataTree( final Collection<Property> properties, final ContentIds.Builder processedIds )
    {
        properties.forEach( ( property -> {
            final String value = property.getString();

            if ( StringUtils.isBlank( value ) )
            {
                return;
            }

            final Matcher contentMatcher = HtmlLinkProcessor.CONTENT_PATTERN.matcher( value );

            while ( contentMatcher.find() )
            {
                if ( contentMatcher.groupCount() >= HtmlLinkProcessor.NB_GROUPS )
                {
                    final String id = contentMatcher.group( HtmlLinkProcessor.ID_INDEX );
                    final ContentId contentId = ContentId.from( id );
                    processedIds.add( contentId );
                }
            }
        } ) );
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setxDataService( final XDataService xDataService )
    {
        this.xDataService = xDataService;
    }

    @Reference
    public void setSiteService( final SiteService siteService )
    {
        this.siteService = siteService;
    }
}
