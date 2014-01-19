module api.content.page.json{

    export interface DescriptorJson{

        key:string;

        name:string;

        displayName:string;

        controller:string;

        config: api.form.json.FormJson;
    }
}