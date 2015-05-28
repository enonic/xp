module api.content {

    export interface PublishContentResult {

        successes: {id:string; name:string}[];

        failures: {path:string; reason:string}[];

        deleted: {id:string; name:string}[];
    }
}