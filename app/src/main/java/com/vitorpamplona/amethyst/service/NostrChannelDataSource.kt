package com.vitorpamplona.amethyst.service

import com.vitorpamplona.amethyst.model.LocalCache
import com.vitorpamplona.amethyst.model.Note
import com.vitorpamplona.amethyst.service.model.ChannelMessageEvent
import nostr.postr.JsonFilter

object NostrChannelDataSource: NostrDataSource<Note>("ChatroomFeed") {
  var channel: com.vitorpamplona.amethyst.model.Channel? = null

  fun loadMessagesBetween(channelId: String) {
    channel = LocalCache.channels[channelId]
  }

  fun createMessagesToChannelFilter() = JsonFilter(
    kinds = listOf(ChannelMessageEvent.kind),
    tags = mapOf("e" to listOf(channel?.idHex).filterNotNull()),
    since = System.currentTimeMillis() / 1000 - (60 * 60 * 24 * 1), // 24 hours
  )

  val messagesChannel = requestNewChannel()

  // returns the last Note of each user.
  override fun feed(): List<Note> {
    return channel?.notes?.values?.sortedBy { it.event!!.createdAt } ?: emptyList()
  }

  override fun updateChannelFilters() {
    messagesChannel.filter = createMessagesToChannelFilter()
  }
}