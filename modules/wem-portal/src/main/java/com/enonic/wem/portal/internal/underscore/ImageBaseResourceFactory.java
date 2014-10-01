package com.enonic.wem.portal.internal.underscore;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.attachment.AttachmentService;
import com.enonic.wem.core.image.filter.ImageFilterBuilder;
import com.enonic.wem.portal.internal.base.ModuleBaseResourceFactory;

public abstract class ImageBaseResourceFactory<T extends ImageBaseResource>
    extends ModuleBaseResourceFactory<T>
{
    private ImageFilterBuilder imageFilterBuilder;

    private AttachmentService attachmentService;

    private BlobService blobService;

    private ContentService contentService;

    public ImageBaseResourceFactory( final Class<T> type )
    {
        super( type );
    }

    @Override
    protected void configure( final T instance )
    {
        super.configure( instance );
        instance.imageFilterBuilder = this.imageFilterBuilder;
        instance.attachmentService = this.attachmentService;
        instance.blobService = this.blobService;
        instance.contentService = this.contentService;
    }

    public final void setImageFilterBuilder( final ImageFilterBuilder imageFilterBuilder )
    {
        this.imageFilterBuilder = imageFilterBuilder;
    }

    public final void setAttachmentService( final AttachmentService attachmentService )
    {
        this.attachmentService = attachmentService;
    }

    public final void setBlobService( final BlobService blobService )
    {
        this.blobService = blobService;
    }

    public final void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
