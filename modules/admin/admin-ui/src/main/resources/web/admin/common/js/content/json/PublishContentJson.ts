module api.content.json {

    export interface PublishContentJson {

        successes: number;

        failures: number;

        deleted: number;

        contentName: string;
    }
}