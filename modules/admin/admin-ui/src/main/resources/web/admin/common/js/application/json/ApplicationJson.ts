module api.application.json {

    export interface ApplicationJson extends api.item.ItemJson {

        key:string;

        version:string;

        displayName:string;

        info:string;

        url:string;

        vendorName:string;

        vendorUrl:string;

        state: string;

        config: api.form.json.FormJson;

        applicationDependencies: string[];

        contentTypeDependencies: string[];

        metaSteps: string[];

        minSystemVersion: string;

        maxSystemVersion: string;
    }
}