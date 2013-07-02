module api_remote {

    export interface UserStore {
        name: string;
        defaultStore: bool;
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
        readOnly: bool;
        remote: bool;
        required: bool;
        iso: bool;
    }

    export interface UserPolicy {
        create: bool;
        updatePassword: bool;
        update: bool;
        delete: bool;
    }

    export interface GroupPolicy {
        create: bool;
        read: bool;
        update: bool;
        delete: bool;
    }

    export interface UserStoreConnector {
        name: string;
        pluginType: string;
        canCreateUser: bool;
        canUpdateUser: bool;
        canUpdateUserPassword: bool;
        canDeleteUser: bool;
        canCreateGroup: bool;
        canUpdateGroup: bool;
        canDeleteGroup: bool;
        canReadGroup: bool;
        canResurrectDeletedGroups: bool;
        canResurrectDeletedUsers: bool;
        groupsLocal: bool;
    }
}