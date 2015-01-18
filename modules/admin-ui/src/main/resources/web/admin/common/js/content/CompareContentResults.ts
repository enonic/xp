module api.content {

    export class CompareContentResults {

        private compareContentResults: CompareContentResult[] = [];

        constructor(compareContentResults: CompareContentResult[]) {
            this.compareContentResults = compareContentResults;
        }

        get(contentId: string): CompareContentResult {

            var compareContentResult: CompareContentResult = null;

            this.compareContentResults.forEach((result: CompareContentResult) => {

                if (result.getId() == contentId) {
                    compareContentResult = result;
                }
            });

            return compareContentResult;
        }

        static fromJson(json: api.content.json.CompareContentResultsJson): CompareContentResults {

            var list: CompareContentResult[] = [];

            json.compareContentResults.forEach((compareContentResult: api.content.json.CompareContentResultJson) => {
                list.push(CompareContentResult.fromJson(compareContentResult));
            });

            return new CompareContentResults(list);
        }
    }
}