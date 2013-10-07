module api_remote_account {

    export interface Account {
        key: string;
        type: string;
        name: string;
        userStore: string;
        qualifiedName: string;
        builtIn: boolean;
        displayName: string;
        modifiedTime: Date;
        createdTime: Date;
        editable: boolean;
        deleted: boolean;
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
        htmlEmail?: boolean;
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

    export interface FindResult {
        accounts: Account[];
        facets?: AccountFacet[];
        total?: number;
    }

    export interface GetGraphParams {
        key: string;
    }

    export interface GetGraphResult {
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

    export interface ChangePasswordResult {
    }

    export interface VerifyUniqueEmailParams {
        userStore: string;
        email: string;
    }

    export interface VerifyUniqueEmailResult {
        emailInUse: boolean;
        key: string;
    }

    export interface SuggestUserNameParams {
        userStore: string;
        firstName: string;
        lastName: string;
    }

    export interface SuggestUserNameResult {
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

    export interface CreateOrUpdateResult {
        created: boolean;
        updated: boolean;
    }

    export interface DeleteParams {
        key:string[];
    }

    export interface DeleteResult {
        deleted:number;
    }

    export interface GetParams {
        key:string;
    }

    export interface GetResult extends Account {

    }

}