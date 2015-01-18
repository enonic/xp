declare var Admin;
declare var CONFIG;

declare var wemjq: JQueryStatic;


new LiveEdit.LiveEditPage();

wemjq(window).load(() => {
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
        }
    });
});

