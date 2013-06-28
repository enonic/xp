module api_remote {

    export interface Account {
        key: string;
        type: string;
        name: string;
        userStore: string;
        qualifiedName: string;
        builtIn: bool;
        displayName: string;
        modifiedTime: Date;
        createdTime: Date;
        editable: bool;
        deleted: bool;
        image_url: string;
        email?: string;
    }

    export interface AccountFacet {
        name: string;
        terms: AccountFacetEntry[];
    }

    export interface AccountFacetEntry {
        name: string;
        count: number;
    }

}