package com.enonic.wem.core.content;

import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.core.content.page.PageSerializer;
import com.enonic.wem.core.content.site.SiteSerializer;
import com.enonic.wem.core.support.SerializerForFormItemToData;

public class ContentNodeRootDataSetBuilder
{
    private static final PageSerializer PAGE_SERIALIZER = new PageSerializer();

    private static final SiteSerializer SITE_SERIALIZER = new SiteSerializer();

    private static final SerializerForFormItemToData SERIALIZER_FOR_FORM_ITEM_TO_DATA = new SerializerForFormItemToData();

    final RootDataSet rootDataSet;

    public ContentNodeRootDataSetBuilder()
    {
        rootDataSet = new RootDataSet();
    }

    public static Builder newRootDataSet()
    {
        return new Builder();
    }


    public static class Builder
    {
        private RootDataSet rootDataSet = new RootDataSet();

        public Builder addPage( final Page page )
        {
            final DataSet pageData = PAGE_SERIALIZER.toData( page, ContentNodeTranslator.PAGE_CONFIG_PATH );

            if ( pageData != null )
            {
                this.rootDataSet.add( pageData );
            }

            return this;
        }

        public Builder addSite( final Site site )
        {
            final DataSet siteData = SITE_SERIALIZER.toData( site, ContentNodeTranslator.SITE_CONFIG_PATH );

            if ( siteData != null )
            {
                this.rootDataSet.add( siteData );
            }

            return this;
        }

        public Builder add( final String propertyName, final Object value )
        {
            if ( value != null )
            {
                this.rootDataSet.setProperty( propertyName, new Value.String( value.toString() ) );
            }
            return this;
        }

        public Builder addForm( final Form form )
        {

            if ( form != null )
            {
                final DataSet formDataSet = new DataSet( ContentNodeTranslator.FORM_PATH );
                final DataSet formItems = new DataSet( ContentNodeTranslator.FORMITEMS_DATA_PATH );
                formDataSet.add( formItems );

                for ( Data formData : SERIALIZER_FOR_FORM_ITEM_TO_DATA.serializeFormItems(
                    form != null ? form.getFormItems() : Form.newForm().build().getFormItems() ) )
                {
                    formItems.add( formData );
                }
                rootDataSet.add( formDataSet );
            }

            return this;
        }

        public Builder addContentData( final ContentData contentData )
        {
            this.rootDataSet.add( contentData.toDataSet( ContentNodeTranslator.CONTENT_DATA_PATH ) );
            return this;
        }


    }


}
