module app.create {

    NewContentEvent.on((event:NewContentEvent) => {
            RecentItems.get().addItemName(event.getContentType(), event.getSiteTemplate());
        }
    );

    export class RecentItems {

        private static INSTANCE = new RecentItems();

        private maximum = 5;

        private cookieKey = 'app.browse.RecentItemsList';

        private cookieExpire = 30;

        private valueSeparator = '|';

        public static get():RecentItems {
            return RecentItems.INSTANCE;
        }

        public addItemName(contentType:api.schema.content.ContentTypeSummary , siteTemplate: api.content.site.template.SiteTemplateSummary) {
            var itemsNames = this.getRecentItemsNames();
            var name = siteTemplate ? siteTemplate.getName() : contentType.getName();

            itemsNames = itemsNames.filter((storedName: string) => storedName != name);
            itemsNames.unshift(name);
            itemsNames = itemsNames.slice(0, this.maximum);

            api.util.CookieHelper.setCookie(this.cookieKey, itemsNames.join(this.valueSeparator), this.cookieExpire);
        }

        public getRecentItemsNames():string[] {
            var cookies = api.util.CookieHelper.getCookie(this.cookieKey);
            return cookies ? cookies.split(this.valueSeparator) : [];
        }

    }

}