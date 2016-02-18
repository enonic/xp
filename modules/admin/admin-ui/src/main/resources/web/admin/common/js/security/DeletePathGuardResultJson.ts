module api.security {

    export interface DeletePathGuardResultJson {

        key: string;

        deleted: boolean;

        reason: string;

    }
}