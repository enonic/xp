import type {
    Request,
    StrictMergeInterfaces,
} from '../core/index';
import {RequestImplementation} from './RequestImplementation';

import {
    expectAssignable,
    // expectDeprecated,
    // expectDocCommentIncludes,
    expectError,
    // expectNever,
	expectNotAssignable,
    // expectNotDeprecated,
    // expectNotType,
    // expectType,
    printType,
} from 'tsd';




// Scenario: When implementing a idprovider login function the Request may have a validTicket property
// Scenario: When implementing a idprovider logout function the Request may have a validTicket property
const idproviderLogoutRequest: Request = new RequestImplementation({
    branch: 'draft',
    contextPath: '/_/idprovider/system',
    cookies: {
        'app.browse.RecentItemsList': 'base%3Afolder%7Cportal%3Asite',
        JSESSIONID: '90g5qk9dul0uwl4r023jb08z0',
    },
    headers: {
        Accept: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        // ...
    } as Record<string, string>,
    host: 'localhost',
    method: 'GET',
    mode: 'live',
    params: {
        redirect: 'http://localhost:8080/admin',
    },
    path: '/_/idprovider/system/logout',
    port: 8080,
    rawPath: '/_/idprovider/system/logout',
    remoteAddress: '127.0.0.1',
    url: 'http://localhost:8080/_/idprovider/system/logout?redirect=http%3A%2F%2Flocalhost%3A8080%2Fadmin&_ticket=6ce7eadaff4dffcb8c8930a4179af5c47680613a',
    scheme: 'http',
    validTicket: true,
    webSocket: false,
}).toObject();

// printType(idproviderLogoutRequest);

expectAssignable<Request>(idproviderLogoutRequest);

// Scenario: Webapp request

const webappControllerGetRequest = {
    'method': 'GET',
    'scheme': 'http',
    'host': 'localhost',
    'port': 8080,
    'path': '/webapp/com.acme.example.tsup',
    'rawPath': '/webapp/com.acme.example.tsup/',
    'url': 'http://localhost:8080/webapp/com.acme.example.tsup',
    'remoteAddress': '127.0.0.1',
    'mode': 'live',
    'webSocket': false,
    'repositoryId': 'com.enonic.cms.default',
    'branch': 'draft',
    'contextPath': '/webapp/com.acme.example.tsup',
    'params': {},
    'headers': {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        'Connection': 'keep-alive',
        'Cookie': 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        'Host': 'localhost:8080',
        'Referer': 'http://localhost:8080/admin/tool/com.enonic.xp.app.applications/main',
        'sec-ch-ua': '\'Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128\'',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '\'macOS\'',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-User': '?1',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    'cookies': {
        'app.browse.RecentItemsList': 'com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        'JSESSIONID': '15hb7cb69ai5vrbg9msvtjmur0',
    },
    'pathParams': {},
};
expectAssignable<Request>(webappControllerGetRequest);

