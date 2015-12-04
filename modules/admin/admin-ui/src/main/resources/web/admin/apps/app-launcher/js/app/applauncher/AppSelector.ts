module app.launcher {

    export class AppSelector extends api.dom.DivEl {

        private selectedAppIndex: number;

        private apps: api.app.Application[];

        private appTiles: {[name: string]: AppTile;};

        private appHighlightedListeners: {(event: AppHighlightedEvent):void}[] = [];

        private appUnhighlightedListeners: {(event: AppUnhighlightedEvent):void}[] = [];

        private appSelectedListeners: {(event: AppSelectedEvent):void}[] = [];

        private emptyMessagePlaceholder: api.dom.DivEl;

        private homeAppSelector: api.dom.DivEl;

        private keyBindings: api.ui.KeyBinding[] = [];

        private tilesPlaceholder: api.dom.DivEl;


        constructor(applications: api.app.Application[]) {
            super("app-selector-container");
            this.apps = applications;
            this.appTiles = {};
            this.selectedAppIndex = -1;

            this.homeAppSelector = new api.dom.DivEl('app-selector');

            this.tilesPlaceholder = new api.dom.DivEl('app-tiles-placeholder');
            this.emptyMessagePlaceholder = new api.dom.DivEl();
            this.emptyMessagePlaceholder.getEl().setInnerHtml('No applications found');

            this.addAppTiles(applications, this.tilesPlaceholder);
            this.homeAppSelector.appendChild(this.tilesPlaceholder);

            this.appendChild(this.homeAppSelector);

            this.keyBindings = this.keyBindings.concat(api.ui.KeyBinding.createMultiple((e: ExtendedKeyboardEvent, combo: string)=> {
                if (this.isVisible()) {
                    this.highlightNextAppTile();
                }
                return false;
            }, 'tab', 'right'));

            this.keyBindings = this.keyBindings.concat(api.ui.KeyBinding.createMultiple((e: ExtendedKeyboardEvent, combo: string)=> {
                if (this.isVisible()) {
                    this.highlightPreviousAppTile();
                }
                return false;
            }, 'shift+tab', 'left'));

            this.keyBindings = this.keyBindings.concat(api.ui.KeyBinding.createMultiple((e: ExtendedKeyboardEvent, combo: string)=> {
                if (this.selectedAppIndex >= 0) {
                    var application: api.app.Application = this.apps[this.selectedAppIndex];
                    this.notifyAppSelected(application);
                }
                return false;
            }, 'return', 'space'));

            this.onRendered((event) => {

                setTimeout(() => {
                    this.homeAppSelector.addClass('fade-in-and-scale-up');
                }, 200);

            });

            this.onShown((event) => {
                api.ui.KeyBindings.get().bindKeys(this.getKeyBindings());
            });

            api.app.ShowAppLauncherEvent.on((event) => {
                this.highlightAppTile(event.getApplication());
            });

        }



        show() {
            this.showAppsCount();
            super.show();
        }

        hide() {
            super.hide();
        }

        getKeyBindings(): api.ui.KeyBinding[] {
            return this.keyBindings;
        }

        giveFocus(): boolean {
            //this.highlightAppTile(this.apps[0], 0);
            return true;
        }

        showAppsCount() {
            this.apps.forEach((application: api.app.Application) => {
                var appTile: AppTile = this.appTiles[application.getName()];
                appTile.showCount();
            });
        }

        highlightNextAppTile() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = (this.selectedAppIndex + i) % n;
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.highlightAppTile(this.apps[idx], idx);
        }

        highlightPreviousAppTile() {
            var n = this.apps.length, i = 0, idx;
            do {
                i++;
                idx = ((this.selectedAppIndex - i % n) + n) % n; // workaround for js negative mod bug
            }
            while (i < n && !this.isAppTileVisible(idx));

            this.highlightAppTile(this.apps[idx], idx);
        }

        setAllowedApps(applications: api.app.Application[]) {
            this.apps.forEach((application: api.app.Application) => {
                var appTile = this.appTiles[application.getName()];
                appTile.hide();
            });
            applications.forEach((application: api.app.Application) => {
                var appTile = this.appTiles[application.getName()];
                appTile.show();
            });
        }

        private addAppTiles(applications: api.app.Application[], tilesPlaceholder: api.dom.DivEl) {
            applications.forEach((application: api.app.Application, idx: number) => {
                var appTile = new AppTile(application, idx);

                appTile.onClicked((event: MouseEvent) => {
                    this.notifyAppSelected(application);
                });

                appTile.onMouseEnter((event: MouseEvent) => {
                    this.highlightAppTile(application, idx, appTile);
                });
                appTile.onMouseLeave((event: MouseEvent) => {
                    this.unhighlightAppTile(application, idx, appTile);
                });

                tilesPlaceholder.appendChild(appTile);
                this.appTiles[application.getName()] = appTile;
            });
        }

        private highlightAppTile(application: api.app.Application, index?: number, appTile?: AppTile) {
            if (!index) {
                index = this.getAppTileIndex(application);
            }
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

        private unhighlightAppTile(application: api.app.Application, index: number, appTile?: AppTile) {
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
            this.apps.forEach((application: api.app.Application) => {
                var isMatch = application.getName().toLowerCase().indexOf(valueLowerCased) > -1;
                if (isMatch) {
                    this.showAppTile(application.getName());
                    anyMatch = true;
                } else {
                    this.hideAppTile(application.getName());
                }
            });

            if (anyMatch) {
                this.tilesPlaceholder.removeChild(this.emptyMessagePlaceholder);
            } else {
                this.tilesPlaceholder.appendChild(this.emptyMessagePlaceholder);
            }
        }

        private getAppTileIndex(application: api.app.Application): number {
            var apps = app.launcher.Applications.getAllApps();
            for (var i = 0; i < apps.length; i++) {
                if (apps[i] == application) {
                    return i;
                }
            }
            return -1;
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

        onAppHighlighted(listener: (event: AppHighlightedEvent)=>void) {
            this.appHighlightedListeners.push(listener);
        }

        onAppUnhighlighted(listener: (event: AppUnhighlightedEvent)=>void) {
            this.appUnhighlightedListeners.push(listener);
        }

        onAppSelected(listener: (event: AppSelectedEvent)=>void) {
            this.appSelectedListeners.push(listener);
        }

        unAppHighlighted(listener: (event: AppHighlightedEvent)=>void) {
            this.appHighlightedListeners = this.appHighlightedListeners.filter((currentListener: (event: AppHighlightedEvent)=>void)=> {
                return listener != currentListener
            });
        }

        unAppUnhighlighted(listener: (event: AppUnhighlightedEvent)=>void) {
            this.appUnhighlightedListeners =
            this.appUnhighlightedListeners.filter((currentListener: (event: AppUnhighlightedEvent)=>void)=> {
                return listener != currentListener
            });
        }

        unAppSelected(listener: (event: AppSelectedEvent)=>void) {
            this.appSelectedListeners = this.appSelectedListeners.filter((currentListener: (event: AppSelectedEvent)=>void)=> {
                return listener != currentListener
            });
        }

        private notifyAppHighlighted(application: api.app.Application) {
            this.appHighlightedListeners.forEach((listener: (event: AppHighlightedEvent)=>void)=> {
                listener.call(this, new AppHighlightedEvent(application));
            });
        }

        private notifyAppUnhighlighted(application: api.app.Application) {
            this.appUnhighlightedListeners.forEach((listener: (event: AppUnhighlightedEvent)=>void)=> {
                listener.call(this, new AppUnhighlightedEvent(application));
            });
        }

        private notifyAppSelected(application: api.app.Application) {
            this.appSelectedListeners.forEach((listener: (event: AppSelectedEvent)=>void)=> {
                listener.call(this, new AppSelectedEvent(application));
            });
        }

    }

}
