module app_new {

    NewContentEvent.on((event) => {
            RecentContentTypes.get().addRecentContentType(event.getContentType());
        }
    );

    export class RecentContentTypes {

        private static INSTANCE = new RecentContentTypes();

        private maximum = 5;

        private cookieKey = 'app_browse.RecentContentTypesList';

        private cookieExpire = 30;

        private valueSeparator = '|';

        public static get():RecentContentTypes {
            return INSTANCE;
        }

        public addRecentContentType(contentType:api_remote_contenttype.ContentType) {

            var cookie:string = this.getCookie(this.cookieKey);
            var qualifiedContentTypeNames = cookie ? cookie.split(this.valueSeparator) : [];

            var qualifiedContentTypeName = contentType.qualifiedName;
            if (qualifiedContentTypeNames.length === 0 || qualifiedContentTypeNames[0] !== qualifiedContentTypeName) {
                qualifiedContentTypeNames.unshift(qualifiedContentTypeName);
            }

            if (qualifiedContentTypeNames.length > this.maximum) {
                // constrain recent items quantity to maximum
                qualifiedContentTypeNames = qualifiedContentTypeNames.slice(0, this.maximum);
            }

            // add chosen item to recent list
            this.setCookie(this.cookieKey, qualifiedContentTypeNames.join(this.valueSeparator));
        }

        public getRecentContentTypes():string[] {

            var qualifiedNames:string[] = [];

            var cookies:string = <string> Ext.util.Cookies.get(this.cookieKey);
            if (cookies) {
                var recentArray = cookies.split(this.valueSeparator);
                for (var i = 0; i < recentArray.length; i++) {
                    var qualifiedName = recentArray[i];
                    qualifiedNames.push(qualifiedName);
                }
            }
            return qualifiedNames;
        }

        /**
         * Recommends the most frequent content types
         * @returns {Array} Array of qualified content type names
         */
        public getRecommendedContentTypes():string[] {

            var qualifiedNames:string[] = this.getRecentContentTypes();

            var recommendations = [];
            if (qualifiedNames && qualifiedNames.length > 0) {
                var qualifiedName, count, maxCount = 0, maxNode;
                var namesMap = {};
                for (var i = 0; i < qualifiedNames.length; i++) {
                    qualifiedName = qualifiedNames[i];
                    count = namesMap[qualifiedName] || 0;
                    namesMap[qualifiedName] = ++count;
                    if (count > maxCount) {
                        maxCount = count;
                        maxNode = qualifiedName;
                    }
                }
                recommendations.push(maxNode);
            }

            return recommendations;
        }

        private getCookie(name:string):string {
            var value;
            var parts = document.cookie.split(name + "=");
            if (parts.length == 2) {
                value = parts.pop().split(";").shift();
            }
            return value;
        }

        private setCookie(name:string, value:string) {
            var expDate = new Date();
            expDate.setDate(expDate.getDate() + this.cookieExpire);
            var value = value + "; expires=" + expDate.toUTCString();
            document.cookie = name + "=" + value;
        }

    }

}