module api_remote {

    export interface ContentData {
        name: string;
        path: string;
        type: string;
        value;
    }

    export interface ContentDataSet extends ContentData {
        value: ContentData[];
    }

    export interface ContentDataProperty  extends ContentData{
        value: string;
    }

    export interface Content {
        id: string;
        path: string;
        name?: string;
        type: string;
        displayName: string;
        owner: string;
        modifier: string;
        isRoot: bool;
        modifiedTime: Date;
        createdTime: Date;
        data: ContentData[];
        iconUrl: string;
    }

}