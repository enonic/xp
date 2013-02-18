package com.enonic.wem.web.rest.resource.content;

import java.awt.image.BufferedImage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.BaseTypeKey;
import com.enonic.wem.api.content.mixin.Mixin;
import com.enonic.wem.api.content.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.mixin.QualifiedMixinNames;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.relationshiptype.RelationshipType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.command.Commands.mixin;
import static com.enonic.wem.api.command.Commands.relationshipType;

@Component
@Path("basetype/image")
@Produces("image/*")
public final class BaseTypeImageResource
{
    private final BaseTypeImageHelper helper;

    private Client client;

    public BaseTypeImageResource()
        throws Exception
    {
        this.helper = new BaseTypeImageHelper();
    }

    @GET
    @Path("{baseTypeKey}")
    public Response getBaseTypeIcon( @PathParam("baseTypeKey") final String baseTypeKey,
                                     @QueryParam("size") @DefaultValue("128") final int size )
        throws Exception
    {
        final BaseTypeKey baseType = BaseTypeKey.from( baseTypeKey );

        String mimeType = "image/png";
        BufferedImage baseTypeImage = null;
        if ( baseType.isContentType() )
        {
            final Icon contentTypeIcon =
                findRootContentTypeIcon( new QualifiedContentTypeName( baseType.getModuleName(), baseType.getLocalName() ) );
            baseTypeImage = helper.getIconImage( contentTypeIcon, size );
            mimeType = contentTypeIcon == null ? mimeType : contentTypeIcon.getMimeType();
        }
        else if ( baseType.isRelationshipType() )
        {
            final Icon relationshipTypeIcon =
                findRelationshipTypeIcon( new QualifiedRelationshipTypeName( baseType.getModuleName(), baseType.getLocalName() ) );
            if ( relationshipTypeIcon == null )
            {
                baseTypeImage = helper.getDefaultRelationshipTypeImage( size );
            }
            else
            {
                baseTypeImage = helper.getIconImage( relationshipTypeIcon, size );
                mimeType = relationshipTypeIcon.getMimeType();
            }
        }
        else if ( baseType.isMixin() )
        {
            final Icon mixinIcon = findMixinIcon( new QualifiedMixinName( baseType.getModuleName(), baseType.getLocalName() ) );
            if ( mixinIcon == null )
            {
                baseTypeImage = helper.getDefaultMixinImage( size );
            }
            else
            {
                baseTypeImage = helper.getIconImage( mixinIcon, size );
                mimeType = mixinIcon.getMimeType();
            }
        }

        if ( baseTypeImage == null )
        {
            throw new WebApplicationException( Response.Status.NOT_FOUND );
        }
        return Response.ok( baseTypeImage, mimeType ).build();
    }

    private Icon findRootContentTypeIcon( final QualifiedContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        while ( contentType != null && contentType.getIcon() == null )
        {
            contentType = getContentType( contentType.getSuperType() );
        }
        return contentType == null ? null : contentType.getIcon();
    }

    private ContentType getContentType( final QualifiedContentTypeName contentTypeName )
    {
        if ( contentTypeName == null )
        {
            return null;
        }
        final QualifiedContentTypeNames contentTypeNames = QualifiedContentTypeNames.from( contentTypeName );
        return client.execute( contentType().get().names( contentTypeNames ) ).first();
    }

    private Icon findMixinIcon( final QualifiedMixinName mixinName )
    {
        final QualifiedMixinNames mixinNames = QualifiedMixinNames.from( mixinName );
        Mixin mixin = client.execute( mixin().get().names( mixinNames ) ).first();
        return mixin == null ? null : mixin.getIcon();
    }

    private Icon findRelationshipTypeIcon( final QualifiedRelationshipTypeName relationshipTypeName )
    {
        final QualifiedRelationshipTypeNames relationshipTypeNames = QualifiedRelationshipTypeNames.from( relationshipTypeName );
        RelationshipType relationshipType = client.execute( relationshipType().get().qualifiedNames( relationshipTypeNames ) ).first();
        return relationshipType == null ? null : relationshipType.getIcon();

    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
