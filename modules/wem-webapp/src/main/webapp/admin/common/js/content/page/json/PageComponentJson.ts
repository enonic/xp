module api.content.page.json {

    export interface PageComponentJson {

        name:string;

        descriptor:string;

        config: api.data.json.DataTypeWrapperJson[];
    }
}