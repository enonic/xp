module api.content {

    export interface MoveContentResultJson {

        successes: { name:string }[];

        failures: { name:string; reason:string }[];
    }
}