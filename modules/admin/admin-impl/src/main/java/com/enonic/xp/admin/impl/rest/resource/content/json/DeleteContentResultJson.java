package com.enonic.xp.admin.impl.rest.resource.content.json;

public class DeleteContentResultJson
{
    private Integer success = 0;

    private Integer pending = 0;

    private String failureReason;

    @SuppressWarnings("unused")
    public Integer getSuccess()
    {
        return success;
    }

    public void setSuccess( final Integer success )
    {
        if(success != null)
        {
            this.success = success;
        }
    }

    public void addSuccess( final Integer success )
    {
        this.success += success;
    }

    @SuppressWarnings("unused")
    public Integer getPending()
    {
        return pending;
    }

    public void setPending( final Integer pending )
    {
        if(pending != null)
        {
            this.pending = pending;
        }
    }

    public void addPending( final Integer pending )
    {
        this.pending += pending;
    }

    @SuppressWarnings("unused")
    public String getFailureReason()
    {
        return failureReason;
    }

    public void setFailureReason( final String failureReason )
    {
        this.failureReason = failureReason;
    }
}
