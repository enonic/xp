module api.query.aggregation {

    export interface TermsAggregationQueryJson {
        name: string;
        fieldName: string;
        size: number;
        orderByDirection: string;
        orderByType: string;
    }
}