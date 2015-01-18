module api.module.json {

    export interface ModuleJson extends api.item.ItemJson {

        key:string;

        version:string;

        displayName:string;

        info:string;

        url:string;

        vendorName:string;

        vendorUrl:string;

        state: string;

        config: api.form.json.FormJson;

        moduleDependencies: string[];

        contentTypeDependencies: string[];

        metaSteps: string[];

        minSystemVersion: string;

        maxSystemVersion: string;
    }
}