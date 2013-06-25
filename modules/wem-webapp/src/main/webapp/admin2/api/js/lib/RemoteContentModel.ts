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

    export interface ContentBase {
        id: string;
        path: string;
        name: string;
        type: string;
        displayName: string;
        owner: string;
        modifier: string;
        modifiedTime: Date;
        createdTime: Date;
        iconUrl: string;
    }

    export interface ContentGet extends ContentBase{
        isRoot: bool;
        data: ContentData[];
    }

    export interface ContentList extends ContentBase{
        editable: bool;
        deletable: bool;
        allowsChildren: bool;
    }
}