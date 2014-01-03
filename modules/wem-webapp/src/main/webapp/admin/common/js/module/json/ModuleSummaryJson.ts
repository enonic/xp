module api.module.json{

    export interface ModuleSummaryJson extends api.item.ItemJson{

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