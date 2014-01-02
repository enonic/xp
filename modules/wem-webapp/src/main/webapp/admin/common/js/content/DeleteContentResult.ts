module api.content {

    export interface DeleteContentResult  {

        successes: {path:string}[];

        failures: {path:string; reason:string}[];
    }
}