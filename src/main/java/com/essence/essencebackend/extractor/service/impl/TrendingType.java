package com.essence.essencebackend.extractor.service.impl;

import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;

public enum TrendingType {

//    solo está funcionando song
    SONGS("trending_music", StreamInfoItem.class),
    ALBUMS("New releases", PlaylistInfoItem.class),
    ARTISTS("Trending", ChannelInfoItem.class);

    private final String kioskId;
    private final Class<?> itemClass;

    TrendingType(String kioskId, Class<?> itemClass) {
        this.kioskId = kioskId;
        this.itemClass = itemClass;
    }
    public String getKioskId() { return kioskId; }
    public Class<?> getItemClass() { return itemClass; }

}