module api.content.page.json{

    export interface BaseDescriptorJson{

        name:string;

        displayName:string;

        controller:string;

        configForm: api.form.json.FormJson;
    }
}