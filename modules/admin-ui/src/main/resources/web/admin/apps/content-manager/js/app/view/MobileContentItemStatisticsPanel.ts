module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import WidgetsPanelToggleButton = app.view.widget.WidgetsPanelToggleButton;
    import WidgetsPanel = app.view.widget.WidgetsPanel;
    import WidgetView = app.view.widget.WidgetView;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import StringHelper = api.util.StringHelper;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export class MobileContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private itemHeader: api.dom.DivEl = new api.dom.DivEl("mobile-content-item-statistics-header");
        private headerLabel: api.dom.SpanEl = new api.dom.SpanEl();

        private previewPanel: ContentItemPreviewPanel;
        private widgetsPanel: WidgetsPanel = new WidgetsPanel(false);
        private widgetsToggleButton: app.view.widget.MobileWidgetsPanelToggleButton;

        constructor() {
            super("mobile-content-item-statistics-panel");

            this.setDoOffset(false);

            this.initHeader();

            this.initPreviewPanel();

            this.initWidgetsPanel();

            this.onRendered(() => {
                this.slideAllOut();
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.slideAllOut();
            });
        }

        private initHeader() {
            this.itemHeader.appendChild(this.headerLabel);
            this.widgetsToggleButton = new app.view.widget.MobileWidgetsPanelToggleButton(this.widgetsPanel);
            var backButton = new api.dom.DivEl("back-button");
            backButton.onClicked((event) => {
                this.slideAllOut();
            });
            this.itemHeader.appendChild(backButton);
            this.itemHeader.appendChild(this.widgetsToggleButton);

            this.appendChild(this.itemHeader);

        }

        private initWidgetsPanel() {
            this.appendChild(this.widgetsPanel);
        }

        private initPreviewPanel() {
            this.previewPanel = new ContentItemPreviewPanel();
            this.previewPanel.setDoOffset(false);
            this.appendChild(this.previewPanel);
        }

        setItem(item: ViewItem<ContentSummary>) {
            if (!item.equals(this.getItem())) {
                super.setItem(item);
                this.previewPanel.setItem(item);
                this.widgetsPanel.setItem(item);
                this.setName(this.makeDisplayName(item));
                this.slideIn();
            }
        }

        private makeDisplayName(item: ViewItem<ContentSummary>): string {
            return StringHelper.isEmpty(item.getDisplayName()) ? StringHelper.escapeHtml("<Unnamed " +
                                                                                         this.convertName(item.getModel().getType().getLocalName() +
                                                                                         "") + ">") : item.getDisplayName();
        }

        private convertName(name: string): string {
            return StringHelper.capitalize(name.replace(/-/g, " ").trim());
        }

        setName(name: string) {
            this.headerLabel.setHtml(name);
        }

        slideAllOut() {
            this.slideOut();
            this.widgetsPanel.slideOut();
            this.widgetsToggleButton.removeClass("expanded");
        }


        slideOut() {
            this.getEl().setLeftPx(-this.getEl().getWidthWithBorder());
        }

        slideIn() {
            this.getEl().setLeftPx(0);
        }
    }

}
