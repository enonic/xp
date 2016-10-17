package com.enonic.xp.core.impl.content.processor;


import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;


@Component
public final class ReferenceContentProcessor
    implements ContentProcessor
{
    private Parser<ContentIds> parser;

    private ContentTypeService contentTypeService;

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();
        final PropertyTree data = createContentParams.getData();

        final ContentType contentType =
            contentTypeService.getByName( new GetContentTypeParams().contentTypeName( createContentParams.getType() ) );

        final ReferenceVisitor visitor = new ReferenceVisitor( data, parser );
        visitor.traverse( contentType.getForm() );

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).
            contentData( data ).
            build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final CreateAttachments createAttachments = params.getCreateAttachments();

        final ContentEditor editor = editable ->
        {

            final ReferenceVisitor visitor = new ReferenceVisitor( editable.data, parser );
            visitor.removeOldReferences();

            visitor.traverse( params.getContentType().getForm() );


        };
        return new ProcessUpdateResult( createAttachments, editor );
    }

    @Override
    public boolean supports( final ContentType contentType )
    {
        return true;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setParser( final Parser<ContentIds> parser )
    {
        this.parser = parser;
    }
}
