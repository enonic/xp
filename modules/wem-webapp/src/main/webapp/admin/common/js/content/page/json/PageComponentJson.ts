module api.content.page.json {

    export interface PageComponentJson {

        name:string;

        template:string;

        config: api.data.json.DataTypeWrapperJson[];
    }
}