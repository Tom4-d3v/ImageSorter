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
import Source.com.drew.metadata.mov.QuickTimeAtomTypes;
import Source.com.drew.metadata.mov.QuickTimeContext;
import Source.com.drew.metadata.mov.QuickTimeMediaHandler;
import Source.com.drew.metadata.mov.atoms.Atom;
import Source.com.drew.metadata.mov.atoms.TimecodeInformationMediaAtom;
import Source.com.drew.metadata.mov.atoms.TimecodeSampleDescriptionAtom;

import java.io.IOException;

/**
 * @author Payton Garland
 */
public class QuickTimeTimecodeHandler extends QuickTimeMediaHandler<QuickTimeTimecodeDirectory>
{
    public QuickTimeTimecodeHandler(Metadata metadata, QuickTimeContext context)
    {
        super(metadata, context);
    }

    @NotNull
    @Override
    protected QuickTimeTimecodeDirectory createDirectory()
    {
        return new QuickTimeTimecodeDirectory();
    }

    @Override
    protected String getMediaInformation()
    {
        return QuickTimeAtomTypes.ATOM_TIMECODE_MEDIA_INFO;
    }

    @Override
    public void processSampleDescription(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TimecodeSampleDescriptionAtom timecodeSampleDescriptionAtom = new TimecodeSampleDescriptionAtom(reader, atom);
        timecodeSampleDescriptionAtom.addMetadata(directory);
    }

    @Override
    public void processMediaInformation(@NotNull SequentialReader reader, @NotNull Atom atom) throws IOException
    {
        TimecodeInformationMediaAtom timecodeInformationMediaAtom = new TimecodeInformationMediaAtom(reader, atom);
        timecodeInformationMediaAtom.addMetadata(directory);
    }

    @Override
    protected void processTimeToSample(@NotNull SequentialReader reader, @NotNull Atom atom, QuickTimeContext context) throws IOException
    {
        // Do nothing
    }
}
