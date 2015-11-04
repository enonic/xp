module api.content {

    export interface DeleteContentResultJson {

        successes: {id:string; name:string; type:string}[];

        pendings: {id:string; name:string}[];

        failures: {id:string; name:string; type:string; reason:string}[];
    }
}