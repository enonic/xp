module api.macro.resource {

    export interface MacroPreviewStringJson {
        macro: string;
    }

    export interface MacroPreviewJson extends MacroPreviewStringJson {
        html: string;
        pageContributions: PageContributionsJson;
    }

    export interface PageContributionsJson {
        bodyBegin: string[];
        bodyEnd: string[];
        headBegin: string[];
        headEnd: string[];
    }
}
