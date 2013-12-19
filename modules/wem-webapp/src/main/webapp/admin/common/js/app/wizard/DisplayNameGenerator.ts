module api_app_wizard {

    export interface DisplayNameGenerator {

        hasScript(): boolean;

        execute(): string;
    }
}