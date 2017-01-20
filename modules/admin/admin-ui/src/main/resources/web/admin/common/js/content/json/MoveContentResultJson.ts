module api.content.json {

    export interface MoveContentResultJson {

        successes: { name: string }[];

        failures: { name: string; reason: string }[];
    }
}
