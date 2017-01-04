module api.content.resource.result {

    export class CompareContentResult implements api.Equitable {

        compareStatus: CompareStatus;

        id: string;

        publishStatus: PublishStatus;

        constructor(id: string, compareStatus: CompareStatus, publishStatus: PublishStatus) {

            this.compareStatus = compareStatus;
            this.id = id;
            this.publishStatus = publishStatus;
        }

        getId(): string {
            return this.id;
        }

        getCompareStatus(): CompareStatus {
            return this.compareStatus;
        }

        getPublishStatus(): PublishStatus {
            return this.publishStatus;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, CompareContentResult)) {
                return false;
            }

            let other = <CompareContentResult>o;

            if (!api.ObjectHelper.stringEquals(this.id.toString(), other.id.toString())) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.content.json.CompareContentResultJson): CompareContentResult {

            let compareStatus: CompareStatus = <CompareStatus>CompareStatus[json.compareStatus],
                publishStatus: PublishStatus = <PublishStatus>PublishStatus[json.publishStatus];

            return new CompareContentResult(json.id, compareStatus, publishStatus);
        }
    }
}