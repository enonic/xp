module api.content {

    export class ContentSummaryAndCompareStatus {

        private contentSummary: ContentSummary;

        private compareContentResult: CompareContentResult;

        constructor(contentSummary: ContentSummary, compareContentResult: CompareContentResult) {
            this.contentSummary = contentSummary;
            this.compareContentResult = compareContentResult;
        }

        getContentSummary(): ContentSummary {
            return this.contentSummary;
        }

        getCompareContentResult(): CompareContentResult {
            return this.compareContentResult;
        }

        getCompareStatus(): string {
            return this.getCompareStatus();
        }

        getId(): string {
            return this.contentSummary.getId();
        }

        hasChildren(): boolean {
            return this.contentSummary.hasChildren();
        }
    }
}