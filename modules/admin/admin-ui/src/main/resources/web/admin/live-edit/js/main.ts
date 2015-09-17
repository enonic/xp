declare var CONFIG;

declare var wemjq: JQueryStatic;


wemjq(document).ready(() => {

    new LiveEdit.LiveEditPage();

    // Notify parent frame if any modifier except shift is pressed
    // For the parent shortcuts to work if the inner iframe has focus
    wemjq(document).on("keypress keydown keyup", (event) => {

        if ((event.metaKey || event.ctrlKey || event.altKey) && event.keyCode) {

            stopBrowserShortcuts(event);

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

    function stopBrowserShortcuts(event) {
        // get the parent's frame bindings
        var activeBindings = parent['api']['ui']['KeyBindings'].get().getActiveBindings();

        var hasMatch = hasMatchingBinding(activeBindings, event);

        if (hasMatch) {
            event.preventDefault();
            console.log('Prevented default for event in live edit because it has binding in parent', event);
        }
    }

    function hasMatchingBinding(keys, event) {
        var isMod = event.ctrlKey || event.metaKey;
        var isAlt = event.altKey;
        var key = event.keyCode || event.which;

        for (var i = 0; i < keys.length; i++) {
            var matches = false;

            switch (keys[i].getCombination()) {
            case 'backspace':
                matches = key == 8;
                break;
            case 'del':
                matches = key == 46;
                // intentional fall-through
            case 'mod+del':
                matches = matches && isMod;
                break;
            case 'mod+s':
                matches = key == 83 && isMod;
                break;
            case 'mod+esc':
                matches = key == 83 && isMod;
                break;
            case 'mod+alt+f4':
                matches = key == 115 && isMod && isAlt;
                break;
            }

            if (matches) {
                return true;
            }
        }

        return false;
    }
});

