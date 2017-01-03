module api.content.resource.result {

    export class GetPublishStatusResult implements api.Equitable {

        publishStatus: api.content.PublishStatus;

        id: string;

        constructor(id: string, publishStatus: PublishStatus) {

            this.publishStatus = publishStatus;
            this.id = id;
        }

        getId(): string {
            return this.id;
        }

        getPublishStatus(): PublishStatus {
            return this.publishStatus;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, GetPublishStatusResult)) {
                return false;
            }

            let other = <GetPublishStatusResult>o;

            if (!api.ObjectHelper.stringEquals(this.id.toString(), other.id.toString())) {
                return false;
            }

            if (!api.ObjectHelper.objectEquals(this.publishStatus, other.publishStatus)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.content.json.GetPublishStatusResultJson): GetPublishStatusResult {

            let status: PublishStatus = <PublishStatus>PublishStatus[json.publishStatus];

            return new GetPublishStatusResult(json.id, status);
        }
    }
}