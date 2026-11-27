import {execFileSync} from 'node:child_process';
import {appendFileSync, copyFileSync, cpSync, existsSync, mkdirSync, rmSync, writeFileSync} from 'node:fs';
import {dirname, join} from 'node:path';

import {libDir, readJson, readMembers, xpPackageDir} from './packages.mjs';

if (!process.env.npm_config_user_agent?.startsWith('pnpm/')) {
    throw new Error('Run this script with `pnpm run test:packages`.');
}
// pnpm 12 is a native binary, but corepack runs it through a JS launcher. npm is the one bundled with the running
// Node.js when the distribution ships it there, as Gradle's does, and the one on PATH otherwise.
const pnpmPath = process.env.npm_execpath;
const pnpm = /\.[cm]?js$/.test(pnpmPath) ? [process.execPath, pnpmPath] : [pnpmPath];
const nodeDir = dirname(process.execPath);
const bundledNpmCli = process.platform === 'win32'
    ? join(nodeDir, 'node_modules', 'npm', 'bin', 'npm-cli.js')
    : join(nodeDir, '..', 'lib', 'node_modules', 'npm', 'bin', 'npm-cli.js');
const npm = existsSync(bundledNpmCli) ? [process.execPath, bundledNpmCli] : ['npm'];
const tsc = [process.execPath, join(libDir, 'node_modules', 'typescript', 'bin', 'tsc')];

const fixturesDir = join(libDir, 'test-packages');
const workDir = join(libDir, 'build', 'test-packages');
const tarballsDir = join(workDir, 'tarballs');

function run([file, ...prefix], args, cwd) {
    return execFileSync(file, [...prefix, ...args], {cwd, encoding: 'utf8', stdio: 'pipe', maxBuffer: 64 * 1024 * 1024});
}

function pack(dirs) {
    const packed = JSON.parse(run(npm, ['pack', '--json', '--pack-destination', tarballsDir, ...dirs], libDir));
    return packed.map(({name, version, filename}) => ({name, version, spec: `file:${join(tarballsDir, filename)}`}));
}

rmSync(workDir, {recursive: true, force: true});
mkdirSync(tarballsDir, {recursive: true});

const members = readMembers();
const xpPackage = readJson(join(xpPackageDir, 'package.json'));
const packed = pack([...members.map(({dir}) => dir), xpPackageDir]);
const tarballs = Object.fromEntries(packed.map(({name, spec}) => [name, spec]));

const memberNames = members.map(({pkg}) => pkg.name);
const libNames = memberNames
    .filter(name => name.startsWith('@enonic-types/lib-'))
    .map(name => name.slice('@enonic-types/lib-'.length));

// The skewed lib-content keeps core at the build version: a second core copy makes npm layouts fail
// with TS2578 on the `@ts-expect-error` in core/index.d.ts.
const skewedName = '@enonic-types/lib-content';
const skewedMember = members.find(({pkg}) => pkg.name === skewedName);
const skewedVersion = xpPackage.version.replace(/^(\d+\.\d+\.)(\d+)/, (_, minor, patch) => `${minor}${Number(patch) + 1}`);
const skewedDir = join(workDir, 'skewed');
cpSync(skewedMember.dir, skewedDir, {recursive: true});
writeFileSync(join(skewedDir, 'package.json'), JSON.stringify({...skewedMember.pkg, version: skewedVersion}, null, 4));
appendFileSync(join(skewedDir, skewedMember.pkg.types), 'export declare const skewMarker: true;\n');
const [skewed] = pack([skewedDir]);

// Members are not on the registry, so their references must resolve to the local tarballs. The keys carry the
// version, so pnpm leaves the skewed direct dependency alone.
const memberOverrides = Object.fromEntries(packed
    .filter(({name}) => name !== xpPackage.name)
    .map(({name, version, spec}) => [`${name}@${version}`, spec]));

const requireAll = [
    'import type {Assert, IsTyped} from \'./assert\';',
    '',
    ...libNames.flatMap(name => [
        `const lib_${name} = require('/lib/xp/${name}');`,
        `export type Lib_${name}_IsTyped = Assert<IsTyped<typeof lib_${name}>>;`,
    ]),
    '',
].join('\n');

const compilerOptions = {
    target: 'es5',
    lib: ['es5'],
    module: 'preserve',
    moduleResolution: 'bundler',
    strict: true,
    noEmit: true,
    skipLibCheck: false,
    noUncheckedSideEffectImports: true,
};

const libPaths = {'/lib/xp/*': ['./node_modules/@enonic-types/lib-*']};

