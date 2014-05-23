declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

declare var $liveEdit;

function initializeLiveEdit() {
    //TODO: Maybe move/make more generic
    $('[data-live-edit-empty-component="true"]').each((index, element) => {
        var type = $(element).data('live-edit-type');
        var path: string = $(element).data('live-edit-component');
        console.log("found empty component", type, path);
        var newEl;
        if (type === "image") {
            newEl = new LiveEdit.component.ImagePlaceholder();
        } else if (type === "part") {
            newEl = new LiveEdit.component.PartPlaceholder();
        } else if (type === "layout") {
            newEl = new LiveEdit.component.LayoutPlaceholder();
        }
        newEl.setComponentPath(api.content.page.ComponentPath.fromString(path));
        $(element).replaceWith(newEl.getHTMLElement());
    });

    var body = api.dom.Body.getAndLoadExistingChildren();
    var map = new api.liveedit.PageComponentIdMapResolver(body).resolve();
    new api.liveedit.NewPageComponentIdMapEvent(map).fire();
}

function getComponentByPath(path: string): api.liveedit.ItemView {
    return api.liveedit.ItemView.fromJQuery($('[data-live-edit-component="' + path + '"]'), false);
}

(function ($) {
    'use strict';

    $(window).load(() => {
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

        $(window).resize(() => $(window).trigger('resizeBrowserWindow.liveEdit'));

        $(window).unload(() => console.log('Clean up any css classes etc. that live edit / sortable has added'));

        //TODO: move this somewhere logical
        $(window).on('componentLoaded.liveEdit', (event, component: api.liveedit.ItemView) => {
            if (component.getType() == api.liveedit.layout.LayoutItemType.get()) {
                LiveEdit.component.dragdropsort.DragDropSort.createSortableLayout(component);
            }
        })
    });

    $(document).ready(() => {
        // Prevent the user from clicking on things
        // This needs more work as we want them to click on Live Edit ui stuff.
        $(document).on('mousedown', 'btn, button, a, select, input', (event) => {
            event.preventDefault();
        });


        // Notify parent frame if any modifier except shift is pressed
        // For the parent shortcuts to work if the inner iframe has focus
        $(document).on("keypress keydown keyup", (event) => {

            if ((event.metaKey || event.ctrlKey || event.altKey) && event.keyCode) {
                $(parent.document).simulate(event.type, {
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

}($liveEdit));
