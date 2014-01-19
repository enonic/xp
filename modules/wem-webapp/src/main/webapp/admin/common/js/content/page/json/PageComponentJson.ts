module api.content.page.json {

    export interface PageComponentJson {

        type:string;

        name:string;

        template:string;

        config: api.data.json.DataTypeWrapperJson[];
    }
}