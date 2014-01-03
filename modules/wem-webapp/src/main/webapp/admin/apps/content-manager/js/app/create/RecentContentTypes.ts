module app.create {

    NewContentEvent.on((event) => {
            RecentContentTypes.get().addRecentContentType(event.getContentType());
        }
    );

    export class RecentContentTypes {

        private static INSTANCE = new RecentContentTypes();

        private maximum = 5;

        private cookieKey = 'app.browse.RecentContentTypesList';

        private cookieExpire = 30;

        private valueSeparator = '|';

        public static get():RecentContentTypes {
            return RecentContentTypes.INSTANCE;
        }

        public addRecentContentType(contentType:api.schema.content.ContentTypeSummary) {

            var cookie:string = api.util.CookieHelper.getCookie(this.cookieKey);
            var contentTypeNames = cookie ? cookie.split(this.valueSeparator) : [];

            var contentTypeName = contentType.getName();
            if (contentTypeNames.length === 0 || contentTypeNames[0] !== contentTypeName) {
                contentTypeNames.unshift(contentTypeName);
            }

            if (contentTypeNames.length > this.maximum) {
                // constrain recent items quantity to maximum
                contentTypeNames = contentTypeNames.slice(0, this.maximum);
            }

            // add chosen item to recent list
            api.util.CookieHelper.setCookie(this.cookieKey, contentTypeNames.join(this.valueSeparator));
        }

        public getRecentContentTypes():api.schema.content.ContentTypeName[] {

            var names:api.schema.content.ContentTypeName[] = [];

            var cookies:string = <string> Ext.util.Cookies.get(this.cookieKey);
            if (cookies) {
                var recentArray = cookies.split(this.valueSeparator);
                for (var i = 0; i < recentArray.length; i++) {
                    var name = recentArray[i];
                    names.push(new api.schema.content.ContentTypeName(name));
                }
            }
            return names;
        }

        /**
         * Recommends the most frequent content types
         */
        public getRecommendedContentTypes():api.schema.content.ContentTypeName[] {

            var recentNames:api.schema.content.ContentTypeName[] = this.getRecentContentTypes();

            var recommendations:api.schema.content.ContentTypeName[] = [];
            if (recentNames && recentNames.length > 0) {
                var name, count, maxCount = 0, maxNode;
                var namesMap = {};
                for (var i = 0; i < recentNames.length; i++) {
                    name = recentNames[i];
                    count = namesMap[name] || 0;
                    namesMap[name] = ++count;
                    if (count > maxCount) {
                        maxCount = count;
                        maxNode = name;
                    }
                }
                recommendations.push(new api.schema.content.ContentTypeName(maxNode));
            }

            return recommendations;
        }

    }

}