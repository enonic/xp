module api.content {

    export class GetContentVersionsResult {

        private from: number;

        private size: number;

        private hits: number;

        private totalHits: number;

        private contentVersions: ContentVersions;

        static fromJson(json: api.content.json.GetContentVersionsResultsJson): GetContentVersionsResult {

            var result = new GetContentVersionsResult();

            result.from = json.from;
            result.size = json.size;
            result.totalHits = json.totalHits;
            result.hits = json.hits;
            result.contentVersions = ContentVersions.fromJson(json.contentVersions);

            return result;
        }
    }
}