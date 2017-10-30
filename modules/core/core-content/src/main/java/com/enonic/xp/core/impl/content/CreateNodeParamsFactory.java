package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.core.impl.content.index.ContentIndexConfigFactory;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;

public class CreateNodeParamsFactory
{
    private final CreateContentTranslatorParams params;

    private final ContentTypeService contentTypeService;

    private final PageDescriptorService pageDescriptorService;

    private static final ContentDataSerializer CONTENT_DATA_SERIALIZER = new ContentDataSerializer();

    public CreateNodeParamsFactory( final Builder builder )
    {
        this.params = builder.params;
        this.contentTypeService = builder.contentTypeService;
        this.pageDescriptorService = builder.pageDescriptorService;
    }

    public CreateNodeParams produce()
    {
        final PropertyTree contentAsData = CONTENT_DATA_SERIALIZER.toCreateNodeData( params );

        final PropertySet pageSet = contentAsData.getPropertySet( PropertyPath.from( ContentPropertyNames.PAGE ));

        final Page page = pageSet != null ? ContentDataSerializer.PAGE_SERIALIZER.fromData( pageSet ) : null;

        final ContentIndexConfigFactory.Builder indexConfigFactoryBuilder = ContentIndexConfigFactory.create().
            contentTypeName( params.getType() ).
            contentTypeService( contentTypeService );

        if ( page != null )
        {
            indexConfigFactoryBuilder.
                descriptorKey( page.getController() ).
                pageDescriptorService( pageDescriptorService );
        }

        final IndexConfigDocument indexConfigDocument = indexConfigFactoryBuilder.build().produce();

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( resolveNodeName( params.getName() ) ).
            parent( ContentNodeHelper.translateContentParentToNodeParentPath( params.getParent() ) ).
            data( contentAsData ).
            indexConfigDocument( indexConfigDocument ).
            permissions( params.getPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            childOrder( params.getChildOrder() ).
            nodeType( ContentConstants.CONTENT_NODE_COLLECTION );

        for ( final CreateAttachment attachment : params.getCreateAttachments() )
        {
            builder.attachBinary( attachment.getBinaryReference(), attachment.getByteSource() );
        }

        return builder.build();
    }

    private static String resolveNodeName( final ContentName name )
    {
        if ( name.isUnnamed() && !name.hasUniqueness() )
        {
            return ContentName.uniqueUnnamed().toString();
        }

        return name.toString();
    }

    public static Builder create( final CreateContentTranslatorParams params )
    {
        return new Builder( params );
    }

    public static class Builder
    {
        private CreateContentTranslatorParams params;

        private ContentTypeService contentTypeService;

        private PageDescriptorService pageDescriptorService;

        Builder( final CreateContentTranslatorParams params )
        {
            this.params = params;
        }

        Builder contentTypeService( final ContentTypeService value )
        {
            this.contentTypeService = value;
            return this;
        }

        Builder pageDescriptorService( final PageDescriptorService value )
        {
            this.pageDescriptorService = value;
            return this;
        }

        void validate()
        {
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( contentTypeService );
            Preconditions.checkNotNull( pageDescriptorService );
        }

        public CreateNodeParamsFactory build()
        {
            validate();
            return new CreateNodeParamsFactory( this );
        }
    }

}
