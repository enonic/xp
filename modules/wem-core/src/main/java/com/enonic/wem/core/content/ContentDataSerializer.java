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

import static com.enonic.wem.api.content.ContentIndexPaths.CONTENT_DATA;
import static com.enonic.wem.api.content.ContentIndexPaths.CONTENT_TYPE_FIELD_NAME;
import static com.enonic.wem.api.content.ContentIndexPaths.DISPLAY_NAME_FIELD_NAME;
import static com.enonic.wem.api.content.ContentIndexPaths.DRAFT;
import static com.enonic.wem.api.content.ContentIndexPaths.FORM;
import static com.enonic.wem.api.content.ContentIndexPaths.METADATA;
import static com.enonic.wem.api.content.ContentIndexPaths.PAGE;
import static com.enonic.wem.api.content.ContentIndexPaths.SITE;

public class ContentDataSerializer
    extends AbstractDataSetSerializer<Content, Content.Builder>
{

    private static final FormDataSerializer FORM_SERIALIZER = new FormDataSerializer( FORM );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( PAGE );

    private static final SiteDataSerializer SITE_SERIALIZER = new SiteDataSerializer( SITE );


    public RootDataSet toData( final Content content )
    {
        final RootDataSet contentAsData = new RootDataSet();

        addPropertyIfNotNull( contentAsData, DRAFT, content.isDraft() );
        addPropertyIfNotNull( contentAsData, DISPLAY_NAME_FIELD_NAME, content.getDisplayName() );
        addPropertyIfNotNull( contentAsData, CONTENT_TYPE_FIELD_NAME, content.getType().getContentTypeName() );

        contentAsData.add( content.getContentData().toDataSet( CONTENT_DATA ) );

        if ( content.getAllMetadata() != null )
        {
            final DataSet dataSet = new DataSet( METADATA );

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
        final ContentTypeName contentTypeName = ContentTypeName.from( dataSet.getProperty( CONTENT_TYPE_FIELD_NAME ).getString() );
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

        if ( dataSet.hasData( DISPLAY_NAME_FIELD_NAME ) )
        {
            builder.displayName( dataSet.getProperty( DISPLAY_NAME_FIELD_NAME ).getString() );
        }

        if ( dataSet.hasData( DRAFT ) )
        {
            builder.draft( Boolean.parseBoolean( dataSet.getProperty( DRAFT ).getString() ) );
        }

        if ( dataSet.hasData( CONTENT_DATA ) )
        {
            builder.contentData( new ContentData( dataSet.getDataSet( CONTENT_DATA ).toRootDataSet() ) );
        }

        if ( dataSet.hasData( METADATA ) )
        {
            List<Metadata> metadataList = new ArrayList<>();
            DataSet data = dataSet.getDataSet( METADATA );
            for ( String name : data.getDataNames() )
            {
                metadataList.add( new Metadata( MetadataSchemaName.from( name ), data.getDataSet( name ).toRootDataSet() ) );
            }

            builder.metadata( metadataList );
        }

        if ( dataSet.hasData( FORM ) )
        {
            builder.form( FORM_SERIALIZER.fromData( dataSet.getDataSet( FORM ) ) );
        }

        if ( dataSet.hasData( PAGE ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( dataSet.getDataSet( PAGE ) ) );
        }

        return builder;
    }

    RootDataSet toData( final CreateContentParams params )
    {
        final RootDataSet contentAsData = new RootDataSet();

        addPropertyIfNotNull( contentAsData, DRAFT, params.isDraft() );
        addPropertyIfNotNull( contentAsData, DISPLAY_NAME_FIELD_NAME, params.getDisplayName() );
        addPropertyIfNotNull( contentAsData, CONTENT_TYPE_FIELD_NAME, params.getContentType() );

        if ( params.getContentData() != null )
        {
            contentAsData.add( params.getContentData().toDataSet( CONTENT_DATA ) );
        }

        if ( params.getMetadata() != null )
        {
            final DataSet dataSet = new DataSet( METADATA );

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
