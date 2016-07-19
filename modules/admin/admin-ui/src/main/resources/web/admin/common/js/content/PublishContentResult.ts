module api.content {

    export interface PublishContentResult {

        successes: number;

        failures: number;

        deleted: number;

        contentName: string;
    }
}