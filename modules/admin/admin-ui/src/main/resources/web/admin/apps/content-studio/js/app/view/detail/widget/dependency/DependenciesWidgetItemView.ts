import "../../../../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import {WidgetItemView} from "../../WidgetItemView";

export class DependenciesWidgetItemView extends WidgetItemView {

    private mainContainer: api.dom.DivEl;
    private nameAndIcon: api.app.NamesAndIconView;

    private noInboundDependencies: api.dom.DivEl;
    private noOutboundDependencies: api.dom.DivEl;

    constructor() {
        super("dependency-widget-item-view");

        this.appendMainContainer();
    }

    public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (DependenciesWidgetItemView.debug) {
            console.debug('DependenciesWidgetItemView.setItem: ', item);
        }

        this.renderContent(item);

        return wemQ<any>(null);
    }

    private appendMainContainer() {
        this.mainContainer = new api.dom.DivEl("main-container");
        this.appendChild(this.mainContainer);
    }

    private appendContentNamesAndIcon(item: ContentSummaryAndCompareStatus) {
        this.nameAndIcon =
            new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.medium))
                .setIconUrl(item.getIconUrl())
                .setMainName(item.getDisplayName())
                .setSubName(item.getPath().toString());

        this.mainContainer.appendChild(this.nameAndIcon);
    }

    private createNoDependenciesDiv(text: string): api.dom.DivEl {
        var div = new api.dom.DivEl("no-dependencies");
        div.setHtml(text);

        this.mainContainer.appendChild(div);

        return div;
    }

    private renderContent(item: ContentSummaryAndCompareStatus) {

        this.mainContainer.removeChildren();

        this.noInboundDependencies = this.createNoDependenciesDiv("No inbound dependencies");
        this.appendContentNamesAndIcon(item);
        this.noOutboundDependencies = this.createNoDependenciesDiv("No outbound dependencies");
    }
}
