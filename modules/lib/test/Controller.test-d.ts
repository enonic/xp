import type {
    Controller,
    Request,
    RequestBranch,
    RequestMethod,
    RequestMode,
    RequestScheme,
    Response,
} from '../core/index';
import {
    expectAssignable,
    // expectDeprecated,
    // expectDocCommentIncludes,
    // expectError,
    // expectNever,
	expectNotAssignable,
    // expectNotDeprecated,
    // expectNotType,
    // expectType,
    // printType,
} from 'tsd';

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
    post: (request: Request) => {
        log.info('post request:%s', JSON.stringify(request, null, 4));
        return {
            status: 200,
        };
    },
};

expectAssignable<Controller>(myController);

type PageRequest = Omit<Request<{
    // Only allow literal string
    branch: RequestBranch
    method: RequestMethod
    mode: RequestMode
    scheme: RequestScheme

    // Make some optional properties required
    repositoryId: string
    webSocket: boolean
}>,
    // Omit/Disallow some optional properties
    'contextPath' | 'validTicket'
>;
type PageRequestHandler = (request: PageRequest) => Response;

interface PageController {
    all?: PageRequestHandler
    // connect?: PageRequestHandler
    delete?: PageRequestHandler
    get?: PageRequestHandler
    head?: PageRequestHandler
    options?: PageRequestHandler
    // patch?: PageRequestHandler
    post?: PageRequestHandler
    put?: PageRequestHandler
    // trace?: PageRequestHandler
    // TODO what about propfind, proppatch, mkcol, copy, move, lock and unlock?
}

const pageRequest /* : PageRequest */ = {
    method: 'GET' as PageRequest['method'], // Avoid flattening down to string
    scheme: 'http' as PageRequest['scheme'], // Avoid flattening down to string
    mode: 'preview' as PageRequest['mode'], // Avoid flattening down to string
    branch: 'draft' as PageRequest['branch'], // Avoid flattening down to string
    host: 'localhost',
    port: 8080,
    path: '/admin/site/preview/my-project/draft/my-site',
    rawPath: '/admin/site/preview/my-project/draft/my-site',
    url: 'http://localhost:8080/admin/site/preview/my-project/draft/my-site',
    remoteAddress: '127.0.0.1',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    params: {},
    headers: {
        Accept: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'app.browse.RecentItemsList=portal%3Asite; JSESSIONID=19g9dxfzwnyqo1i6ufom5ksuhg0',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main',
        'sec-ch-ua': 'Chromium;v=128, Not;A=Brand;v=24, Google Chrome;v=128',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': 'macOS',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: (header: string): string => {
        return pageRequest.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'portal%3Asite',
        JSESSIONID: '19g9dxfzwnyqo1i6ufom5ksuhg0',
    },
};

expectAssignable<PageRequest>(pageRequest);

expectNotAssignable<PageRequest>({
    ...pageRequest,
    contextPath: '/contextPath',
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    validTicket: true,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    branch: 'stringButNotMatchingLiterals',
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    method: 'stringButNotMatchingLiterals',
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    mode: 'stringButNotMatchingLiterals',
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    scheme: 'stringButNotMatchingLiterals',
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    cookies: undefined,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    headers: undefined,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    params: undefined,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    rawPath: undefined,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    remoteAddress: undefined,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    repositoryId: undefined,
});

expectNotAssignable<PageRequest>({
    ...pageRequest,
    webSocket: undefined,
});