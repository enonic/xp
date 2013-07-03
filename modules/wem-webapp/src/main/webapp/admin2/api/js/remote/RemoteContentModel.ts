module api_remote {

    export interface Data {
        name: string;
        path: string;
        type: string;
        value;
    }

    export interface DataSet extends Data {
        value: Data[];
    }

    export interface ContentDataProperty  extends Data{
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
        data: Data[];
    }

    export interface ContentList extends ContentBase{
        editable: bool;
        deletable: bool;
        allowsChildren: bool;
    }

    export interface ContentFind extends ContentList{
        order: number;
        score: number;
    }

    export interface ContentFacet {
        name: string;
        displayName: string;
        _type: string;
        terms?: {
            name?: string;
            displayName?: string;
            count?: number;
            time?: number;
        }[];
        ranges?: {
            from: string;
            to: string;
            total_count: number;
        }[];
    }

    export interface RemoteCallContentGetParams {
        path?: string;
        contentIds?: string[];
    }

    export interface RemoteCallContentGetResult extends RemoteCallResultBase {
        content: ContentGet[];
    }

    export interface RemoteCallContentListParams {
        path: string;
    }

    export interface RemoteCallContentListResult extends RemoteCallResultBase {
        total: number;
        contents: ContentList[];
    }

    export interface RemoteCallContentFindParams {
        fulltext?: string;
        includeFacets?: bool;
        contentTypes: string[];
        spaces?: string[];
        ranges?: {
            lower: string;
            upper: string;
        }[];
        facets: {
            [key:string]:any;
        };
    }

    export interface RemoteCallContentFindResult extends RemoteCallResultBase {
        total: number;
        contents: ContentFind[];
        facets?: ContentFacet[];
    }

    export interface RemoteCallContentValidateParams {
        qualifiedContentTypeName: string;
        contentData: Data;
    }

    export interface RemoteCallContentValidateResult extends RemoteCallResultBase {
        hasError: bool;
        errors: {
            path: string;
            message: string;
        }[];
    }

    export interface RemoteCallContentDeleteParams {
        contentPaths: string[];
    }

    export interface RemoteCallContentDeleteResult extends RemoteCallResultBase {
        successes: {
            path:string;
        }[];
        failures: {
            path:string;
            reason:string;
        }[];
    }

    export interface RemoteCallCreateOrUpdateContentParams {
        contentId?: string;
        temporary?: bool;
        contentName?: string;
        parentContentPath?: string;
        qualifiedContentTypeName: string;
        contentData: {
            [key:string]: string;
        };
        displayName: string;
        attachments?: {
            uploadId: string;
            attachmentName: string;
        }[];
    }

    export interface RemoteCallCreateOrUpdateContentResult extends RemoteCallResultBase{
        created: bool;
        updated: bool;
        contentId?: string;
        contentPath?: string;
        failure?: string;
    }

    export interface RemoteCallGetContentTreeParams {
        contentIds?:string[];
    }

    export interface RemoteCallGetContentTreeResult extends RemoteCallResultBase {
        total:number;
        contents:ContentTreeNode[];
    }

}