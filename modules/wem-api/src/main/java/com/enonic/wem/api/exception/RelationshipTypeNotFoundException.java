package com.enonic.wem.api.exception;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.NotFoundException;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;

public final class RelationshipTypeNotFoundException
    extends NotFoundException
{
    public RelationshipTypeNotFoundException( final RelationshipTypeName name )
    {
        super( "RelationshipType [{0}] was not found", name );
    }

    public RelationshipTypeNotFoundException( final RelationshipTypeNames names )
    {
        super( MessageFormat.format( "RelationshipTypes [{0}] were not found", Joiner.on( ", " ).join( names ) ) );
    }
}
