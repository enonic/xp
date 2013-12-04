module api_module_json{

    export interface ModuleSummaryJson extends api_item.ItemJson{

        key:string;

        name:string;

        version:string;

        displayName:string;

        info:string;

        url:string;

        vendorName:string;

        vendorUrl:string;

    }
}