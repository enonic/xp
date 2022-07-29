const path = require('path');
const fs = require('fs');
const glob = require('glob');

// TODO: Replace with `xpVersion()` before the first actual release
const XP_VERSION = '0.1.0';

const PACKAGE_TEMPLATE = fs.readFileSync(path.resolve(__dirname, 'templates/package.template.json')).toString();

// eslint-disable-next-line no-unused-vars
function xpVersion() {
    const properties = fs.readFileSync(path.resolve('../../gradle.properties')).toString();
    const [, version] = /^version\s*=\s*(.+)$/m.exec(properties) || [];
    return version;
}

function createData(version, fullName, shortName) {
    return PACKAGE_TEMPLATE
        .replaceAll(/%VERSION%/g, XP_VERSION)
        .replaceAll(/%FULL_NAME%/g, fullName)
        .replaceAll(/%SHORT_NAME%/g, shortName);
}

function copyPackages() {
    glob.sync('lib-*').forEach(async (libPath) => {
        const shortName = libPath.substr(4);
        const fullName = `lib-${shortName}`;

        const hasTs = fs.existsSync(path.join(libPath, `src/main/resources/lib/xp/${shortName}.ts`));
        if (!hasTs) {
            return;
        }

        fs.writeFileSync(path.join(libPath, 'package.json'), createData(XP_VERSION, fullName, shortName));
    });
}

function copyGlobalPackage() {
    glob.sync('typescript').forEach(async (libPath) => {
        fs.writeFileSync(path.join(libPath, 'package.json'), createData(XP_VERSION, 'global', 'common'));
    });
}

void function () {
    copyGlobalPackage();
    copyPackages();
}();
