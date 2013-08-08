package com.enonic.wem.admin.rest.resource.relationship.model;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

public class RelationshipKeyParam
{
    private String fromContent;

    private String toContent;

    private String type;

    public RelationshipKeyParam()
    {
    }

    public RelationshipKeyParam( String str )
    {
    }

    public String getFromContent()
    {
        return fromContent;
    }

    public void setFromContent( final String fromContent )
    {
        this.fromContent = fromContent;
    }

    public String getToContent()
    {
        return toContent;
    }

    public void setToContent( final String toContent )
    {
        this.toContent = toContent;
    }

    public String getType()
    {
        return type;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public RelationshipKey from()
    {
        final QualifiedRelationshipTypeName type = QualifiedRelationshipTypeName.from( this.type );
        final ContentId fromContent = new ContentId( this.fromContent );
        final ContentId toContent = new ContentId( this.toContent );

        return RelationshipKey.from( type, fromContent, toContent );
    }
}
