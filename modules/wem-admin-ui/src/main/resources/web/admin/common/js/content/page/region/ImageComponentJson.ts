module api.content.page.region {


    export interface ImageComponentJson extends ComponentJson {
        
        image:string;

        config: api.data.PropertyArrayJson[];
    }
}