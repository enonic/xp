module api.form.inputtype.content {

    export interface ContentSummaryLoaderListener extends api.event.Listener {

        onLoading: () => void;

        onLoaded: (contentSummaries:api.content.ContentSummary[]) => void;

    }

}