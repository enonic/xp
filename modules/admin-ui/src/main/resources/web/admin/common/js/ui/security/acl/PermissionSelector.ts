module api.ui.security.acl {

    import Permission = api.security.acl.Permission;
    import PermissionState = api.security.acl.PermissionState;

    interface PermissionSelectorOption {
        value: Permission;
        name: string;
    }

    export class PermissionSelector extends api.dom.DivEl {

        private toggles: PermissionToggle[] = [];
        private oldValue: {allow: Permission[]; deny: Permission[]};
        private valueChangedListeners: {(event: api.ui.ValueChangedEvent):void}[] = [];
        private enabled: boolean = true;

        private static OPTIONS: PermissionSelectorOption[] = [
            {name: 'Read', value: Permission.READ},
            {name: 'Create', value: Permission.CREATE},
            {name: 'Modify', value: Permission.MODIFY},
            {name: 'Delete', value: Permission.DELETE},
            {name: 'Publish', value: Permission.PUBLISH},
            {name: 'Read Permissions', value: Permission.READ_PERMISSIONS},
            {name: 'Write Permissions', value: Permission.WRITE_PERMISSIONS}
        ];

        constructor() {
            super('permission-selector');

            PermissionSelector.OPTIONS.forEach((option: PermissionSelectorOption) => {
                var toggle = new PermissionToggle(option);
                toggle.setEnabled(this.enabled);
                toggle.onValueChanged((event: api.ui.ValueChangedEvent) => {
                    var newValue = this.getValue();
                    this.notifyValueChanged(new api.ui.ValueChangedEvent(JSON.stringify(this.oldValue), JSON.stringify(newValue)));
                    this.oldValue = newValue;
                });
                this.toggles.push(toggle);
                this.appendChild(toggle);
            });
        }

        setEnabled(enabled: boolean): PermissionSelector {
            if (enabled != this.enabled) {
                this.toggleClass('disabled', !enabled);
                this.toggles.forEach((toggle) => {
                    toggle.setEnabled(enabled);
                });
                this.enabled = enabled;
            }
            return this;
        }

        isEnabled(): boolean {
            return this.enabled;
        }

        getValue(): {allow: Permission[]; deny: Permission[]} {
            var values = {
                allow: [],
                deny: []
            };
            this.toggles.forEach((toggle: PermissionToggle) => {
                switch (toggle.getState()) {
                case PermissionState.ALLOW:
                    values.allow.push(toggle.getValue());
                    break;
                case PermissionState.DENY:
                    values.deny.push(toggle.getValue());
                    break;
                }

            });
            return values;
        }

        setValue(newValue: {allow: Permission[]; deny: Permission[]}, silent?: boolean): PermissionSelector {
            this.toggles.forEach((toggle: PermissionToggle) => {
                var value = toggle.getValue();
                var state;
                if (newValue.allow.indexOf(value) >= 0) {
                    state = PermissionState.ALLOW;
                } else if (newValue.deny.indexOf(value) >= 0) {
                    state = PermissionState.DENY;
                } else {
                    state = PermissionState.INHERIT;
                }
                // set it silently to throw just 1 change event
                toggle.setState(state, true);
            });
            if (!silent) {
                this.notifyValueChanged(new api.ui.ValueChangedEvent(JSON.stringify(this.oldValue), JSON.stringify(newValue)));
            }
            this.oldValue = newValue;
            return this;
        }

        onValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        notifyValueChanged(event: api.ui.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener) => {
                listener(event);
            })
        }
    }


    export class PermissionToggle extends api.dom.AEl {

        private static STATES: PermissionState[] = [PermissionState.ALLOW, PermissionState.DENY, PermissionState.INHERIT];
        private valueChangedListeners: {(event: api.ui.ValueChangedEvent):void}[] = [];

        private originalStateIndex: number = -1;
        private stateIndex: number = -1;
        private value: Permission;
        private enabled: boolean = true;

        constructor(option: PermissionSelectorOption, state: PermissionState = PermissionState.INHERIT) {
            super('permission-toggle ' + state);
            this.setHtml(option.name);
            this.value = option.value;

            if (state) {
                this.setState(state, true);
            }

            this.onClicked((event: MouseEvent) => {
                if (this.enabled) {
                    var newIndex = (this.stateIndex + 1) % PermissionToggle.STATES.length;
                    this.setState(PermissionToggle.STATES[newIndex]);

                    event.preventDefault();
                    event.stopPropagation();
                }
            });
        }

        setEnabled(enabled: boolean): PermissionToggle {
            this.enabled = enabled;
            this.toggleClass('disabled', !enabled);
            return this;
        }

        isEnabled(): boolean {
            return this.enabled;
        }

        getValue(): Permission {
            return this.value;
        }

        getState(): PermissionState {
            return PermissionToggle.STATES[this.stateIndex];
        }

        setState(newState: PermissionState, silent?: boolean): PermissionToggle {
            var newStateIndex = PermissionToggle.STATES.indexOf(newState);
            if (newStateIndex != this.stateIndex) {
                if (this.originalStateIndex < 0) {
                    this.originalStateIndex = newStateIndex;
                }
                this.toggleClass('dirty', this.originalStateIndex >= 0 && this.originalStateIndex != newStateIndex);

                var oldState = this.getState();
                if (oldState != undefined) {
                    this.removeClass(PermissionState[oldState].toLowerCase());
                }
                this.addClass(PermissionState[newState].toLowerCase());

                this.stateIndex = newStateIndex;
                if (!silent) {
                    this.notifyValueChanged(new api.ui.ValueChangedEvent(PermissionState[oldState], PermissionState[newState]));
                }
            }
            return this;
        }

        onValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyValueChanged(event: api.ui.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener) => {
                listener(event);
            })
        }

    }

}