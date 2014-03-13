module api.content.page.part {

    export interface PartComponentJson extends api.content.page.PageComponentJson {

        name:string;

        config:api.data.json.DataTypeWrapperJson[];
    }
}