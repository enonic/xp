package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class ContentTypeEntityIndexConfigFactory
{
    public static final PropertyIndexConfig CONTENT_TYPE_DEFAULT_INDEX_CONFIG = PropertyIndexConfig.newPropertyIndexConfig().
        tokenizedEnabled( false ).
        fulltextEnabled( false ).
        enabled( false ).
        build();

    public static final String CONTENT_TYPE_COLLECTION_NAME = "content-type";

    public static EntityIndexConfig create( final RootDataSet rootDataSet )
    {
        final EntityIndexConfig.Builder builder = EntityIndexConfig.newEntityIndexConfig();

        builder.collection( CONTENT_TYPE_COLLECTION_NAME );

        PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                if ( ContentTypeNodeTranslator.DISPLAY_NAME_PROPERTY.equals( property.getName() ) )
                {
                    builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                        enabled( true ).
                        tokenizedEnabled( true ).
                        fulltextEnabled( true ).
                        build() );
                }
                else
                {
                    builder.addPropertyIndexConfig( property, CONTENT_TYPE_DEFAULT_INDEX_CONFIG );
                }
            }


        };

        visitor.traverse( rootDataSet );

        return builder.build();
    }

}
