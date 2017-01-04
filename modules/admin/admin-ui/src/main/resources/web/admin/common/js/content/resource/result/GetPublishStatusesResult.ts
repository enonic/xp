module api.content.resource.result {

    export class GetPublishStatusesResult {

        private getPublishStatusesResult: GetPublishStatusResult[] = [];

        constructor(getPublishStatusesResult: GetPublishStatusResult[]) {
            this.getPublishStatusesResult = getPublishStatusesResult;
        }

        get(contentId: string): GetPublishStatusResult {

            let getPublishStatusResult: GetPublishStatusResult = null;

            this.getPublishStatusesResult.forEach((result: GetPublishStatusResult) => {

                if (result.getId() == contentId) {
                    getPublishStatusResult = result;
                }
            });

            return getPublishStatusResult;
        }

        getAll(): GetPublishStatusResult[] {
            return this.getPublishStatusesResult;
        }

        static fromJson(json: api.content.json.GetPublishStatusesResultJson): GetPublishStatusesResult {

            let list: GetPublishStatusResult[] = [];

            json.publishStatuses.forEach((getPublishStatusResult: api.content.json.GetPublishStatusResultJson) => {
                list.push(GetPublishStatusResult.fromJson(getPublishStatusResult));
            });

            return new GetPublishStatusesResult(list);
        }
    }
}