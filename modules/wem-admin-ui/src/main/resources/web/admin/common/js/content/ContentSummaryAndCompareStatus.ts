module api.content {

    export class ContentSummaryAndCompareStatus implements api.node.Node {

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

        hasChildren(): boolean {
            return this.content.hasChildren();
        }

        getId(): string {
            return this.content.getId();
        }

        getCreatedTime(): Date {
            return this.content.getCreatedTime();
        }

        getModifiedTime(): Date {
            return this.content.getModifiedTime();
        }

        isDeletable(): boolean {
            return this.content.isDeletable();
        }

        isEditable(): boolean {
            return this.content.isEditable();
        }

        getCompareStatus(): string {
            return this.getCompareStatus();
        }

    }
}