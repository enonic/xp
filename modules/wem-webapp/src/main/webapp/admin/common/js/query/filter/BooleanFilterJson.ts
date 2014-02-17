module api.query.filter {


    export interface BooleanFilterJson {

        must: api.query.filter.FilterTypeWrapperJson[];
        mustNot: api.query.filter.FilterTypeWrapperJson[];
        should: api.query.filter.FilterTypeWrapperJson[];

    }


}