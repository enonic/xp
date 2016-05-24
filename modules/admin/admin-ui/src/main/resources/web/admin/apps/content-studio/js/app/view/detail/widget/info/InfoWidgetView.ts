import "../../../../../api.ts";

import {WidgetView} from "../../WidgetView";
import {WidgetItemView} from "../../WidgetItemView";
import {StatusWidgetItemView} from "./StatusWidgetItemView";
import {PropertiesWidgetItemView} from "./PropertiesWidgetItemView";
import {AttachmentsWidgetItemView} from "./AttachmentsWidgetItemView";
import {UserAccessWidgetItemView} from "./UserAccessWidgetItemView";
import {DetailsPanel} from "../../DetailsPanel";

export class InfoWidgetView extends WidgetView {
    private statusWidgetItemView: StatusWidgetItemView;
    private propWidgetItemView: PropertiesWidgetItemView;
    private attachmentsWidgetItemView: AttachmentsWidgetItemView;
    private userAccessWidgetItemView: UserAccessWidgetItemView;

    constructor(detailsPanel: DetailsPanel) {
        this.statusWidgetItemView = new StatusWidgetItemView();
        this.propWidgetItemView = new PropertiesWidgetItemView();
        this.attachmentsWidgetItemView = new AttachmentsWidgetItemView();
        this.userAccessWidgetItemView = new UserAccessWidgetItemView();

        var builder  = WidgetView.create()
            .setName("Info")
            .setDetailsPanel(detailsPanel)
            .setWidgetItemViews([
                this.statusWidgetItemView,
                this.userAccessWidgetItemView,
                this.propWidgetItemView,
                this.attachmentsWidgetItemView
            ]);

        super(builder);
    }

    getItemViews(): WidgetItemView[] {
        return [
            this.statusWidgetItemView,
            this.propWidgetItemView,
            this.attachmentsWidgetItemView,
            this.userAccessWidgetItemView
        ];
    }


    updateWidgetViews(): wemQ.Promise<any> {
        var promises = [],
            item = this.getDetailsPanel().getItem();

        promises.push(this.statusWidgetItemView.setStatus(item.getCompareStatus()));
        promises.push(this.propWidgetItemView.setContent(item.getContentSummary()));
        promises.push(this.attachmentsWidgetItemView.setContent(item.getContentSummary()));
        promises.push(this.userAccessWidgetItemView.setContentId(item.getContentId()));

        return wemQ.all(promises);
    }
}