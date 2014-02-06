module api.content.page {

    export interface PageTemplateSummaryLoaderListener extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (templates:PageTemplateSummary[]) => void;

    }

}