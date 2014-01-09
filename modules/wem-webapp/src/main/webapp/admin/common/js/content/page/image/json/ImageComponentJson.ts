module api.content.page.image.json {

    export interface ImageComponentJson extends api.content.page.json.PageComponentJson {

        name:string;

        image:string;

        config:api.data.json.DataTypeWrapperJson[];
    }
}