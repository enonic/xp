package com.enonic.xp.web;

public enum HttpStatus
{
    OK( 200, "OK" );

    private final int value;

    private final String reasonPhrase;

    HttpStatus( final int value, final String reasonPhrase )
    {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value()
    {
        return this.value;
    }

    public String getReasonPhrase()
    {
        return this.reasonPhrase;
    }

    public boolean is1xxInformational()
    {
        final int code = this.value / 100;
        return code == 1;
    }

    public boolean is2xxSuccessful()
    {
        final int code = this.value / 100;
        return code == 2;
    }

    public boolean is3xxRedirection()
    {
        final int code = this.value / 100;
        return code == 3;
    }

    public boolean is4xxClientError()
    {
        final int code = this.value / 100;
        return code == 4;
    }

    public boolean is5xxServerError()
    {
        final int code = this.value / 100;
        return code == 5;
    }

    @Override
    public String toString()
    {
        return Integer.toString( this.value );
    }
}
