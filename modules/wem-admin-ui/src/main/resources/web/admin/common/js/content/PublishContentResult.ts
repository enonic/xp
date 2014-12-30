module api.content {

    export interface PublishContentResult  {

        successes: {id:string; name:string}[];

        failures: {id:string; reason:string}[];
    }
}