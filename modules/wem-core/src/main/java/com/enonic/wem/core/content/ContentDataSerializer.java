package com.enonic.wem.core.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.Metadata;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.core.content.page.PageDataSerializer;
import com.enonic.wem.core.content.site.SiteDataSerializer;
import com.enonic.wem.core.form.FormDataSerializer;

public class ContentDataSerializer
    extends AbstractDataSetSerializer<Content, Content.Builder>
{
    public static final String DISPLAY_NAME_FIELD_NAME = "displayName";

    public static final String DRAFT = "draft";

    public static final String CONTENT_DATA = "data";

    public static final String METADATA = "metadata";

    public static final String CONTENT_TYPE_FIELD_NAME = "contentType";

    public static final String FORM = "form";

    public static final String PAGE = "page";

    public static final String SITE = "site";

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
        if ( content.hasSite() )
        {
            contentAsData.add( SITE_SERIALIZER.toData( content.getSite() ) );
        }

        return contentAsData;
    }


    public Content.Builder fromData( final DataSet dataSet )
    {
        final Content.Builder builder = Content.newContent();

        if ( dataSet.hasData( DISPLAY_NAME_FIELD_NAME ) )
        {
            builder.displayName( dataSet.getProperty( DISPLAY_NAME_FIELD_NAME ).getString() );
        }

        if ( dataSet.hasData( CONTENT_TYPE_FIELD_NAME ) )
        {
            builder.type( ContentTypeName.from( dataSet.getProperty( CONTENT_TYPE_FIELD_NAME ).getString() ) );
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

        if ( dataSet.hasData( SITE ) )
        {
            builder.site( SITE_SERIALIZER.fromData( dataSet.getDataSet( SITE ) ) );
        }

        if ( dataSet.hasData( DRAFT ) )
        {
            builder.draft( Boolean.parseBoolean( dataSet.getProperty( DRAFT ).getString() ) );
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
            contentAsData.add( params.getContentData().toDataSet( ContentDataSerializer.CONTENT_DATA ) );
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
