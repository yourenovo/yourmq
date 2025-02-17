package org.yourmq.client.base;

import java.io.IOException;

public class FragmentHandlerDefault extends FragmentHandlerBase {
    public FragmentHandlerDefault() {
    }

    protected FragmentAggregator createFragmentAggregator(MessageInternal message) throws IOException {
        return new FragmentAggregatorDefault(message);
    }

    public boolean aggrEnable() {
        return true;
    }
}
