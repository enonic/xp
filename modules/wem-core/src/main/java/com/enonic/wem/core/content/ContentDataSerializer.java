package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.core.content.page.PageDataSerializer;
import com.enonic.wem.core.form.FormDataSerializer;

public class ContentDataSerializer
    extends AbstractDataSetSerializer<Content, Content.Builder>
{

    private static final FormDataSerializer FORM_SERIALIZER = new FormDataSerializer( ContentFieldNames.FORM_SET );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( ContentFieldNames.PAGE_SET );

    private static final SiteDataSerializer SITE_SERIALIZER = new SiteDataSerializer( ContentFieldNames.SITE_SET );


    public RootDataSet toData( final Content content )
    {
        final RootDataSet contentAsData = new RootDataSet();

        addPropertyIfNotNull( contentAsData, ContentFieldNames.DRAFT, content.isDraft() );
        addPropertyIfNotNull( contentAsData, ContentFieldNames.DISPLAY_NAME, content.getDisplayName() );
        addPropertyIfNotNull( contentAsData, ContentFieldNames.CONTENT_TYPE, content.getType().getContentTypeName() );

        contentAsData.add( content.getContentData().toDataSet( ContentFieldNames.CONTENT_DATA_SET ) );

        if ( content.getAllMetadata() != null )
        {
            final DataSet dataSet = new DataSet( ContentFieldNames.METADATA );

            List<Metadata> metadataList = content.getAllMetadata();
            for ( Metadata metadata : metadataList )
            {
                dataSet.add( metadata.getData().toDataSet( metadata.getName().toString() ) );
            }

            contentAsData.add( dataSet );
        }

        if ( content.getForm() != null )
        {
            contentAsData.add( FORM_SERIALIZER.toData( content.getForm() ) );
        }

        if ( content.hasPage() )
        {
            contentAsData.add( PAGE_SERIALIZER.toData( content.getPage() ) );
        }
        if ( content instanceof Site )
        {
            final Site site = (Site) content;
            contentAsData.add( SITE_SERIALIZER.toData( site ) );
        }

        return contentAsData;
    }


    public Content.Builder fromData( final DataSet dataSet )
    {
        final ContentTypeName contentTypeName = ContentTypeName.from( dataSet.getProperty( ContentFieldNames.CONTENT_TYPE ).getString() );
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

        if ( dataSet.hasData( ContentFieldNames.DISPLAY_NAME ) )
        {
            builder.displayName( dataSet.getProperty( ContentFieldNames.DISPLAY_NAME ).getString() );
        }

        if ( dataSet.hasData( ContentFieldNames.DRAFT ) )
        {
            builder.draft( Boolean.parseBoolean( dataSet.getProperty( ContentFieldNames.DRAFT ).getString() ) );
        }

        if ( dataSet.hasData( ContentFieldNames.CONTENT_DATA_SET ) )
        {
            builder.contentData( new ContentData( dataSet.getDataSet( ContentFieldNames.CONTENT_DATA_SET ).toRootDataSet() ) );
        }

        if ( dataSet.hasData( ContentFieldNames.METADATA ) )
        {
            List<Metadata> metadataList = new ArrayList<>();
            DataSet data = dataSet.getDataSet( ContentFieldNames.METADATA );
            for ( String name : data.getDataNames() )
            {
                metadataList.add( new Metadata( MetadataSchemaName.from( name ), data.getDataSet( name ).toRootDataSet() ) );
            }

            builder.metadata( metadataList );
        }

        if ( dataSet.hasData( ContentFieldNames.FORM_SET ) )
        {
            builder.form( FORM_SERIALIZER.fromData( dataSet.getDataSet( ContentFieldNames.FORM_SET ) ) );
        }

        if ( dataSet.hasData( ContentFieldNames.PAGE_SET ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( dataSet.getDataSet( ContentFieldNames.PAGE_SET ) ) );
        }

        return builder;
    }

    RootDataSet toData( final CreateContentParams params )
    {
        final RootDataSet contentAsData = new RootDataSet();

        addPropertyIfNotNull( contentAsData, ContentFieldNames.DRAFT, params.isDraft() );
        addPropertyIfNotNull( contentAsData, ContentFieldNames.DISPLAY_NAME, params.getDisplayName() );
        addPropertyIfNotNull( contentAsData, ContentFieldNames.CONTENT_TYPE, params.getContentType() );

        if ( params.getContentData() != null )
        {
            contentAsData.add( params.getContentData().toDataSet( ContentFieldNames.CONTENT_DATA_SET ) );
        }

        if ( params.getMetadata() != null )
        {
            final DataSet dataSet = new DataSet( ContentFieldNames.METADATA );

            List<Metadata> metadataList = params.getMetadata();
            for ( Metadata metadata : metadataList )
            {
                dataSet.add( metadata.getData().toDataSet( metadata.getName().toString() ) );
            }

            contentAsData.add( dataSet );
        }

        if ( params.getForm() != null )
        {
            contentAsData.add( FORM_SERIALIZER.toData( params.getForm() ) );
        }

        return contentAsData;
    }

    private void addPropertyIfNotNull( final RootDataSet rootDataSet, final String propertyName, final Object value )
    {
        if ( value != null )
        {
            rootDataSet.setProperty( propertyName, Value.newString( value.toString() ) );
        }
    }
}
