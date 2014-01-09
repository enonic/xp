module api.schema.content {

    export interface ContentTypeSummaryLoaderListener extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (contentSummaries:api.schema.content.ContentTypeSummary[]) => void;

    }

}