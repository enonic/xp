module app_new {

    export class RecentContentTypesList extends api_dom.DivEl {

        private recentCount = 5;

        private cookieKey = 'app_browse.RecentContentTypesList';

        private cookieExpire = 30;

        private cookieSeparator = '|';

        private contentTypesList:ContentTypesList;

        constructor(className?:string) {
            super("RecentContentTypesList", className);

            var h4 = new api_dom.H4El();
            h4.getEl().setInnerHtml("Recent");
            this.appendChild(h4);

            this.contentTypesList = new ContentTypesList();

            this.appendChild(this.contentTypesList);

            NewContentEvent.on((event) => {
                    this.addRecentContentType(event.getContentType());
                }
            );
        }

        refresh() {
            this.contentTypesList.setNodes(this.getRecentContentTypes());
        }

        getNodes():api_remote_contenttype.ContentTypeListNode[] {
            return this.contentTypesList.getNodes();
        }

        private getRecentContentTypes():api_remote_contenttype.ContentTypeListNode[] {

            var recentRecords:api_remote_contenttype.ContentTypeListNode[] = [];

            var cookies:string = <string> Ext.util.Cookies.get(this.cookieKey);
            if (cookies) {
                var recentArray = cookies.split(this.cookieSeparator);
                for (var i = 0; i < recentArray.length; i++) {
                    recentRecords.push(this.parseContentType(recentArray[i]));
                }
            }

            return recentRecords;
        }

        private addRecentContentType(contentType:api_remote_contenttype.ContentTypeListNode) {

            var cookies:string = this.getCookie(this.cookieKey);
            var recentArray = cookies ? cookies.split(this.cookieSeparator) : [];

            var recentItem = this.serializeContentType(contentType);
            if (recentArray.length === 0 || recentArray[0] !== recentItem) {
                recentArray.unshift(recentItem);
            }

            if (recentArray.length > this.recentCount) {
                // constrain recent items quantity to recentCount
                recentArray = recentArray.slice(0, this.recentCount);
            }

            // add chosen item to recent list
            this.setCookie(this.cookieKey, recentArray.join(this.cookieSeparator));
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