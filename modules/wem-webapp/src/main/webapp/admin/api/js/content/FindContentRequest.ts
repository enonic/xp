module api_content {

    export class FindContentRequest<T> extends ContentResourceRequest<FindContentResult<T>> {

        public static EXPAND_NONE = 'none';
        public static EXPAND_SUMMARY = 'summary';
        public static EXPAND_FULL = 'full';

        private fulltext:string;

        private includeFacets:boolean = true;

        private contentTypes:string[];

        private spaces:string[];

        private ranges:{
            lower:Date;
            upper:Date;
        }[];

        private facets:Object;

        private expand:string = FindContentRequest.EXPAND_NONE;

        // don't limit by default
        private count:number = -1;


        constructor(fulltext?:string) {
            super();
            super.setMethod("POST");
            if (fulltext) {
                this.fulltext = fulltext;
            }
        }

        public setFulltext(fulltext:string) {
            this.fulltext = fulltext;
            return this;
        }

        public setContentTypes(contentTypes:string[]) {
            this.contentTypes = contentTypes;
            return this;
        }

        public setSpaces(spaces:string[]) {
            this.spaces = spaces;
            return this;
        }

        public setRanges(ranges:{lower:Date; upper:Date}[]) {
            this.ranges = ranges;
            return this;
        }

        public setFacets(facets:Object) {
            this.facets = facets;
            this.setIncludeFacets(true);
            return this;
        }

        public setIncludeFacets(includeFacets:boolean) {
            this.includeFacets = includeFacets;
            return this;
        }

        public setExpand(expand:string) {
            this.expand = expand;
            return this;
        }

        public setCount(count:number) {
            this.count = count;
            return this;
        }


        getParams():Object {
            return {
                fulltext: this.fulltext || '',
                contentTypes: this.contentTypes || [],
                spaces: this.spaces || [],
                ranges: this.ranges || [],
                facets: this.facets || this.getDefaultFacets(),
                include: this.includeFacets,
                expand: this.expand,
                count: this.count
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "find");
        }

        private getDefaultFacets():Object {
            var now = new Date();
            var oneDayAgo = new Date();
            var oneWeekAgo = new Date();
            var oneHourAgo = new Date();
            oneDayAgo.setDate(now.getDate() - 1);
            oneWeekAgo.setDate(now.getDate() - 7);
            Admin.lib.DateHelper.addHours(oneHourAgo, -1);

            return {
                "space": {
                    "terms": {
                        "field": "space",
                        "size": 10,
                        "all_terms": true,
                        "order": "term"
                    }
                },
                "contentType": {
                    "terms": {
                        "field": "contentType",
                        "size": 10,
                        "all_terms": true,
                        "order": "term"
                    }
                },
                "< 1 day": {
                    "query": {
                        "range": {
                            "lastModified.date": {
                                "from": oneDayAgo.toISOString(),
                                "include_lower": true
                            }
                        }
                    }
                },
                "< 1 hour": {
                    "query": {
                        "range": {
                            "lastModified.date": {
                                "from": oneHourAgo.toISOString(),
                                "include_lower": true
                            }
                        }
                    }
                },
                "< 1 week": {
                    "query": {
                        "range": {
                            "lastModified.date": {
                                "from": oneWeekAgo.toISOString(),
                                "include_lower": true
                            }
                        }
                    }
                }
            };

        }

    }
}