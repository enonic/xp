import '../../api.ts';
import {NewContentEvent} from './NewContentEvent';

NewContentEvent.on((event: NewContentEvent) => {
        RecentItems.get().addItemName(event.getContentType());
    }
);

export class RecentItems {

    private static INSTANCE: RecentItems = new RecentItems();

    private maximum: number = 7;

    private cookieKey: string = 'app.browse.RecentItemsList';

    private cookieExpire: number = 30;

    private valueSeparator: string = '|';

    public static get(): RecentItems {
        return RecentItems.INSTANCE;
    }

    public addItemName(contentType: api.schema.content.ContentTypeSummary) {
        let itemsNames = this.getRecentItemsNames();
        let name = contentType.getName();

        itemsNames = itemsNames.filter((storedName: string) => storedName != name);
        itemsNames.unshift(name);
        itemsNames = itemsNames.slice(0, this.maximum);

        api.util.CookieHelper.setCookie(this.cookieKey, itemsNames.join(this.valueSeparator), this.cookieExpire);
    }

    public getRecentItemsNames(): string[] {
        let cookies = api.util.CookieHelper.getCookie(this.cookieKey);
        return cookies ? cookies.split(this.valueSeparator) : [];
    }

}
