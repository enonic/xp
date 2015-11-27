module api.content {

    export interface DeleteContentResultJson {

        successes: {id:string; path: string; name:string; type:string}[];

        pendings: {id:string; path: string; name:string}[];

        failures: {id:string; path: string; name:string; type:string; reason:string}[];
    }
}