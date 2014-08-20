module api.content {

    export class CompareContentRequest extends ContentResourceRequest<api.content.json.CompareContentResultsJson, CompareContentResults> {

        private ids: string[];

        constructor(ids: string[]) {
            super();
            super.setMethod("POST");
            this.ids = ids;
        }

        static fromContentSummaries(contentSummaries: ContentSummary[]): CompareContentRequest {

            var ids: string[] = [];

            contentSummaries.forEach((contentSummary: ContentSummary) => {

                ids.push(contentSummary.getContentId().toString());
            });

            return new CompareContentRequest(ids);
        }

        getParams(): Object {
            return {
                ids: this.ids
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "compare");
        }

        sendAndParse(): wemQ.Promise<CompareContentResults> {
            return this.send().then((response: api.rest.JsonResponse<api.content.json.CompareContentResultsJson>) => {
                return this.fromJsonToCompareResults(response.getResult());
            });
        }

        fromJsonToCompareResults(json: api.content.json.CompareContentResultsJson): CompareContentResults {
            return CompareContentResults.fromJson(json);
        }
    }
}