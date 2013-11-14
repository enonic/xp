module api_facet {

    export interface FacetJson {
        name: string;
        displayName: string;
        _type: string;
        count?:number;
        entries?: {
            name?: string;
            displayName?: string;
            count?: number;
            time?: number;
        }[];
        ranges?: {
            from: string;
            to: string;
            total_count: number;
        }[];
    }
}