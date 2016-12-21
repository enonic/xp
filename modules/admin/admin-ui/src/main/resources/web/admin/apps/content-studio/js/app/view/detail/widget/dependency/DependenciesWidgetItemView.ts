import "../../../../../api.ts";
import {WidgetItemView} from "../../WidgetItemView";
import {DependencyGroup, DependencyType} from "./DependencyGroup";
import {ToggleSearchPanelWithDependenciesEvent} from "../../../../browse/ToggleSearchPanelWithDependenciesEvent";

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentDependencyJson = api.content.json.ContentDependencyJson;
import ContentDependencyGroupJson = api.content.json.ContentDependencyGroupJson;
import ActionButton = api.ui.button.ActionButton;
import Action = api.ui.Action;
import NamesAndIconViewSize = api.app.NamesAndIconViewSize;
import NamesAndIconViewBuilder = api.app.NamesAndIconViewBuilder;

export class DependenciesWidgetItemView extends WidgetItemView {

    private mainContainer: api.dom.DivEl;
    private nameAndIcon: api.app.NamesAndIconView;

    private noInboundDependencies: api.dom.DivEl;
    private noOutboundDependencies: api.dom.DivEl;

    private item: ContentSummaryAndCompareStatus;
    private inboundDependencies: DependencyGroup[];
    private outboundDependencies: DependencyGroup[];

    private inboundButton: ActionButton;
    private outboundButton: ActionButton;

    constructor() {
        super("dependency-widget-item-view");

        this.inboundButton = this.appendButton("Show Inbound", "btn-inbound");
        this.appendMainContainer();
        this.outboundButton = this.appendButton("Show Outbound", "btn-outbound");
        this.manageButtonClick();
    }

    private manageButtonClick() {
        this.inboundButton.getAction().onExecuted((action: Action) => {
            new ToggleSearchPanelWithDependenciesEvent(this.item.getContentSummary(), true).fire();
        });

        this.outboundButton.getAction().onExecuted((action: Action) => {
            new ToggleSearchPanelWithDependenciesEvent(this.item.getContentSummary(), false).fire();
        });
    }

    private setButtonDecoration(button: ActionButton, dependencies: DependencyGroup[]) {
        if (dependencies.length == 0) {
            button.hide();
        }
        else {
            button.setLabel(button.getAction().getLabel() + " (" + this.getTotalItemCount(dependencies) + ")");
            button.show();
        }
    }

    private appendButton(label: string, cls: string): ActionButton {
        var action = new Action(label)
        var button = new ActionButton(action);

        button.addClass(cls);
        this.appendChild(button);

        return button;
    }

    public setContentAndUpdateView(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (DependenciesWidgetItemView.debug) {
            console.debug('DependenciesWidgetItemView.setItem: ', item);
        }

        this.item = item;
        return this.resolveDependencies(item);
    }

    private resetContainers() {
        this.mainContainer.removeChildren();

        this.removeClass("no-inbound");
        this.removeClass("no-outbound");
    }

    private appendMainContainer() {
        this.mainContainer = new api.dom.DivEl("main-container");
        this.appendChild(this.mainContainer);
    }

    private appendContentNamesAndIcon(item: ContentSummaryAndCompareStatus) {
        this.nameAndIcon =
            new api.app.NamesAndIconView(new NamesAndIconViewBuilder().setSize(NamesAndIconViewSize.medium))
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
            this.addClass("no-"  + typeAsString);
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
        this.resetContainers();

        this.noInboundDependencies = this.createDependenciesContainer(DependencyType.INBOUND, this.inboundDependencies);
        this.appendContentNamesAndIcon(item);
        this.noOutboundDependencies = this.createDependenciesContainer(DependencyType.OUTBOUND, this.outboundDependencies);

        this.setButtonDecoration(this.inboundButton, this.inboundDependencies);
        this.setButtonDecoration(this.outboundButton, this.outboundDependencies);
    }

    private getTotalItemCount(dependencies: DependencyGroup[]): number {
        var sum = 0;
        dependencies.forEach((dependencyGroup: DependencyGroup) => {
            sum += dependencyGroup.getItemCount();
        });

        return sum;
    }

    private appendDependencies(container: api.dom.DivEl, dependencies: DependencyGroup[]) {
        dependencies.forEach((dependencyGroup: DependencyGroup) => {
            var dependencyGroupView = new api.app.NamesAndIconView(new NamesAndIconViewBuilder().setSize(NamesAndIconViewSize.small))
                .setIconUrl(dependencyGroup.getIconUrl())
                .setMainName("(" + dependencyGroup.getItemCount().toString() + ")");

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

        var resolveDependenciesRequest = new api.content.resource.ResolveDependenciesRequest(item.getContentId());

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
