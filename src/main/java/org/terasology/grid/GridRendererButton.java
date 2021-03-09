// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.grid;

import org.terasology.engine.input.BindButtonEvent;
import org.terasology.engine.input.DefaultBinding;
import org.terasology.engine.input.RegisterBindButton;
import org.terasology.input.InputType;
import org.terasology.input.Keyboard;

/**
 *
 */
@RegisterBindButton(id = "grid_renderer", description = "Toggle grid renderer")
@DefaultBinding(type = InputType.KEY, id = Keyboard.KeyId.F6)
public class GridRendererButton extends BindButtonEvent {
}
