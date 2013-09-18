package com.enonic.wem.admin.rpc.util;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.json.JsonResult;
import com.enonic.wem.core.country.Country;
import com.enonic.wem.core.country.Region;

final class CountryJsonResult
    extends JsonResult
{
    private final Collection<Country> list;

    public CountryJsonResult( final Collection<Country> list )
    {
        this.list = list;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", this.list.size() );

        final ArrayNode array = json.putArray( "countries" );
        for ( final Country model : this.list )
        {
            array.add( toJson( model ) );
        }
    }

    private ObjectNode toJson( final Country model )
    {
        final ObjectNode json = objectNode();
        json.put( "code", model.getCode().toString() );
        json.put( "englishName", model.getEnglishName() );
        json.put( "localName", model.getLocalName() );
        json.put( "regionsEnglishName", model.getRegionsEnglishName() );
        json.put( "regionsLocalName", model.getRegionsLocalName() );

        final ArrayNode codes = json.putArray( "callingCodes" );
        codes.add( toJson( model.getCallingCode(), model ) );

        final ArrayNode regions = json.putArray( "regions" );
        for ( final Region region : model.getRegions() )
        {
            regions.add( toJson( region ) );
        }

        return json;
    }

    private JsonNode toJson( final String callingCode, final Country model )
    {
        final ObjectNode json = objectNode();
        json.put( "callingCodeId", callingCode + "_" + model.getCode() );
        json.put( "callingCode", "+" + callingCode );
        json.put( "englishName", model.getEnglishName() );
        json.put( "localName", model.getLocalName() );
        return json;
    }

    private ObjectNode toJson( final Region model )
    {
        final ObjectNode json = objectNode();
        json.put( "regionCode", model.getCode() );
        json.put( "englishName", model.getEnglishName() );
        json.put( "localName", model.getLocalName() );
        return json;
    }
}
