module api.content{

    export interface FindContentResult<T>{

        facets:api.facet.FacetJson[];

        contents:T[];

    }
}