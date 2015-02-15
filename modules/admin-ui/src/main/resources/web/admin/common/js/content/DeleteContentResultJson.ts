module api.content {

    export interface DeleteContentResultJson {

        successes: {path:string}[];

        failures: {path:string; reason:string}[];
    }
}