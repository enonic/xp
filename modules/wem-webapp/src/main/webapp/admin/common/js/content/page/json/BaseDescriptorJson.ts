module api_content_page_json{

    export interface BaseDescriptorJson{

        name:string;

        displayName:string;

        controller:string;

        configForm: api_form_json.FormJson;
    }
}