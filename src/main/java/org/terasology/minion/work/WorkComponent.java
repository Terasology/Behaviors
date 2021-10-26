// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.minion.work;

import org.terasology.engine.core.SimpleUri;
import org.terasology.gestalt.entitysystem.component.Component;

public class WorkComponent implements Component<WorkComponent> {
    public String workType;
    public transient SimpleUri uri;

    public SimpleUri getUri() {
        if (uri == null) {
            uri = new SimpleUri(workType);
        }
        return uri;
    }

    @Override
    public void copyFrom(WorkComponent other) {
        this.workType = other.workType;
        this.uri = other.uri;
    }
}
