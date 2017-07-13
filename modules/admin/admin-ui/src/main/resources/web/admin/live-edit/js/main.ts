import i18n = api.util.i18n;
import KeyBinding = api.ui.KeyBinding;
declare var CONFIG;
declare var wemjq: JQueryStatic;

/*
 Prefix must match @_CLS_PREFIX in n web\admin\live-edit\styles\less\live-edit.less
 */
api.StyleHelper.setCurrentPrefix(api.StyleHelper.PAGE_EDITOR_PREFIX);

let liveEditPage: LiveEdit.LiveEditPage;

wemjq(document).ready(() => {
    api.util.i18nInit(CONFIG.messages);

    if (liveEditPage) {
        liveEditPage.destroy();
    }

    liveEditPage = new LiveEdit.LiveEditPage();

    // Notify parent frame if any modifier except shift is pressed
    // For the parent shortcuts to work if the inner iframe has focus
    wemjq(document).on('keypress keydown keyup', (event) => {

        if (shouldBubbleEvent(event)) {

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

    function shouldBubbleEvent(event: any): boolean {
        let shouldBubble: boolean;
        switch (event.keyCode) {
        case 113:  // F2 global help shortcut
            shouldBubble = true;
            break;
        default:
            shouldBubble = (event.metaKey || event.ctrlKey || event.altKey) && !!event.keyCode;
            break;
        }
        return shouldBubble;
    }

    function stopBrowserShortcuts(event: any) {
        // get the parent's frame bindings
        let activeBindings = parent['api']['ui']['KeyBindings'].get().getActiveBindings();

        let hasMatch = hasMatchingBinding(activeBindings, event);

        if (hasMatch) {
            event.preventDefault();
            console.log('Prevented default for event in live edit because it has binding in parent', event);
        }
    }

    function hasMatchingBinding(keys: KeyBinding[], event: KeyboardEvent) {
        let isMod = event.ctrlKey || event.metaKey;
        let isAlt = event.altKey;
        let key = event.keyCode || event.which;

        for (let i = 0; i < keys.length; i++) {
            let matches = false;

            switch (keys[i].getCombination()) {
            case 'backspace':
                matches = key === 8;
                break;
            case 'del':
                matches = key === 46;
                // intentional fall-through
            case 'mod+del':
                matches = matches && isMod;
                break;
            case 'mod+s':
                matches = key === 83 && isMod;
                break;
            case 'mod+esc':
                matches = key === 83 && isMod;
                break;
            case 'mod+alt+f4':
                matches = key === 115 && isMod && isAlt;
                break;
            }

            if (matches) {
                return true;
            }
        }

        return false;
    }
});
