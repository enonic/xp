module api.content.page.region {


    export interface FragmentComponentJson extends ComponentJson {

        fragment:string;

        config: api.data.PropertyArrayJson[];
    }
}