import "../../api.ts";
import {MostPopularItem} from "./MostPopularItem";
import {NewContentDialogList} from "./NewContentDialogList";
import {NewContentDialogListItem} from "./NewContentDialogListItem";
import {MostPopularItemsBlock} from "./MostPopularItemsBlock";
import ContentTypeSummary = api.schema.content.ContentTypeSummary;

export class MostPopularItemsList extends NewContentDialogList {

    constructor() {
        super("most-popular-content-types-list");
    }

    createItemView(item: MostPopularItem): api.dom.LiEl {
        let namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
        namesAndIconView
            .setIconUrl(item.getIconUrl())
            .setMainName(item.getDisplayName() + " (" + item.getHits() + ")")
            .setSubName(item.getName())
            .setDisplayIconLabel(item.isSite());

        let itemEl = new api.dom.LiEl('content-types-list-item' + (item.isSite() ? ' site' : ''));
        itemEl.getEl().setTabIndex(0);
        itemEl.appendChild(namesAndIconView);
        itemEl.onClicked((event: MouseEvent) => this.notifySelected(item));
        itemEl.onKeyPressed((event: KeyboardEvent) => {
            if (event.keyCode == 13) {
                this.notifySelected(item);
            }
        });
        return itemEl;
    }

    createItems(listItems: NewContentDialogListItem[],
                directChildContents: api.content.ContentSummary[]) {

        let contentTypes = listItems.map((el) => el.getContentType());

        let mostPopularItems: MostPopularItem[] = [];
        let allowedContentTypes: api.content.ContentSummary[] = directChildContents.filter((content: api.content.ContentSummary) => {
                return this.isAllowedContentType(contentTypes, content);
            });
        let aggregatedList: ContentTypeInfo[] = this.getAggregatedItemList(allowedContentTypes);

        for (let i = 0; i < aggregatedList.length && i < MostPopularItemsBlock.DEFAULT_MAX_ITEMS; i++) {
            let contentType: ContentTypeSummary = api.util.ArrayHelper.findElementByFieldValue(contentTypes, "name",
                aggregatedList[i].contentType);
            mostPopularItems.push(new MostPopularItem(contentType, aggregatedList[i].count));
        }

        this.setItems(mostPopularItems);
    }

    private isAllowedContentType(allowedContentTypes: ContentTypeSummary[], content: api.content.ContentSummary) {
        return !content.getType().isMedia() && !content.getType().isDescendantOfMedia() &&
               Boolean(api.util.ArrayHelper.findElementByFieldValue(allowedContentTypes, "id", content.getType().toString()));
    }

    private getAggregatedItemList(contentTypes: api.content.ContentSummary[]) {
        let aggregatedList: ContentTypeInfo[] = [];

        contentTypes.forEach((content: api.content.ContentSummary) => {
            let contentType = content.getType().toString();
            let existingContent = api.util.ArrayHelper.findElementByFieldValue(aggregatedList, "contentType", contentType);

            if (existingContent) {
                existingContent.count++;
                if (content.getModifiedTime() > existingContent.lastModified) {
                    existingContent.lastModified = content.getModifiedTime();
                }
            }
            else {
                aggregatedList.push({contentType: contentType, count: 1, lastModified: content.getModifiedTime()});
            }
        });

        aggregatedList.sort(this.sortByCountAndDate);

        return aggregatedList;
    }

    private sortByCountAndDate(contentType1: ContentTypeInfo, contentType2: ContentTypeInfo) {
        if (contentType2.count == contentType1.count) {
            return contentType2.lastModified > contentType1.lastModified ? 1 : -1;
        }
        return contentType2.count - contentType1.count;
    }

}

export interface ContentTypeInfo {
    contentType: string;
    count: number;
    lastModified: Date;
}