const scenarios = [
    {
        // Only the meta package is a direct dependency, so every lib must come in through its references.
        name: 'require',
        dependencies: {[xpPackage.name]: tarballs[xpPackage.name]},
        compilerOptions: {types: [xpPackage.name]},
        // Apps compiled to CommonJS: TypeScript 6 deprecates node10, the only resolution `module: commonjs` accepts in 5.x.
        variants: {node16: {module: 'node16', moduleResolution: 'node16'}},
        files: ['assert.ts', 'require.ts', 'require-all.ts'],
    },
    {
        // The README setup: `paths` into top-level node_modules, which pnpm only fills through public hoisting.
        name: 'import',
        dependencies: {[xpPackage.name]: tarballs[xpPackage.name]},
        publicHoistPattern: ['@enonic-types/*'],
        compilerOptions: {types: [xpPackage.name], paths: libPaths},
        files: ['assert.ts', 'import.ts', 'api/content.ts', 'api/context.ts', 'api/node.ts', 'api/portal.ts'],
    },
    {
        name: 'import-members',
        dependencies: Object.fromEntries(memberNames.map(name => [name, tarballs[name]])),
        compilerOptions: {types: ['@enonic-types/global'], paths: libPaths},
        files: ['assert.ts', 'import.ts'],
    },
    {
        name: 'skew',
        dependencies: {[xpPackage.name]: tarballs[xpPackage.name], [skewedName]: skewed.spec},
        // npm rejects any override of a direct dependency's name, so xp's own lib-content is overridden inside xp only.
        npmOverrides: {
            ...Object.fromEntries(Object.entries(memberOverrides).filter(([key]) => !key.startsWith(`${skewedName}@`))),
            [xpPackage.name]: {[skewedName]: tarballs[skewedName]},
        },
        installed: {[skewedName]: skewedVersion},
        compilerOptions: {types: [xpPackage.name]},
        files: ['assert.ts', 'skew.ts'],
    },
];

const installers = {
    npm: cwd => run(npm, ['install', '--offline', '--ignore-scripts', '--no-audit', '--no-fund'], cwd),
    // Without hoisting, a package resolves only what it declares, so tsc fails on an undeclared `@enonic-types/*` import.
    pnpm: cwd => run(pnpm, ['install', '--offline', '--ignore-scripts', '--no-frozen-lockfile', '--config.hoist=false',
        '--store-dir', join(workDir, 'pnpm-store')], cwd),
};

const failures = [];

function fail(label, error) {
    console.log(`FAIL ${label}`);
    failures.push(`${label}:\n${error.stdout ?? error.message}${error.stderr ?? ''}`.trim());
}

for (const {pkg} of [...members, {pkg: xpPackage}]) {
    const problems = Object.entries(pkg.dependencies ?? {})
        .filter(([name, version]) => name.startsWith('@enonic-types/') && version !== pkg.version)
        .map(([name, version]) => `depends on ${name}@${version} instead of ${pkg.version}`);
    if (problems.length > 0) {
        console.log(`FAIL ${pkg.name} dependencies`);
        failures.push(`${pkg.name}:\n${problems.join('\n')}`);
    }
}

for (const [manager, install] of Object.entries(installers)) {
    for (const scenario of scenarios) {
        const label = `${manager}/${scenario.name}`;
        const appDir = join(workDir, manager, scenario.name);
        mkdirSync(appDir, {recursive: true});

        writeFileSync(join(appDir, 'package.json'), JSON.stringify({
            name: `fixture-${manager}-${scenario.name}`,
            private: true,
            devDependencies: scenario.dependencies,
            overrides: scenario.npmOverrides ?? memberOverrides,
        }, null, 4));
        if (manager === 'pnpm') {
            // JSON is valid YAML, and pnpm reads these settings only from pnpm-workspace.yaml.
            writeFileSync(join(appDir, 'pnpm-workspace.yaml'), JSON.stringify({
                overrides: memberOverrides,
                publicHoistPattern: scenario.publicHoistPattern,
            }, null, 4));
        }
        const tsconfigs = Object.entries({'': {}, ...scenario.variants}).map(([variant, options]) => {
            const file = variant ? `tsconfig.${variant}.json` : 'tsconfig.json';
            writeFileSync(join(appDir, file), JSON.stringify({
                compilerOptions: {...compilerOptions, ...scenario.compilerOptions, ...options},
                files: scenario.files,
            }, null, 4));
            return {label: variant ? `${label}/${variant}` : label, file};
        });
        writeFileSync(join(appDir, 'require-all.ts'), requireAll);
        for (const file of scenario.files.filter(file => file !== 'require-all.ts')) {
            mkdirSync(dirname(join(appDir, file)), {recursive: true});
            copyFileSync(join(fixturesDir, file), join(appDir, file));
        }

        try {
            install(appDir);
            for (const [name, version] of Object.entries(scenario.installed ?? {})) {
                const installedVersion = readJson(join(appDir, 'node_modules', name, 'package.json')).version;
                if (installedVersion !== version) {
                    throw new Error(`node_modules/${name} is ${installedVersion} instead of ${version}`);
                }
            }
        } catch (error) {
            fail(label, error);
            continue;
        }
        for (const tsconfig of tsconfigs) {
            try {
                run(tsc, ['--project', tsconfig.file], appDir);
                console.log(`ok   ${tsconfig.label}`);
            } catch (error) {
                fail(tsconfig.label, error);
            }
        }
    }
}

if (failures.length > 0) {
    console.error(`\n${failures.join('\n\n')}`);
    process.exitCode = 1;
} else {
    writeFileSync(join(workDir, '.ok'), '');
}
