declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

declare var wemjq: JQueryStatic;

declare var pageItemViews: api.liveedit.PageItemViews;

api.liveedit.InitializeLiveEditEvent.on((event: api.liveedit.InitializeLiveEditEvent) => {

    api.liveedit.PageItemType.get().setContent(event.getContent());
    api.liveedit.PageItemType.get().setSiteTemplate(event.getSiteTemplate());

    var body = api.dom.Body.getAndLoadExistingChildren();
    var map = new api.liveedit.PageComponentIdMapResolver(body).resolve();
    new api.liveedit.NewPageComponentIdMapEvent(map).fire();

    pageItemViews = new api.liveedit.PageItemViewsParser(body).parse();
    pageItemViews.initializeEmpties();
    console.log("pageItemViews", pageItemViews);
    var pageView = pageItemViews.getPageView();


    api.liveedit.PageComponentLoadedEvent.on((event: api.liveedit.PageComponentLoadedEvent) => {
        pageItemViews.addItemView(event.getItemView());
        if (event.getItemView().getType() == api.liveedit.layout.LayoutItemType.get()) {
            LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(event.getItemView());
        }
    });

    new api.liveedit.PageViewItemsParsedEvent(pageView).fire();
});

wemjq(window).load(() => {
    new LiveEdit.component.mouseevent.Page();
    new LiveEdit.component.mouseevent.Region();
    new LiveEdit.component.mouseevent.Layout();
    new LiveEdit.component.mouseevent.Part();
    new LiveEdit.component.mouseevent.Image();
    new LiveEdit.component.mouseevent.Text();
    new LiveEdit.component.mouseevent.Content();

    new LiveEdit.component.helper.ComponentResizeObserver();

    new LiveEdit.ui.Highlighter();
    new LiveEdit.ui.ToolTip();
    new LiveEdit.ui.Cursor();
    new LiveEdit.ui.contextmenu.ContextMenu();
    new LiveEdit.ui.Shader();
    new LiveEdit.ui.Editor();

    LiveEdit.component.dragdropsort.DragDropSort.init();

    wemjq(window).resize(() => wemjq(window).trigger('resizeBrowserWindow.liveEdit'));

    wemjq(window).unload(() => console.log('Clean up any css classes etc. that live edit / sortable has added'));

});

wemjq(document).ready(() => {
    // Prevent the user from clicking on things
    // This needs more work as we want them to click on Live Edit ui stuff.
    wemjq(document).on('mousedown', 'btn, button, a, select, input', (event) => {
        event.preventDefault();
    });

    // Notify parent frame if any modifier except shift is pressed
    // For the parent shortcuts to work if the inner iframe has focus
    wemjq(document).on("keypress keydown keyup", (event) => {

        if ((event.metaKey || event.ctrlKey || event.altKey) && event.keyCode) {
            wemjq(parent.document).simulate(event.type, {
                bubbles: event.bubbles,
                cancelable: event.cancelable,
                view: parent,
                ctrlKey: event.ctrlKey,
                altKey: event.altKey,
                shiftKey: event.shiftKey,
                metaKey: event.metaKey,
                keyCode: event.keyCode,
                charCode: event.charCode
            });
            return false;
        }
    });
});

