module api.module.json {

    export interface ModuleJson extends api.item.ItemJson {

        key:string;

        name:string;

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

        metadataSchemaDependencies: string[];

        minSystemVersion: string;

        maxSystemVersion: string;
    }
}