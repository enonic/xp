module api_content_json{

    export interface ContentSummaryJson extends api_item.ItemJson{

        name:string;

        displayName:string;

        path:string;

        isRoot:boolean;

        hasChildren:boolean;

        type:string;

        iconUrl:string;

        modifier:string;

        owner:string;

        isSite:boolean;

        isPage:boolean;

    }
}