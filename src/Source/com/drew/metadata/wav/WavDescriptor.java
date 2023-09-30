package Source.com.drew.metadata.wav;

import Source.com.drew.lang.annotations.NotNull;
import Source.com.drew.lang.annotations.Nullable;
import Source.com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class WavDescriptor extends TagDescriptor<WavDirectory>
{
    public WavDescriptor(@NotNull WavDirectory directory)
    {
        super(directory);
    }

    @Override
    @Nullable
    public String getDescription(int tagType)
    {
        return super.getDescription(tagType);
    }
}
