package com.enonic.wem.api.command.schema.relationship;


public enum DeleteRelationshipTypeResult
{
    SUCCESS, NOT_FOUND;

    public boolean isNotFound()
    {
        return equals( NOT_FOUND );
    }
}
