package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.core.content.page.PageDataSerializer;
import com.enonic.wem.core.form.FormDataSerializer;

public class ContentDataSerializer
    extends AbstractDataSetSerializer<Content, Content.Builder>
{

    private static final FormDataSerializer FORM_SERIALIZER = new FormDataSerializer( ContentPropertyNames.FORM_SET );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentPropertyNames.PAGE_SET );

    private static final SiteDataSerializer SITE_SERIALIZER = new SiteDataSerializer( ContentPropertyNames.SITE_SET );

    public void toData( final Content content, final PropertySet contentAsData )
    {
        contentAsData.setBoolean( ContentPropertyNames.DRAFT, content.isDraft() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.DISPLAY_NAME, content.getDisplayName() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.CONTENT_TYPE, content.getType().toString() );

        contentAsData.addSet( ContentPropertyNames.CONTENT_DATA_SET, content.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( content.hasMetadata() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.METADATA );
            for ( final Metadata metadata : content.getAllMetadata() )
            {
                metadataSet.addSet( metadata.getName().toString(), metadata.getData().getRoot().copy( contentAsData.getTree() ) );
            }
        }

        if ( content.getForm() != null )
        {
            FORM_SERIALIZER.toData( content.getForm(), contentAsData );
        }

        if ( content.hasPage() )
        {
            PAGE_SERIALIZER.toData( content.getPage(), contentAsData );
        }

        if ( content instanceof Site )
        {
            final Site site = (Site) content;
            SITE_SERIALIZER.toData( site, contentAsData );
        }
    }

    public Content.Builder fromData( final PropertySet set )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( set.getString( ContentPropertyNames.CONTENT_TYPE ) );
        final Content.Builder builder;
        if ( contentTypeName.isPageTemplate() )
        {
            builder = PageTemplate.newPageTemplate();
        }
        else if ( contentTypeName.isSite() )
        {
            builder = Site.newSite();
        }
        else
        {
            builder = Content.newContent();
        }
        builder.type( contentTypeName );

        builder.displayName( set.getString( ContentPropertyNames.DISPLAY_NAME ) );
        builder.draft( set.getBoolean( ContentPropertyNames.DRAFT ) );
        builder.contentData( set.getSet( ContentPropertyNames.CONTENT_DATA_SET ).toTree() );

        final PropertySet metadataSet = set.getSet( ContentPropertyNames.METADATA );
        if ( metadataSet != null )
        {
            final List<Metadata> metadataList = new ArrayList<>();

            for ( final String metadataName : metadataSet.getPropertyNames() )
            {
                metadataList.add( new Metadata( MetadataSchemaName.from( metadataName ), metadataSet.getSet( metadataName ).toTree() ) );
            }

            builder.metadata( metadataList );
        }

        builder.form( FORM_SERIALIZER.fromData( set.getSet( ContentPropertyNames.FORM_SET ) ) );
        if ( set.hasProperty( ContentPropertyNames.PAGE_SET ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( set.getSet( ContentPropertyNames.PAGE_SET ) ) );
        }
        return builder;
    }

    void toData( final CreateContentParams params, final PropertySet contentAsData )
    {
        contentAsData.addBoolean( ContentPropertyNames.DRAFT, params.isDraft() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.DISPLAY_NAME, params.getDisplayName() );
        contentAsData.ifNotNull().addString( ContentPropertyNames.CONTENT_TYPE,
                                             params.getContentType() != null ? params.getContentType().toString() : null );

        contentAsData.addSet( ContentPropertyNames.CONTENT_DATA_SET, params.getData().getRoot().copy( contentAsData.getTree() ) );

        if ( params.getMetadata() != null && !params.getMetadata().isEmpty() )
        {
            final PropertySet metadataSet = contentAsData.addSet( ContentPropertyNames.METADATA );
            for ( final Metadata metadata : params.getMetadata() )
            {
                metadataSet.addSet( metadata.getName().toString(), metadata.getData().getRoot().copy( metadataSet.getTree() ) );
            }
        }

        if ( params.getForm() != null )
        {
            FORM_SERIALIZER.toData( params.getForm(), contentAsData );
        }
    }
}
