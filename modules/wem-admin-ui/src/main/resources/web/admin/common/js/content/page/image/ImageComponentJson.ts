module api.content.page.image {

    export interface ImageComponentJson extends api.content.page.ComponentJson {

        image:string;

        config: api.data.PropertyArrayJson[];
    }
}