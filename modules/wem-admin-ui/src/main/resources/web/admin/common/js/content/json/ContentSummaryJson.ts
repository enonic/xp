module api.content.json {

    export interface ContentSummaryJson extends ContentIdBaseItemJson, api.item.ItemJson {

        name:string;

        displayName:string;

        path:string;

        isRoot:boolean;

        hasChildren:boolean;

        type:string;

        iconUrl:string;

        thumbnail:ThumbnailJson;

        modifier:string;

        owner:string;

        isSite:boolean;

        siteTemplateKey:string;

        isPage:boolean;

        draft:boolean;

    }
}