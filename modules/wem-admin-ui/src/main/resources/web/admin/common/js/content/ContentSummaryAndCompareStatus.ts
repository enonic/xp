module api.content {

    export class ContentSummaryAndCompareStatus implements api.ui.treegrid.TreeItem {

        private content: ContentSummary;

        private compareContentResult: CompareContentResult;

        constructor(content: ContentSummary, compareContentResult: CompareContentResult) {
            this.content = content;
            this.compareContentResult = compareContentResult;
        }

        getContentSummary(): ContentSummary {
            return this.content;
        }

        getCompareContentResult(): CompareContentResult {
            return this.compareContentResult;
        }

        getCompareStatus(): string {
            return this.getCompareStatus();
        }

        getId(): string {
            return this.content.getId();
        }

        hasChildren(): boolean {
            return this.content.hasChildren();
        }
    }
}