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
package Source.com.drew.metadata.mov.media;

import Source.com.drew.lang.SequentialReader;
import Source.com.drew.lang.annotations.NotNull;
import Source.com.drew.metadata.Metadata;
import Source.com.drew.metadata.mov.QuickTimeContext;
import Source.com.drew.metadata.mov.QuickTimeMediaHandler;
import Source.com.drew.metadata.mov.atoms.Atom;
import Source.com.drew.metadata.mov.atoms.MusicSampleDescriptionAtom;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class QuickTimeMusicHandler extends QuickTimeMediaHandler<QuickTimeMusicDirectory>
{
    public QuickTimeMusicHandler(Metadata metadata, QuickTimeContext context)
    {
        super(metadata, context);
    }

    @NotNull
    @Override
    protected QuickTimeMusicDirectory createDirectory()
    {
        return new QuickTimeMusicDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return null;
    }

    @Override
    protected void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        MusicSampleDescriptionAtom musicSampleDescriptionAtom = new MusicSampleDescriptionAtom(reader, atom);
        musicSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    protected void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        // Not yet implemented
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom, QuickTimeContext context) throws IOException
    {
        // Not yet implemented
    }
}
