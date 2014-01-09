module api.content.page.part.json {

    export interface PartComponentJson extends api.content.page.json.PageComponentJson {

        name:string;

        config:api.data.json.DataTypeWrapperJson[];
    }
}