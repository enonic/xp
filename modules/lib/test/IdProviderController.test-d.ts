import type {
    IdProviderController,
    Request,
} from '../core/index';

import {expectAssignable} from 'tsd';

const log = {
    info: (message?: string, ...optionalParams: unknown[]) => { /* no-op */ },
};

const myIdProviderController: IdProviderController = {
    autoLogin: (request: Request) => {
        log.info('autoLogin request:%s', JSON.stringify(request, null, 4));
    },
    handle401: (request: Request) => {
        log.info('401 request:%s', JSON.stringify(request, null, 4));
        return {
            contentType: 'text/html',
            status: 401 as const,
            body: `
<DOCTYPE html>
<html>
    <head>
        <title>401 Unauthorized</title>
    </head>
    <body>
        <h1>401 Unauthorized</h1>
        <p>Unauthorized access to this resource, please login.</p>
    </body>
</html>`,
        };
    },
    get: (request: Request) => {
        log.info('get request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    login: (request: Request) => {
        log.info('login request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    logout: (request: Request) => {
        log.info('logout request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    post: (request: Request) => {
        log.info('post request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
};

expectAssignable<IdProviderController>(myIdProviderController);