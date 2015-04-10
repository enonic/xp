module api.content {

    export interface MoveContentResultJson {

        successes: { contentId:string }[];

        failures: { contentId:string; reason:string }[];
    }
}