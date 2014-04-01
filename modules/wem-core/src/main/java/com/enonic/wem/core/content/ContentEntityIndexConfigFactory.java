package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentName;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.EntityPatternIndexConfig;
import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.index.IndexConstants;

public class ContentEntityIndexConfigFactory
{
    public static EntityIndexConfig create( final CreateContent createContent )
    {

        final ContentName contentName = createContent.getName();
        // Content name unknown should also trigger skip, but this has to wait until rename handling is fixed

        boolean skipIndex = createContent.isEmbed(); // || contentName.isUnnamed();

        return doCreateEntityIndexConfig( skipIndex );
    }


    public static EntityIndexConfig create( final Content content )
    {
        final ContentName contentName = content.getName();

        boolean skipIndex = content.isEmbedded(); // || contentName.isUnnamed();

        return doCreateEntityIndexConfig( skipIndex );
    }

    private static EntityIndexConfig doCreateEntityIndexConfig( final boolean skipIndex )
    {
        final EntityPatternIndexConfig.Builder builder = EntityPropertyIndexConfig.newPatternIndexConfig().
            collection( IndexConstants.CONTENT_COLLECTION_NAME ).
            skip( skipIndex ).
            analyzer( "content_default" ).
            addConfig( ContentDataSerializer.PAGE, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addConfig( ContentDataSerializer.CONTENT_DATA, PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG ).
            addConfig( ContentDataSerializer.FORM, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addConfig( ContentDataSerializer.SITE, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            decideFulltextByValueType( true ).
            defaultConfig( PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                nGramEnabled( true ).
                build() );

        return builder.build();
    }
}
