module api.query.filter {

    export interface FilterTypeWrapperJson {

        RangeFilter?: api.query.filter.RangeFilterJson;
        BooleanFilter?: api.query.filter.BooleanFilterJson;

    }

}