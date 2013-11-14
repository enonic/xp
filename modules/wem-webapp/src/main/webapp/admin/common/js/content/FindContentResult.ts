module api_content{

    export interface FindContentResult<T>{

        facets:api_facet.FacetJson[];

        contents:T[];

    }
}