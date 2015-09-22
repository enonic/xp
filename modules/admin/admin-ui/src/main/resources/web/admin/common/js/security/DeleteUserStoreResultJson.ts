module api.security {

    export interface DeleteUserStoreResultJson {

        userStoreKey: string;

        deleted: boolean;

        reason: string;

    }
}