module api.content.page {

    export interface DescriptorJson{

        key:string;

        name:string;

        displayName:string;

        controller:string;

        config: api.form.json.FormJson;
    }
}