package com.enonic.wem.admin.rest.resource.schema;


import java.util.HashMap;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.Schema;
import com.enonic.wem.api.schema.SchemaName;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypeParams;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.GetRelationshipTypeParams;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;

public class SchemaIconResolver
{
    private ContentTypeService contentTypeService;

    private MixinService mixinService;

    private RelationshipTypeService relationshipTypeService;

    private HashMap<ContentTypeName, ContentType> contentTypesByName = new HashMap<>();

    public SchemaIconResolver( final ContentTypeService contentTypeService, final MixinService mixinService,
                               final RelationshipTypeService relationshipTypeService )
    {
        this.contentTypeService = contentTypeService;
        this.mixinService = mixinService;
        this.relationshipTypeService = relationshipTypeService;
    }

    public SchemaIconResolver( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public SchemaIconResolver( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    public SchemaIconResolver( final RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public Icon resolveFromName( SchemaName schemaName )
    {
        if ( schemaName instanceof ContentTypeName )
        {
            return resolveFromName( (ContentTypeName) schemaName );
        }
        else if ( schemaName instanceof MixinName )
        {
            return resolveFromName( (MixinName) schemaName );
        }
        else if ( schemaName instanceof RelationshipTypeName )
        {
            return resolveFromName( (RelationshipTypeName) schemaName );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown SchemaName type: " + schemaName.getClass().getSimpleName() );
        }
    }

    public Icon resolveFromName( ContentTypeName contentTypeName )
    {
        final ContentType contentType = findContentTypeIcon( contentTypeName );
        return contentType != null ? contentType.getIcon() : null;
    }

    public Icon resolveFromName( MixinName mixinName )
    {
        final Mixin mixin = mixinService.getByName( new GetMixinParams( mixinName ) );
        return mixin == null ? null : mixin.getIcon();
    }

    public Icon resolveFromName( RelationshipTypeName relationshipTypeName )
    {
        final GetRelationshipTypeParams params = new GetRelationshipTypeParams().name( relationshipTypeName );
        final RelationshipType relationshipType = relationshipTypeService.getByName( params );
        return relationshipType == null ? null : relationshipType.getIcon();
    }

    public Icon resolveFromSchema( Schema schema )
    {
        if ( schema instanceof ContentType )
        {
            return resolveFromSchema( (ContentType) schema );
        }
        else if ( schema instanceof Mixin )
        {
            return resolveFromSchema( (Mixin) schema );
        }
        else if ( schema instanceof RelationshipType )
        {
            return resolveFromSchema( (RelationshipType) schema );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown SchemaName type: " + schema.getClass().getSimpleName() );
        }
    }

    public Icon resolveFromSchema( final ContentType contentType )
    {
        if ( contentType.getIcon() != null )
        {
            return contentType.getIcon();
        }
        else
        {
            final ContentType superTypeWithIcon = findContentTypeIcon( contentType.getSuperType() );
            return superTypeWithIcon.getIcon();
        }
    }

    public Icon resolveFromSchema( final Mixin mixin )
    {
        if ( mixin.getIcon() != null )
        {
            return mixin.getIcon();
        }
        return null;
    }

    public Icon resolveFromSchema( RelationshipType relationshipType )
    {
        if ( relationshipType.getIcon() != null )
        {
            return relationshipType.getIcon();
        }
        return null;
    }

    private ContentType findContentTypeIcon( final ContentTypeName contentTypeName )
    {
        ContentType contentType = getContentType( contentTypeName );
        if ( contentType == null )
        {
            return null;
        }
        else if ( contentType.getIcon() != null )
        {
            return contentType;
        }

        do
        {
            contentType = getContentType( contentType.getSuperType() );
            if ( contentType != null && contentType.getIcon() != null )
            {
                return contentType;
            }
        }
        while ( contentType != null );
        return null;
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        ContentType contentType = this.contentTypesByName.get( contentTypeName );
        if ( contentType != null )
        {
            return contentType;
        }
        else
        {
            final GetContentTypeParams params = new GetContentTypeParams().contentTypeName( contentTypeName );
            contentType = contentTypeService.getByName( params );
            if ( contentType == null )
            {
                return null;
            }
            this.contentTypesByName.put( contentType.getName(), contentType );
            return contentType;
        }

    }
}
