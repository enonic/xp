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

        public addRecentContentType(contentType:api_remote_contenttype.ContentTypeListNode) {

            var cookies:string = this.getCookie(this.cookieKey);
            var recentArray = cookies ? cookies.split(this.valueSeparator) : [];

            var recentItem = this.serializeContentType(contentType);
            if (recentArray.length === 0 || recentArray[0] !== recentItem) {
                recentArray.unshift(recentItem);
            }

            if (recentArray.length > this.maximum) {
                // constrain recent items quantity to maximum
                recentArray = recentArray.slice(0, this.maximum);
            }

            // add chosen item to recent list
            this.setCookie(this.cookieKey, recentArray.join(this.valueSeparator));
        }

        public getRecentContentTypes():api_remote_contenttype.ContentTypeListNode[] {

            var recentRecords:api_remote_contenttype.ContentTypeListNode[] = [];

            var cookies:string = <string> Ext.util.Cookies.get(this.cookieKey);
            if (cookies) {
                var recentArray = cookies.split(this.valueSeparator);
                for (var i = 0; i < recentArray.length; i++) {
                    recentRecords.push(this.parseContentType(recentArray[i]));
                }
            }

            return recentRecords;
        }

        /**
         * Recommends the most frequent content types
         * @returns {Array} Array of recommendations
         */
        public recommendContentTypes():api_remote_contenttype.ContentTypeListNode[] {

            var contentTypes:api_remote_contenttype.ContentTypeListNode[] = this.getRecentContentTypes();

            var recommendations = [];
            if (contentTypes && contentTypes.length > 0) {
                var node, count, maxCount = 0, maxNode;
                var namesMap = {};
                for (var i = 0; i < contentTypes.length; i++) {
                    node = contentTypes[i];
                    count = namesMap[node.qualifiedName] || 0;
                    namesMap[node.qualifiedName] = ++count;
                    if (count > maxCount) {
                        maxCount = count;
                        maxNode = node;
                    }
                }
                recommendations.push(maxNode);
            }

            return recommendations;
        }

        private serializeContentType(contentType:api_remote_contenttype.ContentTypeListNode) {
            // serialize only the crucial info
            // because cookie size is just 4093 bytes per domain
            return JSON.stringify({
                iconUrl: contentType.iconUrl,
                displayName: contentType.displayName,
                qualifiedName: contentType.qualifiedName,
                name: contentType.name
            });
        }

        private parseContentType(text:string):api_remote_contenttype.ContentTypeListNode {
            var json = JSON.parse(text);
            return <api_remote_contenttype.ContentTypeListNode> json;
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