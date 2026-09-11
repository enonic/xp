# Predefined image processing styles

WebP and AVIF output requires a predefined style and an explicitly configured
ImageMagick executable with WebP/AVIF encoding support. Existing JPEG/PNG/GIF
processing continues to use ImageIO.

Define each permitted variant in the application's `cms/style/style.yml`:

```yaml
kind: Style
styles:
  - type: Image
    name: card-webp
    label: Card WebP
    scale: block(640,360)
    format: webp
    quality: 80
  - type: Image
    name: card-avif
    label: Card AVIF
    scale: block(640,360)
    format: avif
    quality: 60
```

Generate the URL with:

```javascript
portal.imageUrl({ id: imageId, style: 'com.example.site:card-webp' });
```

The URL retains the original file name and uses the `full` path placeholder plus
`?style=com.example.site%3Acard-webp`. The response Content-Type is determined by
the style. Both the legacy image endpoint and `media:image` support this form.

A processing style must define `scale` and `format`. Supported scales are
`max`, `width`, `height`, `square`, `block`, and `wide`, with fixed positive
dimensions and the existing `scale.maxDimension` limit. `full` is deliberately
excluded from processing styles. Formats are `jpeg`, `png`, `gif`, `webp`, and
`avif`. Optional `quality` defaults to 85; optional `filter` uses the existing
filter syntax and limits. `background` is a hexadecimal RGB value used when
flattening to JPEG/GIF; PNG/WebP/AVIF preserve transparency. The existing
`aspectRatio` field remains an editor hint; the processing `scale` explicitly
defines the output geometry. Define separate styles for responsive sizes.

Style URLs reject scale, format, quality, filter, and background overrides,
including empty query parameters, duplicate styles, and added output extensions.
The image service resolves styles again before processing, so arbitrary URL
parameters cannot authorize a modern-format conversion. Content permissions,
stored cropping, focal point, and orientation still apply.

Configure `com.enonic.xp.image.cfg`:

```properties
encoding.executable = /usr/bin/magick
encoding.maxConcurrent = 2
encoding.maxQueue = 8
encoding.queueTimeoutSeconds = 5
encoding.timeoutSeconds = 30
encoding.maxPixels = 40000000
```

The executable is a single path, not a shell command. ImageMagick 6 installations
can use `/usr/bin/convert`. Verify that the installed build can **write** both
WebP and AVIF; not all distributions include an AVIF encoder. No process is
started for ordinary formats or modern-format cache hits. Encoding is disabled
by default. Unstyled original WebP/AVIF/SVG files retain their existing
pass-through behavior. Styled processing currently accepts formats decoded by
the existing ImageIO backend; WebP/AVIF/SVG inputs are rejected, and GIF styles
process the first frame.

Modern cache misses have bounded concurrency and a bounded waiting queue.
Capacity exhaustion and queue timeout return HTTP 429. Source and output pixel
counts are checked, and the existing heap memory estimate is a hard admission
limit for modern conversions. The external encoder receives only a generated
PNG and fixed arguments, uses one configured ImageMagick thread, and is killed
on timeout or interruption. Temporary files and failed cache entries are
removed. ImageMagick pixel-cache memory/map/disk limits are also set; these are
not an operating-system limit on all memory allocated by codec libraries.

The cache key includes the source checksum and resolved processing parameters.
Concurrent requests recheck the cache after obtaining the file lock, preventing
duplicate conversions. The HTTP response uses `Cache-Control: private, no-cache` because
the URL's source fingerprint does not version the style definition; requests
revalidate and reuse the disk cache until the source or style changes.

Unit tests cover style enforcement, cache coalescing, and encoder failure and
timeout cleanup. Optional native codec tests require
`XP_TEST_IMAGEMAGICK=/path/to/magick` with both encoders installed.
