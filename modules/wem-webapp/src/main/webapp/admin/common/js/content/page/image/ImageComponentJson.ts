module api.content.page.image {

    export interface ImageComponentJson extends api.content.page.PageComponentJson {

        name:string;

        image:string;

        config:api.data.json.DataTypeWrapperJson[];
    }
}