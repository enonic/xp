module api_remote_account {

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

    export interface UserProfile {
        country?: string;
        fax?: string;
        description?: string;
        firstName?: string;
        globalPosition?: string;
        homePage?: string;
        initials?: string;
        lastName?: string;
        memberId?: string;
        middleName?: string;
        mobile?: string;
        nickName?: string;
        organization?: string;
        personalId?: string;
        phone?: string;
        prefix?: string;
        suffix?: string;
        title?: string;
        birthday?: Date;
        gender?: string;
        htmlEmail?: bool;
        locale?: string;
        timezone?: string;
        addresses?: Address[];
    }

    export interface Address {
        label?: string;
        country?: string;
        isoCountry?: string;
        region?: string;
        isoRegion?: string;
        postalAddress?: string;
        postalCode?: string;
        street?: string;
    }

    export interface FindParams {
        key?: string[];
        query?: string;
        start?: number;
        limit?: number;
        userstores?: string[];
        sort?: string;
        dir?: string;
        types?: string[];
    }

    export interface FindResult extends api_remote.BaseResult {
        accounts: Account[];
        facets?: AccountFacet[];
        total?: number;
    }

    export interface GetGraphParams {
        key: string;
    }

    export interface GetGraphResult extends api_remote.BaseResult {
        graph: {
            id: string;
            name: string;
            data: {
                type: string;
                key: string;
                image_uri: string;
                name: string;
            };
            adjacencies?: {
                nodeTo: string;
            }[];
        }[];
    }

    export interface ChangePasswordParams {
        key: string;
        password: string;
    }

    export interface ChangePasswordResult extends api_remote.BaseResult {
    }

    export interface VerifyUniqueEmailParams {
        userStore: string;
        email: string;
    }

    export interface VerifyUniqueEmailResult extends api_remote.BaseResult {
        emailInUse: bool;
        key: string;
    }

    export interface SuggestUserNameParams {
        userStore: string;
        firstName: string;
        lastName: string;
    }

    export interface SuggestUserNameResult extends api_remote.BaseResult {
        username: string;
    }

    export interface CreateOrUpdateParams {
        key: string;
        email?: string;
        imageRef?: string;
        profile?: UserProfile;
        members?: string[];
        displayName: string;
        groups?: string[];
    }

    export interface CreateOrUpdateResult extends api_remote.BaseResult {
        created: bool;
        updated: bool;
    }

    export interface DeleteParams {
        key:string[];
    }

    export interface DeleteResult extends api_remote.BaseResult {
        deleted:number;
    }

    export interface GetParams {
        key:string;
    }

    export interface GetResult extends api_remote.BaseResult, Account {

    }

}