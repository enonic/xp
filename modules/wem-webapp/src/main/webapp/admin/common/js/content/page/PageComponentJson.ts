module api.content.page {

    export interface PageComponentJson {

        name:string;

        descriptor:string;

        config: api.data.json.DataTypeWrapperJson[];
    }
}