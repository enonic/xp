module api_form_inputtype_content {

    export interface ContentSummaryLoaderListener extends api_event.Listener {

        onLoading: () => void;

        onLoaded: (contentSummaries:api_content.ContentSummary[]) => void;

    }

}