module api_content{

    export interface FindContentResult<T>{

        facets:api_remote_content.ContentFacet[];

        contents:T[];

    }
}