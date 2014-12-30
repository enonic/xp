module app.create {

    NewContentEvent.on((event: NewContentEvent) => {
            RecentItems.get().addItemName(event.getContentType());
        }
    );

    export class RecentItems {

        private static INSTANCE = new RecentItems();

        private maximum = 7;

        private cookieKey = 'app.browse.RecentItemsList';

        private cookieExpire = 30;

        private valueSeparator = '|';

        public static get(): RecentItems {
            return RecentItems.INSTANCE;
        }

        public addItemName(contentType: api.schema.content.ContentTypeSummary) {
            var itemsNames = this.getRecentItemsNames();
            var name = contentType.getName();

            itemsNames = itemsNames.filter((storedName: string) => storedName != name);
            itemsNames.unshift(name);
            itemsNames = itemsNames.slice(0, this.maximum);

            api.util.CookieHelper.setCookie(this.cookieKey, itemsNames.join(this.valueSeparator), this.cookieExpire);
        }

        public getRecentItemsNames(): string[] {
            var cookies = api.util.CookieHelper.getCookie(this.cookieKey);
            return cookies ? cookies.split(this.valueSeparator) : [];
        }

    }

}