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

}