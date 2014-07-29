module api.content.site.template {

    export interface TemplateSummaryJson extends api.item.ItemJson {

        key:string;

        parentKey:string;

        name:string;

        displayName:string;

        description:string;

        editable:boolean;

        deletable:boolean;

        version:string;

        templateType: string;

        iconUrl: string;
    }
}