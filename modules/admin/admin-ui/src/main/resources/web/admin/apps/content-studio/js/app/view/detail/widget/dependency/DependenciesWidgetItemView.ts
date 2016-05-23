import "../../../../../api.ts";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentDependencyJson = api.content.json.ContentDependencyJson;
import ContentDependencyGroupJson = api.content.json.ContentDependencyGroupJson;
import {WidgetItemView} from "../../WidgetItemView";
import {DependencyGroup, DependencyType} from "./DependencyGroup";

export class DependenciesWidgetItemView extends WidgetItemView {

    private mainContainer: api.dom.DivEl;
    private nameAndIcon: api.app.NamesAndIconView;

    private noInboundDependencies: api.dom.DivEl;
    private noOutboundDependencies: api.dom.DivEl;

    private item: ContentSummaryAndCompareStatus;
    private inboundDependencies: DependencyGroup[];
    private outboundDependencies: DependencyGroup[];

    constructor() {
        super("dependency-widget-item-view");

        this.appendMainContainer();
    }

    public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (DependenciesWidgetItemView.debug) {
            console.debug('DependenciesWidgetItemView.setItem: ', item);
        }

        return this.resolveDependencies(item);
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

        this.nameAndIcon.addClass("main-content");

        this.mainContainer.appendChild(this.nameAndIcon);
    }

    private createDependenciesContainer(type: DependencyType, dependencies: DependencyGroup[]): api.dom.DivEl {
        var typeAsString = DependencyType[type].toLowerCase();
        var div = new api.dom.DivEl("dependencies-container " + typeAsString);
        if (dependencies.length == 0) {
            div.addClass("no-dependencies");
            div.setHtml("No " + typeAsString + " dependencies");
        }
        else {
            this.appendDependencies(div, dependencies);
        }

        this.mainContainer.appendChild(div);

        return div;
    }

    private renderContent(item: ContentSummaryAndCompareStatus) {

        this.mainContainer.removeChildren();

        this.noInboundDependencies = this.createDependenciesContainer(DependencyType.INBOUND, this.inboundDependencies);
        this.appendContentNamesAndIcon(item);
        this.noOutboundDependencies = this.createDependenciesContainer(DependencyType.OUTBOUND, this.outboundDependencies);
    }

    private appendDependencies(container: api.dom.DivEl, dependencies: DependencyGroup[]) {
        dependencies.forEach((dependencyGroup: DependencyGroup) => {
            var dependencyGroupView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small))
                .setIconUrl(dependencyGroup.getIconUrl())
                .setMainName(dependencyGroup.getItemCount().toString());

            /* Tooltip is buggy
            dependencyGroupView.getEl().setTitle(dependencyGroup.getName());
            */

            container.appendChild(dependencyGroupView);
        });
    }

    /**
     * Perform request to resolve dependency items of given item.
     */
    private resolveDependencies(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {

        var resolveDependenciesRequest = new api.content.ResolveDependenciesRequest(item.getContentId());

        return resolveDependenciesRequest.send().then((jsonResponse: api.rest.JsonResponse<ContentDependencyJson>) => {
            this.initResolvedDependenciesItems(jsonResponse.getResult());
            this.renderContent(item);
        });
    }

    /**
     * Inits arrays of properties that store results of performing resolve request.
     */
    private initResolvedDependenciesItems(json: ContentDependencyJson) {
        this.inboundDependencies = DependencyGroup.fromDependencyGroupJson(DependencyType.INBOUND, json.inbound);
        this.outboundDependencies = DependencyGroup.fromDependencyGroupJson(DependencyType.OUTBOUND, json.outbound);
    }

}
