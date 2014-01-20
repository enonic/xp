module app.launcher {

    export class AppSelector extends api.dom.DivEl {
        private selectedAppIndex: number;
        private apps: Application[];
        private appTiles: {[name: string]: AppTile;};
        private listeners: AppSelectorListener[] = [];
        private applicationSearchInput: api.ui.TextInput;
        private emptyMessagePlaceholder: api.dom.DivEl;
        private homeAppSelector: api.dom.DivEl;

        private keyBindings: api.ui.KeyBinding[] = [];

        constructor(applications: Application[]) {
            super();
            this.apps = applications;
            this.appTiles = {};
            this.selectedAppIndex = -1;

            this.homeAppSelector = new api.dom.DivEl('app-selector');

            var searchInputContainer = new api.dom.DivEl('search-input-container');
            this.applicationSearchInput = new api.ui.TextInput();
            this.applicationSearchInput.setPlaceholder('Application Filter');
            this.applicationSearchInput.addListener({
                onValueChanged: (oldValue, newValue) => {
                    this.filterTiles(newValue);
                }
            });
            searchInputContainer.appendChild(this.applicationSearchInput);

            this.homeAppSelector.appendChild(searchInputContainer);

            var tilesPlaceholder = new api.dom.DivEl('app-tiles-placeholder');
            this.emptyMessagePlaceholder = new api.dom.DivEl();
            this.emptyMessagePlaceholder.getEl().setInnerHtml('No applications found');
            this.emptyMessagePlaceholder.hide();
            tilesPlaceholder.appendChild(this.emptyMessagePlaceholder);

            this.addAppTiles(applications, tilesPlaceholder);
            this.homeAppSelector.appendChild(tilesPlaceholder);

            this.appendChild(this.homeAppSelector);

            this.keyBindings.push(new api.ui.KeyBinding('tab', (e: ExtendedKeyboardEvent, combo: string)=> {
                if (this.isVisible()) {
                    this.highlightNextAppTile();
                }
                return false;
            }));
            this.keyBindings.push(new api.ui.KeyBinding('shift+tab', (e: ExtendedKeyboardEvent, combo: string)=> {
                if (this.isVisible()) {
                    this.highlightPreviousAppTile();
                }
                return false;
            }));
            this.keyBindings.push(new api.ui.KeyBinding('return', (e: ExtendedKeyboardEvent, combo: string)=> {
                if (this.selectedAppIndex >= 0) {
                    var app: Application = this.apps[this.selectedAppIndex];
                    this.notifyAppSelected(app);
                }
                return false;
            }));

        }

        show() {
            super.show();
            this.activateKeyBindings();
        }

        hide() {
            super.hide();
            this.deactivateKeyBindings();
        }

        activateKeyBindings() {
            api.ui.KeyBindings.get().bindKeys(this.keyBindings);
        }

        deactivateKeyBindings() {
            api.ui.KeyBindings.get().unbindKeys(this.keyBindings);
        }

        giveFocus(): boolean {
            return this.applicationSearchInput.giveFocus();
        }

        addListener(listener: AppSelectorListener) {
            this.listeners.push(listener);
        }

        afterRender() {
            super.afterRender();

            this.activateKeyBindings();

            setTimeout(() => {
                this.homeAppSelector.addClass('fade-in-and-scale-up');
            }, 200);
        }

        private highlightNextAppTile() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = (this.selectedAppIndex + i) % n;
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.highlightAppTile(this.apps[idx], idx);
        }

        private highlightPreviousAppTile() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = ((this.selectedAppIndex - i % n) + n) % n; // workaround for js negative mod bug
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.highlightAppTile(this.apps[idx], idx);
        }

        private addAppTiles(applications: Application[], tilesPlaceholder: api.dom.DivEl) {
            applications.forEach((application: Application, idx: number) => {
                var appTile = new AppTile(application);

                appTile.onMouseEnter((event: MouseEvent) => {
                    this.highlightAppTile(application, idx, appTile);
                });
                appTile.onMouseLeave((event: MouseEvent) => {
                    this.unhighlightAppTile(application, idx, appTile);
                });
                appTile.getEl().addEventListener("click", (evt: Event) => {
                    this.notifyAppSelected(application);
                });

                tilesPlaceholder.appendChild(appTile);
                this.appTiles[application.getName()] = appTile;
            });
        }

        private highlightAppTile(application: Application, index: number, appTile?: AppTile) {
            if (!appTile) {
                appTile = this.appTiles[application.getName()];
            }
            var currentSelected = this.selectedAppIndex >= 0;
            if (currentSelected) {
                var currentTileSelected = this.appTiles[this.apps[this.selectedAppIndex].getName()];
                currentTileSelected.removeClass('app-tile-over');
            }
            appTile.addClass('app-tile-over');
            this.selectedAppIndex = index;
            this.notifyAppHighlighted(application);
        }

        private unhighlightAppTile(application: Application, index: number, appTile?: AppTile) {
            if (!appTile) {
                appTile = this.appTiles[application.getName()];
            }
            appTile.removeClass('app-tile-over');
            if (this.selectedAppIndex === index) {
                this.selectedAppIndex = -1;
            }
            this.notifyAppUnhighlighted(application);
        }

        private filterTiles(value: string) {
            var valueLowerCased = value.toLowerCase();
            var anyMatch = false;
            this.apps.forEach((app: Application) => {
                var isMatch = app.getName().toLowerCase().indexOf(valueLowerCased) > -1;
                if (isMatch) {
                    this.showAppTile(app.getName());
                    anyMatch = true;
                } else {
                    this.hideAppTile(app.getName());
                }
            });

            if (anyMatch) {
                this.emptyMessagePlaceholder.hide();
            } else {
                this.emptyMessagePlaceholder.show();
            }
        }

        private showAppTile(appName: string) {
            var appTile: AppTile = this.appTiles[appName];
            appTile.show();
        }

        private hideAppTile(appName: string) {
            var appTile: AppTile = this.appTiles[appName];
            appTile.hide();
        }

        private isAppTileVisible(appIndex: number): boolean {
            return this.appTiles[this.apps[appIndex].getName()].isVisible();
        }

        private notifyAppHighlighted(app: Application) {
            this.listeners.forEach((listener: AppSelectorListener)=> {
                listener.onAppHighlighted(app);
            });
        }

        private notifyAppUnhighlighted(app: Application) {
            this.listeners.forEach((listener: AppSelectorListener)=> {
                listener.onAppUnhighlighted(app);
            });
        }

        private notifyAppSelected(app: Application) {
            this.listeners.forEach((listener: AppSelectorListener)=> {
                listener.onAppSelected(app);
            });
        }

    }

}
