module api.content.page.image {

    export interface ImageComponentJson extends api.content.page.PageComponentJson {

        image:string;

        config: api.data2.PropertyArrayJson[];
    }
}