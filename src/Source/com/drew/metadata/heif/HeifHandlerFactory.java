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
package Source.com.drew.metadata.heif;

import Source.com.drew.imaging.heif.HeifHandler;
import Source.com.drew.metadata.Metadata;
import Source.com.drew.metadata.heif.boxes.HandlerBox;

public class HeifHandlerFactory
{
    private static final String HANDLER_PICTURE             = "pict";

    private HeifHandler<?> caller;

    public HeifHandlerFactory(HeifHandler<?> caller)
    {
        this.caller = caller;
    }

    public HeifHandler<?> getHandler(HandlerBox box, Metadata metadata)
    {
        String type = box.getHandlerType();
        if (type.equals(HANDLER_PICTURE)) {
            return new HeifPictureHandler(metadata);
        }
        return caller;
    }
}
