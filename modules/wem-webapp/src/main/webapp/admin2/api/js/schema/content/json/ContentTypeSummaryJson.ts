module api_schema_content_json{

    export interface ContentTypeSummaryJson extends api_item.ItemJson {

        abstract:boolean;

        allowChildContent:boolean;

        contentDisplayNameScript: string;

        displayName:string;

        final: boolean;

        iconUrl: string;

        module:string;

        name:string;

        qualifiedName:string;

        superType:string;

        createdTime:string;

        modifiedTime:string;

        owner:string;

        modifier:string;
    }
}