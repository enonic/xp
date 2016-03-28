module api.content.json {

    export interface ContentVersionViewJson extends ContentVersionJson {

        workspaces: string[];
    }
}