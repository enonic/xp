package com.enonic.xp.portal.impl.processor;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.EditableSite;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.content.processor.ProcessCreateParams;
import com.enonic.xp.content.processor.ProcessCreateResult;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.impl.url.HtmlLinkProcessor;
import com.enonic.xp.schema.content.ContentType;

@Component
public class HtmlAreaContentProcessor
    implements ContentProcessor
{
    private ContentIds.Builder processedContents;

    @Override
    public boolean supports( final ContentType contentType )
    {
        return true;
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final ContentEditor editor;

        editor = editable -> {
            processedContents = ContentIds.create();

            processContentData( editable );
            processSiteConfigData( editable );
            processExtraData( editable );
//            processPageData( editedContent );

            editable.processedReferences.addAll( processedContents.build() );
        };

        return new ProcessUpdateResult( createAttachments, editor );
    }

    private void processSiteConfigData( final EditableContent content )
    {
        if ( content instanceof EditableSite )
        {
            ( (EditableSite) content ).siteConfigs.forEach( siteConfig -> processDataTree( siteConfig.getConfig() ) );
        }
    }

    private void processExtraData( final EditableContent content )
    {
        if ( content.extraDatas != null )
        {
            content.extraDatas.forEach( extraData -> processDataTree( extraData.getData() ) );
        }
    }

    private void processContentData( final EditableContent content )
    {
        processDataTree( content.data );
    }

    private void processDataTree( final PropertyTree data )
    {
        data.getProperties().
            forEach( ( property -> {
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
                        processedContents.add( contentId );
                    }
                }
            } ) );
    }
}
