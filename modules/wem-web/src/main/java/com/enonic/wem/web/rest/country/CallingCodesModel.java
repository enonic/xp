package com.enonic.wem.web.rest.country;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents response for CallingCodeResource,
 * it then will be converted to JSON
 */
public class CallingCodesModel
{

    private int total;

    public List<CallingCodeModel> codes;

    public CallingCodesModel()
    {
        codes = new ArrayList<CallingCodeModel>();
    }

    public List<CallingCodeModel> getCodes()
    {
        return codes;
    }

    public void setCodes( List<CallingCodeModel> codes )
    {
        this.codes = codes;
    }

    public int getTotal()
    {
        return total;
    }

    public void setTotal( int total )
    {
        this.total = total;
    }

    public void addCode( CallingCodeModel code )
    {
        this.codes.add( code );
    }


}
