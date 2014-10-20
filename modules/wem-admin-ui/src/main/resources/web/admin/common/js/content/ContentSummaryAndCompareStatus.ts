module api.content {

    export class ContentSummaryAndCompareStatus {

        private contentSummary: ContentSummary;

        private compareContentResult: CompareContentResult;

        constructor(contentSummary: ContentSummary, compareContentResult: CompareContentResult) {
            this.contentSummary = contentSummary;
            this.compareContentResult = compareContentResult;
        }

        getContentId(): ContentId {
            return this.contentSummary.getContentId();
        }

        getContentSummary(): ContentSummary {
            return this.contentSummary;
        }

        setContentSummary(contentSummary: ContentSummary): void {
            this.contentSummary = contentSummary;
        }

        getCompareContentResult(): CompareContentResult {
            return this.compareContentResult;
        }

        getCompareStatus(): string {
            return this.getCompareStatus();
        }

        getId(): string {
            return !!this.contentSummary ? this.contentSummary.getId() : "";
        }

        hasChildren(): boolean {
            return !!this.contentSummary ? this.contentSummary.hasChildren() : false;
        }
    }
}