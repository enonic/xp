module api.content.page.region {

    export interface DescriptorBasedComponentJson extends ComponentJson {

        descriptor:string;

        config: api.data.PropertyArrayJson[];
    }
}