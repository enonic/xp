import "../../../../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import {WidgetItemView} from "../../WidgetItemView";

export class DependenciesWidgetItemView extends WidgetItemView {

    private nameAndIcon: api.app.NamesAndIconView;

    public static debug = true;

    constructor() {
        super("dependency-widget-item-view");
    }

    public layout(): wemQ.Promise<any> {
        if (DependenciesWidgetItemView.debug) {
            console.debug('DependenciesWidgetItemView.layout');
        }

        return super.layout();
    }

    public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (DependenciesWidgetItemView.debug) {
            console.debug('DependenciesWidgetItemView.setItem: ', item);
        }

        this.removeChildren();

        this.nameAndIcon =
            new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.medium))
                .setIconUrl(item.getIconUrl())
                .setMainName(item.getDisplayName())
                .setSubName(item.getPath().toString());

        this.appendChild(this.nameAndIcon);

        return wemQ<any>(null);
    }

}
