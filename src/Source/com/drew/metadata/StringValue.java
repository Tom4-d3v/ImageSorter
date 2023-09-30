/*
 * Copyright 2002-2019 Drew Noakes and contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package Source.com.drew.metadata;

import Source.com.drew.lang.annotations.NotNull;
import Source.com.drew.lang.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author Drew Noakes https://drewnoakes.com
 */
public final class StringValue
{
    @NotNull
    private final byte[] _bytes;

    @Nullable
    private final Charset _charset;

    public StringValue(@NotNull byte[] bytes, @Nullable Charset charset)
    {
        _bytes = bytes;
        _charset = charset;
    }

    @NotNull
    public byte[] getBytes()
    {
        return _bytes;
    }

    @Nullable
    public Charset getCharset()
    {
        return _charset;
    }

    @Override
    public String toString()
    {
        return toString(_charset);
    }

    public String toString(@Nullable Charset charset)
    {
        if (charset != null) {
            try {
                return new String(_bytes, charset.name());
            } catch (UnsupportedEncodingException ex) {
                // fall through
            }
        }

        return new String(_bytes);
    }
}
