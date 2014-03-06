module api.content.page.text.json {

    export interface TextComponentJson extends api.content.page.json.PageComponentJson {

        text:string;

        name:string;

        config:api.data.json.DataTypeWrapperJson[];
    }
}