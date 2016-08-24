module api.content.form.inputtype.contentselector {

    import ResourceRequest = api.rest.ResourceRequest;

    export interface CustomSelectorResponse {
        total: number,
        count: number,
        hits: CustomSelectorItem[]
    }

    export class CustomSelectorRequest extends ResourceRequest<CustomSelectorResponse, CustomSelectorItem[]> {

        public static DEFAULT_SIZE = 10;

        private requestPath: string;
        private ids: string[] = [];
        private query: string;
        private start: number;
        private count: number = CustomSelectorRequest.DEFAULT_SIZE;

        private results: CustomSelectorItem[];
        private loaded: boolean = false;

        constructor(requestPath: string) {
            super();
            this.requestPath = requestPath;
            this.setMethod('GET');
        }

        isPartiallyLoaded(): boolean {
            return this.results.length > 0 && !this.loaded;
        }

        resetParams() {
            this.start = 0;
            this.loaded = false;
        }

        getParams(): Object {
            return {
                ids: this.ids,
                query: this.query || null,
                start: this.start || null,
                count: this.count || null
            }
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromString(this.requestPath);
        }

        sendAndParse(): wemQ.Promise<CustomSelectorItem[]> {
            return this.send().then((response: api.rest.JsonResponse<CustomSelectorResponse>) => {

                var result = response.getResult();
                if (this.start == 0) {
                    this.results = [];
                }

                this.start += result.count;
                this.loaded = this.start >= result.total;

                let items = result.hits.map((hit) => {
                    return new CustomSelectorItem(hit);
                });

                this.results = this.results.concat(items);

                return this.results;
            });
        }

        setIds(ids: string[]): CustomSelectorRequest {
            this.ids = ids;
            return this;
        }

        setFrom(from: number): CustomSelectorRequest {
            this.start = from;
            return this;
        }

        setSize(size: number): CustomSelectorRequest {
            this.count = size;
            return this;
        }

        setQuery(query: string): CustomSelectorRequest {
            this.query = query;
            return this;
        }
    }

}