module api.content.page.layout.json {

    export interface LayoutComponentJson extends api.content.page.json.PageComponentJson {

        name:string;

        config:api.data.json.DataTypeWrapperJson[];
    }
}