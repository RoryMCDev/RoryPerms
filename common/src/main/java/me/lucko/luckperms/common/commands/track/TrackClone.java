/*
 * This file is part of RoryPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.commands.track;

import me.lucko.luckperms.common.actionlog.LoggedAction;
import me.lucko.luckperms.common.command.abstraction.ChildCommand;
import me.lucko.luckperms.common.command.access.ArgumentPermissions;
import me.lucko.luckperms.common.command.access.CommandPermission;
import me.lucko.luckperms.common.command.spec.CommandSpec;
import me.lucko.luckperms.common.command.utils.ArgumentList;
import me.lucko.luckperms.common.command.utils.StorageAssistant;
import me.lucko.luckperms.common.locale.Message;
import me.lucko.luckperms.common.model.Track;
import me.lucko.luckperms.common.plugin.RoryPermsPlugin;
import me.lucko.luckperms.common.sender.Sender;
import me.lucko.luckperms.common.storage.misc.DataConstraints;
import me.lucko.luckperms.common.util.Predicates;

import net.kyori.adventure.text.Component;
import net.luckperms.api.event.cause.CreationCause;

public class TrackClone extends ChildCommand<Track> {
    public TrackClone() {
        super(CommandSpec.TRACK_CLONE, "clone", CommandPermission.TRACK_CLONE, Predicates.not(1));
    }

    @Override
    public void execute(RoryPermsPlugin plugin, Sender sender, Track target, ArgumentList args, String label) {
        if (ArgumentPermissions.checkViewPerms(plugin, sender, getPermission().get(), target)) {
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        String newTrackName = args.get(0).toLowerCase();
        if (!DataConstraints.TRACK_NAME_TEST.test(newTrackName)) {
            Message.TRACK_INVALID_ENTRY.send(sender, newTrackName);
            return;
        }

        Track newTrack = plugin.getStorage().createAndLoadTrack(newTrackName, CreationCause.INTERNAL).join();
        if (newTrack == null) {
            Message.TRACK_LOAD_ERROR.send(sender);
            return;
        }

        if (ArgumentPermissions.checkModifyPerms(plugin, sender, getPermission().get(), newTrack)) {
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        newTrack.setGroups(target.getGroups());

        Message.CLONE_SUCCESS.send(sender, Component.text(target.getName()), Component.text(newTrack.getName()));

        LoggedAction.build().source(sender).target(target)
                .description("clone", newTrack.getName())
                .build().submit(plugin, sender);

        StorageAssistant.save(newTrack, sender, plugin);
    }
}
