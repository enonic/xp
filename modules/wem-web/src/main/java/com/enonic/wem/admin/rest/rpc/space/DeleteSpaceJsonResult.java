package com.enonic.wem.admin.rest.rpc.space;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;

final class DeleteSpaceJsonResult
    extends JsonResult
{
    private final boolean deleted;

    private final String failureReason;

    private DeleteSpaceJsonResult( final boolean deleted, final String failureReason )
    {
        this.deleted = deleted;
        this.failureReason = failureReason;
    }

    public static DeleteSpaceJsonResult success()
    {
        return new DeleteSpaceJsonResult( true, null );
    }

    public static DeleteSpaceJsonResult failure( final String failureReason )
    {
        return new DeleteSpaceJsonResult( false, failureReason );
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "deleted", deleted );
        if ( failureReason != null )
        {
            json.put( "reason", failureReason );
        }
    }

}
