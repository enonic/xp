package com.enonic.xp.admin.impl.json.content;

public class SvgContentSourceJson
{
    private String svgSource;

    public SvgContentSourceJson( final String svgSource )
    {
        this.svgSource = svgSource;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSvgSource()
    {
        return this.svgSource;
    }
}
