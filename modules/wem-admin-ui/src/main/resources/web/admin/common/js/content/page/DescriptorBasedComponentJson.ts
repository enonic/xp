module api.content.page {

    export interface DescriptorBasedComponentJson extends ComponentJson {

        descriptor:string;

        config: api.data.PropertyArrayJson[];
    }
}