module api.content.page {

    export interface TemplateSummaryLoaderListener<T extends TemplateSummary> extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (contentSummaries:T[]) => void;

    }

}