// Scenario: A page controller get request does not have a contextPath property
const pageRequest = {
    method: 'HEAD',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/site/inline/my-project/draft/my-site',
    rawPath: '/admin/site/inline/my-project/draft/my-site',
    url: 'http://localhost:8080/admin/site/inline/my-project/draft/my-site',
    remoteAddress: '127.0.0.1',
    mode: 'inline',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    branch: 'draft',
    params: {},
    headers: {
        Accept: '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'app.browse.RecentItemsList=base%3Afolder%7Cportal%3Asite; JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'base%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};

expectAssignable<Request>(pageRequest);

// Scenario: A layout controller get request
const layoutControllerGetRequest = {
    'method': 'GET',
    'scheme': 'http',
    'host': 'localhost',
    'port': 8080,
    'path': '/admin/site/edit/my-project/draft/ce47cc0b-2502-4df5-add1-826204b3fc72/_/component/main/0',
    'rawPath': '/admin/site/edit/my-project/draft/ce47cc0b-2502-4df5-add1-826204b3fc72/_/component/main/0',
    'url': 'http://localhost:8080/admin/site/edit/my-project/draft/ce47cc0b-2502-4df5-add1-826204b3fc72/_/component/main/0',
    'remoteAddress': '127.0.0.1',
    'mode': 'edit',
    'webSocket': false,
    'repositoryId': 'com.enonic.cms.my-project',
    'branch': 'draft',
    'params': {},
    'headers': {
        'Accept': '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        'Connection': 'keep-alive',
        'Cookie': 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'Host': 'localhost:8080',
        'Referer': 'http://localhost:8080/admin/site/edit/my-project/draft/ce47cc0b-2502-4df5-add1-826204b3fc72',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
        'X-Requested-With': 'XMLHttpRequest',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    'cookies': {
        'app.browse.RecentItemsList': 'base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'JSESSIONID': '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(layoutControllerGetRequest);

// Scenario: A layout fragment request
const layoutFragmentRequest ={
    'method': 'GET',
    'scheme': 'http',
    'host': 'localhost',
    'port': 8080,
    'path': '/admin/site/preview/my-project/draft/my-site/page-with-layout/fragment-sample-layout',
    'rawPath': '/admin/site/preview/my-project/draft/my-site/page-with-layout/fragment-sample-layout',
    'url': 'http://localhost:8080/admin/site/preview/my-project/draft/my-site/page-with-layout/fragment-sample-layout',
    'remoteAddress': '127.0.0.1',
    'mode': 'preview',
    'webSocket': false,
    'repositoryId': 'com.enonic.cms.my-project',
    'branch': 'draft',
    'params': {},
    'headers': {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        'Connection': 'keep-alive',
        'Cookie': 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'Host': 'localhost:8080',
        'Referer': 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main/my-project/edit/d0ea0d64-1736-423c-83cd-3e4987757df6',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    'cookies': {
        'app.browse.RecentItemsList': 'base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'JSESSIONID': '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(layoutFragmentRequest);

// Scenario: A layout componentUrl request

// Scenario: A part controller get request
const partControllerGetRequest = {
    'method': 'GET',
    'scheme': 'http',
    'host': 'localhost',
    'port': 8080,
    'path': '/admin/site/edit/my-project/draft/419f1109-85b4-4336-916d-416877687b65/_/component/main/0',
    'rawPath': '/admin/site/edit/my-project/draft/419f1109-85b4-4336-916d-416877687b65/_/component/main/0',
    'url': 'http://localhost:8080/admin/site/edit/my-project/draft/419f1109-85b4-4336-916d-416877687b65/_/component/main/0',
    'remoteAddress': '127.0.0.1',
    'mode': 'edit',
    'webSocket': false,
    'repositoryId': 'com.enonic.cms.my-project',
    'branch': 'draft',
    'params': {},
    'headers': {
        'Accept': '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        'Connection': 'keep-alive',
        'Cookie': 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'Host': 'localhost:8080',
        'Referer': 'http://localhost:8080/admin/site/edit/my-project/draft/419f1109-85b4-4336-916d-416877687b65',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
        'X-Requested-With': 'XMLHttpRequest',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    'cookies': {
        'app.browse.RecentItemsList': 'base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'JSESSIONID': '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(partControllerGetRequest);

// Scenario: A part fragment request
const partFragmentRequest = {
    'method': 'GET',
    'scheme': 'http',
    'host': 'localhost',
    'port': 8080,
    'path': '/admin/site/preview/my-project/draft/my-site/page-with-part/fragment-sample-part',
    'rawPath': '/admin/site/preview/my-project/draft/my-site/page-with-part/fragment-sample-part',
    'url': 'http://localhost:8080/admin/site/preview/my-project/draft/my-site/page-with-part/fragment-sample-part',
    'remoteAddress': '127.0.0.1',
    'mode': 'preview',
    'webSocket': false,
    'repositoryId': 'com.enonic.cms.my-project',
    'branch': 'draft',
    'params': {},
    'headers': {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        'Connection': 'keep-alive',
        'Cookie': 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'Host': 'localhost:8080',
        'Referer': 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main/my-project/edit/78eda2c1-3e8b-4356-b619-497bc8302a7b',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    'cookies': {
        'app.browse.RecentItemsList': 'base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'JSESSIONID': '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(partFragmentRequest);

// Scenario: A part componentUrl request


// Scenario: The request inside an error controller handleError function
// This particaular error is a 404, and doesn't include contextPath either.
const error = {
    status: 404,
    message: 'Page [/my-site/asdadfs] not found',
    request: {
        method: 'GET',
        scheme: 'http',
        host: 'localhost',
        port: 8080,
        path: '/admin/site/preview/my-project/draft/my-site/asdadfs',
        rawPath: '/admin/site/preview/my-project/draft/my-site/asdadfs',
        url: 'http://localhost:8080/admin/site/preview/my-project/draft/my-site/asdadfs',
        remoteAddress: '127.0.0.1',
        mode: 'preview',
        webSocket: false,
        repositoryId: 'com.enonic.cms.my-project',
        branch: 'draft',
        params: {},
        headers: {
            Accept: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
            'Accept-Encoding': 'gzip, deflate, br, zstd',
            'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
            Connection: 'keep-alive',
            Cookie: 'app.browse.RecentItemsList=base%3Afolder%7Cportal%3Asite; JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0',
            Host: 'localhost:8080',
            'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
            'sec-ch-ua-mobile': '?0',
            'sec-ch-ua-platform': '"macOS"',
            'Sec-Fetch-Dest': 'document',
            'Sec-Fetch-Mode': 'navigate',
            'Sec-Fetch-Site': 'none',
            'Sec-Fetch-User': '?1',
            'Upgrade-Insecure-Requests': '1',
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
        } as Record<string, string>,
        getHeader: function (header: string): string {
            return this.headers[header];
        },
        cookies: {
            'app.browse.RecentItemsList': 'base%3Afolder%7Cportal%3Asite',
            JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
        },
    },
};

expectAssignable<Request>(error.request);


// Scenario: Service request
const serviceRequest ={
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/site/inline/my-project/draft/my-site/folder-using-dynamicpage/_/service/com.example.app.lib.static.page/myStatic/css/style.css',
    rawPath: '/admin/site/inline/my-project/draft/my-site/folder-using-dynamicpage/_/service/com.example.app.lib.static.page/myStatic/css/style.css',
    url: 'http://localhost:8080/admin/site/inline/my-project/draft/my-site/folder-using-dynamicpage/_/service/com.example.app.lib.static.page/myStatic/css/style.css',
    remoteAddress: '127.0.0.1',
    mode: 'inline',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    branch: 'draft',
    contextPath: '/admin/site/inline/my-project/draft/my-site/folder-using-dynamicpage/_/service/com.example.app.lib.static.page/myStatic',
    params: {},
    headers: {
        Accept: 'text/css,*/*;q=0.1',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'app.browse.RecentItemsList=base%3Afolder%7Cportal%3Asite; JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/site/inline/my-project/draft/my-site/folder-using-dynamicpage',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'style',
        'Sec-Fetch-Mode': 'no-cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'base%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};

expectAssignable<Request>(serviceRequest);

const siteMappingRequest = {
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/site/preview/my-project/draft/my-site/_static/css/style.css',
    rawPath: '/admin/site/preview/my-project/draft/my-site/_static/css/style.css',
    url: 'http://localhost:8080/admin/site/preview/my-project/draft/my-site/_static/css/style.css',
    remoteAddress: '127.0.0.1',
    mode: 'preview',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    branch: 'draft',
    contextPath: '/admin/site/preview/my-project/draft/my-site',
    params: {},
    headers: {
        Accept: 'text/css,*/*;q=0.1',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'app.browse.RecentItemsList=base%3Afolder%7Cportal%3Asite; JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/site/preview/my-project/draft/my-site/_static/',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'style',
        'Sec-Fetch-Mode': 'no-cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'base%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
    pathParams: {
        path: '/_static/css/style.css',
    },
};

expectAssignable<Request>(siteMappingRequest);

// Scenario: Custom selector request

const customSelectorRequest = {
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/site/edit/my-project/draft/022f32d3-df5b-4a0b-ac1a-fea778b128fa/_/service/com.example.myproject/countries',
    rawPath: '/admin/site/edit/my-project/draft/022f32d3-df5b-4a0b-ac1a-fea778b128fa/_/service/com.example.myproject/countries',
    url: 'http://localhost:8080/admin/site/edit/my-project/draft/022f32d3-df5b-4a0b-ac1a-fea778b128fa/_/service/com.example.myproject/countries?count=10',
    remoteAddress: '127.0.0.1',
    mode: 'edit',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    branch: 'draft',
    contextPath: '/admin/site/edit/my-project/draft/022f32d3-df5b-4a0b-ac1a-fea778b128fa/_/service/com.example.myproject/countries',
    params: {
        count: '10',
    },
    headers: {
        Accept: 'application/json',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main/my-project/edit/022f32d3-df5b-4a0b-ac1a-fea778b128fa',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};

expectAssignable<Request>(customSelectorRequest);

// TODO https://developer.enonic.com/docs/xp/stable/framework/filters
// TODO processors

const responseProcessorRequest = {
    'method': 'HEAD',
    'scheme': 'http',
    'host': 'localhost',
    'port': 8080,
    'path': '/admin/site/inline/my-project/draft/my-site',
    'rawPath': '/admin/site/inline/my-project/draft/my-site',
    'url': 'http://localhost:8080/admin/site/inline/my-project/draft/my-site',
    'remoteAddress': '127.0.0.1',
    'mode': 'inline',
    'webSocket': false,
    'repositoryId': 'com.enonic.cms.my-project',
    'branch': 'draft',
    'params': {},
    'headers': {
        'Accept': '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        'Connection': 'keep-alive',
        'Cookie': 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'Host': 'localhost:8080',
        'Referer': 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main',
        'sec-ch-ua': '\'Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128\'',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '\'macOS\'',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    'cookies': {
        'app.browse.RecentItemsList': 'base%3Afolder%7Ccom.example.myproject%3Aperson%7Cportal%3Asite',
        'JSESSIONID': '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(responseProcessorRequest);

// Scenario: Admin tool request
const adminToolRequest = {
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/tool/com.acme.example.tsup/sample',
    rawPath: '/admin/tool/com.acme.example.tsup/sample',
    url: 'http://localhost:8080/admin/tool/com.acme.example.tsup/sample',
    remoteAddress: '127.0.0.1',
    mode: 'admin',
    webSocket: false,
    repositoryId: 'com.enonic.cms.default',
    branch: 'draft',
    contextPath: '/admin/tool/com.acme.example.tsup/sample',
    params: {},
    headers: {
        Accept: 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'same-origin',
        'Sec-Fetch-User': '?1',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(adminToolRequest);

// Scenario: Admin context panel widget request
const contextpanelWidgetRequest = {
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/contextPanel',
    rawPath: '/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/contextPanel',
    url: 'http://localhost:8080/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/contextPanel?repository=com.enonic.cms.my-project&branch=draft&t=1727866860019',
    remoteAddress: '127.0.0.1',
    mode: 'admin',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    branch: 'draft',
    contextPath: '/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/contextPanel',
    params: {
        repository: 'com.enonic.cms.my-project',
        branch: 'draft',
        t: '1727866860019',
    },
    headers: {
        Accept: '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(contextpanelWidgetRequest);

// Scenario: Admin dashboard widget request
const dashboardWidgetRequest = {
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/tool/_/widgets/com.acme.example.tsup/dashboard',
    rawPath: '/admin/tool/_/widgets/com.acme.example.tsup/dashboard',
    url: 'http://localhost:8080/admin/tool/_/widgets/com.acme.example.tsup/dashboard',
    remoteAddress: '127.0.0.1',
    mode: 'admin',
    webSocket: false,
    repositoryId: 'com.enonic.cms.default',
    branch: 'draft',
    contextPath: '/admin/tool/_/widgets/com.acme.example.tsup/dashboard',
    params: {},
    headers: {
        Accept: '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(dashboardWidgetRequest);

// Scenario: Admin menuitem widget request
const menuitemWidgetRequest = {
    method: 'GET',
    scheme: 'http',
    host: 'localhost',
    port: 8080,
    path: '/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/menuItem',
    rawPath: '/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/menuItem',
    url: 'http://localhost:8080/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/menuItem',
    remoteAddress: '127.0.0.1',
    mode: 'admin',
    webSocket: false,
    repositoryId: 'com.enonic.cms.my-project',
    branch: 'draft',
    contextPath: '/admin/site/admin/my-project/draft/_/widgets/com.acme.example.tsup/menuItem',
    params: {},
    headers: {
        Accept: '*/*',
        'Accept-Encoding': 'gzip, deflate, br, zstd',
        'Accept-Language': 'en-GB,en-US;q=0.9,en;q=0.8,no;q=0.7',
        Connection: 'keep-alive',
        Cookie: 'JSESSIONID=15hb7cb69ai5vrbg9msvtjmur0; app.browse.RecentItemsList=com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        Host: 'localhost:8080',
        Referer: 'http://localhost:8080/admin/tool/com.enonic.app.contentstudio/main',
        'sec-ch-ua': '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"macOS"',
        'Sec-Fetch-Dest': 'empty',
        'Sec-Fetch-Mode': 'cors',
        'Sec-Fetch-Site': 'same-origin',
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36',
    } as Record<string, string>,
    getHeader: function (header: string): string {
        return this.headers[header];
    },
    cookies: {
        'app.browse.RecentItemsList': 'com.example.myproject%3Aperson%7Cbase%3Afolder%7Cportal%3Asite',
        JSESSIONID: '15hb7cb69ai5vrbg9msvtjmur0',
    },
};
expectAssignable<Request>(menuitemWidgetRequest);

// Scenario: Illegal values for properties
const requiredProperties = {
    branch: 'draft',
    cookies: {},
    getHeader: (header: string): string => {
        return requiredProperties.headers[header];
    },
    headers: {} as Record<string, string>,
    host: 'localhost',
    method: 'GET',
    mode: 'admin',
    params: {},
    rawPath: '/whatever',
    remoteAddress: '127.0.0.1',
    path: '/whatever',
    port: 8080,
    scheme: 'http',
    url: 'http://localhost:8080/whatever',
    webSocket: false,
};
expectAssignable<Request>(requiredProperties);

// LiteralUnion suggests 'draft'|'master', but allows string
expectAssignable<Request>({
    ...requiredProperties,
    branch: 'string but not draft|master',
});

// LiteralUnion suggests 'GET'|'POST'|'PUT'|'DELETE'|'HEAD'|'OPTIONS'|'PATCH'|'TRACE'|'CONNECT', but allows string
expectAssignable<Request>({
    ...requiredProperties,
    method: 'string but not GET|POST|PUT|DELETE|HEAD|OPTIONS|PATCH|TRACE|CONNECT',
});

// LiteralUnion suggests 'edit'|'inline'|'live'|'preview'|'admin', but allows string
expectAssignable<Request>({
    ...requiredProperties,
    mode: 'string but not edit|inline|live|preview|admin',
});

// LiteralUnion suggests 'http'|'https', but allows string
expectAssignable<Request>({
    ...requiredProperties,
    scheme: 'string but not http|https',
});

const branchNotString = {
    ...requiredProperties,
    branch: 123,
};
// printType(branchNotString);

expectNotAssignable<Request>(branchNotString);

// Allowed to change type of branch to number
expectAssignable<StrictMergeInterfaces<Request,{branch: number}>>(branchNotString);

expectNotAssignable<Request>({
    ...requiredProperties,
    host: 123, // not string
});

expectNotAssignable<Request>({
    ...requiredProperties,
    method: 123, // not string
});

expectNotAssignable<Request>({
    ...requiredProperties,
    mode: 123, // not string
});

expectNotAssignable<Request>({
    ...requiredProperties,
    path: 123, // not string
});

expectNotAssignable<Request>({
    ...requiredProperties,
    port: '8080', // not number
});

expectNotAssignable<Request>({
    ...requiredProperties,
    scheme: 123, // not string
});

expectNotAssignable<Request>({
    ...requiredProperties,
    url: 123, // not string
});

expectNotAssignable<Request>({
    ...requiredProperties,
    custom: 'whatever', // Untyped custom property NOT allowed
});

expectAssignable<StrictMergeInterfaces<Request,{
    custom: string
}>>({
    ...requiredProperties,
    custom: 'whatever', // Typed custom property allowed
});
