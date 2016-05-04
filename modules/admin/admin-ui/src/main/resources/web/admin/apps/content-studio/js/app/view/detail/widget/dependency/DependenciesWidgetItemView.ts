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

        return wemQ<any>(null);
    }

}
