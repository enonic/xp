module api_remote_userstore {

    export interface UserStore {
        name: string;
        defaultStore: boolean;
        connectorName: string;
        configXML?: string;
        userFields?: UserStoreFieldConfig[];
        plugin?: string;
        userPolicy?: UserPolicy;
        groupPolicy?: GroupPolicy;
        userCount?: number;
        groupCount?: number;
        roleCount?: number;
        administrators?: string[];
    }

    export interface UserStoreFieldConfig {
        type: string;
        readOnly: boolean;
        remote: boolean;
        required: boolean;
        iso: boolean;
    }

    export interface UserPolicy {
        create: boolean;
        updatePassword: boolean;
        update: boolean;
        delete: boolean;
    }

    export interface GroupPolicy {
        create: boolean;
        read: boolean;
        update: boolean;
        delete: boolean;
    }

    export interface UserStoreConnector {
        name: string;
        pluginType: string;
        canCreateUser: boolean;
        canUpdateUser: boolean;
        canUpdateUserPassword: boolean;
        canDeleteUser: boolean;
        canCreateGroup: boolean;
        canUpdateGroup: boolean;
        canDeleteGroup: boolean;
        canReadGroup: boolean;
        canResurrectDeletedGroups: boolean;
        canResurrectDeletedUsers: boolean;
        groupsLocal: boolean;
    }

    export interface GetAllParams {
    }

    export interface GetAllResult {
        total: number;
        userStores: UserStore[];
    }

    export interface GetParams {
        name: string;
    }

    export interface GetResult extends UserStore {
        success: boolean;
        error?: string;
    }

    export interface GetConnectorsParams {
    }

    export interface GetConnectorsResult {
        total: number;
        userStoreConnectors: UserStoreConnector[];
    }

    export interface CreateOrUpdateParams {
        name: string[];
        defaultUserstore: boolean;
        configXML: string;
        connectorName: string;
        administrators: string[];
    }

    export interface CreateOrUpdateResult {
        created: boolean;
        updated: boolean;
    }

    export interface DeleteParams {
        name: string[];
    }

    export interface DeleteResult {
        deleted: number;
    }
}