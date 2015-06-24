module api.content {

    export interface DeleteContentResultJson {

        successes: {id:string; name:string}[];

        pendings: {name:string}[];

        failures: {id:string; name:string; reason:string}[];
    }
}