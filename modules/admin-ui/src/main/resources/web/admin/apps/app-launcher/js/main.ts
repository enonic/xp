declare var CONFIG;

function startApplication() {
    new app.Main().start();
}

// called from api.app.Application.getApplication()
function getApplication(id: string): api.app.Application {
    return app.launcher.Applications.getAppById(id);
}

// called from Router
function setHash(path: string) {
    hasher.changed.active = false;
    hasher.setHash(path);
    hasher.changed.active = true;
}