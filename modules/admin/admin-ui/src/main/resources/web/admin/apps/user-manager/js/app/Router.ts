import '../api.ts';

export class Router {
    static setHash(path: string) {
        hasher.changed.active = false;
        hasher.setHash(path);
        hasher.changed.active = true;
    }

    static getPath(): string {
        return window.location.hash ? window.location.hash.substr(1) : '/';
    }
}
