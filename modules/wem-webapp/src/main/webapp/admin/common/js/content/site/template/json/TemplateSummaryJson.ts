module api.content.site.template.json {

    export interface TemplateSummaryJson extends api.item.ItemJson {

        key:string;

        name:string;

        displayName:string;

        description:string;

        editable:boolean;

        deletable:boolean;

        version:string;

        templateType: string;
    }
}