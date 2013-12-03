module api_content {

    export interface DeleteContentResult  {

        successes: {path:string}[];

        failures: {path:string; reason:string}[];
    }
}