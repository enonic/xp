module api.macro.resource {

    export interface MacroPreviewJson {
        html: string;
        macro: string;
        pageContributions: PageContributionsJson;
    }

    export interface PageContributionsJson {
        bodyBegin: string[];
        bodyEnd: string[];
        headBegin: string[];
        headEnd: string[];
    }
}