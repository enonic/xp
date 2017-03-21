import '../../api.ts';
import {ApplicationBrowseToolbar} from './ApplicationBrowseToolbar';
import {ApplicationBrowseActions} from './ApplicationBrowseActions';
import {ApplicationTreeGrid} from './ApplicationTreeGrid';
import {ApplicationBrowseItemPanel} from './ApplicationBrowseItemPanel';
import {StopApplicationEvent} from './StopApplicationEvent';
import {StartApplicationEvent} from './StartApplicationEvent';
import {UninstallApplicationEvent} from './UninstallApplicationEvent';

import ApplicationKey = api.application.ApplicationKey;
import Application = api.application.Application;
import TreeNode = api.ui.treegrid.TreeNode;
import BrowseItem = api.app.browse.BrowseItem;
import StartApplicationRequest = api.application.StartApplicationRequest;
import StopApplicationRequest = api.application.StopApplicationRequest;
import UninstallApplicationRequest = api.application.UninstallApplicationRequest;
import ApplicationEvent = api.application.ApplicationEvent;
import ApplicationEventType = api.application.ApplicationEventType;

export class ApplicationBrowsePanel extends api.app.browse.BrowsePanel<Application> {

    private applicationIconUrl: string;

    protected treeGrid: ApplicationTreeGrid;

    constructor() {
        super();

        this.applicationIconUrl = api.util.UriHelper.getAdminUri('common/images/icons/icoMoon/128x128/puzzle.png');

        this.registerEvents();
    }

    protected createToolbar(): ApplicationBrowseToolbar {
        let browseActions = <ApplicationBrowseActions> this.treeGrid.getContextMenu().getActions();

        return new ApplicationBrowseToolbar(browseActions);
    }

    protected createTreeGrid(): ApplicationTreeGrid {
        return new ApplicationTreeGrid();
    }

    protected createBrowseItemPanel(): ApplicationBrowseItemPanel {
        return new ApplicationBrowseItemPanel(this.treeGrid);
    }

    treeNodesToBrowseItems(nodes: TreeNode<Application>[]): BrowseItem<Application>[] {
        let browseItems: BrowseItem<Application>[] = [];

        // do not proceed duplicated content. still, it can be selected
        nodes.forEach((node: TreeNode<Application>, index: number) => {
            let i = 0;
            for (; i <= index; i++) {
                if (nodes[i].getData().getId() === node.getData().getId()) {
                    break;
                }
            }
            if (i === index) {
                let applicationEl = node.getData();
                let item = new BrowseItem<Application>(applicationEl).setId(applicationEl.getId()).setDisplayName(
                    applicationEl.getDisplayName()).setPath(applicationEl.getName()).setIconUrl(this.applicationIconUrl);
                browseItems.push(item);
            }
        });
        return browseItems;
    }

    private registerEvents() {
        StopApplicationEvent.on((event: StopApplicationEvent) => {
            let applicationKeys = ApplicationKey.fromApplications(event.getApplications());
            new StopApplicationRequest(applicationKeys).sendAndParse().done();
        });

        StartApplicationEvent.on((event: StartApplicationEvent) => {
            let applicationKeys = ApplicationKey.fromApplications(event.getApplications());
            new StartApplicationRequest(applicationKeys).sendAndParse().done();
        });

        UninstallApplicationEvent.on((event: UninstallApplicationEvent) => {
            let applicationKeys = ApplicationKey.fromClusterApplications(event.getApplications());
            new UninstallApplicationRequest(applicationKeys).sendAndParse().done();
        });

        ApplicationEvent.on((event: ApplicationEvent) => {
            if (ApplicationEventType.INSTALLED === event.getEventType()) {
                this.treeGrid.placeApplicationNode(event.getApplicationKey()).then(() => {
                    setTimeout(() => { // timeout lets grid to remove UploadMockNode so that its not counted in the toolbar
                        this.treeGrid.triggerSelectionChangedListeners();
                        let installedApp = this.treeGrid.getByApplicationKey(event.getApplicationKey());
                        let installedAppName = installedApp ? installedApp.getDisplayName() : event.getApplicationKey();
                        api.notify.showFeedback(`Application '${installedAppName}' installed successfully`);
                    }, 200);
                });

            } else if (ApplicationEventType.UNINSTALLED === event.getEventType()) {
                let uninstalledApp = this.treeGrid.getByApplicationKey(event.getApplicationKey());
                let uninstalledAppName = uninstalledApp ? uninstalledApp.getDisplayName() : event.getApplicationKey();
                api.notify.showFeedback(`Application '${uninstalledAppName}' uninstalled successfully`);
                this.treeGrid.deleteApplicationNode(event.getApplicationKey());
            } else if (ApplicationEventType.STOPPED === event.getEventType()) {
                setTimeout(() => { // as uninstall usually follows stop event, lets wait to check if app still exists
                    let stoppedApp = this.treeGrid.getByApplicationKey(event.getApplicationKey());
                    // seems to be present in the grid and xp is running
                    if (stoppedApp && api.app.ServerEventsConnection.getInstance().isConnected()) {
                        this.treeGrid.updateApplicationNode(event.getApplicationKey());
                    }
                }, 400);
            } else if (event.isNeedToUpdateApplication()) {
                this.treeGrid.updateApplicationNode(event.getApplicationKey());
            }
        });

        api.application.ApplicationUploadStartedEvent.on((event) => {
            this.handleNewAppUpload(event);
        });

    }

    private handleNewAppUpload(event: api.application.ApplicationUploadStartedEvent) {
        event.getUploadItems().forEach((item: api.ui.uploader.UploadItem<Application>) => {
            this.treeGrid.appendUploadNode(item);
        });
    }
}
