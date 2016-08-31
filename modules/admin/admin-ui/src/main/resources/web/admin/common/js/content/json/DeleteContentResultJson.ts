module api.content.json {

    export interface DeleteContentResultJson {

        success: number;

        pending: number;

        failure: number;

        failureReason: string;
    }
}