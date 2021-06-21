package com.enonic.xp.admin.impl.rest.resource.content;

import java.util.Optional;

import com.enonic.xp.content.Content;
import com.enonic.xp.core.internal.Interpolator;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Objects.requireNonNullElse;

public class ContentListTitleResolver
{
    private final ContentTypeService contentTypeService;

    public ContentListTitleResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public String resolve( final Content content )
    {
        final ContentType contentType = contentTypeService.getByName( GetContentTypeParams.from( content.getType() ) );

        final String listTitleExpression = Optional.ofNullable( contentType )
            .map( ContentType::getSchemaConfig )
            .map( sc -> sc.getValue( "listTitleExpression" ) )
            .orElse( "" );

        if ( nullToEmpty( listTitleExpression ).isBlank() )
        {
            return content.getDisplayName();
        }
        else
        {
            final PropertyTree propertyTree = new PropertyTree();
            if ( content.getData() != null )
            {
                propertyTree.addSet( "data", content.getData().getRoot().detach() );
            }
            propertyTree.addString( "displayName", content.getDisplayName() );

            return Interpolator.classic().interpolate( listTitleExpression, k -> requireNonNullElse( propertyTree.getString( k ), "" ) );
        }
    }
}
