package com.essence.essencebackend.extractor.service.impl;

import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

public enum TrendingType {

    SONGS(StreamInfoItem.class),
    ALBUMS(PlaylistInfoItem.class),
    ARTISTS(ChannelInfoItem.class);

    private final Class<?> itemClass;

    TrendingType(Class<?> itemClass) {
        this.itemClass = itemClass;
    }

    public Class<?> getItemClass() { return itemClass; }
}