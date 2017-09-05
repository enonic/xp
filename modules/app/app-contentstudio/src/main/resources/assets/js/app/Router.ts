import '../api.ts';

export class Router {

    private static prevHash: string;

    static setHash(path: string) {

        if(Router.prevHash != hasher.getHash()) {
            Router.prevHash = hasher.getHash();
        }

        hasher.changed.active = false;
        hasher.setHash(path);
        hasher.changed.active = true;
    }

    static getPath(): string {
        return window.location.hash ? window.location.hash.substr(1) : '/';
    }

    static back() {
        if(Router.prevHash) {
            Router.setHash(Router.prevHash);
        }
    }
}
