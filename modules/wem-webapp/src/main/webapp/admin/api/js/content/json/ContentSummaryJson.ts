module api_content_json{

    export interface ContentSummaryJson extends api_item.ItemJson{

        id:string;

        name:string;

        displayName:string;

        path:string;

        root:boolean;

        hasChildren:boolean;

        type:string;

        iconUrl:string;

        createdTime:string;

        modifiedTime:string;

        modifier:string;

        owner:string;

    }
}