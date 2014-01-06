module api.content.page.json{

    export interface DescriptorJson{

        name:string;

        displayName:string;

        controller:string;

        configForm: api.form.json.FormJson;
    }
}