package com.enonic.wem.core.content;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.core.schema.content.ContentTypeNodeTranslator;

public class ContentPropertyIndexConfigVisitor
    extends PropertyVisitor
{
    private final EntityIndexConfig.Builder builder;

    public static final PropertyIndexConfig CONTENT_DEFAULT_INDEX_CONFIG = PropertyIndexConfig.newPropertyIndexConfig().
        tokenizedEnabled( false ).
        fulltextEnabled( false ).
        enabled( false ).
        build();


    public ContentPropertyIndexConfigVisitor( final EntityIndexConfig.Builder builder )
    {
        this.builder = builder;
    }


    @Override
    public void visit( final Property property )
    {
        if ( property.getPath().startsWith( DataPath.from( ContentNodeTranslator.CONTENT_DATA_PATH ) ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                tokenizedEnabled( true ).
                fulltextEnabled( true ).
                build() );
        }
        else if ( property.getPath().startsWith( DataPath.from( ContentNodeTranslator.FORM_PATH ) ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                tokenizedEnabled( false ).
                fulltextEnabled( false ).
                build() );
        }
        else if ( ContentTypeNodeTranslator.DISPLAY_NAME_PROPERTY.equals( property.getName() ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                tokenizedEnabled( true ).
                fulltextEnabled( true ).
                build() );
        }
        else
        {
            builder.addPropertyIndexConfig( property, CONTENT_DEFAULT_INDEX_CONFIG );
        }
    }
}
