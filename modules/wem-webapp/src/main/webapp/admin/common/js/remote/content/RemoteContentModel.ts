module api_remote_content {

    export interface Data {
        name: string;
        path: string;
        type: string;
        value;
    }

    export interface DataSet extends Data {
        value: Data[];
    }

    export interface Property  extends Data{
        value: string;
    }

    export interface ContentSummary extends api_remote.Item {
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
        hasChildren: boolean;
    }

    export interface Content extends ContentSummary{
        isRoot: boolean;
        data: Data[];
    }

    export interface ContentList extends ContentSummary{
        allowsChildren: boolean;
    }

    export interface ContentFind extends ContentList{
        order: number;
        score: number;
    }

    export interface ContentFacet {
        name: string;
        displayName: string;
        _type: string;
        count?:number;
        entries?: {
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

    export interface ContentTreeNode {
        allowsChildren:boolean;
        contents:ContentTreeNode[];
        createdTime?:Date;
        deletable:boolean;
        displayName:string;
        editable:boolean;
        hasChildren:boolean;
        iconUrl:string;
        id:string;
        modifiedTime?:Date;
        modifier:string;
        name:string;
        owner:string;
        path:string;
        type:string;
    }

    export interface GetParams {
        path?: string;
        contentIds?: string[];
    }

    export interface GetResult {
        content: api_remote_content.Content[];
    }

    export interface ListParams {
        path: string;
    }

    export interface ListResult {
        total: number;
        contents: ContentList[];
    }

    export interface FindParams {
        fulltext?: string;
        includeFacets?: boolean;
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

    export interface FindResult {
        total: number;
        contents: ContentFind[];
        facets?: ContentFacet[];
    }

    export interface ValidateParams {
        qualifiedContentTypeName: string;
        contentData: Data;
    }

    export interface ValidateResult {
        hasError: boolean;
        errors: {
            path: string;
            message: string;
        }[];
    }

    export interface DeleteParams {
        contentPaths: string[];
    }

    export interface DeleteResult {
        successes: {
            path:string;
        }[];
        failures: {
            path:string;
            reason:string;
        }[];
    }

    export interface CreateOrUpdateParams {
        contentId?: string;
        draft?: boolean;
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

    export interface CreateOrUpdateResult{
        created: boolean;
        updated: boolean;
        contentId?: string;
        contentPath?: string;
        failure?: string;
    }

    export interface GetTreeParams {
        contentIds?:string[];
    }

    export interface GetTreeResult {
        total:number;
        contents:ContentTreeNode[];
    }

}