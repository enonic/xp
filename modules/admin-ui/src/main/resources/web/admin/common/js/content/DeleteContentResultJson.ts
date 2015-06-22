module api.content {

    export interface DeleteContentResultJson {

        successes: {name:string}[];

        pendings: {name:string}[];

        failures: {name:string; reason:string}[];
    }
}