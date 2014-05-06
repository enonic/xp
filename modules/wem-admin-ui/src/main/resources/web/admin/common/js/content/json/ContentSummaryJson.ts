module api.content.json {

    export interface ContentSummaryJson extends ContentIdBaseItemJson, api.item.ItemJson {

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

        draft:boolean;

    }
}