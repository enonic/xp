module api_form_input_type {

    export interface ContentSummaryLoaderListener extends api_event.Listener {

        onLoading: () => void;

        onLoaded: (contentSummaries:api_content.ContentSummary[]) => void;

    }

}