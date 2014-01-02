module api.app.wizard {

    export interface DisplayNameGenerator {

        hasScript(): boolean;

        execute(): string;
    }
}