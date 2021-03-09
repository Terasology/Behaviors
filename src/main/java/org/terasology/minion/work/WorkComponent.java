// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.Component;

/**
 *
 */
public class WorkComponent implements Component {
    public String workType;
    public transient SimpleUri uri;

    public SimpleUri getUri() {
        if (uri == null) {
            uri = new SimpleUri(workType);
        }
        return uri;
    }

}
