package com.enonic.wem.core.content;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.entity.NodePropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;

public class ContentPropertyIndexConfigVisitor
    extends PropertyVisitor
{
    private final NodePropertyIndexConfig.Builder builder;

    public static final PropertyIndexConfig CONTENT_DEFAULT_INDEX_CONFIG = PropertyIndexConfig.create().
        nGramEnabled( false ).
        fulltextEnabled( false ).
        enabled( true ).
        build();

    public static final PropertyIndexConfig CONTENT_ROOT_DEFAULT_INDEX_CONFIG = PropertyIndexConfig.create().
        nGramEnabled( false ).
        fulltextEnabled( false ).
        enabled( true ).
        build();

    public ContentPropertyIndexConfigVisitor( final NodePropertyIndexConfig.Builder builder )
    {
        this.builder = builder;
    }

    @Override
    public void visit( final Property property )
    {
        final DataPath basePath = property.getPath().removeIndexFromLastElement();

        if ( basePath.getParent().equals( DataPath.ROOT ) )
        {
            if ( ContentDataSerializer.DISPLAY_NAME_FIELD_NAME.equals( property.getName() ) )
            {
                builder.addPropertyIndexConfig( property, PropertyIndexConfig.FULL );
            }
            else
            {
                builder.addPropertyIndexConfig( property, CONTENT_ROOT_DEFAULT_INDEX_CONFIG );
            }
        }
        else if ( isChildOf( basePath, ContentDataSerializer.CONTENT_DATA ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.create().
                enabled( true ).
                nGramEnabled( true ).
                fulltextEnabled( true ).
                build() );
        }
        else if ( isChildOf( basePath, ContentNodeTranslator.FORM_PATH ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.create().
                enabled( false ).
                nGramEnabled( false ).
                fulltextEnabled( false ).
                build() );
        }
        else if ( isChildOf( basePath, ContentDataSerializer.PAGE ) )
        {
            builder.addPropertyIndexConfig( property, PropertyIndexConfig.create().
                enabled( false ).
                nGramEnabled( false ).
                fulltextEnabled( false ).
                build() );
        }
        else
        {
            builder.addPropertyIndexConfig( property, CONTENT_DEFAULT_INDEX_CONFIG );
        }
    }

    private boolean isChildOf( final DataPath childPath, final String parent )
    {
        return childPath.getFirstElement().equals( DataPath.from( parent ).getFirstElement() );
    }
}
