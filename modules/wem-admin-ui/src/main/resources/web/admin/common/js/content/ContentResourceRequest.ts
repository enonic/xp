module api.content {

    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class ContentResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        public static EXPAND_NONE = 'none';
        public static EXPAND_SUMMARY = 'summary';
        public static EXPAND_FULL = 'full';

        private resourcePath: api.rest.Path;

        propertyIdProvider: PropertyIdProvider;

        constructor() {
            super();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content");
            this.propertyIdProvider = api.Client.get().getPropertyIdProvider();
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToContent(json: json.ContentJson): Content {
            return Content.fromJson(json, this.propertyIdProvider);
        }

        fromJsonToContentArray(json: json.ContentJson[]): Content[] {

            var array: Content[] = [];
            json.forEach((itemJson: json.ContentJson) => {
                array.push(this.fromJsonToContent(itemJson));
            });

            return array;
        }

        fromJsonToContentSummary(json: json.ContentSummaryJson): ContentSummary {
            return ContentSummary.fromJson(json, this.propertyIdProvider);
        }

        fromJsonToContentSummaryArray(json: json.ContentSummaryJson[]): ContentSummary[] {

            var array: ContentSummary[] = [];
            json.forEach((itemJson: json.ContentSummaryJson) => {
                array.push(this.fromJsonToContentSummary(itemJson));
            });

            return array;
        }

        fromJsonToContentIdBaseItem(json: json.ContentIdBaseItemJson): ContentIdBaseItem {
            return ContentIdBaseItem.fromJson(json, this.propertyIdProvider);
        }

        fromJsonToContentIdBaseItemArray(jsonArray: json.ContentIdBaseItemJson[]): ContentIdBaseItem[] {

            return ContentIdBaseItem.fromJsonArray(jsonArray, this.propertyIdProvider);
        }
    }
}