package com.enonic.wem.api.form.inputtype;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.xml.DomBuilder;
import com.enonic.wem.api.xml.DomHelper;

final class ContentSelectorConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<ContentSelectorConfig>
{
    static final ContentSelectorConfigXmlSerializer DEFAULT = new ContentSelectorConfigXmlSerializer();

    private static final String RELATIONSHIP_TYPE_ELEMENT = "relationship-type";

    private static final String ALLOWED_CONTENT_TYPE_ELEMENT = "allow-content-type";

    @Override
    protected void serializeConfig( final ContentSelectorConfig contentSelectorConfig, final DomBuilder builder )
    {
        builder.start( RELATIONSHIP_TYPE_ELEMENT );
        if ( contentSelectorConfig.getRelationshipType() != null )
        {
            builder.text( contentSelectorConfig.getRelationshipType().toString() );
        }
        builder.end();

        for ( ContentTypeName allowedContentTypeName : contentSelectorConfig.getAllowedContentTypes() )
        {
            builder.start( ALLOWED_CONTENT_TYPE_ELEMENT );
            builder.text( allowedContentTypeName.toString() );
            builder.end();
        }
    }

    @Override
    public ContentSelectorConfig parseConfig( final Element elem )
    {
        final ContentSelectorConfig.Builder builder = ContentSelectorConfig.newRelationshipConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, RELATIONSHIP_TYPE_ELEMENT );
        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( RelationshipTypeName.from( text ) );
        }

        final List<Element> allowContentTypeEls = DomHelper.getChildElementsByTagName( elem, ALLOWED_CONTENT_TYPE_ELEMENT );
        for ( Element allowContentTypeEl : allowContentTypeEls )
        {
            final String allowContentTypeText = DomHelper.getTextValue( allowContentTypeEl );
            if ( StringUtils.isNotBlank( allowContentTypeText ) )
            {
                builder.addAllowedContentType( ContentTypeName.from( allowContentTypeText ) );
            }
        }

        return builder.build();
    }
}
