module api.content.json {

    export interface DeleteContentResultJson {

        success: number;

        pending: number;

        failure: number;

        contentName: string;

        contentType: string;

        failureReason: string;
    }
}