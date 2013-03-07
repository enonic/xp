package com.enonic.wem.api.content.relationship;


public enum DeleteRelationshipResult
{
    SUCCESS, NOT_FOUND;

    public static DeleteRelationshipResult from( Exception e )
    {
        if ( e instanceof RelationshipNotFoundException )
        {
            return NOT_FOUND;
        }
        else
        {
            throw new IllegalArgumentException( "Unable to map exception: " + e.getClass().getName() );
        }
    }
}
