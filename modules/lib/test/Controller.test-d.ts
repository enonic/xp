import type {
    Controller,
    Request,
} from '../core/index';
import {expectAssignable} from 'tsd';

const log = {
    info: (message?: string, ...optionalParams: string[]) => { /* no-op */ },
};

const myController = {
    all: (request: Request) => {
        log.info('all request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    connect: (request: Request) => {
        log.info('connect request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    get: (request: Request) => {
        log.info('get request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    options: (request: Request) => {
        log.info('options request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
    patch: (request: Request) => {
        log.info('patch request:%s', JSON.stringify(request, null, 4));
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
    trace: (request: Request) => {
        log.info('trace request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
};

expectAssignable<Controller>(myController);
